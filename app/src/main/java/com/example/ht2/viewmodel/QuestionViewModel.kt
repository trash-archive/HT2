package com.example.ht2.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.ht2.data.DataStoreManager
import com.example.ht2.data.Question
import com.example.ht2.data.QuestionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class QuestionViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStoreManager = DataStoreManager(application)
    private val repository = QuestionRepository(dataStoreManager)

    // All categories including "All" and "My Questions" if custom questions exist
    private val _categories = MutableStateFlow(listOf("All"))
    val categories: StateFlow<List<String>> = _categories.asStateFlow()

    // Store shuffled questions per category
    private val shuffledQuestionsMap = mutableMapOf<String, List<Question>>()

    // Store current index per category
    private val indexMap = mutableMapOf<String, Int>()

    // Store liked questions
    private val _likedQuestions = MutableStateFlow<Set<Int>>(emptySet())
    val likedQuestions: StateFlow<Set<Int>> = _likedQuestions.asStateFlow()

    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()

    private val _currentQuestion = MutableStateFlow<Question?>(null)
    val currentQuestion: StateFlow<Question?> = _currentQuestion.asStateFlow()

    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    init {
        // Initialize repository and load saved data
        viewModelScope.launch {
            // Load repository data
            repository.initialize()

            // Load liked questions
            val savedLikedQuestions = dataStoreManager.getLikedQuestions().first()
            _likedQuestions.value = savedLikedQuestions

            // Refresh categories after loading
            refreshCategories()

            // Set initial question
            val questions = getQuestionsForCategory("All")
            if (questions.isNotEmpty()) {
                _currentQuestion.value = questions[0]
            }

            _isInitialized.value = true
        }

        // Observe liked questions changes and save them
        viewModelScope.launch {
            _likedQuestions.collect { likedIds ->
                dataStoreManager.saveLikedQuestions(likedIds)
            }
        }
    }

    // Get or create shuffled list for a category
    private fun getQuestionsForCategory(category: String): List<Question> {
        return shuffledQuestionsMap.getOrPut(category) {
            repository.getQuestionsByCategory(category).shuffled()
        }
    }

    // Get or create index for a category
    private fun getIndexForCategory(category: String): Int {
        return indexMap.getOrPut(category) { 0 }
    }

    // Refresh categories (e.g., when custom questions are added)
    private fun refreshCategories() {
        _categories.value = repository.getAllCategories()
    }

    // Refresh questions for current category (when custom questions are added)
    private fun refreshCurrentCategory() {
        val category = _selectedCategory.value
        shuffledQuestionsMap.remove(category) // Clear cached shuffle
        indexMap[category] = 0 // Reset index

        val questions = getQuestionsForCategory(category)
        if (questions.isNotEmpty()) {
            _currentQuestion.value = questions[0]
        }
    }

    fun setCategory(category: String) {
        _selectedCategory.value = category

        val questions = getQuestionsForCategory(category)
        val index = getIndexForCategory(category)

        _currentQuestion.value = questions[index]
    }

    fun getNextQuestion() {
        val category = _selectedCategory.value
        val questions = getQuestionsForCategory(category)

        val newIndex = (getIndexForCategory(category) + 1) % questions.size
        indexMap[category] = newIndex

        _currentQuestion.value = questions[newIndex]
    }

    fun getPreviousQuestion() {
        val category = _selectedCategory.value
        val questions = getQuestionsForCategory(category)

        val newIndex = if (getIndexForCategory(category) - 1 < 0) {
            questions.size - 1
        } else {
            getIndexForCategory(category) - 1
        }

        indexMap[category] = newIndex
        _currentQuestion.value = questions[newIndex]
    }

    fun toggleLike(questionId: Int) {
        val currentLikes = _likedQuestions.value.toMutableSet()
        if (currentLikes.contains(questionId)) {
            currentLikes.remove(questionId)
        } else {
            currentLikes.add(questionId)
        }
        _likedQuestions.value = currentLikes
    }

    fun isQuestionLiked(questionId: Int): Boolean {
        return _likedQuestions.value.contains(questionId)
    }

    fun getLikedQuestionsList(): List<Question> {
        return repository.getAllQuestions().filter { question ->
            _likedQuestions.value.contains(question.id)
        }
    }

    // Custom question management
    fun addCustomQuestion(text: String) {
        viewModelScope.launch {
            repository.addCustomQuestion(text)
            refreshCategories()

            // If on "All" or "My Questions" category, refresh to show new question
            if (_selectedCategory.value == "All" || _selectedCategory.value == "My Questions") {
                refreshCurrentCategory()
            }
        }
    }

    fun deleteCustomQuestion(questionId: Int) {
        viewModelScope.launch {
            repository.deleteCustomQuestion(questionId)

            // Unlike the question if it was liked
            if (_likedQuestions.value.contains(questionId)) {
                toggleLike(questionId)
            }

            refreshCategories()

            // If on "My Questions", handle the deletion properly
            if (_selectedCategory.value == "My Questions") {
                val questions = repository.getCustomQuestions()
                if (questions.isEmpty()) {
                    // Switch to "All" if no more custom questions
                    shuffledQuestionsMap.clear()
                    indexMap.clear()
                    setCategory("All")
                } else {
                    // Refresh the category with remaining questions
                    refreshCurrentCategory()
                }
            } else if (_selectedCategory.value == "All") {
                // If on "All", just refresh to remove the deleted question
                refreshCurrentCategory()
            }
        }
    }

    fun getCustomQuestions(): List<Question> {
        return repository.getCustomQuestions()
    }

    // Check if user can swipe in current category
    fun canSwipeInCurrentCategory(): Boolean {
        val category = _selectedCategory.value
        val questions = getQuestionsForCategory(category)
        // Don't allow swiping if there's only 1 question in My Questions
        return !(category == "My Questions" && questions.size == 1)
    }
}