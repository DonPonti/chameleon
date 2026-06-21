package com.example.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.api.GeminiClient
import com.example.data.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID

class GameViewModel(application: Application) : AndroidViewModel(application) {

    private val TAG = "GameViewModel"

    // --- State flows ---
    private val _gameMode = MutableStateFlow(GameMode.SOLO)
    val gameMode: StateFlow<GameMode> = _gameMode.asStateFlow()

    private val _gameStage = MutableStateFlow(GameStage.WELCOME)
    val gameStage: StateFlow<GameStage> = _gameStage.asStateFlow()

    private val _players = MutableStateFlow<List<Player>>(emptyList())
    val players: StateFlow<List<Player>> = _players.asStateFlow()

    private val _currentCategory = MutableStateFlow(CategoriesData.predefinedCategories[0])
    val currentCategory: StateFlow<Category> = _currentCategory.asStateFlow()

    private val _secretWord = MutableStateFlow("")
    val secretWord: StateFlow<String> = _secretWord.asStateFlow()

    private val _secretCoord = MutableStateFlow(Pair(1, 1))
    val secretCoord: StateFlow<Pair<Int, Int>> = _secretCoord.asStateFlow()

    // Pass and Play reveal sub-stages
    private val _currentRevealPlayerIndex = MutableStateFlow(0)
    val currentRevealPlayerIndex: StateFlow<Int> = _currentRevealPlayerIndex.asStateFlow()

    private val _isShowingRoleCard = MutableStateFlow(false)
    val isShowingRoleCard: StateFlow<Boolean> = _isShowingRoleCard.asStateFlow()

    // Clues and turns
    private val _clueHistory = MutableStateFlow<List<ClueHistory>>(emptyList())
    val clueHistory: StateFlow<List<ClueHistory>> = _clueHistory.asStateFlow()

    private val _currentTurnPlayerId = MutableStateFlow("")
    val currentTurnPlayerId: StateFlow<String> = _currentTurnPlayerId.asStateFlow()

    // Nominated Chameleon & Guesses
    private val _nominatedPlayer = MutableStateFlow<Player?>(null)
    val nominatedPlayer: StateFlow<Player?> = _nominatedPlayer.asStateFlow()

    private val _chameleonGuessWord = MutableStateFlow("")
    val chameleonGuessWord: StateFlow<String> = _chameleonGuessWord.asStateFlow()

    private val _gameWinner = MutableStateFlow<String>("") // "Players" or "Chameleon"
    val gameWinner: StateFlow<String> = _gameWinner.asStateFlow()

    private val _recapReason = MutableStateFlow("")
    val recapReason: StateFlow<String> = _recapReason.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Premium live telemetry: AI logs to reveal what bots are deciding behind the scenes!
    private val _apiLogs = MutableStateFlow<List<String>>(listOf("System Initialized."))
    val apiLogs: StateFlow<List<String>> = _apiLogs.asStateFlow()

    private val _isApiKeyActive = MutableStateFlow(GeminiClient.isApiKeyConfigured())
    val isApiKeyActive: StateFlow<Boolean> = _isApiKeyActive.asStateFlow()

    // Setup temporary names (Pass & Play)
    private val _setupTempNames = MutableStateFlow<List<String>>(listOf("You", "Alice", "Bob", "Charlie"))
    val setupTempNames: StateFlow<List<String>> = _setupTempNames.asStateFlow()

    init {
        checkApiKey()
    }

    fun checkApiKey() {
        _isApiKeyActive.value = GeminiClient.isApiKeyConfigured()
        Log.d(TAG, "API Key configured status: ${_isApiKeyActive.value}")
    }

    fun setGameMode(mode: GameMode) {
        _gameMode.value = mode
        if (mode == GameMode.SOLO) {
            _setupTempNames.value = listOf("You")
        } else {
            _setupTempNames.value = listOf("Player 1", "Player 2", "Player 3")
        }
    }

    fun navigateToSetup() {
        _gameStage.value = GameStage.SETUP
        checkApiKey()
    }

    fun returnToMainMenu() {
        _gameStage.value = GameStage.WELCOME
        _clueHistory.value = emptyList()
        _nominatedPlayer.value = null
        _chameleonGuessWord.value = ""
        _gameWinner.value = ""
        _recapReason.value = ""
    }

    // --- Pass & Play setup helpers ---
    fun addSetupName(name: String) {
        val currentList = _setupTempNames.value.toMutableList()
        if (currentList.size < 8) {
            currentList.add(name)
            _setupTempNames.value = currentList
        }
    }

