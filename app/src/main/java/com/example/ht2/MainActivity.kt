package com.example.ht2

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ht2.ui.*
import com.example.ht2.ui.theme.HT2Theme
import com.example.ht2.viewmodel.QuestionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            HT2Theme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    HT2App()
                }
            }
        }
    }
}

sealed class Screen(val route: String, val title: String) {
    object Home     : Screen("home",     "Questions")
    object Liked    : Screen("liked",    "Liked")
    object History  : Screen("history",  "History")
    object Disliked : Screen("disliked", "Disliked")
}

data class NavigationItem(
    val screen: Screen,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val label: String
)

@Composable
fun HT2App() {
    var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
    var showAddDialog by remember { mutableStateOf(false) }
    val viewModel: QuestionViewModel = viewModel()
    val isInitialized by viewModel.isInitialized.collectAsState()
    val currentQuestion by viewModel.currentQuestion.collectAsState()

    // ── Flower easter egg state lives here so the overlay covers the nav bar ──
    var flowerDrawableId by remember { mutableStateOf<Int?>(null) }
    var flowerBlobSeed   by remember { mutableStateOf(0L) }
    var showFlower       by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val currentTheme = currentQuestion?.let { getCategoryTheme(it.category) }
        ?: getCategoryTheme("Us")

    val view = LocalView.current
    val window = (view.context as? ComponentActivity)?.window

    val navBarHeight  = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    val isGestureMode = navBarHeight < 40.dp
    val fabBottomPadding = if (isGestureMode) 35.dp else 76.dp

    LaunchedEffect(currentTheme) {
        delay(300)
        window?.let {
            it.statusBarColor    = currentTheme.gradient[0].toArgb()
            it.navigationBarColor = currentTheme.gradient[2].toArgb()
        }
    }

    val navigationItems = listOf(
        NavigationItem(Screen.Home,     Icons.Filled.Home,      Icons.Outlined.Home,           "Home"),
        NavigationItem(Screen.Liked,    Icons.Filled.Favorite,  Icons.Outlined.FavoriteBorder,  "Liked"),
        NavigationItem(Screen.History,  Icons.Filled.History,   Icons.Outlined.History,         "History"),
        NavigationItem(Screen.Disliked, Icons.Filled.ThumbDown, Icons.Outlined.ThumbDown,       "Disliked"),
    )

    if (!isInitialized) {
        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(Color(0xFFB85C5C), Color(0xFFA84848), Color(0xFF963D3D)))
            ),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(24.dp)) {
                Text("HT²", fontSize = 64.sp, color = Color.White, fontWeight = FontWeight.ExtraBold)
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(48.dp), strokeWidth = 4.dp)
            }
        }
    } else {
        // Outermost Box — flower overlay lives here, above the Scaffold
        Box(modifier = Modifier.fillMaxSize()) {
            Scaffold(
                bottomBar = {
                    Surface(color = currentTheme.gradient[2], shadowElevation = 16.dp, modifier = Modifier.fillMaxWidth()) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(72.dp)
                                    .padding(horizontal = 16.dp)
                                    .padding(bottom = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                navigationItems.take(2).forEach { item ->
                                    NavigationButton(item, currentScreen == item.screen, { currentScreen = item.screen }, Modifier.weight(1f))
                                }
                                Spacer(modifier = Modifier.weight(1f))
                                navigationItems.drop(2).forEach { item ->
                                    NavigationButton(item, currentScreen == item.screen, { currentScreen = item.screen }, Modifier.weight(1f))
                                }
                            }
                            Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars))
                        }
                    }
                }
            ) { paddingValues ->
                Box(modifier = Modifier.padding(paddingValues)) {
                    when (currentScreen) {
                        Screen.Home -> QuestionScreen(
                            viewModel = viewModel,
                            onNavigateToLiked = { currentScreen = Screen.Liked },
                            // Callback: receive flower data and show overlay from here
                            onFlowerTriggered = { drawableId, blobSeed ->
                                flowerDrawableId = drawableId
                                flowerBlobSeed   = blobSeed
                                showFlower       = true
                                scope.launch {
                                    delay(4500) // slightly longer than animation (600+3500+500)
                                    showFlower       = false
                                    flowerDrawableId = null
                                }
                            }
                        )
                        Screen.Liked    -> LikedQuestionsScreen(viewModel, currentTheme) { currentScreen = Screen.Home }
                        Screen.History  -> HistoryScreen(viewModel, currentTheme)        { currentScreen = Screen.Home }
                        Screen.Disliked -> DislikedQuestionsScreen(viewModel, currentTheme) { currentScreen = Screen.Home }
                    }
                }
            }

            // FAB
            FloatingActionButton(
                onClick = { showAddDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = fabBottomPadding)
                    .size(64.dp)
                    .zIndex(1f),
                containerColor = Color.White,
                contentColor = currentTheme.accentColor,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 12.dp, pressedElevation = 16.dp)
            ) {
                Icon(Icons.Filled.Add, "Add Question", modifier = Modifier.size(32.dp))
            }

            // ── Flower overlay — zIndex(2f) so it sits above FAB and nav bar ──
            // fillMaxSize() here reaches the edges of the outermost Box which
            // is NOT clipped by the Scaffold, so it covers the navigation bar.
            if (showFlower && flowerDrawableId != null) {
                Box(modifier = Modifier.fillMaxSize().zIndex(2f)) {
                    FullScreenFlowerEasterEgg(
                        drawableId = flowerDrawableId!!,
                        blobSeed   = flowerBlobSeed
                    )
                }
            }
        }

        if (showAddDialog) {
            AddQuestionDialog(
                currentTheme = currentTheme,
                onDismiss = { showAddDialog = false },
                onSave = { questionText ->
                    viewModel.addCustomQuestion(questionText)
                    viewModel.setCategory("My Questions")
                    showAddDialog = false
                }
            )
        }
    }
}

@Composable
fun NavigationButton(item: NavigationItem, isSelected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.fillMaxHeight()
    ) {
        IconButton(onClick = onClick, modifier = Modifier.size(48.dp)) {
            Icon(
                imageVector = if (isSelected) item.selectedIcon else item.unselectedIcon,
                contentDescription = item.label,
                tint = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f),
                modifier = Modifier.size(if (isSelected) 28.dp else 24.dp)
            )
        }
        Text(
            text = item.label, fontSize = 12.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) Color.White else Color.White.copy(alpha = 0.6f)
        )
    }
}