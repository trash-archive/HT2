package com.example.ht2.ui

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.example.ht2.R
import com.example.ht2.viewmodel.QuestionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// ─────────────────────────────────────────────────────────────────────────────
// makeBlobShape
// ─────────────────────────────────────────────────────────────────────────────
fun makeBlobShape(seed: Long) = GenericShape { size, _ ->
    val rng  = Random(seed)
    val cx   = size.width  / 2f
    val cy   = size.height / 2f
    val base = size.width  * 0.43f
    val harmonics = listOf(
        Triple(2, rng.nextFloat() * 0.02f + 0.02f, rng.nextFloat() * 6.28f),
        Triple(3, rng.nextFloat() * 0.02f + 0.02f, rng.nextFloat() * 6.28f),
        Triple(4, rng.nextFloat() * 0.01f + 0.01f, rng.nextFloat() * 6.28f)
    )
    fun radius(angleDeg: Float): Float {
        val a = Math.toRadians(angleDeg.toDouble()).toFloat()
        var r = base
        harmonics.forEach { (freq, amp, phase) -> r += base * amp * sin(freq * a + phase) }
        return r
    }
    val steps = 180
    moveTo(cx + radius(0f), cy)
    for (i in 1..steps) {
        val angle = i * 360f / steps
        val r = radius(angle)
        lineTo(cx + r * cos(Math.toRadians(angle.toDouble())).toFloat(),
            cy + r * sin(Math.toRadians(angle.toDouble())).toFloat())
    }
    close()
}