    fun removeSetupName(index: Int) {
        val currentList = _setupTempNames.value.toMutableList()
        if (currentList.size > 3) {
            currentList.removeAt(index)
            _setupTempNames.value = currentList
        }
    }

    fun updateSetupName(index: Int, newName: String) {
        val currentList = _setupTempNames.value.toMutableList()
        if (index in currentList.indices) {
            currentList[index] = newName
            _setupTempNames.value = currentList
        }
    }

    // --- Game Initialization ---
    fun startGame(selectedCategory: Category) {
        _isLoading.value = true
        _currentCategory.value = selectedCategory
        _clueHistory.value = emptyList()
        _nominatedPlayer.value = null
        _chameleonGuessWord.value = ""
        _gameWinner.value = ""
        _recapReason.value = ""
        _apiLogs.value = listOf("Spinning up game engine for theme: ${selectedCategory.name}")

        // 1. Pick a random Secret Word from the 16 terms
        val randomIndex = (0..15).random()
        _secretWord.value = selectedCategory.words[randomIndex]

        // Calculate coordinate representation on a 4x4 grid (Row 1-4, Col 1-4)
        val row = (randomIndex / 4) + 1
        val col = (randomIndex % 4) + 1
        _secretCoord.value = Pair(row, col)

        Log.d(TAG, "Secret Word: ${_secretWord.value} at position Row $row, Col $col")

        // 2. Setup players based on Game Mode
        val playersList = mutableListOf<Player>()
        if (_gameMode.value == GameMode.SOLO) {
            // Solo Mode: User + 4 AI Bots
            val userName = _setupTempNames.value.firstOrNull()?.trim() ?: "You"
            val validUserName = if (userName.isEmpty()) "You" else userName
            playersList.add(Player(id = "user", name = validUserName, isHuman = true, avatarEmoji = "👤"))

            val bots = listOf(
                Pair("Bot APEX", "🤖"),
                Pair("Bot MYSTIC", "🔮"),
                Pair("Bot JUNGLE", "🌿"),
                Pair("Bot ECLIPSE", "🌑")
            )
            bots.forEachIndexed { i, botInfo ->
                playersList.add(Player(id = "bot_$i", name = botInfo.first, isHuman = false, avatarEmoji = botInfo.second))
            }
        } else {
            // Pass & Play Mode: All humans
            _setupTempNames.value.forEachIndexed { index, name ->
                val emoji = when (index) {
                    0 -> "🦉"
                    1 -> "🦊"
                    2 -> "🐸"
                    3 -> "🐯"
                    4 -> "🐨"
                    5 -> "🦉"
                    6 -> "🐲"
                    else -> "🦁"
                }
                playersList.add(
                    Player(
                        id = UUID.randomUUID().toString(),
                        name = name.trim().ifEmpty { "Player ${index + 1}" },
                        isHuman = true,
                        avatarEmoji = emoji
                    )
                )
            }
        }

        // 3. Assign roles (exactly 1 Chameleon, rest are Players)
        val chameleonIndex = playersList.indices.random()
        val formattedList = playersList.mapIndexed { index, player ->
            if (index == chameleonIndex) {
                player.copy(role = PlayerRole.CHAMELEON)
            } else {
                player.copy(role = PlayerRole.PLAYER)
            }
        }
        _players.value = formattedList

        val chameleonPlayer = formattedList.first { it.role == PlayerRole.CHAMELEON }
        Log.d(TAG, "The secret Chameleon is: ${chameleonPlayer.name} (${if (chameleonPlayer.isHuman) "Human" else "AI Bot"})")
        addLog("Camouflage assigned. One player is blending in.")

        // 4. Initiate Stages
        if (_gameMode.value == GameMode.SOLO) {
            _gameStage.value = GameStage.ROLE_REVEAL
        } else {
            _currentRevealPlayerIndex.value = 0
            _isShowingRoleCard.value = false
            _gameStage.value = GameStage.ROLE_REVEAL
        }
        _isLoading.value = false
    }

    // --- Pass & Play Role Reveal navigation ---
    fun toggleRoleCardShow(show: Boolean) {
        _isShowingRoleCard.value = show
    }

    fun proceedToNextPassPlayer() {
        val nextIdx = _currentRevealPlayerIndex.value + 1
        if (nextIdx < _players.value.size) {
            _currentRevealPlayerIndex.value = nextIdx
            _isShowingRoleCard.value = false
        } else {
            // Everyone has seen their role! Move to Clue Session
            startClueRound()
        }
    }

