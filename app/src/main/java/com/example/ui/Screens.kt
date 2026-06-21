package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.viewmodel.GameViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChameleonGameApp(viewModel: GameViewModel) {
    val stage by viewModel.gameStage.collectAsState()
    val mode by viewModel.gameMode.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "🦎 CHAMELEON",
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.5.sp,
                            fontFamily = FontFamily.SansSerif,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                actions = {
                    if (stage != GameStage.WELCOME) {
                        IconButton(
                            onClick = { viewModel.returnToMainMenu() },
                            modifier = Modifier.testTag("home_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Exit Game",
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            AnimatedContent(
                targetState = stage,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "stage_transition"
            ) { targetStage ->
                when (targetStage) {
                    GameStage.WELCOME -> WelcomeScreen(viewModel)
                    GameStage.SETUP -> GameSetupScreen(viewModel)
                    GameStage.ROLE_REVEAL -> RoleRevealScreen(viewModel)
                    GameStage.CLUE_ROUND -> ClueRoundScreen(viewModel)
                    GameStage.VOTING -> VotingScreen(viewModel)
                    GameStage.REVELATION -> RevelationScreen(viewModel)
                    GameStage.RECAP -> RecapScreen(viewModel)
                }
            }
        }
    }
}

// ==========================================
// 1. WELCOME SCREEN
// ==========================================
@Composable
fun WelcomeScreen(viewModel: GameViewModel) {
    var showRules by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        item {
            Spacer(modifier = Modifier.height(16.dp))
            // Majestic Glowing Mascot
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .clip(RoundedCornerShape(40.dp))
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                MaterialTheme.colorScheme.primary,
                                MaterialTheme.colorScheme.secondary
                            )
                        )
                    )
                    .padding(4.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(36.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🦎", fontSize = 64.sp)
                        Text(
                            text = "CAMOUFLAGE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }
        }

        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "CHAMELEON",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = "The ultimate mobile hide-and-seek word puzzle game.",
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )
            }
        }

        // Mode card buttons
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.setGameMode(GameMode.SOLO)
                        viewModel.navigateToSetup()
                    }
                    .testTag("mode_solo"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = "🤖", fontSize = 40.sp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "SOLO VS AI BOTS",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Play with 4 intelligent bots powered by Google Gemini AI! Perfect for offline/single-player. Bots guess, accuse, and vote clever clues.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        viewModel.setGameMode(GameMode.PASS_AND_PLAY)
                        viewModel.navigateToSetup()
                    }
                    .testTag("mode_pass"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f))
            ) {
                Row(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = "👥", fontSize = 40.sp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "PASS & PLAY (LOCAL)",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = "A party pack in your pocket! Pass the phone to distribute roles, then make your observations, vote, and catch the Chameleon together.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }

        // Rules Accordion
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showRules = !showRules },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(imageVector = Icons.Default.Info, contentDescription = "Rules", tint = MaterialTheme.colorScheme.primary)
                            Text(text = "Rules & How to Play", fontWeight = FontWeight.Bold)
                        }
                        Text(text = if (showRules) "▲" else "▼", color = MaterialTheme.colorScheme.primary)
                    }

                    if (showRules) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "🎭 THE PREMISE\n" +
                                    "Everyone is dealt a secret grid coordinate corresponding to a word on a 4x4 card (e.g. Row 2, Col 3 - \"Pizza\") EXCEPT one secret player who receives \"YOU ARE THE CHAMELEON\". The Chameleon does not know the secret word!\n\n" +
                                    "🗣️ THE CLUE ROUND\n" +
                                    "Starting with Player 1, everyone says EXACTLY ONE clue word related to the secret word. Normal players want to prove they know the word while keeping it subtle (so the Chameleon doesn't guess the word). The Chameleon wants to blend in by saying a word that sounds plausible!\n\n" +
                                    "🗳️ THE VOTING ROUND\n" +
                                    "Everyone discusses the clues and casts a vote for who they believe is the Chameleon.\n\n" +
                                    "🏆 CONQUER & ESCAPE\n" +
                                    "- If an innocent player is nominated, the Chameleon immediately escapes and WINS!\n" +
                                    "- If the Chameleon is nominated, they get one final chance to steal the victory: they can guess the secret word from the card words! If they guess right, the Chameleon wins! Otherwise, the other players win.",
                            fontSize = 13.sp,
                            lineHeight = 18.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

