package com.example.data.repository

import com.example.data.database.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AppRepository(private val appDao: AppDao) {

    val allTournaments: Flow<List<Tournament>> = appDao.getAllTournaments()
    val allRegistrations: Flow<List<Registration>> = appDao.getAllRegistrations()
    val leaderboard: Flow<List<LeaderboardUser>> = appDao.getLeaderboard()
    val allNotifications: Flow<List<Notification>> = appDao.getAllNotifications()

    fun getRegistrationsForPlayer(uid: String): Flow<List<Registration>> {
        return appDao.getRegistrationsByPlayerUid(uid)
    }

    fun getRegistrationsForTournament(tournamentId: Int): Flow<List<Registration>> {
        return appDao.getRegistrationsForTournament(tournamentId)
    }

    suspend fun getTournamentById(id: Int): Tournament? {
        return appDao.getTournamentById(id)
    }

    suspend fun insertTournament(tournament: Tournament): Long {
        return appDao.insertTournament(tournament)
    }

    suspend fun updateTournament(tournament: Tournament) {
        appDao.updateTournament(tournament)
    }

    suspend fun deleteTournament(tournament: Tournament) {
        appDao.deleteTournament(tournament)
    }

    suspend fun insertRegistration(registration: Registration): Long {
        // Decrease remaining slots when a user registers (or keep it and decrease upon admin approval!)
        // Let's decrease remaining slots when registered to avoid overbooking
        val tournament = appDao.getTournamentById(registration.tournamentId)
        if (tournament != null && tournament.remainingSlots > 0) {
            appDao.updateTournament(tournament.copy(remainingSlots = tournament.remainingSlots - 1))
        }
        return appDao.insertRegistration(registration)
    }

    suspend fun updateRegistration(registration: Registration) {
        appDao.updateRegistration(registration)
    }

    suspend fun deleteRegistration(registration: Registration) {
        // Increase remaining slots back if deleted/rejected
        val tournament = appDao.getTournamentById(registration.tournamentId)
        if (tournament != null && tournament.remainingSlots < tournament.totalSlots) {
            appDao.updateTournament(tournament.copy(remainingSlots = tournament.remainingSlots + 1))
        }
        appDao.deleteRegistration(registration)
    }

    suspend fun insertLeaderboardUser(user: LeaderboardUser) {
        appDao.insertLeaderboardUser(user)
    }

    suspend fun clearLeaderboard() {
        appDao.clearLeaderboard()
    }

    suspend fun insertNotification(notification: Notification) {
        appDao.insertNotification(notification)
    }

    suspend fun deleteNotificationById(id: Int) {
        appDao.deleteNotificationById(id)
    }

    // Prepopulate DB if empty
    suspend fun prepopulateIfNeeded() {
        val tournaments = allTournaments.first()
        if (tournaments.isEmpty()) {
            // Insert mock tournaments
            val mockTournaments = listOf(
                Tournament(
                    name = "VactorX Pro League - Season 1",
                    map = "Bermuda",
                    mode = "Squad",
                    dateTime = "July 24, 2026 - 06:00 PM",
                    entryFee = 100,
                    prizePool = 50000,
                    totalSlots = 48,
                    remainingSlots = 42,
                    rules = "1. No hacks or third-party tools allowed.\n2. Emulator players not permitted unless squad represents a custom bypass bracket.\n3. Team captains must report to lobby 15 minutes prior to launch.\n4. Screenshot proof required for all complaints.",
                    status = "Open"
                ),
                Tournament(
                    name = "Bullsai Solo Showdown",
                    map = "Kalahari",
                    mode = "Solo",
                    dateTime = "July 26, 2026 - 04:00 PM",
                    entryFee = 20,
                    prizePool = 10000,
                    totalSlots = 100,
                    remainingSlots = 87,
                    rules = "1. Solo matchmaking.\n2. Clean play only, teaming up will result in immediate lifetime ban.\n3. Exact lobby details will be published in dashboard.",
                    status = "Open"
                ),
                Tournament(
                    name = "Clash Squad Elite 4v4",
                    map = "Clash Squad",
                    mode = "4v4",
                    dateTime = "July 28, 2026 - 08:00 PM",
                    entryFee = 50,
                    prizePool = 15000,
                    totalSlots = 32,
                    remainingSlots = 16,
                    rules = "1. Classic 4v4 brackets.\n2. Best of 3 maps.\n3. Standard tournament weapons rules apply.",
                    status = "Open"
                ),
                Tournament(
                    name = "Purgatory Duo Clash",
                    map = "Purgatory",
                    mode = "Duo",
                    dateTime = "August 01, 2026 - 07:00 PM",
                    entryFee = 40,
                    prizePool = 20000,
                    totalSlots = 50,
                    remainingSlots = 38,
                    rules = "1. Duo survival match.\n2. Friendly fire rules disabled.\n3. Room credentials will show 10 mins before startup.",
                    status = "Open"
                ),
                Tournament(
                    name = "Alpine Survival Championship",
                    map = "Alpine",
                    mode = "Squad",
                    dateTime = "August 05, 2026 - 05:00 PM",
                    entryFee = 80,
                    prizePool = 30000,
                    totalSlots = 48,
                    remainingSlots = 45,
                    rules = "1. Mobile-only players.\n2. Standard point system applies.",
                    status = "Open"
                ),
                Tournament(
                    name = "Nexterra Neon Rush",
                    map = "Nexterra",
                    mode = "Solo",
                    dateTime = "August 10, 2026 - 03:00 PM",
                    entryFee = 0,
                    prizePool = 5000,
                    totalSlots = 100,
                    remainingSlots = 92,
                    rules = "1. Free to enter.\n2. Fun play and guild recruitment tournament.",
                    status = "Open"
                )
            )
            for (t in mockTournaments) {
                appDao.insertTournament(t)
            }

            // Insert leaderboard users
            val mockLeaderboard = listOf(
                LeaderboardUser(playerName = "VactorX_Boss", inGameId = "FF-9938827", points = 1450, matchesPlayed = 25, kills = 142, booyahs = 8),
                LeaderboardUser(playerName = "Bullsai_Pro", inGameId = "FF-1029348", points = 1320, matchesPlayed = 24, kills = 115, booyahs = 6),
                LeaderboardUser(playerName = "Viper_FF", inGameId = "FF-7729104", points = 1150, matchesPlayed = 20, kills = 98, booyahs = 4),
                LeaderboardUser(playerName = "Matrix_Captain", inGameId = "FF-5538201", points = 980, matchesPlayed = 22, kills = 85, booyahs = 3),
                LeaderboardUser(playerName = "Sniper_God", inGameId = "FF-1122990", points = 850, matchesPlayed = 18, kills = 72, booyahs = 2)
            )
            for (u in mockLeaderboard) {
                appDao.insertLeaderboardUser(u)
            }

            // Insert notifications
            val mockNotifications = listOf(
                Notification(
                    title = "Welcome to VactorX Bullsaí eSports†!",
                    message = "Get ready to compete, conquer, and become a champion. Browse tournaments and register today!",
                    timestamp = System.currentTimeMillis()
                ),
                Notification(
                    title = "Free Fire Bermuda Squad Tournament Open!",
                    message = "Bermuda Squad Tournament registration is now live. Entry: ₹100. Prize Pool: ₹50,000. Limited slots available, register now!",
                    timestamp = System.currentTimeMillis() - 3600000
                )
            )
            for (n in mockNotifications) {
                appDao.insertNotification(n)
            }
        }
    }
}
