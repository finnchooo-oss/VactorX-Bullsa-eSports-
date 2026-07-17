package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tournaments")
data class Tournament(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val map: String, // Bermuda, Purgatory, Kalahari, Alpine, Nexterra, Clash Squad, Custom Room
    val mode: String, // Solo, Duo, Squad, 1v1, 1v6, 4v4
    val dateTime: String,
    val entryFee: Int,
    val prizePool: Int,
    val totalSlots: Int,
    val remainingSlots: Int,
    val rules: String,
    val roomId: String = "",
    val roomPassword: String = "",
    val status: String = "Open", // "Open" or "Closed"
    val winnerName: String = "",
    val winnerUid: String = "",
    val announcement: String = ""
)

@Entity(tableName = "registrations")
data class Registration(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tournamentId: Int,
    val tournamentName: String,
    val playerName: String,
    val freeFireUid: String,
    val inGameName: String,
    val mobileNumber: String,
    val whatsAppNumber: String,
    val teamName: String,
    val captainName: String,
    val stateRegion: String,
    val paymentScreenshot: String = "",
    val status: String = "Pending", // "Pending", "Approved", "Rejected"
    val timestamp: Long = System.currentTimeMillis()
)

@Entity(tableName = "leaderboard")
data class LeaderboardUser(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val playerName: String,
    val inGameId: String,
    val points: Int,
    val matchesPlayed: Int,
    val kills: Int,
    val booyahs: Int
)

@Entity(tableName = "notifications")
data class Notification(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val message: String,
    val timestamp: Long = System.currentTimeMillis()
)