// ─────────────────────────────────────────────────────────────────────────────
// QuestionScreen
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun QuestionScreen(
    viewModel: QuestionViewModel,
    onNavigateToLiked: () -> Unit,
    onFlowerTriggered: (drawableId: Int, blobSeed: Long) -> Unit = { _, _ -> }
) {
    val currentQuestion  by viewModel.currentQuestion.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val likedQuestions   by viewModel.likedQuestions.collectAsState()
    val categories       by viewModel.categories.collectAsState()
    val endearment       by viewModel.endearment.collectAsState()
    val shownFlowers     by viewModel.shownFlowers.collectAsState()
    val couplePhotoUri   by viewModel.couplePhotoUri.collectAsState()

    var isCategoryMenuExpanded by remember { mutableStateOf(false) }
    var tapCount               by remember { mutableStateOf(0) }
    var showCoupleCard by remember { mutableStateOf(false) }

    val scope   = rememberCoroutineScope()
    val context = LocalContext.current

    val canSwipe     = viewModel.canSwipeInCurrentCategory()
    val currentTheme = currentQuestion?.let { getCategoryTheme(it.category) } ?: getCategoryTheme("Us")

    val photoPicker = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            try {
                context.contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
            } catch (_: Exception) {}
            viewModel.setCouplePhotoUri(it.toString())
        }
    }

    val targetGradient = currentTheme.gradient
    val animatedColors = targetGradient.map { remember { Animatable(it) } }
    LaunchedEffect(currentTheme) {
        animatedColors.forEachIndexed { index, anim ->
            launch { anim.animateTo(targetGradient[index], tween(600, easing = FastOutSlowInEasing)) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(animatedColors.map { it.value }))
            .pointerInput(Unit) {
                detectTapGestures(onTap = {
                    if (showCoupleCard) { showCoupleCard = false; return@detectTapGestures }
                    tapCount++
                    if (tapCount >= 3) {
                        val allFlowers = listOf(
                            R.drawable.flower1,  R.drawable.flower2,  R.drawable.flower3,
                            R.drawable.flower4,  R.drawable.flower5,  R.drawable.flower6,
                            R.drawable.flower7,  R.drawable.flower8,  R.drawable.flower9,
                            R.drawable.flower10, R.drawable.flower11, R.drawable.flower12,
                            R.drawable.flower13, R.drawable.flower14, R.drawable.flower15
                        )
                        val available = if (shownFlowers.size >= allFlowers.size) {
                            viewModel.resetShownFlowers(); allFlowers
                        } else allFlowers.filter { it !in shownFlowers }
                        val chosen = available.random()
                        viewModel.addShownFlower(chosen)
                        onFlowerTriggered(chosen, System.currentTimeMillis())
                        tapCount = 0
                    }
                    scope.launch { delay(2000); tapCount = 0 }
                })
            }
    ) {
        // ── Top bar: FIXED POSITION - stays in place ──────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .statusBarsPadding()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Left — category selector pill
            Box {
                Surface(
                    onClick = { isCategoryMenuExpanded = !isCategoryMenuExpanded },
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = 0.22f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = selectedCategory,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = Color.White,
                            letterSpacing = 0.2.sp
                        )
                        Icon(
                            Icons.Filled.ArrowDropDown,
                            contentDescription = "Category",
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier
                                .size(18.dp)
                                .rotate(if (isCategoryMenuExpanded) 180f else 0f)
                        )
                    }
                }
                DropdownMenu(
                    expanded = isCategoryMenuExpanded,
                    onDismissRequest = { isCategoryMenuExpanded = false },
                    offset = DpOffset(0.dp, 8.dp),
                    modifier = Modifier
                        .widthIn(min = 180.dp)
                        .heightIn(max = 400.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color.White),
                    tonalElevation = 0.dp,
                    shadowElevation = 0.dp
                ) {
                    categories.forEach { category ->
                        val catTheme = getCategoryTheme(category)
                        DropdownMenuItem(
                            text = {
                                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                                    Text(
                                        category,
                                        fontWeight = if (selectedCategory == category) FontWeight.Bold else FontWeight.Medium,
                                        fontSize = 15.sp,
                                        color = if (selectedCategory == category) catTheme.accentColor else Color(0xFF2D2D2D)
                                    )
                                    if (selectedCategory == category)
                                        Box(Modifier.size(8.dp).clip(CircleShape).background(catTheme.accentColor))
                                }
                            },
                            onClick = { viewModel.setCategory(category); isCategoryMenuExpanded = false },
                            modifier = Modifier.background(
                                if (selectedCategory == category) catTheme.accentColor.copy(alpha = 0.08f) else Color.Transparent
                            )
                        )
                    }
                }
            }

            // Right — profile avatar with indicator at BOTTOM
            Box(contentAlignment = Alignment.Center) {
                // Subtle glow ring when card is open
                androidx.compose.animation.AnimatedVisibility(
                    visible = showCoupleCard,
                    enter = fadeIn(tween(300)),
                    exit  = fadeOut(tween(300))
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .background(
                                Brush.radialGradient(listOf(
                                    Color.White.copy(alpha = 0.35f),
                                    Color.White.copy(alpha = 0f)
                                )),
                                CircleShape
                            )
                    )
                }

                // Avatar circle
                Surface(
                    onClick = { showCoupleCard = !showCoupleCard },
                    shape = CircleShape,
                    color = Color.Transparent,
                    modifier = Modifier
                        .size(52.dp)
                        .border(
                            width = 2.dp,
                            brush = Brush.sweepGradient(listOf(
                                Color.White.copy(alpha = if (showCoupleCard) 1f else 0.85f),
                                Color.White.copy(alpha = if (showCoupleCard) 0.6f else 0.35f),
                                Color.White.copy(alpha = if (showCoupleCard) 1f else 0.85f)
                            )),
                            shape = CircleShape
                        )
                ) {
                    if (couplePhotoUri.isNotEmpty()) {
                        AsyncImage(
                            model = couplePhotoUri,
                            contentDescription = "Couple photo",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize().clip(CircleShape)
                        )
                    } else {
                        // DEFAULT: Gray placeholder with couple icon
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Color.White.copy(alpha = 0.20f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Filled.AccountCircle,
                                contentDescription = "Add photo",
                                tint = Color.White.copy(alpha = 0.65f),
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }

                // Indicator dot at BOTTOM CENTER (not bottom-right)
                androidx.compose.animation.AnimatedVisibility(
                    visible = showCoupleCard,
                    enter = scaleIn(spring(Spring.DampingRatioMediumBouncy)),
                    exit  = scaleOut(tween(150)),
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .offset(y = 4.dp) // Slightly below the avatar
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(Color.White, CircleShape)
                            .border(1.5.dp, currentTheme.accentColor.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(modifier = Modifier.size(5.dp).background(currentTheme.accentColor, CircleShape))
                    }
                }
            }
        }

        // ── Card area ──────────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 110.dp, bottom = 90.dp),
            contentAlignment = Alignment.Center
        ) {
            AnimatedContent(
                targetState = showCoupleCard,
                transitionSpec = {
                    if (targetState) {
                        (fadeIn(tween(350)) + scaleIn(
                            initialScale = 0.88f,
                            animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow)
                        )).togetherWith(
                            fadeOut(animationSpec = tween(250)) +
                                    scaleOut(targetScale = 0.92f, animationSpec = tween<Float>(250))
                        )
                    } else {
                        (fadeIn(tween(350)) + scaleIn(
                            initialScale = 0.92f,
                            animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessMediumLow)
                        )).togetherWith(
                            fadeOut(animationSpec = tween(200)) +
                                    scaleOut(targetScale = 0.88f, animationSpec = tween<Float>(200))
                        )
                    }
                },
                label = "card switch"
            ) { coupleCardVisible ->
                if (coupleCardVisible) {
                    CoupleCard(
                        couplePhotoUri = couplePhotoUri,
                        endearment     = endearment,
                        currentTheme   = currentTheme,
                        onPickPhoto    = { photoPicker.launch("image/*") },
                        onSaveEndearment = { viewModel.setEndearment(it) },
                        onDismiss      = { showCoupleCard = false }
                    )
                } else {
                    currentQuestion?.let { question ->
                        AnimatedContent(
                            targetState = question,
                            transitionSpec = {
                                (fadeIn(tween(400)) + scaleIn(
                                    initialScale = 0.92f,
                                    animationSpec = spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)
                                )).togetherWith(
                                    fadeOut(animationSpec = tween(300)) +
                                            scaleOut(targetScale = 0.92f, animationSpec = tween(300))
                                )
                            },
                            label = "card animation"
                        ) { animatedQuestion ->
                            val isCurrentlyLiked = likedQuestions.contains(animatedQuestion.id)

                            QuestionCard(
                                question = animatedQuestion,
                                isLiked = isCurrentlyLiked,
                                onSwipeLeft  = { viewModel.markAsAsked(animatedQuestion.id); viewModel.getNextQuestion() },
                                onSwipeRight = { viewModel.getPreviousQuestion() },
                                onToggleLike = { viewModel.toggleLike(animatedQuestion.id) },
                                onDislike    = { viewModel.markAsDisliked(animatedQuestion.id); viewModel.getNextQuestion() },
                                onDelete     = if (animatedQuestion.isCustom) ({ viewModel.deleteCustomQuestion(animatedQuestion.id) }) else null,
                                canSwipe     = canSwipe,
                                modifier     = Modifier.padding(horizontal = 24.dp)
                            )
                        }
                    } ?: run {
                        // Empty state
                        Box(
                            modifier = Modifier.fillMaxSize().padding(32.dp),
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
                                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                        Icon(Icons.Filled.CheckCircle, null, tint = Color.White.copy(alpha = 0.5f), modifier = Modifier.size(48.dp))
                                    }
                                }
                                Text("All questions completed!", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = Color.White, textAlign = TextAlign.Center)
                                Text("Check the History tab to restore\nquestions you want to answer again", fontSize = 15.sp, color = Color.White.copy(alpha = 0.8f), textAlign = TextAlign.Center, lineHeight = 22.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// CoupleCard - REMOVED "tap to edit" text, IMPROVED default states
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun CoupleCard(
    couplePhotoUri: String,
    endearment: String,
    currentTheme: CategoryTheme,
    onPickPhoto: () -> Unit,
    onSaveEndearment: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var editingEndearment by remember { mutableStateOf(false) }
    var endearmentDraft   by remember(endearment) { mutableStateOf(endearment) }
    val maxChars = 20

    Card(
        modifier = Modifier
            .fillMaxWidth(0.9f)
            .aspectRatio(0.65f)
            .shadow(12.dp, RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {

            // ── Photo or DEFAULT placeholder ──────────────────────────────────
            if (couplePhotoUri.isNotEmpty()) {
                AsyncImage(
                    model = couplePhotoUri,
                    contentDescription = "Couple photo",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onPickPhoto() }
                )
            } else {
                // DEFAULT: Soft gradient with centered icon
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    currentTheme.gradient[0].copy(alpha = 0.4f),
                                    currentTheme.gradient[1].copy(alpha = 0.6f),
                                    currentTheme.gradient[2].copy(alpha = 0.8f)
                                )
                            )
                        )
                        .clickable { onPickPhoto() },
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = Color.White.copy(alpha = 0.15f),
                            modifier = Modifier.size(90.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Icon(
                                    Icons.Filled.AccountCircle,
                                    contentDescription = "Add photo",
                                    tint = Color.White.copy(alpha = 0.75f),
                                    modifier = Modifier.size(50.dp)
                                )
                            }
                        }
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                "Add Your Photo",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White.copy(alpha = 0.9f)
                            )
                            Text(
                                "Tap to choose from gallery",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Normal,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                }
            }

            // ── Bottom gradient scrim ──────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.45f)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            listOf(Color.Transparent, Color.Black.copy(alpha = 0.72f))
                        )
                    )
            )

            // ── Endearment panel at bottom ─────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 24.dp, vertical = 22.dp),
                contentAlignment = Alignment.Center
            ) {
                if (editingEndearment) {
                    // Inline edit field
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = endearmentDraft,
                            onValueChange = { if (it.length <= maxChars) endearmentDraft = it },
                            singleLine = true,
                            textStyle = LocalTextStyle.current.copy(
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center,
                                color = Color.White
                            ),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor   = Color.White.copy(alpha = 0.7f),
                                unfocusedBorderColor = Color.White.copy(alpha = 0.4f),
                                cursorColor          = Color.White,
                                focusedTextColor     = Color.White,
                                unfocusedTextColor   = Color.White
                            ),
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier.fillMaxWidth()
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedButton(
                                onClick = {
                                    endearmentDraft = endearment
                                    editingEndearment = false
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.5f))
                            ) { Text("Cancel", fontWeight = FontWeight.SemiBold) }
                            Button(
                                onClick = {
                                    if (endearmentDraft.trim().isNotEmpty()) {
                                        onSaveEndearment(endearmentDraft.trim())
                                    }
                                    editingEndearment = false
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color.White,
                                    contentColor = currentTheme.accentColor
                                )
                            ) { Text("Save", fontWeight = FontWeight.Bold) }
                        }
                    }
                } else {
                    // Display mode — REMOVED "tap to edit" text, just show endearment
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { editingEndearment = true }
                    ) {
                        Text(
                            text = endearment.ifEmpty { "HT²" },
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 0.5.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // ── Close button (top-right) ───────────────────────────────────────
            Surface(
                onClick = onDismiss,
                shape = CircleShape,
                color = Color.Black.copy(alpha = 0.30f),
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(14.dp)
                    .size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(Icons.Filled.Close, "Close", tint = Color.White, modifier = Modifier.size(16.dp))
                }
            }

            // ── Camera badge (top-left, only when photo exists) ────────────────
            if (couplePhotoUri.isNotEmpty()) {
                Surface(
                    onClick = onPickPhoto,
                    shape = CircleShape,
                    color = Color.Black.copy(alpha = 0.30f),
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(14.dp)
                        .size(34.dp)
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(Icons.Filled.CameraAlt, "Change photo", tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Flower overlay
// ─────────────────────────────────────────────────────────────────────────────
@Composable
fun FullScreenFlowerEasterEgg(drawableId: Int, blobSeed: Long) {
    Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.45f)))
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        AnimatedCenteredFlower(drawableId = drawableId, blobSeed = blobSeed)
    }
}