    // --- Clue Round Mechanics ---
    private fun startClueRound() {
        _gameStage.value = GameStage.CLUE_ROUND

        // In Solo play, decide starting turn and generate if bot
        if (_gameMode.value == GameMode.SOLO) {
            // Randomly order the players for entering clues.
            // For simplicity, we just go down the list order but start the turn chain.
            _currentTurnPlayerId.value = _players.value.first().id
            addLog("Clue phase started. Let's hear the descriptions.")
            runNextBotClueIfNeeded()
        } else {
            // Pass & play clues: Just prompt players down the list to enter of say out loud.
            // In pass-and-play, cards are enterable or say-able. Let's let them type their words
            // into the device so we have a gorgeous summary of all words on screen for voting!
            _currentTurnPlayerId.value = _players.value.first().id
        }
    }

    fun submitClueWord(playerId: String, word: String) {
        val trimmedWord = word.trim()
        if (trimmedWord.isEmpty()) return

        // Update player's clue details
        val updatedList = _players.value.map {
            if (it.id == playerId) it.copy(clueWord = trimmedWord) else it
        }
        _players.value = updatedList

        // Add to Clue History
        val actingPlayer = updatedList.first { it.id == playerId }
        val newClueRecord = ClueHistory(
            playerId = playerId,
            playerName = actingPlayer.name,
            clue = trimmedWord,
            isHuman = actingPlayer.isHuman
        )
        _clueHistory.value = _clueHistory.value + newClueRecord

        addLog("${actingPlayer.name} declared clue: \"$trimmedWord\"")

        // Progress turns
        progressClueTurn(playerId)
    }

    private fun progressClueTurn(currentPlayerId: String) {
        val currentList = _players.value
        val currentIndex = currentList.indexOfFirst { it.id == currentPlayerId }
        val nextIndex = currentIndex + 1

        if (nextIndex < currentList.size) {
            val nextPlayer = currentList[nextIndex]
            _currentTurnPlayerId.value = nextPlayer.id
            runNextBotClueIfNeeded()
        } else {
            // Clue round complete! Move directly to voting
            _gameStage.value = GameStage.VOTING
            addLog("All clues submitted. Accusations and analysis can begin.")
        }
    }

    private fun runNextBotClueIfNeeded() {
        val nextTurnId = _currentTurnPlayerId.value
        val player = _players.value.firstOrNull { it.id == nextTurnId } ?: return

        if (!player.isHuman) {
            generateBotClue(player)
        }
    }

    private fun generateBotClue(bot: Player) {
        viewModelScope.launch {
            _isLoading.value = true
            addLog("Analyzing database. ${bot.name} is writing a clue...")

            val generatedClue = generateAIClue(bot)
            submitClueWord(bot.id, generatedClue)

            _isLoading.value = false
        }
    }

