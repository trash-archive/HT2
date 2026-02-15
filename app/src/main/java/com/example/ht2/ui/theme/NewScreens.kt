package com.example.ht2.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ht2.data.Question
import com.example.ht2.viewmodel.QuestionViewModel

// ═══════════════════════════════════════════════════════════════════════════
// HISTORY SCREEN
// ═══════════════════════════════════════════════════════════════════════════

@Composable
fun HistoryScreen(
    viewModel: QuestionViewModel,
    currentTheme: CategoryTheme,
    onNavigateBack: () -> Unit
) {
    val askedQuestionsSet by viewModel.askedQuestions.collectAsState()

    val askedQuestionsList = remember(askedQuestionsSet) {
        viewModel.getAskedQuestionsList()
    }

    var showClearAllDialog by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(currentTheme.gradient))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        onClick = onNavigateBack,
                        shape = CircleShape,
                        color = Color.White.copy(alpha = 0.25f),
                        modifier = Modifier.size(48.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column {
                        Text(
                            text = "History",
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            letterSpacing = (-0.5).sp
                        )
                        Text(
                            text = "${askedQuestionsList.size} ${if (askedQuestionsList.size == 1) "question" else "questions"} asked",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                // Clear All button
                if (askedQuestionsList.isNotEmpty()) {
                    Surface(
                        onClick = { showClearAllDialog = true },
                        shape = RoundedCornerShape(12.dp),
                        color = Color.White.copy(alpha = 0.25f)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = "Clear All",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "Clear All",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Content
            if (askedQuestionsList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.15f),
                            modifier = Modifier.size(100.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.History,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.5f),
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }
                        Text(
                            text = "No questions asked yet",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Questions you swipe on will appear here\nYou can restore them anytime",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(items = askedQuestionsList, key = { it.id }) { question ->
                        var visible by remember { mutableStateOf(true) }

                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(tween(300)) + scaleIn(
                                initialScale = 0.85f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                )
                            ),
                            exit = fadeOut(tween(250)) + scaleOut(
                                targetScale = 0.75f,
                                animationSpec = tween(250, easing = FastOutLinearInEasing)
                            ) + shrinkVertically(
                                animationSpec = tween(300, delayMillis = 200)
                            )
                        ) {
                            HistoryQuestionCard(
                                question = question,
                                onRestore = {
                                    visible = false
                                    viewModel.unmarkAsAsked(question.id)
                                }
                            )
                        }
                    }
                }
            }
        }

        if (showClearAllDialog) {
            AlertDialog(
                onDismissRequest = { showClearAllDialog = false },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = null,
                        tint = Color(0xFFFF6B6B)
                    )
                },
                title = { Text("Clear All History?", fontWeight = FontWeight.Bold) },
                text = {
                    Text("This will restore all ${askedQuestionsList.size} questions back to the question pool.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.clearAllAskedQuestions()
                            showClearAllDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B6B))
                    ) { Text("Clear All") }
                },
                dismissButton = {
                    TextButton(onClick = { showClearAllDialog = false }) { Text("Cancel") }
                }
            )
        }
    }
}

@Composable
fun HistoryQuestionCard(
    question: Question,
    onRestore: () -> Unit
) {
    var showRestoreDialog by remember { mutableStateOf(false) }
    val theme = getCategoryTheme(question.category)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.verticalGradient(theme.cardGradient))
        ) {
            // Category badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = theme.accentColor.copy(alpha = 0.12f),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
            ) {
                Text(
                    text = question.category,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    color = theme.accentColor,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp,
                    letterSpacing = 0.3.sp
                )
            }

            // Restore button
            Surface(
                onClick = { showRestoreDialog = true },
                shape = CircleShape,
                color = Color(0xFF4CAF50).copy(alpha = 0.15f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = "Restore",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Question text
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(top = 52.dp, bottom = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = question.text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 19.sp,
                        letterSpacing = (-0.2).sp
                    ),
                    textAlign = TextAlign.Center,
                    color = Color(0xFF2D2D2D),
                    maxLines = 6,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // "Asked" indicator
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFF666666).copy(alpha = 0.1f),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = Color(0xFF666666),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "Asked",
                        color = Color(0xFF666666),
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }

    if (showRestoreDialog) {
        AlertDialog(
            onDismissRequest = { showRestoreDialog = false },
            icon = {
                Icon(
                    imageVector = Icons.Filled.Refresh,
                    contentDescription = null,
                    tint = Color(0xFF4CAF50)
                )
            },
            title = { Text("Restore Question?", fontWeight = FontWeight.Bold) },
            text = { Text("This question will be added back to the question pool and can appear again.") },
            confirmButton = {
                Button(
                    onClick = {
                        onRestore()
                        showRestoreDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                ) { Text("Restore") }
            },
            dismissButton = {
                TextButton(onClick = { showRestoreDialog = false }) { Text("Cancel") }
            }
        )
    }
}