@Composable
fun AnimatedCenteredFlower(drawableId: Int, blobSeed: Long) {
    var visible by remember { mutableStateOf(false) }
    val alpha = remember { Animatable(0f) }
    val scale = remember { Animatable(0.5f) }
    val blobShape = remember(blobSeed) { makeBlobShape(blobSeed) }

    LaunchedEffect(Unit) {
        visible = true
        launch {
            alpha.animateTo(1f, tween(600, easing = FastOutSlowInEasing))
            delay(3500)
            alpha.animateTo(0f, tween(500, easing = FastOutSlowInEasing))
        }
        launch { scale.animateTo(1f, spring(Spring.DampingRatioMediumBouncy, Spring.StiffnessLow)) }
    }

    if (visible) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Box(modifier = Modifier.size(320.dp).alpha(alpha.value).scale(scale.value).background(Color(0xFFFFFAED), blobShape))
            Image(
                painter = painterResource(id = drawableId),
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxWidth(0.95f).aspectRatio(1f).alpha(alpha.value).scale(scale.value)
            )
        }
    }
}

@Composable
fun EndearmentDialog(currentEndearment: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var text by remember { mutableStateOf(currentEndearment) }
    val max = 20
    Dialog(onDismissRequest = onDismiss) {
        Card(modifier = Modifier.fillMaxWidth().padding(16.dp), shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Row(Modifier.fillMaxWidth(), Arrangement.SpaceBetween, Alignment.CenterVertically) {
                    Text("Edit Name", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = Color(0xFF2D2D2D))
                    IconButton(onClick = onDismiss, modifier = Modifier.size(32.dp)) {
                        Icon(Icons.Filled.Close, "Close", tint = Color(0xFF666666))
                    }
                }
                OutlinedTextField(
                    value = text, onValueChange = { if (it.length <= max) text = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("e.g., HT², Babe, Love", color = Color(0xFFAAAAAA)) },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFFB85C5C), unfocusedBorderColor = Color(0xFFDDDDDD), focusedTextColor = Color(0xFF2D2D2D), unfocusedTextColor = Color(0xFF2D2D2D), cursorColor = Color(0xFFB85C5C)),
                    shape = RoundedCornerShape(16.dp), singleLine = true
                )
                Text("${text.length} / $max", fontSize = 12.sp, color = if (text.length >= max) MaterialTheme.colorScheme.error else Color(0xFF999999), modifier = Modifier.align(Alignment.End))
                Row(Modifier.fillMaxWidth(), Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF666666))) {
                        Text("Cancel", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(vertical = 8.dp))
                    }
                    Button(onClick = { if (text.trim().isNotEmpty()) onSave(text.trim()) }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFB85C5C), contentColor = Color.White), enabled = text.trim().isNotEmpty()) {
                        Text("Save", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(vertical = 8.dp))
                    }
                }
            }
        }
    }
}