    private suspend fun generateAIClue(bot: Player): String {
        val isChameleon = bot.role == PlayerRole.CHAMELEON
        val theme = _currentCategory.value.name
        val allWords = _currentCategory.value.words.joinToString(", ")
        val previousCluesText = _clueHistory.value.joinToString("; ") { "${it.playerName}: \"${it.clue}\"" }

        if (_isApiKeyActive.value) {
            try {
                if (!isChameleon) {
                    val systemInstruction = """
                        You are a highly intelligent and witty bot playing a social deduction party game 'The Chameleon' as a normal player.
                        Your name is ${bot.name}.
                        You know the secret word inside the 16 words.
                        Your goal is to give a single descriptive clue word that proves you know the secret word to your fellow players, but is subtle enough that the secret Chameleon player cannot deduce what the secret word is.
                        Rules:
                        1. Your response MUST be exactly a SINGLE English word. No punctuation, no spaces, no lower/upper casing text formatting, no sentences. E.g. "Claw".
                        2. Do not copy exactly another player's clue if you can avoid it, but remain highly thematic.
                    """.trimIndent()

                    val prompt = """
                        Theme Category: $theme
                        The 16 possible words are: $allWords
                        The SECRET WORD IS: "${_secretWord.value}"
                        Already given clues by other players: [$previousCluesText]
                        Write your single clue word:
                    """.trimIndent()

                    val res = GeminiClient.generateContent(systemInstruction, prompt, temperature = 0.6)
                    val parsed = res.split(" ", "\n", "\t").firstOrNull { it.isNotBlank() } ?: ""
                    val cleaned = parsed.replace(Regex("[^a-zA-Z]"), "")
                    if (cleaned.isNotEmpty() && cleaned.length > 1) {
                        return cleaned
                    }
                } else {
                    // Bot holds Chameleon status!
                    val systemInstruction = """
                        You are a highly intelligent and clever bot playing the social deduction board game 'The Chameleon' as the Secret Chameleon!
                        Your name is ${bot.name}.
                        YOU DO NOT KNOW THE SECRET WORD! You only see the grid of 16 potential words.
                        Your goal is to say a single clue word to BLEND IN. Try to look at the clues already given by other players, and deduce which of the 16 words might be the secret word, then write a clue that fits that deduced word (or fits several words) so players don't suspect you.
                        Rules:
                        1. Your response MUST be exactly a SINGLE English word. No explanations. E.g., "Water".
                        2. Avoid complete nonsense words. Try to align with the category and previous clues.
                    """.trimIndent()

                    val prompt = """
                        Theme Category: $theme
                        The 16 board words are: $allWords
                        Clues already given by normal players: [$previousCluesText]
                        Generate your single blending clue word:
                    """.trimIndent()

                    val res = GeminiClient.generateContent(systemInstruction, prompt, temperature = 0.8)
                    val parsed = res.split(" ", "\n", "\t").firstOrNull { it.isNotBlank() } ?: ""
                    val cleaned = parsed.replace(Regex("[^a-zA-Z]"), "")
                    if (cleaned.isNotEmpty() && cleaned.length > 1) {
                        addLog("DEBUG: ${bot.name} (Chameleon) used blending deduction.")
                        return cleaned
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Gemini client failed to generate clue for ${bot.name}. Falling back locally.", e)
            }
        }

        // Safe robust offline/error local fallback
        return if (!isChameleon) {
            CategoriesData.getLocalFallbackClue(_secretWord.value, false)
        } else {
            CategoriesData.getChameleonFallbackClue(_clueHistory.value.map { it.clue })
        }
    }


    // --- Voting Round ---
    fun submitUserVote(votedPlayerId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            addLog("Compiling player votes...")

            // 1. Log user's vote
            val updatedList = _players.value.map {
                if (it.id == "user") it.copy(votedForId = votedPlayerId) else it
            }
            _players.value = updatedList

            // 2. Generate bot votes sequentially or in parallel
            // For safety and premium immersion, let's trigger Gemini for bots to analyze clues and cast their vote!
            val finalVoteList = updatedList.toMutableList()

            for (i in finalVoteList.indices) {
                val player = finalVoteList[i]
                if (!player.isHuman) {
                    addLog("${player.name} is assessing suspicious patterns...")
                    val botVoteTargetId = runBotVoteDecision(player, finalVoteList)
                    finalVoteList[i] = player.copy(votedForId = botVoteTargetId)
                }
            }

            // 3. Tally all votes
            // Clear prior counts
            val tallies = finalVoteList.map { it.copy(votesReceivedCount = 0) }.toMutableList()
            for (player in finalVoteList) {
                val voteTargetId = player.votedForId
                if (voteTargetId != null) {
                    val idx = tallies.indexOfFirst { it.id == voteTargetId }
                    if (idx != -1) {
                        tallies[idx] = tallies[idx].copy(votesReceivedCount = tallies[idx].votesReceivedCount + 1)
                    }
                }
            }

            // Find Nominee (the player with the most votes)
            var highestVotes = -1
            var nominated: Player? = null
            val tiedPlayers = mutableListOf<Player>()

            for (p in tallies) {
                if (p.votesReceivedCount > highestVotes) {
                    highestVotes = p.votesReceivedCount
                    nominated = p
                    tiedPlayers.clear()
                    tiedPlayers.add(p)
                } else if (p.votesReceivedCount == highestVotes) {
                    tiedPlayers.add(p)
                }
            }

            // Tie-break resolution
            if (tiedPlayers.size > 1) {
                // If the real Chameleon is in the tie, suspect them (makes the AI feels smarter). Otherwise pick a random tied nominee.
                val secretChameleon = tallies.first { it.role == PlayerRole.CHAMELEON }
                nominated = if (tiedPlayers.any { it.id == secretChameleon.id }) {
                    secretChameleon
                } else {
                    tiedPlayers.random()
                }
                addLog("Tie-break triggered! Consonants elect ${nominated.name}.")
            }

            // Annotate who is nominated
            val markedNomineeList = tallies.map {
                if (it.id == nominated?.id) it.copy(isNominated = true) else it
            }

            _players.value = markedNomineeList
            _nominatedPlayer.value = markedNomineeList.firstOrNull { it.id == nominated?.id }

            addLog("${_nominatedPlayer.value?.name} received major doubts and is nominated.")

            // Determine what happens next
            _gameStage.value = GameStage.REVELATION
            _isLoading.value = false
        }
    }

    private suspend fun runBotVoteDecision(bot: Player, allPlayers: List<Player>): String {
        val theme = _currentCategory.value.name
        val cluesText = allWordsAndClues(allPlayers)
        val isChameleon = bot.role == PlayerRole.CHAMELEON

        if (_isApiKeyActive.value) {
            try {
                if (!isChameleon) {
                    val systemInstruction = """
                        You are ${bot.name} playing 'The Chameleon' social deduction game.
                        The Theme is: "$theme".
                        The secret word known to all normal players is "${_secretWord.value}".
                        The secret Chameleon player DOES NOT know the secret word, so they had to guess or write something unrelated, overly generic, or mimic someone else.
                        Look at the clues. Analyze which player is most likely the Chameleon.
                        Write a 1-sentence reasoning, then on a new line write: "VOTE: <IdOfPlayer>" where <IdOfPlayer> MUST be one of the IDs listed (e.g. user, bot_0, bot_1).
                        Do NOT vote for yourself (${bot.id}).
                    """.trimIndent()

                    val prompt = """
                        Current Players list and their given clues:
                        $cluesText
                        Compare these and decide who has the most suspicious clue.
                        Your analysis and VOTE:
                    """.trimIndent()

                    val res = GeminiClient.generateContent(systemInstruction, prompt, temperature = 0.5)
                    addLog("${bot.name} reasoning: \"${res.split("\n").firstOrNull { it.isNotBlank() } ?: ""}\"")

                    val voteLine = res.split("\n", "\r").firstOrNull { it.trim().startsWith("VOTE:") }
                        ?: res.split("\n", "\r").lastOrNull { it.trim().contains("bot_") || it.trim().contains("user") }

                    if (voteLine != null) {
                        val targetId = voteLine.replace("VOTE:", "").replace("[", "").replace("]", "").trim()
                        val matched = allPlayers.firstOrNull { it.id == targetId && it.id != bot.id }
                        if (matched != null) return matched.id
                    }
                } else {
                    // Bot is Chameleon - wants to deflect blame to suspect who gave a poor/confusing clue!
                    val systemInstruction = """
                        You are the Secret Chameleon, playing as ${bot.name}.
                        You do not know the secret word.
                        Other players are voting. You want to accuse another player who gave a weird, generic, or confusing clue to shift focus from yourself.
                        Write a 1-sentence analytical accusation, then write on a new line: "VOTE: <IdOfPlayer>"
                        Never write your own ID (${bot.id})!
                    """.trimIndent()

                    val prompt = """
                        The category is "$theme".
                        Players and clues list:
                        $cluesText
                        Choose one player (not yourself) to suspect to save yourself.
                        Your analysis and VOTE:
                    """.trimIndent()

                    val res = GeminiClient.generateContent(systemInstruction, prompt, temperature = 0.7)
                    val firstLine = res.split("\n").firstOrNull { it.isNotBlank() } ?: ""
                    addLog("${bot.name} (Chameleon) accusation defense: \"$firstLine\"")

                    val targetId = res.split("\n").firstOrNull { it.startsWith("VOTE:") }
                        ?.replace("VOTE:", "")?.trim() ?: ""
                    val matched = allPlayers.firstOrNull { it.id == targetId && it.id != bot.id }
                    if (matched != null) return matched.id
                }
            } catch (e: Exception) {
                Log.e(TAG, "Gemini vote decision failed for ${bot.name}. Falling back to random.", e)
            }
        }

        // Fallback or random vote logic
        val candidates = allPlayers.filter { it.id != bot.id }
        // Chameleon prefers voting user. Normal bots prefer voting Chameleon (35% accuracy fallback)
        val secretChameleon = allPlayers.first { it.role == PlayerRole.CHAMELEON }
        val randomProbability = (1..100).random()
        return if (!isChameleon && randomProbability <= 35 && secretChameleon.id != bot.id) {
            secretChameleon.id
        } else {
            candidates.random().id
        }
    }

    private fun allWordsAndClues(playersList: List<Player>): String {
        return playersList.joinToString("\n") {
            "- Name: ${it.name}, ID: ${it.id}, Clue word: \"${it.clueWord}\""
        }
    }


    // --- Pass & Play Voting Logic ---
    fun submitPassAndPlayNomination(nominatedId: String) {
        val tallies = _players.value.map {
            it.copy(
                isNominated = it.id == nominatedId,
                votesReceivedCount = if (it.id == nominatedId) 1 else 0
            )
        }
        _players.value = tallies
        _nominatedPlayer.value = tallies.find { it.id == nominatedId }
        _gameStage.value = GameStage.REVELATION
    }


    // --- Nominee Guessing / Resolution Phase ---
    fun submitChameleonGuess(guessedWord: String) {
        _chameleonGuessWord.value = guessedWord
        val isCorrect = guessedWord.lowercase() == _secretWord.value.lowercase()

        if (isCorrect) {
            _gameWinner.value = "Chameleon"
            _recapReason.value = "The Chameleon was nominative but successfully guessed the Secret Word (\"${_secretWord.value}\")!"
        } else {
            _gameWinner.value = "Players"
            _recapReason.value = "The Chameleon was caught! They guessed \"$guessedWord\" which was incorrect. The Secret Word was indeed \"${_secretWord.value}\"!"
        }

        _gameStage.value = GameStage.RECAP
    }

    fun triggerBotChameleonGuess(botChameleon: Player) {
        viewModelScope.launch {
            _isLoading.value = true
            addLog("The AI Chameleon (${botChameleon.name}) is decoding the secret word from player clues...")

            val allWords = _currentCategory.value.words
            val clueSummary = _clueHistory.value.joinToString(", ") { "${it.playerName}: \"${it.clue}\"" }

            var finalGuess = allWords.random() // Default fallback

            if (_isApiKeyActive.value) {
                try {
                    val systemInstruction = """
                        You are the Secret Chameleon, playing as ${botChameleon.name}.
                        You got CAUGHT and voted out!
                        However, you can still WIN the entire game if you can guess the Secret Word from the 16 board words.
                        Look at the clues. Analyze which of the 16 words fits these clues perfectly.
                        Your response MUST be exactly a SINGLE word from the list of 16 potential secret words. Do not write any other sentences.
                    """.trimIndent()

                    val prompt = """
                        Theme coordinates: ${_currentCategory.value.name}
                        The 16 board words are: ${allWords.joinToString(", ")}
                        The clues other players submitted: [$clueSummary]
                        Write your SINGLE guess from the 16 board words:
                    """.trimIndent()

                    val res = GeminiClient.generateContent(systemInstruction, prompt, temperature = 0.4)
                    val cleaned = res.replace(Regex("[^a-zA-Z\\s]"), "").trim()
                    val match = allWords.firstOrNull { it.lowercase() == cleaned.lowercase() }
                    if (match != null) {
                        finalGuess = match
                    } else {
                        // fuzzy mapping
                        val fuzzy = allWords.firstOrNull { cleaned.lowercase().contains(it.lowercase()) }
                        if (fuzzy != null) finalGuess = fuzzy
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Chameleon bot guess failed. Fallback to random.", e)
                }
            }

            _chameleonGuessWord.value = finalGuess
            val isCorrect = finalGuess.lowercase() == _secretWord.value.lowercase()

            if (isCorrect) {
                _gameWinner.value = "Chameleon"
                _recapReason.value = "AI Chameleon (${botChameleon.name}) guessed \"$finalGuess\" correctly and stole victory!"
            } else {
                _gameWinner.value = "Players"
                _recapReason.value = "AI Chameleon (${botChameleon.name}) guessed \"$finalGuess\" which was FALSE. The Secret Word was \"${_secretWord.value}\"!"
            }

            _gameStage.value = GameStage.RECAP
            _isLoading.value = false
        }
    }

    fun revealWrongAccusationScore() {
        _gameWinner.value = "Chameleon"
        val realChameleon = _players.value.first { it.role == PlayerRole.CHAMELEON }
        _recapReason.value = "Innocent player nominated! The Secret Chameleon (${realChameleon.name}) successfully blended in and escaped. The Secret Word was \"${_secretWord.value}\"."
        _gameStage.value = GameStage.RECAP
    }


    // --- Logging & Helper functions ---
    private fun addLog(text: String) {
        val current = _apiLogs.value.toMutableList()
        current.add(text)
        _apiLogs.value = current
        Log.i("TelemetryLog", text)
    }
}
