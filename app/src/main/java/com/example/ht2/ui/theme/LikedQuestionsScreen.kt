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
import androidx.compose.material.icons.filled.Favorite
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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ht2.data.Question
import com.example.ht2.viewmodel.QuestionViewModel

@Composable
fun LikedQuestionsScreen(
    viewModel: QuestionViewModel = viewModel(),
    currentTheme: CategoryTheme,
    onNavigateBack: () -> Unit
) {
    val likedQuestionsSet by viewModel.likedQuestions.collectAsState()

    val likedQuestionsList = remember(likedQuestionsSet) {
        viewModel.getLikedQuestionsList()
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
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
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
                        text = "Liked Questions",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        letterSpacing = (-0.5).sp
                    )
                    Text(
                        text = "${likedQuestionsList.size} ${if (likedQuestionsList.size == 1) "question" else "questions"}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            // Content
            if (likedQuestionsList.isEmpty()) {
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
                                    imageVector = Icons.Filled.Favorite,
                                    contentDescription = null,
                                    tint = Color.White.copy(alpha = 0.5f),
                                    modifier = Modifier.size(48.dp)
                                )
                            }
                        }
                        Text(
                            text = "No liked questions yet",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Tap the heart icon on any card\nto save your favorite questions here",
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
                    items(items = likedQuestionsList, key = { it.id }) { question ->
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
                            LikedQuestionCard(
                                question = question,
                                onUnlike = {
                                    visible = false
                                    viewModel.toggleLike(question.id)
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
fun LikedQuestionCard(
    question: Question,
    onUnlike: () -> Unit
) {
    // Use the same category theme as History/Disliked cards
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
                // Category-coloured gradient — identical pattern to HistoryQuestionCard
                .background(brush = Brush.verticalGradient(theme.cardGradient))
        ) {
            // Category badge (top-left)
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

            // Unlike / heart button (top-right) — uses accent colour from theme
            Surface(
                onClick = onUnlike,
                shape = CircleShape,
                color = theme.accentColor.copy(alpha = 0.15f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(12.dp)
                    .size(36.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = "Unlike",
                        tint = theme.accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            // Question text — centred between badge and bottom indicator
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

            // "Liked" indicator (bottom-centre) — accent tinted, matches category
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = theme.accentColor.copy(alpha = 0.10f),
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
                        imageVector = Icons.Filled.Favorite,
                        contentDescription = null,
                        tint = theme.accentColor,
                        modifier = Modifier.size(12.dp)
                    )
                    Text(
                        text = "Liked",
                        color = theme.accentColor,
                        fontWeight = FontWeight.Medium,
                        fontSize = 10.sp
                    )
                }
            }
        }
    }
}