// ==========================================
// 2. GAME SETUP SCREEN
// ==========================================
@Composable
fun GameSetupScreen(viewModel: GameViewModel) {
    val mode by viewModel.gameMode.collectAsState()
    val setupNames by viewModel.setupTempNames.collectAsState()
    val isApiKeyActive by viewModel.isApiKeyActive.collectAsState()

    var newPlayerName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(CategoriesData.predefinedCategories[0]) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        item {
            Text(
                text = if (mode == GameMode.SOLO) "SOLO MODE SETUP" else "PASS & PLAY SETUP",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Configure names and select a grid taxonomy to begin.",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }

        // Players names input card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = if (mode == GameMode.SOLO) "Your Player Identity" else "Add Players (3 - 8)",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )

                    if (mode == GameMode.SOLO) {
                        OutlinedTextField(
                            value = setupNames.firstOrNull() ?: "You",
                            onValueChange = { viewModel.updateSetupName(0, it) },
                            label = { Text("Your Name") },
                            modifier = Modifier.fillMaxWidth().testTag("user_name_input"),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            singleLine = true
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(text = "🤖", fontSize = 16.sp)
                            Text(
                                text = "4 AI Bots (APEX, MYSTIC, ECLIPSE, CHRONO) will automatically join.",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )
                        }

                        // Gemini API Key Warning if not set
                        if (!isApiKeyActive) {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(imageVector = Icons.Default.Warning, contentDescription = "Warning", tint = MaterialTheme.colorScheme.error)
                                    Column {
                                        Text(
                                            text = "No Gemini API Key found!",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                        Text(
                                            text = "Configure a GEMINI_API_KEY inside the Secrets Panel for intelligent AI agents. The game will automatically load local fallback heuristics offline.",
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                                        )
                                    }
                                }
                            }
                        } else {
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(imageVector = Icons.Default.Check, contentDescription = "Active", tint = MaterialTheme.colorScheme.primary)
                                    Text(
                                        text = "Gemini AI Engine Online. Ready for semantic bot gameplay!",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.primary,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                }
                            }
                        }

                    } else {
                        // Pass & play add/remove names list
                        setupNames.forEachIndexed { index, name ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(imageVector = Icons.Default.Person, contentDescription = "Player", tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f))
                                OutlinedTextField(
                                    value = name,
                                    onValueChange = { viewModel.updateSetupName(index, it) },
                                    label = { Text("Player ${index + 1}") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true
                                )
                                if (setupNames.size > 3) {
                                    IconButton(onClick = { viewModel.removeSetupName(index) }) {
                                        Icon(imageVector = Icons.Default.Close, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        }

                        if (setupNames.size < 8) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = newPlayerName,
                                    onValueChange = { newPlayerName = it },
                                    label = { Text("New Player Name") },
                                    modifier = Modifier.weight(1f),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                    keyboardActions = KeyboardActions(onDone = {
                                        if (newPlayerName.isNotBlank()) {
                                            viewModel.addSetupName(newPlayerName)
                                            newPlayerName = ""
                                        }
                                    })
                                )
                                Button(
                                    onClick = {
                                        if (newPlayerName.isNotBlank()) {
                                            viewModel.addSetupName(newPlayerName)
                                            newPlayerName = ""
                                        }
                                    },
                                    modifier = Modifier.height(56.dp)

                                ) {
                                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add")
                                }
                            }
                        }
                    }
                }
            }
        }

        // Category Cards Selector
        item {
            Text(
                text = "Select Board Category Card",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
        }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(CategoriesData.predefinedCategories) { category ->
                    val isSelected = selectedCategory.name == category.name
                    Card(
                        modifier = Modifier
                            .width(180.dp)
                            .height(130.dp)
                            .clickable { selectedCategory = category },
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                        ),
                        border = BorderStroke(
                            width = if (isSelected) 2.dp else 1.dp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(12.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = when (category.name) {
                                    "Wild Animals" -> "🦁"
                                    "Delectable Foods" -> "🍕"
                                    "Global Destinations" -> "✈️"
                                    "Cinematic Masterpieces" -> "🎬"
                                    "Modern Gadgets" -> "💻"
                                    else -> "💼"
                                },
                                fontSize = 24.sp
                            )
                            Column {
                                Text(
                                    text = category.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = category.description,
                                    fontSize = 10.sp,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    lineHeight = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Start button
        item {
            Button(
                onClick = { viewModel.startGame(selectedCategory) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("start_game_button"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(text = "START CAMOUFLAGE GAME 🦎", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}

// ==========================================
// 3. ROLE REVEAL SCREEN
// ==========================================
@Composable
fun RoleRevealScreen(viewModel: GameViewModel) {
    val mode by viewModel.gameMode.collectAsState()
    val players by viewModel.players.collectAsState()

    if (mode == GameMode.SOLO) {
        SoloRoleRevealScreen(viewModel)
    } else {
        PassAndPlayRoleRevealScreen(viewModel)
    }
}

@Composable
fun SoloRoleRevealScreen(viewModel: GameViewModel) {
    val players by viewModel.players.collectAsState()
    val user = players.first { it.id == "user" }
    val secretWord by viewModel.secretWord.collectAsState()
    val secretCoord by viewModel.secretCoord.collectAsState()
    val category by viewModel.currentCategory.collectAsState()

    var isRevealed by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "ROLE CONFIRMATION", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(text = "Verify your status before Clues arise", fontSize = 18.sp, fontWeight = FontWeight.Black)
        }

        // Secret visual reveal card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clickable { isRevealed = !isRevealed }
                .testTag("card_reveal_tap"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isRevealed) {
                    if (user.role == PlayerRole.CHAMELEON) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                    else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                } else {
                    MaterialTheme.colorScheme.surface
                }
            ),
            border = BorderStroke(
                2.dp,
                if (isRevealed) {
                    if (user.role == PlayerRole.CHAMELEON) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                }
            )
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (!isRevealed) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(text = "🦎", fontSize = 72.sp)
                        Text(
                            text = "TAP HERE TO REVEAL ROLE",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Hide your screen from onlookers!",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (user.role == PlayerRole.CHAMELEON) {
                            Text(text = "🎭", fontSize = 64.sp)
                            Text(
                                text = "YOU ARE THE CHAMELEON",
                                fontWeight = FontWeight.Black,
                                fontSize = 24.sp,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "You do not know the Secret Word!\n" +
                                        "1. Listen to clues from standard players closely.\n" +
                                        "2. Look at the 16-word board grid.\n" +
                                        "3. Formulate a convincing bluff to blend in!",
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                                lineHeight = 18.sp
                            )
                        } else {
                            Text(text = "🔑", fontSize = 48.sp)
                            Text(
                                text = "YOU ARE A PLAYER",
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Theme is: ${category.name}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = secretWord.uppercase(),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 24.sp,
                                        color = Color(0xFF021E14)
                                    )
                                    Text(
                                        text = "Coordinate: Row ${secretCoord.first}, Col ${secretCoord.second}",
                                        fontSize = 12.sp,
                                        color = Color(0xFF021E14).copy(alpha = 0.8f)
                                    )
                                }
                            }
                            Text(
                                text = "Propose a subtle word clue on your turn. Do not make it too obvious, or the Chameleon will guess it!",
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                                modifier = Modifier.padding(horizontal = 12.dp)
                            )
                        }
                    }
                }
            }
        }

        Button(
            onClick = { viewModel.submitClueWord("system", "ready") }, // triggers clue session via VM state
            enabled = isRevealed,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("verify_role_ok")
        ) {
            Text(text = "I AM READY!", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun PassAndPlayRoleRevealScreen(viewModel: GameViewModel) {
    val players by viewModel.players.collectAsState()
    val revealIndex by viewModel.currentRevealPlayerIndex.collectAsState()
    val isShowingCard by viewModel.isShowingRoleCard.collectAsState()

    val secretWord by viewModel.secretWord.collectAsState()
    val secretCoord by viewModel.secretCoord.collectAsState()
    val category by viewModel.currentCategory.collectAsState()

    val currentPlayer = players.getOrNull(revealIndex) ?: return

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(text = "PASS & PLAY ROLE DISTRIBUTION", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(
                text = "Player ${revealIndex + 1} of ${players.size}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
            )
        }

        // Pass notice card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clickable { viewModel.toggleRoleCardShow(!isShowingCard) }
                .testTag("peeking_card"),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = if (isShowingCard) {
                    if (currentPlayer.role == PlayerRole.CHAMELEON) MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                    else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                } else {
                    MaterialTheme.colorScheme.surface
                }
            ),
            border = BorderStroke(
                2.dp,
                if (isShowingCard) {
                    if (currentPlayer.role == PlayerRole.CHAMELEON) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
                }
            )
        ) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                if (!isShowingCard) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Text(text = currentPlayer.avatarEmoji, fontSize = 72.sp)
                        Text(
                            text = "PASS PHONE TO: ${currentPlayer.name.uppercase()}",
                            fontWeight = FontWeight.Black,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Tap to unlock card. Make sure no one else is looking!",
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                        )
                    }
                } else {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (currentPlayer.role == PlayerRole.CHAMELEON) {
                            Text(text = "🎭", fontSize = 64.sp)
                            Text(
                                text = "YOU ARE THE CHAMELEON",
                                fontWeight = FontWeight.Black,
                                fontSize = 22.sp,
                                color = MaterialTheme.colorScheme.error,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "You don't know the secret word!\n" +
                                        "Observe other players' clues and find semantic camouflage.",
                                fontSize = 13.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                            )
                        } else {
                            Text(text = "🔑", fontSize = 48.sp)
                            Text(
                                text = "YOU KNOW THE SECRET",
                                fontWeight = FontWeight.Black,
                                fontSize = 20.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Theme is: ${category.name}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Card(
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = secretWord.uppercase(),
                                        fontWeight = FontWeight.Black,
                                        fontSize = 24.sp,
                                        color = Color(0xFF021E14)
                                    )
                                    Text(
                                        text = "Coordinate: Row ${secretCoord.first}, Col ${secretCoord.second}",
                                        fontSize = 12.sp,
                                        color = Color(0xFF021E14).copy(alpha = 0.8f)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        Button(
            onClick = { viewModel.proceedToNextPassPlayer() },
            enabled = isShowingCard,
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .testTag("confirm_and_hide")
        ) {
            Text(
                text = if (revealIndex < players.size - 1) "I HAVE READ IT (HIDE CARD)" else "ALL ROLES REVEALED!",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// ==========================================
// 4. CLUE ROUND SCREEN
// ==========================================
@Composable
fun ClueRoundScreen(viewModel: GameViewModel) {
    val category by viewModel.currentCategory.collectAsState()
    val secretWord by viewModel.secretWord.collectAsState()
    val secretCoord by viewModel.secretCoord.collectAsState()
    val players by viewModel.players.collectAsState()
    val turnId by viewModel.currentTurnPlayerId.collectAsState()
    val clueHistory by viewModel.clueHistory.collectAsState()
    val isBotLoading by viewModel.isLoading.collectAsState()
    val apiLogs by viewModel.apiLogs.collectAsState()

    val currentTurnPlayer = players.find { it.id == turnId }
    val user = players.find { it.id == "user" } ?: players.first()

    var inputClue by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Topic Header
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "BOARD CARD: ${category.name.uppercase()}",
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 1.sp
                )
                if (user.role == PlayerRole.PLAYER) {
                    Text(
                        text = "Secret Word: ${secretWord.uppercase()} (Row ${secretCoord.first}, Col ${secretCoord.second})",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "⚠️ You are the Chameleon! Look at clues and blend in.",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        // Row header for clues
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Clues Session", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            if (isBotLoading) {
                CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
            }
        }

        // Two primary components: 4x4 GRID ofWords (Top / Mid) and CLUES LIST (Bottom)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Left Column: The 4x4 Grid
            Card(
                modifier = Modifier
                    .weight(1.1f)
                    .fillMaxHeight(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "POSSIBLE WORDS (4x4)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                    )

                    // 4x4 Words visualizer
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(4),
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(category.words) { word ->
                            val isSecret = user.role == PlayerRole.PLAYER && word.lowercase() == secretWord.lowercase()
                            Box(
                                modifier = Modifier
                                    .aspectRatio(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSecret) MaterialTheme.colorScheme.primaryContainer
                                        else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                                    )
                                    .border(
                                        width = if (isSecret) 1.5.dp else 0.dp,
                                        color = if (isSecret) MaterialTheme.colorScheme.primary else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = word,
                                    fontSize = 9.sp,
                                    fontWeight = if (isSecret) FontWeight.Black else FontWeight.Medium,
                                    color = if (isSecret) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis,
                                    lineHeight = 11.sp
                                )
                            }
                        }
                    }
                }
            }

            // Right Column: Clue logs entered so far
            Card(
                modifier = Modifier
                    .weight(0.9f)
                    .fillMaxHeight(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text(
                        text = "SUBMITTED CLUES",
                        fontWeight = FontWeight.Bold,
                        fontSize = 10.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    if (clueHistory.isEmpty()) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Text(
                                "No clues. The tension builds!",
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                            )
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(clueHistory) { record ->
                                val executingPlayer = players.find { it.id == record.playerId }
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f))
                                        .padding(6.dp)
                                ) {
                                    Text(text = executingPlayer?.avatarEmoji ?: "👤", fontSize = 14.sp)
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = record.playerName,
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        Text(
                                            text = "\"${record.clue.uppercase()}\"",
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Black,
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // User Input / Status Panel at Bottom
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (currentTurnPlayer != null) {
                if (currentTurnPlayer.isHuman) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f))
                            .padding(12.dp)
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text(text = currentTurnPlayer.avatarEmoji, fontSize = 20.sp)
                                Text(
                                    text = "YOUR TURN TO SUBMIT CLUE (ONE WORD)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                OutlinedTextField(
                                    value = inputClue,
                                    onValueChange = { inputClue = it.replace(" ", "") }, // single-word validation
                                    placeholder = { Text("Clue word, e.g. Fang") },
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("clue_input_field"),
                                    singleLine = true,
                                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                                    keyboardActions = KeyboardActions(onSend = {
                                        if (inputClue.isNotBlank()) {
                                            viewModel.submitClueWord(currentTurnPlayer.id, inputClue)
                                            inputClue = ""
                                            focusManager.clearFocus()
                                        }
                                    })
                                )

                                Button(
                                    onClick = {
                                        if (inputClue.isNotBlank()) {
                                            viewModel.submitClueWord(currentTurnPlayer.id, inputClue)
                                            inputClue = ""
                                            focusManager.clearFocus()
                                        }
                                    },
                                    modifier = Modifier
                                        .height(56.dp)
                                        .testTag("submit_clue_button"),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text("SUBMIT")
                                }
                            }
                        }
                    }
                } else {
                    // Bot generating status
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                            .padding(16.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(text = currentTurnPlayer.avatarEmoji, fontSize = 28.sp)
                            Column {
                                Text(
                                    text = "${currentTurnPlayer.name} is writing their clue...",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "Analyzing the coordinate map...",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }
                }
            }

            // Real-time developer debug logs window (Very Premium Design touch!)
            if (viewModel.gameMode.value == GameMode.SOLO) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF030D0A)),
                    border = BorderStroke(1.dp, Color(0xFF103328))
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(
                            text = "📡 INTELLIGENCE FEED (GEMINI REAL-TIME ENGINE LOGS)",
                            fontWeight = FontWeight.Bold,
                            fontSize = 8.sp,
                            color = MaterialTheme.colorScheme.primary,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyColumn(modifier = Modifier.fillMaxWidth()) {
                            items(apiLogs.reversed()) { logLine ->
                                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(text = ">", color = MaterialTheme.colorScheme.primary, fontSize = 9.sp, fontFamily = FontFamily.Monospace)
                                    Text(
                                        text = logLine,
                                        color = Color(0xFFA7F3D0).copy(alpha = 0.9f),
                                        fontSize = 9.sp,
                                        fontFamily = FontFamily.Monospace,
                                        lineHeight = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

// ==========================================
// 5. VOTING SCREEN
// ==========================================
@Composable
fun VotingScreen(viewModel: GameViewModel) {
    val mode by viewModel.gameMode.collectAsState()
    val players by viewModel.players.collectAsState()
    val isBotLoading by viewModel.isLoading.collectAsState()
    val category by viewModel.currentCategory.collectAsState()
    val clueHistory by viewModel.clueHistory.collectAsState()

    var selectedPlayerId by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "🗳️ EXPOSE THE CHAMELEON!",
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Compare clue associations carefully. The Chameleon does not know the secret card term and had to bluff. Choose who you suspect!",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )
        }

        // Board information card recap
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(modifier = Modifier.padding(12.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = "Category: ${category.name}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text(text = "${clueHistory.size} Clues Rendered", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                }
            }
        }

        // List of players and their clues as selectable vote rows!
        items(players) { player ->
            val isSelected = selectedPlayerId == player.id
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectedPlayerId = player.id }
                    .testTag("vote_row_${player.name}"),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                    else MaterialTheme.colorScheme.surface
                ),
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = player.avatarEmoji, fontSize = 36.sp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (player.id == "user") "${player.name} (You)" else player.name,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(text = "CLUE:", fontSize = 10.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = player.clueWord.ifEmpty { "None" }.uppercase(),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Black,
                                color = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }

                    // Selection indicator
                    RadioButton(
                        selected = isSelected,
                        onClick = { selectedPlayerId = player.id }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            if (isBotLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        CircularProgressIndicator()
                        Text("Gathering bot testimonies...", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                    }
                }
            } else {
                Button(
                    onClick = {
                        val targetId = selectedPlayerId
                        if (targetId != null) {
                            if (mode == GameMode.SOLO) {
                                viewModel.submitUserVote(targetId)
                            } else {
                                // In Pass & play, the person nominating holds votes offline, then sets nomination.
                                viewModel.submitPassAndPlayNomination(targetId)
                            }
                        }
                    },
                    enabled = selectedPlayerId != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("cast_vote_button"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = if (mode == GameMode.SOLO) "SUBMIT VOTE & SEE RESULTS 🗳️" else "CONFIRM NOMINEE 🗳️",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

// ==========================================
// 6. REVELATION SCREEN (VOTE RESULTS)
// ==========================================
@Composable
fun RevelationScreen(viewModel: GameViewModel) {
    val mode by viewModel.gameMode.collectAsState()
    val players by viewModel.players.collectAsState()
    val nominee by viewModel.nominatedPlayer.collectAsState()
    val secretWord by viewModel.secretWord.collectAsState()
    val category by viewModel.currentCategory.collectAsState()
    val isBotLoading by viewModel.isLoading.collectAsState()

    val user = players.find { it.id == "user" } ?: players.first()

    var userChameleonGuess by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    val realChameleon = players.first { it.role == PlayerRole.CHAMELEON }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = "JUDGMENT REVEAL", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Text(text = "The Tally is Settled!", fontSize = 24.sp, fontWeight = FontWeight.Black)
        }

        nominee?.let { nominated ->
            val isChameleon = nominated.role == PlayerRole.CHAMELEON

            Card(
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isChameleon) MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                    else MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.2f)
                ),
                border = BorderStroke(
                    2.dp,
                    if (isChameleon) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(text = nominated.avatarEmoji, fontSize = 64.sp)
                    Text(
                        text = "${nominated.name.uppercase()} NOMINATED!",
                        fontWeight = FontWeight.Black,
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )

                    Divider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))

                    if (isChameleon) {
                        Text(
                            text = "🎯 BULLSEYE! YOU CAUGHT THE CHAMELEON!",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "${nominated.name} is indeed the secret Chameleon! However, they get one final chance to escape. They must guess the secret grid word correctly!",
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                        )
                    } else {
                        Text(
                            text = "❌ WRONG ACCUSATION!",
                            fontWeight = FontWeight.Black,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Oops! ${nominated.name} was an honest player. An innocent is banished, and the real secret Chameleon successfully blends in and escapes!",
                            fontSize = 13.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                        )
                    }
                }
            }

            // Actions mapping based on accusation
            if (isChameleon) {
                // If Chameleon is nominated
                if (nominated.id == "user") {
                    // User caught! User must enter guess from grid
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            Text(
                                text = "🦎 FINAL CHANCE: GUESS SECRET WORD",
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Select or type the word you think was the secret term:",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                            )

                            // Let user tap from grid or enter
                            OutlinedTextField(
                                value = userChameleonGuess,
                                onValueChange = { userChameleonGuess = it },
                                label = { Text("Enter secret word guess") },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("chameleon_guess_input"),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                                keyboardActions = KeyboardActions(onDone = {
                                    if (userChameleonGuess.isNotBlank()) {
                                        viewModel.submitChameleonGuess(userChameleonGuess)
                                        focusManager.clearFocus()
                                    }
                                })
                            )

                            Button(
                                onClick = {
                                    if (userChameleonGuess.isNotBlank()) {
                                        viewModel.submitChameleonGuess(userChameleonGuess)
                                    }
                                },
                                enabled = userChameleonGuess.isNotBlank(),
                                modifier = Modifier.fillMaxWidth().testTag("submit_guess_button")
                            ) {
                                Text("SUBMIT FINAL GUESS")
                            }
                        }
                    }
                } else if (!nominated.isHuman) {
                    // Bot Chameleon caught! Let Bot guess via AI
                    Button(
                        onClick = { viewModel.triggerBotChameleonGuess(nominated) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("trigger_bot_guess"),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Text("LET BOTS CALCULATE FINAL GUESS 🦾")
                    }
                } else {
                    // Pass & play caught: Let real human player select secret word guess
                    var selectedGuessWord by remember { mutableStateOf("") }
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "${nominated.name}, type your secret word guess:",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )

                        OutlinedTextField(
                            value = selectedGuessWord,
                            onValueChange = { selectedGuessWord = it },
                            label = { Text("Word Guess, eg. $secretWord") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("human_guess_field"),
                            singleLine = true
                        )

                        Button(
                            onClick = { viewModel.submitChameleonGuess(selectedGuessWord) },
                            enabled = selectedGuessWord.isNotBlank(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .testTag("human_guess_confirm")
                        ) {
                            Text("SUBMIT GUESS")
                        }
                    }
                }
            } else {
                // Innocent player nominated - Chameleon wins automatically
                Button(
                    onClick = { viewModel.revealWrongAccusationScore() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("revelation_wrong_ok"),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text("CONTINUE TO SCORECARD ➡️")
                }
            }
        } ?: run {
            Text("Resolve votes in Setup stages.")
        }
    }
}

// ==========================================
// 7. RECAP SCREEN (GAME OVER)
// ==========================================
@Composable
fun RecapScreen(viewModel: GameViewModel) {
    val winner by viewModel.gameWinner.collectAsState()
    val reason by viewModel.recapReason.collectAsState()
    val players by viewModel.players.collectAsState()
    val secretWord by viewModel.secretWord.collectAsState()
    val category by viewModel.currentCategory.collectAsState()

    val realChameleon = players.firstOrNull { it.role == PlayerRole.CHAMELEON }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            Text(
                text = "GAME OVER",
                fontSize = 14.sp,
                fontWeight = FontWeight.Black,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f),
                letterSpacing = 2.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (winner == "Chameleon") "🦎 CHAMELEON VICTORY!" else "🏆 PLAYERS WIN!",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (winner == "Chameleon") MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center
            )
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(24.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "ROUND SUMMARY",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        fontSize = 12.sp,
                        letterSpacing = 1.sp
                    )

                    // Secret Details
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(text = "Board Card Category", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = category.name, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                            Text(text = "Secret Word", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(text = secretWord.uppercase(), fontWeight = FontWeight.Black, fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                        }
                        realChameleon?.let {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text(text = "The Secret Chameleon", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(text = "${it.name} ${it.avatarEmoji}", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = MaterialTheme.colorScheme.error)
                            }
                        }
                    }

                    Divider(color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.1f))

                    // Explanation reason Text
                    Text(
                        text = reason,
                        fontSize = 14.sp,
                        lineHeight = 20.sp,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)
                    )
                }
            }
        }

        item {
            Text(
                text = "Player Status Checklist",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 14.sp,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Checklist of players and roles
        items(players) { player ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(text = player.avatarEmoji, fontSize = 28.sp)
                    Column(modifier = Modifier.weight(1f)) {
                        Text(text = player.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Clue: \"${player.clueWord.ifEmpty { "None" }.uppercase()}\"",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Role details indicator
                    val isCam = player.role == PlayerRole.CHAMELEON
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (isCam) MaterialTheme.colorScheme.errorContainer else MaterialTheme.colorScheme.primaryContainer
                        )
                    ) {
                        Text(
                            text = if (isCam) "CHAMELEON" else "SAFE PLAYER",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isCam) MaterialTheme.colorScheme.onErrorContainer else MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(10.dp))
            Button(
                onClick = { viewModel.returnToMainMenu() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .testTag("restart_recap_button"),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(text = "PLAY AGAIN 🔄", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }
        }
    }
}