// ═══════════════════════════════════════════════════════════════════════════
// DISLIKED SCREEN
// ═══════════════════════════════════════════════════════════════════════════

@Composable
fun DislikedQuestionsScreen(
    viewModel: QuestionViewModel,
    currentTheme: CategoryTheme,
    onNavigateBack: () -> Unit
) {
    val dislikedQuestionsSet by viewModel.dislikedQuestions.collectAsState()

    val dislikedQuestionsList = remember(dislikedQuestionsSet) {
        viewModel.getDislikedQuestionsList()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = Brush.verticalGradient(currentTheme.gradient))
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 16.dp)
                    .padding(top = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    onClick = onNavigateBack,
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.25f),
                    modifier = Modifier.size(48.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Disliked Questions",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "${dislikedQuestionsList.size} ${if (dislikedQuestionsList.size == 1) "question" else "questions"} hidden",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            // Content
            if (dislikedQuestionsList.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.15f),
                            modifier = Modifier.size(100.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.ThumbDown,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.5f),
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }
                        Text(
                            text = "No disliked questions",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Questions you dislike will be hidden\nand won't appear in your cards",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Normal,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center,
                            lineHeight = 22.sp
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(items = dislikedQuestionsList, key = { it.id }) { question ->
                        var visible by remember { mutableStateOf(true) }

                        AnimatedVisibility(
                            visible = visible,
                            enter = fadeIn(tween(300)) + scaleIn(
                                initialScale = 0.85f,
                                animationSpec = spring(
                                    dampingRatio = Spring.DampingRatioMediumBouncy,
                                    stiffness = Spring.StiffnessMedium
                                )
                            ),
                            exit = fadeOut(tween(250)) + scaleOut(
                                targetScale = 0.75f,
                                animationSpec = tween(250, easing = FastOutLinearInEasing)
                            ) + shrinkVertically(
                                animationSpec = tween(300, delayMillis = 200)
                            )
                        ) {
                            DislikedQuestionCard(
                                question = question,
                                onRestore = {
                                    visible = false
                                    viewModel.unmarkAsDisliked(question.id)
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DislikedQuestionCard(
    question: Question,
    onRestore: () -> Unit
) {
    val theme = getCategoryTheme(question.category)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.75f),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(brush = Brush.verticalGradient(theme.cardGradient))
        ) {
            // Category badge
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = theme.accentColor.copy(alpha = 0.12f),
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(12.dp)
            ) {
                Text(
                    text = question.category,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    color = theme.accentColor,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 10.sp,
                    letterSpacing = 0.3.sp
                )
            }

            // Restore button
            Surface(
                onClick = onRestore,
                shape = CircleShape,
                color = Color(0xFF4CAF50).copy(alpha = 0.15f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = Icons.Filled.ThumbUp,
                        contentDescription = "Restore",
                        tint = Color(0xFF4CAF50),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Question text
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
                    .padding(top = 52.dp, bottom = 48.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = question.text,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                        lineHeight = 19.sp,
                        letterSpacing = (-0.2).sp
                    ),
                    textAlign = TextAlign.Center,
                    color = Color(0xFF2D2D2D).copy(alpha = 0.5f),
                    maxLines = 6,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // "Disliked" indicator
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = Color(0xFFFF6B6B).copy(alpha = 0.1f),
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ThumbDown,
                        contentDescription = null,
                        tint = Color(0xFFFF6B6B),
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "Disliked",
                        color = Color(0xFFFF6B6B),
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}