package com.example.ht2.ui

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.BaselineShift
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ht2.data.Question
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
fun QuestionCard(
    question: Question,
    isLiked: Boolean,
    onSwipeLeft: () -> Unit,
    onSwipeRight: () -> Unit,
    onToggleLike: () -> Unit,
    onDelete: (() -> Unit)? = null, // Optional delete callback for custom questions
    canSwipe: Boolean = true, // Add parameter to control swiping
    modifier: Modifier = Modifier
) {
    val offsetX = remember { Animatable(0f) }
    var isDragging by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val heartScale = remember { Animatable(1f) }
    var showDeleteDialog by remember { mutableStateOf(false) }

    val swipeThreshold = 300f

    // Gradient colors based on swipe direction
    val cardGradient = when {
        offsetX.value > 50f -> listOf(
            Color(0xFFFFF5F5),
            Color(0xFFFFEBEB)
        )
        offsetX.value < -50f -> listOf(
            Color(0xFFF0F4FF),
            Color(0xFFE8EEFF)
        )
        else -> listOf(
            Color(0xFFFFFBF5),
            Color(0xFFFFF8F0)
        )
    }

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
                    shape = RoundedCornerShape(28.dp),
                    spotColor = Color.Black.copy(alpha = 0.25f)
                )
                .pointerInput(question.id, canSwipe) { // Add canSwipe to key
                    if (canSwipe) { // Only enable gestures if canSwipe is true
                        detectHorizontalDragGestures(
                            onDragStart = { isDragging = true },
                            onDragEnd = {
                                isDragging = false
                                scope.launch {
                                    when {
                                        offsetX.value < -swipeThreshold -> {
                                            offsetX.animateTo(-1500f, animationSpec = tween(250))
                                            onSwipeLeft()
                                        }
                                        offsetX.value > swipeThreshold -> {
                                            offsetX.animateTo(1500f, animationSpec = tween(250))
                                            onSwipeRight()
                                        }
                                        else -> {
                                            offsetX.animateTo(0f, animationSpec = tween(300))
                                        }
                                    }
                                }
                            },
                            onHorizontalDrag = { _, dragAmount ->
                                scope.launch {
                                    offsetX.snapTo(offsetX.value + dragAmount)
                                }
                            }
                        )
                    }
                },
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.Transparent
            )
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(cardGradient)
                    )
            ) {
                // Decorative corner elements
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .align(Alignment.TopStart)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFB85C5C).copy(alpha = 0.08f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .align(Alignment.BottomEnd)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFB85C5C).copy(alpha = 0.06f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Question text
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 40.dp, vertical = 60.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = question.text,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = 28.sp,
                            fontWeight = FontWeight.Medium,
                            lineHeight = 40.sp,
                            letterSpacing = (-0.5).sp
                        ),
                        textAlign = TextAlign.Center,
                        color = Color(0xFF2D2D2D)
                    )
                }

                // Top action buttons row
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Delete button (only for custom questions)
                    if (onDelete != null) {
                        Surface(
                            onClick = { showDeleteDialog = true },
                            shape = CircleShape,
                            color = Color(0xFFFF6B6B).copy(alpha = 0.15f),
                            modifier = Modifier.size(48.dp)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Delete",
                                    tint = Color(0xFFFF6B6B),
                                    modifier = Modifier.size(24.dp)
                                )
                            }
                        }
                    }

                    // Heart button
                    Surface(
                        onClick = {
                            scope.launch {
                                heartScale.animateTo(
                                    1.3f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessHigh
                                    )
                                )
                                heartScale.animateTo(
                                    1f,
                                    animationSpec = spring(
                                        dampingRatio = Spring.DampingRatioMediumBouncy,
                                        stiffness = Spring.StiffnessHigh
                                    )
                                )
                            }
                            onToggleLike()
                        },
                        shape = CircleShape,
                        color = if (isLiked) Color(0xFFFF6B9D).copy(alpha = 0.15f) else Color.White.copy(alpha = 0.5f),
                        modifier = Modifier
                            .size(48.dp)
                            .graphicsLayer {
                                scaleX = heartScale.value
                                scaleY = heartScale.value
                            }
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Icon(
                                imageVector = if (isLiked) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = if (isLiked) "Unlike" else "Like",
                                tint = if (isLiked) Color(0xFFFF6B9D) else Color(0xFF666666),
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                // Enhanced HT² logo at bottom
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomCenter)
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(
                                    Color(0xFF1A1A1A),
                                    Color(0xFF2D2D2D),
                                    Color(0xFF1A1A1A)
                                )
                            )
                        )
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    val logoText = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 3.sp,
                                color = Color.White
                            )
                        ) {
                            append("HT")
                        }
                        withStyle(
                            style = SpanStyle(
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.sp,
                                baselineShift = BaselineShift.Superscript,
                                color = Color(0xFFFFB3B3)
                            )
                        ) {
                            append("2")
                        }
                    }

                    Text(text = logoText)
                }

                // Category badge (top left)
                Box(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(20.dp)
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = if (question.isCustom)
                            Color(0xFF6B9DFF).copy(alpha = 0.12f)
                        else
                            Color(0xFFB85C5C).copy(alpha = 0.12f),
                        modifier = Modifier
                    ) {
                        Text(
                            text = question.category,
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                            color = if (question.isCustom) Color(0xFF6B9DFF) else Color(0xFFB85C5C),
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp,
                            letterSpacing = 0.5.sp
                        )
                    }
                }
            }
        }
    }

    // Delete confirmation dialog
    if (showDeleteDialog && onDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = {
                Text(
                    "Delete Question?",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            text = {
                Text(
                    "Are you sure you want to delete this question? This action cannot be undone.",
                    fontSize = 15.sp,
                    lineHeight = 22.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDelete()
                        showDeleteDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF6B6B)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Delete", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteDialog = false },
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Cancel", fontWeight = FontWeight.SemiBold)
                }
            },
            shape = RoundedCornerShape(24.dp)
        )
    }
}