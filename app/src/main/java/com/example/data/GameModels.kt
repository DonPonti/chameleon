package com.example.data

enum class GameMode {
    SOLO, PASS_AND_PLAY
}

enum class PlayerRole {
    PLAYER, CHAMELEON
}

enum class GameStage {
    WELCOME, SETUP, ROLE_REVEAL, CLUE_ROUND, VOTING, REVELATION, RECAP
}

data class Category(
    val name: String,
    val description: String,
    val words: List<String> // MUST have exactly 16 words
)

data class Player(
    val id: String,
    val name: String,
    val isHuman: Boolean,
    val role: PlayerRole = PlayerRole.PLAYER,
    val avatarEmoji: String = "👤",
    var clueWord: String = "",
    var votedForId: String? = null,
    var votesReceivedCount: Int = 0,
    var isNominated: Boolean = false,
    var score: Int = 0
)

data class ClueHistory(
    val playerId: String,
    val playerName: String,
    val clue: String,
    val isHuman: Boolean
)
