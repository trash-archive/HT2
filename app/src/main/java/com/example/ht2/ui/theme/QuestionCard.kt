package com.example.ht2.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.ThumbDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ht2.data.Question
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

// ─────────────────────────────────────────────────────────────────────────────
// Category themes
// ─────────────────────────────────────────────────────────────────────────────
data class CategoryTheme(
    val gradient: List<Color>,
    val cardGradient: List<Color>,
    val accentColor: Color
)

fun getCategoryTheme(category: String): CategoryTheme {
    return when (category) {
        "Memories" -> CategoryTheme(
            gradient = listOf(Color(0xFF8B7AB8), Color(0xFF7B5FAF), Color(0xFF6B4FA5)),
            cardGradient = listOf(Color(0xFFF5F3FF), Color(0xFFEDE9FF)),
            accentColor = Color(0xFF8B7AB8)
        )
        "Love" -> CategoryTheme(
            gradient = listOf(Color(0xFFE63946), Color(0xFFD62839), Color(0xFFC6182C)),
            cardGradient = listOf(Color(0xFFFFEBEE), Color(0xFFFFE0E3)),
            accentColor = Color(0xFFE63946)
        )
        "Sweet" -> CategoryTheme(
            gradient = listOf(Color(0xFFFF6B9D), Color(0xFFFF5A8E), Color(0xFFFF4A7F)),
            cardGradient = listOf(Color(0xFFFFF0F5), Color(0xFFFFE5EE)),
            accentColor = Color(0xFFFF6B9D)
        )
        "Future" -> CategoryTheme(
            gradient = listOf(Color(0xFF4A90E2), Color(0xFF3A7FD5), Color(0xFF2A6EC8)),
            cardGradient = listOf(Color(0xFFE8F4FF), Color(0xFFD9EDFF)),
            accentColor = Color(0xFF4A90E2)
        )
        "Deep" -> CategoryTheme(
            gradient = listOf(Color(0xFF2D5A7B), Color(0xFF1E4A6B), Color(0xFF0F3A5B)),
            cardGradient = listOf(Color(0xFFE0EAF3), Color(0xFFD1E1F0)),
            accentColor = Color(0xFF2D5A7B)
        )
        "Fun" -> CategoryTheme(
            gradient = listOf(Color(0xFFFFA726), Color(0xFFFB8C00), Color(0xFFF57C00)),
            cardGradient = listOf(Color(0xFFFFF8E1), Color(0xFFFFECB3)),
            accentColor = Color(0xFFFFA726)
        )
        "Adventure" -> CategoryTheme(
            gradient = listOf(Color(0xFF66BB6A), Color(0xFF4CAF50), Color(0xFF43A047)),
            cardGradient = listOf(Color(0xFFE8F5E9), Color(0xFFDCEDC8)),
            accentColor = Color(0xFF66BB6A)
        )
        "Us" -> CategoryTheme(
            gradient = listOf(Color(0xFFB85C5C), Color(0xFFA84848), Color(0xFF963D3D)),
            cardGradient = listOf(Color(0xFFFFFBF5), Color(0xFFFFF8F0)),
            accentColor = Color(0xFFB85C5C)
        )
        "Intimacy" -> CategoryTheme(
            gradient = listOf(Color(0xFFAB47BC), Color(0xFF9C27B0), Color(0xFF8E24AA)),
            cardGradient = listOf(Color(0xFFF3E5F5), Color(0xFFE1BEE7)),
            accentColor = Color(0xFFAB47BC)
        )
        "Growth" -> CategoryTheme(
            gradient = listOf(Color(0xFF26A69A), Color(0xFF00897B), Color(0xFF00796B)),
            cardGradient = listOf(Color(0xFFE0F2F1), Color(0xFFB2DFDB)),
            accentColor = Color(0xFF26A69A)
        )
        "Communication" -> CategoryTheme(
            gradient = listOf(Color(0xFF5C6BC0), Color(0xFF3F51B5), Color(0xFF3949AB)),
            cardGradient = listOf(Color(0xFFE8EAF6), Color(0xFFC5CAE9)),
            accentColor = Color(0xFF5C6BC0)
        )
        "Gratitude" -> CategoryTheme(
            gradient = listOf(Color(0xFFFFCA28), Color(0xFFFFC107), Color(0xFFFFB300)),
            cardGradient = listOf(Color(0xFFFFF9C4), Color(0xFFFFF59D)),
            accentColor = Color(0xFFFFCA28)
        )
        "Romantic" -> CategoryTheme(
            gradient = listOf(Color(0xFFEC407A), Color(0xFFE91E63), Color(0xFFD81B60)),
            cardGradient = listOf(Color(0xFFFCE4EC), Color(0xFFF8BBD0)),
            accentColor = Color(0xFFEC407A)
        )
        "Spicy" -> CategoryTheme(
            gradient = listOf(Color(0xFFEF5350), Color(0xFFE53935), Color(0xFFD32F2F)),
            cardGradient = listOf(Color(0xFFFFEBEE), Color(0xFFFFCDD2)),
            accentColor = Color(0xFFEF5350)
        )
        "My Questions" -> CategoryTheme(
            gradient = listOf(Color(0xFF7E57C2), Color(0xFF673AB7), Color(0xFF5E35B1)),
            cardGradient = listOf(Color(0xFFEDE7F6), Color(0xFFD1C4E9)),
            accentColor = Color(0xFF7E57C2)
        )
        else -> CategoryTheme(
            gradient = listOf(Color(0xFFB85C5C), Color(0xFFA84848), Color(0xFF963D3D)),
            cardGradient = listOf(Color(0xFFFFFBF5), Color(0xFFFFF8F0)),
            accentColor = Color(0xFFB85C5C)
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// QuestionCard
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun QuestionCard(
    question: Question,
    isLiked: Boolean,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onToggleLike: () -> Unit,
    onDislike: () -> Unit,
    onDelete: (() -> Unit)? = null,
    canSwipe: Boolean = true,
    modifier: Modifier = Modifier
) {
    val offsetX = remember { Animatable(0f) }
    val heartScale = remember { Animatable(1f) }
    var isDragging by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var showDislikeDialog by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val swipeThreshold = 150f
    val theme = getCategoryTheme(question.category)

    val currentCardGradient = when {
        offsetX.value > 50f  -> listOf(Color(0xFFF0F4FF), Color(0xFFE8EEFF))
        offsetX.value < -50f -> listOf(Color(0xFFFFF5F5), Color(0xFFFFEBEB))
        else -> theme.cardGradient
    }

    // Inactive state: both buttons share identical grey palette
    val inactiveIconColor = Color(0xFFB85C5C)
    val inactiveBgColor   = Color(0xFFB85C5C).copy(alpha = 0.10f)

    // Dislike button: animates to red when dialog is open, back to grey on cancel
    val dislikeIconColor by animateColorAsState(
        targetValue = if (showDislikeDialog) Color(0xFFFF6B6B) else inactiveIconColor,
        animationSpec = tween(200), label = "dislike icon"
    )
    val dislikeBgColor by animateColorAsState(
        targetValue = if (showDislikeDialog)
            Color(0xFFFF6B6B).copy(alpha = 0.13f) else inactiveBgColor,
        animationSpec = tween(200), label = "dislike bg"
    )

    Box(modifier = modifier) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.9f)
                .aspectRatio(0.65f)
                .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                .graphicsLayer {
                    rotationZ = offsetX.value / 50f
                    alpha = 1f - (offsetX.value.absoluteValue / 1200f)
                }
                .shadow(
                    elevation = if (isDragging) 24.dp else 12.dp,
                    shape = RoundedCornerShape(28.dp)
                )
                .pointerInput(question.id, canSwipe) {
                    if (canSwipe) {
                        detectHorizontalDragGestures(
                            onDragStart = { isDragging = true },
                            onDragEnd = {
                                isDragging = false
                                scope.launch {
                                    when {
                                        offsetX.value > swipeThreshold -> {
                                            offsetX.animateTo(1500f, tween(250))
                                            onSwipeRight()
                                        }
                                        offsetX.value < -swipeThreshold -> {
                                            offsetX.animateTo(-1500f, tween(250))
                                            onSwipeLeft()
                                        }
                                        else -> offsetX.animateTo(
                                            0f, spring(stiffness = Spring.StiffnessMedium)
                                        )
                                    }
                                }
                            },
                            onHorizontalDrag = { _, dragAmount ->
                                scope.launch { offsetX.snapTo(offsetX.value + dragAmount) }
                            }
                        )
                    }
                },
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(currentCardGradient))
            ) {
                // Question text
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 40.dp)
                        .padding(top = 70.dp, bottom = 100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = question.text,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 26.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 38.sp,
                            letterSpacing = (-0.2).sp
                        ),
                        color = Color(0xFF2D2D2D)
                    )
                }

                // Category badge
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(18.dp),
                    shape = RoundedCornerShape(10.dp),
                    color = theme.accentColor.copy(alpha = 0.12f)
                ) {
                    Text(
                        text = question.category,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 11.sp,
                        color = theme.accentColor,
                        letterSpacing = 0.4.sp
                    )
                }

                // Delete button (custom only)
                if (onDelete != null) {
                    IconButton(
                        onClick = { showDeleteDialog = true },
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(18.dp)
                            .size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete",
                            tint = Color(0xFFFF6B6B),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                // Bottom row ── Heart  ·  Dislike
                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(horizontal = 40.dp, vertical = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Heart button
                    Surface(
                        onClick = {
                            scope.launch {
                                heartScale.animateTo(1.3f, spring())
                                heartScale.animateTo(1f, spring())
                            }
                            onToggleLike()
                        },
                        shape = CircleShape,
                        color = if (isLiked)
                            Color(0xFFFF6B9D).copy(alpha = 0.13f)
                        else
                            inactiveBgColor,   // ← same as dislike inactive
                        modifier = Modifier
                            .size(56.dp)
                            .graphicsLayer { scaleX = heartScale.value; scaleY = heartScale.value }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = if (isLiked) Icons.Filled.Favorite
                                else Icons.Outlined.FavoriteBorder,
                                contentDescription = "Like",
                                tint = if (isLiked) Color(0xFFFF6B9D) else inactiveIconColor,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }

                    // Dislike button — mirrors heart's inactive look, goes red when active
                    Surface(
                        onClick = { showDislikeDialog = true },
                        shape = CircleShape,
                        color = dislikeBgColor,
                        modifier = Modifier.size(56.dp)
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = if (showDislikeDialog) Icons.Filled.ThumbDown
                                else Icons.Outlined.ThumbDown,
                                contentDescription = "Dislike",
                                tint = dislikeIconColor,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                }
            }
        }
    }

    if (showDeleteDialog && onDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Filled.Delete, null, tint = Color(0xFFFF6B6B)) },
            title = { Text("Delete Question?", fontWeight = FontWeight.Bold) },
            text = { Text("This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = { onDelete(); showDeleteDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B6B))
                ) { Text("Delete") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") }
            }
        )
    }

    if (showDislikeDialog) {
        AlertDialog(
            onDismissRequest = { showDislikeDialog = false },
            icon = { Icon(Icons.Filled.ThumbDown, null, tint = Color(0xFFFF6B6B)) },
            title = { Text("Hide Question?", fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "This question will be hidden and won't appear again. " +
                            "You can restore it from the Disliked tab."
                )
            },
            confirmButton = {
                Button(
                    onClick = { onDislike(); showDislikeDialog = false },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF6B6B))
                ) { Text("Hide") }
            },
            dismissButton = {
                TextButton(onClick = { showDislikeDialog = false }) { Text("Cancel") }
            }
        )
    }
}