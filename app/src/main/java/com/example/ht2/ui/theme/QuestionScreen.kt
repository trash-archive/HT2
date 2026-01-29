package com.example.ht2.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ht2.viewmodel.QuestionViewModel

@Composable
fun QuestionScreen(
    viewModel: QuestionViewModel = viewModel(),
    onNavigateToLiked: () -> Unit
) {
    val currentQuestion by viewModel.currentQuestion.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val likedQuestions by viewModel.likedQuestions.collectAsState()
    val categories by viewModel.categories.collectAsState()
    var isCategoryMenuExpanded by remember { mutableStateOf(false) }
    var showAddQuestionDialog by remember { mutableStateOf(false) }

    // Check if swiping is allowed
    val canSwipe = viewModel.canSwipeInCurrentCategory()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFB85C5C),
                        Color(0xFFA84848),
                        Color(0xFF963D3D)
                    )
                )
            )
    ) {
        // Category Dropdown (top left)
        Box(
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 48.dp, start = 16.dp)
        ) {
            Surface(
                onClick = { isCategoryMenuExpanded = !isCategoryMenuExpanded },
                shape = RoundedCornerShape(16.dp),
                color = Color.White.copy(alpha = 0.25f),
                modifier = Modifier
                    .widthIn(min = 140.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = selectedCategory,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = Color.White,
                        letterSpacing = 0.3.sp
                    )
                    Icon(
                        imageVector = Icons.Filled.ArrowDropDown,
                        contentDescription = "Select Category",
                        tint = Color.White,
                        modifier = Modifier
                            .size(20.dp)
                            .rotate(if (isCategoryMenuExpanded) 180f else 0f)
                    )
                }
            }

            // Category dropdown menu
            DropdownMenu(
                expanded = isCategoryMenuExpanded,
                onDismissRequest = { isCategoryMenuExpanded = false },
                offset = DpOffset(0.dp, 8.dp),
                modifier = Modifier
                    .widthIn(min = 180.dp)
                    .heightIn(max = 400.dp)
                    .background(
                        color = Color.White,
                        shape = RoundedCornerShape(16.dp)
                    )
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = category,
                                    fontWeight = if (selectedCategory == category) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 15.sp,
                                    color = if (selectedCategory == category) Color(0xFFB85C5C) else Color(0xFF2D2D2D)
                                )
                                if (selectedCategory == category) {
                                    Box(
                                        modifier = Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFFB85C5C))
                                    )
                                }
                            }
                        },
                        onClick = {
                            viewModel.setCategory(category)
                            isCategoryMenuExpanded = false
                        },
                        modifier = Modifier.background(
                            if (selectedCategory == category)
                                Color(0xFFB85C5C).copy(alpha = 0.08f)
                            else
                                Color.Transparent
                        )
                    )
                }
            }
        }

        // Liked button (top right)
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 48.dp, end = 16.dp)
        ) {
            Surface(
                onClick = onNavigateToLiked,
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.25f),
                modifier = Modifier.size(56.dp)
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Liked Questions",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }

        // Card in center
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // Only show card if currentQuestion is not null
            currentQuestion?.let { question ->
                // Force reading the latest liked state directly from StateFlow
                val isCurrentlyLiked = likedQuestions.contains(question.id)

                AnimatedContent(
                    targetState = question.id,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInHorizontally(
                                initialOffsetX = { 1500 },
                                animationSpec = tween(300)
                            ) + fadeIn(tween(200))).togetherWith(
                                slideOutHorizontally(
                                    targetOffsetX = { -1500 },
                                    animationSpec = tween(300)
                                ) + fadeOut(tween(200))
                            )
                        } else {
                            (slideInHorizontally(
                                initialOffsetX = { -1500 },
                                animationSpec = tween(300)
                            ) + fadeIn(tween(200))).togetherWith(
                                slideOutHorizontally(
                                    targetOffsetX = { 1500 },
                                    animationSpec = tween(300)
                                ) + fadeOut(tween(200))
                            )
                        }
                    },
                    label = "card animation"
                ) { _ ->
                    QuestionCard(
                        question = question,
                        isLiked = isCurrentlyLiked,
                        onSwipeLeft = {
                            viewModel.getNextQuestion()
                        },
                        onSwipeRight = {
                            viewModel.getPreviousQuestion()
                        },
                        onToggleLike = {
                            viewModel.toggleLike(question.id)
                        },
                        onDelete = if (question.isCustom) {
                            { viewModel.deleteCustomQuestion(question.id) }
                        } else null,
                        canSwipe = canSwipe, // Pass the canSwipe parameter
                        modifier = Modifier.padding(24.dp)
                    )
                }
            }
        }

        // Add Question FAB (bottom right)
        FloatingActionButton(
            onClick = { showAddQuestionDialog = true },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 50.dp, end = 24.dp),
            containerColor = Color.White,
            contentColor = Color(0xFFB85C5C),
            shape = CircleShape
        ) {
            Icon(
                imageVector = Icons.Filled.Add,
                contentDescription = "Add Question",
                modifier = Modifier.size(28.dp)
            )
        }

        // Category indicator at bottom
        AnimatedVisibility(
            visible = selectedCategory != "All",
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 50.dp),
            enter = fadeIn() + slideInVertically { it },
            exit = fadeOut() + slideOutVertically { it }
        ) {
            Surface(
                color = Color.White.copy(alpha = 0.25f),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier.clip(RoundedCornerShape(24.dp))
            ) {
                Text(
                    text = selectedCategory,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp),
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }

    // Add Question Dialog
    if (showAddQuestionDialog) {
        AddQuestionDialog(
            onDismiss = { showAddQuestionDialog = false },
            onSave = { questionText ->
                viewModel.addCustomQuestion(questionText)
            }
        )
    }
}