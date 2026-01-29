package com.example.ht2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ht2.ui.LikedQuestionsScreen
import com.example.ht2.ui.QuestionScreen
import com.example.ht2.ui.theme.HT2Theme
import com.example.ht2.viewmodel.QuestionViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HT2Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    HT2App()
                }
            }
        }
    }
}

@Composable
fun HT2App() {
    var currentScreen by remember { mutableStateOf("questions") }
    val viewModel: QuestionViewModel = viewModel()
    val isInitialized by viewModel.isInitialized.collectAsState()

    if (!isInitialized) {
        // Show loading screen while data is being loaded
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
                ),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(
                color = Color.White,
                modifier = Modifier.size(48.dp)
            )
        }
    } else {
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = {
                if (targetState == "liked") {
                    slideInHorizontally { it } togetherWith slideOutHorizontally { -it }
                } else {
                    slideInHorizontally { -it } togetherWith slideOutHorizontally { it }
                }
            },
            label = "screen transition"
        ) { screen ->
            when (screen) {
                "questions" -> QuestionScreen(
                    viewModel = viewModel,
                    onNavigateToLiked = { currentScreen = "liked" }
                )
                "liked" -> LikedQuestionsScreen(
                    viewModel = viewModel,
                    onNavigateBack = { currentScreen = "questions" }
                )
            }
        }
    }
}