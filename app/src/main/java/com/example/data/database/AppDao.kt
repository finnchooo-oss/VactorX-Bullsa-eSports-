package com.example.data.database

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // Tournaments
    @Query("SELECT * FROM tournaments ORDER BY id DESC")
    fun getAllTournaments(): Flow<List<Tournament>>

    @Query("SELECT * FROM tournaments WHERE id = :id")
    suspend fun getTournamentById(id: Int): Tournament?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTournament(tournament: Tournament): Long

    @Update
    suspend fun updateTournament(tournament: Tournament)

    @Delete
    suspend fun deleteTournament(tournament: Tournament)

    // Registrations
    @Query("SELECT * FROM registrations ORDER BY timestamp DESC")
    fun getAllRegistrations(): Flow<List<Registration>>

    @Query("SELECT * FROM registrations WHERE freeFireUid = :uid ORDER BY timestamp DESC")
    fun getRegistrationsByPlayerUid(uid: String): Flow<List<Registration>>

    @Query("SELECT * FROM registrations WHERE tournamentId = :tournamentId ORDER BY timestamp DESC")
    fun getRegistrationsForTournament(tournamentId: Int): Flow<List<Registration>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRegistration(registration: Registration): Long

    @Update
    suspend fun updateRegistration(registration: Registration)

    @Delete
    suspend fun deleteRegistration(registration: Registration)

    // Leaderboard
    @Query("SELECT * FROM leaderboard ORDER BY points DESC, booyahs DESC")
    fun getLeaderboard(): Flow<List<LeaderboardUser>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLeaderboardUser(user: LeaderboardUser)

    @Query("DELETE FROM leaderboard")
    suspend fun clearLeaderboard()

    // Notifications
    @Query("SELECT * FROM notifications ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<Notification>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNotification(notification: Notification)

    @Query("DELETE FROM notifications WHERE id = :id")
    suspend fun deleteNotificationById(id: Int)
}
