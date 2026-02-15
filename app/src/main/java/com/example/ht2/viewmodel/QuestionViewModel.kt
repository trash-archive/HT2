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
    private val _categories = MutableStateFlow(listOf("All"))
    val categories: StateFlow<List<String>> = _categories.asStateFlow()
    private val shuffledQuestionsMap = mutableMapOf<String, List<Question>>()
    private val indexMap = mutableMapOf<String, Int>()
    private val _likedQuestions = MutableStateFlow<Set<Int>>(emptySet())
    val likedQuestions: StateFlow<Set<Int>> = _likedQuestions.asStateFlow()
    private val likedActionTimes = mutableMapOf<Int, Long>()
    private val askedActionTimes = mutableMapOf<Int, Long>()
    private val dislikedActionTimes = mutableMapOf<Int, Long>()
    private val _askedQuestions = MutableStateFlow<Set<Int>>(emptySet())
    val askedQuestions: StateFlow<Set<Int>> = _askedQuestions.asStateFlow()
    private val _dislikedQuestions = MutableStateFlow<Set<Int>>(emptySet())
    val dislikedQuestions: StateFlow<Set<Int>> = _dislikedQuestions.asStateFlow()
    private val _endearment = MutableStateFlow("HT²")
    val endearment: StateFlow<String> = _endearment.asStateFlow()
    private val _couplePhotoUri = MutableStateFlow("")
    val couplePhotoUri: StateFlow<String> = _couplePhotoUri.asStateFlow()
    private val _shownFlowers = MutableStateFlow<Set<Int>>(emptySet())
    val shownFlowers: StateFlow<Set<Int>> = _shownFlowers.asStateFlow()
    private val _selectedCategory = MutableStateFlow("All")
    val selectedCategory: StateFlow<String> = _selectedCategory.asStateFlow()
    private val _currentQuestion = MutableStateFlow<Question?>(null)
    val currentQuestion: StateFlow<Question?> = _currentQuestion.asStateFlow()
    private val _isInitialized = MutableStateFlow(false)
    val isInitialized: StateFlow<Boolean> = _isInitialized.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initialize()
            val loadedLiked = dataStoreManager.getLikedQuestions().first()
            val loadedAsked = dataStoreManager.getAskedQuestions().first()
            val loadedDisliked = dataStoreManager.getDislikedQuestions().first()
            _likedQuestions.value = loadedLiked
            _askedQuestions.value = loadedAsked
            _dislikedQuestions.value = loadedDisliked
            _endearment.value = dataStoreManager.getEndearment().first()
            _couplePhotoUri.value = dataStoreManager.getCouplePhotoUri().first()
            loadedLiked.forEachIndexed { i, id -> likedActionTimes[id] = i.toLong() }
            loadedAsked.forEachIndexed { i, id -> askedActionTimes[id] = i.toLong() }
            loadedDisliked.forEachIndexed { i, id -> dislikedActionTimes[id] = i.toLong() }
            refreshCategories()
            val questions = getAvailableQuestionsForCategory("All")
            if (questions.isNotEmpty()) _currentQuestion.value = questions[0]
            _isInitialized.value = true
        }
        viewModelScope.launch { _likedQuestions.collect { dataStoreManager.saveLikedQuestions(it) } }
        viewModelScope.launch { _askedQuestions.collect { dataStoreManager.saveAskedQuestions(it) } }
        viewModelScope.launch { _dislikedQuestions.collect { dataStoreManager.saveDislikedQuestions(it) } }
        viewModelScope.launch { _endearment.collect { dataStoreManager.saveEndearment(it) } }
        viewModelScope.launch { _couplePhotoUri.collect { dataStoreManager.saveCouplePhotoUri(it) } }
    }

    private fun getAvailableQuestionsForCategory(category: String): List<Question> {
        val allQuestions = repository.getQuestionsByCategory(category)
        val filtered = allQuestions.filterNot {
            _askedQuestions.value.contains(it.id) || _dislikedQuestions.value.contains(it.id)
        }
        return shuffledQuestionsMap.getOrPut(category) { filtered.shuffled() }
    }
    private fun getIndexForCategory(category: String) = indexMap.getOrPut(category) { 0 }
    private fun refreshCategories() { _categories.value = repository.getAllCategories() }
    private fun refreshCurrentCategory() {
        val category = _selectedCategory.value
        shuffledQuestionsMap.remove(category); indexMap[category] = 0
        val questions = getAvailableQuestionsForCategory(category)
        _currentQuestion.value = if (questions.isNotEmpty()) questions[0] else null
    }
    fun setCategory(category: String) {
        _selectedCategory.value = category
        val questions = getAvailableQuestionsForCategory(category)
        _currentQuestion.value = if (questions.isNotEmpty()) questions[getIndexForCategory(category)] else null
    }
    fun getNextQuestion() {
        val category = _selectedCategory.value
        val questions = getAvailableQuestionsForCategory(category)
        if (questions.isEmpty()) { _currentQuestion.value = null; return }
        val newIndex = (getIndexForCategory(category) + 1) % questions.size
        indexMap[category] = newIndex; _currentQuestion.value = questions[newIndex]
    }
    fun getPreviousQuestion() {
        val category = _selectedCategory.value
        val questions = getAvailableQuestionsForCategory(category)
        if (questions.isEmpty()) { _currentQuestion.value = null; return }
        val newIndex = if (getIndexForCategory(category) - 1 < 0) questions.size - 1 else getIndexForCategory(category) - 1
        indexMap[category] = newIndex; _currentQuestion.value = questions[newIndex]
    }
    fun toggleLike(questionId: Int) {
        val s = _likedQuestions.value.toMutableSet()
        if (s.contains(questionId)) { s.remove(questionId); likedActionTimes.remove(questionId) }
        else { s.add(questionId); likedActionTimes[questionId] = System.currentTimeMillis() }
        _likedQuestions.value = s
    }
    fun markAsAsked(questionId: Int) {
        val s = _askedQuestions.value.toMutableSet(); s.add(questionId)
        askedActionTimes[questionId] = System.currentTimeMillis(); _askedQuestions.value = s
    }
    fun unmarkAsAsked(questionId: Int) {
        val s = _askedQuestions.value.toMutableSet(); s.remove(questionId)
        askedActionTimes.remove(questionId); _askedQuestions.value = s
    }
    fun markAsDisliked(questionId: Int) {
        val s = _dislikedQuestions.value.toMutableSet(); s.add(questionId)
        dislikedActionTimes[questionId] = System.currentTimeMillis(); _dislikedQuestions.value = s
        if (_likedQuestions.value.contains(questionId)) toggleLike(questionId)
    }
    fun unmarkAsDisliked(questionId: Int) {
        val s = _dislikedQuestions.value.toMutableSet(); s.remove(questionId)
        dislikedActionTimes.remove(questionId); _dislikedQuestions.value = s
    }
    fun clearAllAskedQuestions() { askedActionTimes.clear(); _askedQuestions.value = emptySet() }
    fun setEndearment(v: String) { _endearment.value = v }
    fun setCouplePhotoUri(uri: String) { _couplePhotoUri.value = uri }
    fun addShownFlower(id: Int) { _shownFlowers.value = _shownFlowers.value + id }
    fun resetShownFlowers() { _shownFlowers.value = emptySet() }
    fun getLikedQuestionsList() = repository.getAllQuestions().filter { _likedQuestions.value.contains(it.id) }.sortedByDescending { likedActionTimes[it.id] ?: 0L }
    fun getAskedQuestionsList() = repository.getAllQuestions().filter { _askedQuestions.value.contains(it.id) }.sortedByDescending { askedActionTimes[it.id] ?: 0L }
    fun getDislikedQuestionsList() = repository.getAllQuestions().filter { _dislikedQuestions.value.contains(it.id) }.sortedByDescending { dislikedActionTimes[it.id] ?: 0L }
    fun addCustomQuestion(text: String) {
        viewModelScope.launch {
            val newQuestion = repository.addCustomQuestion(text)
            refreshCategories(); _selectedCategory.value = "My Questions"
            shuffledQuestionsMap.remove("My Questions"); indexMap["My Questions"] = 0
            _currentQuestion.value = newQuestion
        }
    }
    fun deleteCustomQuestion(questionId: Int) {
        viewModelScope.launch {
            repository.deleteCustomQuestion(questionId)
            if (_likedQuestions.value.contains(questionId)) toggleLike(questionId)
            refreshCategories()
            if (_selectedCategory.value == "My Questions") {
                if (repository.getCustomQuestions().isEmpty()) { shuffledQuestionsMap.clear(); indexMap.clear(); setCategory("All") }
                else refreshCurrentCategory()
            } else if (_selectedCategory.value == "All") refreshCurrentCategory()
        }
    }
    fun getCustomQuestions() = repository.getCustomQuestions()
    fun canSwipeInCurrentCategory(): Boolean {
        val category = _selectedCategory.value
        return !(category == "My Questions" && getAvailableQuestionsForCategory(category).size == 1)
    }
}