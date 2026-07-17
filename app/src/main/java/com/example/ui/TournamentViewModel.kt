package com.example.ui

import android.app.Application
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.database.*
import com.example.data.repository.AppRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class Screen {
    object Home : Screen()
    object Tournaments : Screen()
    data class RegistrationForm(val tournamentId: Int) : Screen()
    object MyRegistrations : Screen()
    object Results : Screen()
    object Leaderboard : Screen()
    object Rules : Screen()
    object Contact : Screen()
    object AdminDashboard : Screen()
}

class TournamentViewModel(
    application: Application,
    private val repository: AppRepository
) : AndroidViewModel(application) {

    // Current Active Screen
    var currentScreen by mutableStateOf<Screen>(Screen.Home)
        private set

    fun navigateTo(screen: Screen) {
        currentScreen = screen
    }

    // User Authentication States
    var loggedInUserUid by mutableStateOf("") // Free Fire UID used as local login identifier
    var loggedInUserMobile by mutableStateOf("")
    var loggedInUserInGameName by mutableStateOf("")
    var isPlayerLoggedIn by mutableStateOf(false)

    // Admin Auth State
    var isAdminLoggedIn by mutableStateOf(false)

    // Database Flows
    val tournaments: StateFlow<List<Tournament>> = repository.allTournaments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val registrations: StateFlow<List<Registration>> = repository.allRegistrations
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val leaderboard: StateFlow<List<LeaderboardUser>> = repository.leaderboard
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val notifications: StateFlow<List<Notification>> = repository.allNotifications
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Search & Filter state
    private val _searchQuery = MutableStateFlow("")
    var searchQuery: String
        get() = _searchQuery.value
        set(value) { _searchQuery.value = value }

    private val _filterMap = MutableStateFlow("All")
    var filterMap: String
        get() = _filterMap.value
        set(value) { _filterMap.value = value }

    private val _filterMode = MutableStateFlow("All")
    var filterMode: String
        get() = _filterMode.value
        set(value) { _filterMode.value = value }

    // Filtered tournaments stream
    val filteredTournaments: StateFlow<List<Tournament>> = combine(
        tournaments,
        _searchQuery,
        _filterMap,
        _filterMode
    ) { list, query, map, mode ->
        list.filter { tournament ->
            val matchesQuery = tournament.name.contains(query, ignoreCase = true) ||
                    tournament.map.contains(query, ignoreCase = true) ||
                    tournament.mode.contains(query, ignoreCase = true)
            val matchesMap = map == "All" || tournament.map.equals(map, ignoreCase = true)
            val matchesMode = mode == "All" || tournament.mode.equals(mode, ignoreCase = true)
            matchesQuery && matchesMap && matchesMode
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        viewModelScope.launch {
            repository.prepopulateIfNeeded()
        }
    }

    // --- Player Actions ---

    // Send Mock OTP
    var otpSentMobile by mutableStateOf("")
    var generatedOtp by mutableStateOf("")
    var showOtpDialog by mutableStateOf(false)

    fun sendMockOtp(mobile: String) {
        otpSentMobile = mobile
        // Generate a 6-digit pin
        generatedOtp = (100000..999999).random().toString()
        showOtpDialog = true
        Toast.makeText(getApplication(), "Mock OTP sent to $mobile: $generatedOtp", Toast.LENGTH_LONG).show()
    }

    fun verifyMockOtp(otpCode: String, uid: String, inGameName: String): Boolean {
        if (otpCode == generatedOtp || otpCode == "123456") { // Support bypass test OTP
            loggedInUserMobile = otpSentMobile
            loggedInUserUid = uid
            loggedInUserInGameName = inGameName
            isPlayerLoggedIn = true
            showOtpDialog = false
            Toast.makeText(getApplication(), "Login successful!", Toast.LENGTH_SHORT).show()
            return true
        }
        return false
    }

    fun logoutPlayer() {
        loggedInUserMobile = ""
        loggedInUserUid = ""
        loggedInUserInGameName = ""
        isPlayerLoggedIn = false
        Toast.makeText(getApplication(), "Logged out", Toast.LENGTH_SHORT).show()
    }

    // Submit Registration
    fun submitRegistration(
        tournamentId: Int,
        tournamentName: String,
        playerName: String,
        freeFireUid: String,
        inGameName: String,
        mobileNumber: String,
        whatsAppNumber: String,
        teamName: String,
        captainName: String,
        stateRegion: String,
        paymentScreenshot: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            val registration = Registration(
                tournamentId = tournamentId,
                tournamentName = tournamentName,
                playerName = playerName,
                freeFireUid = freeFireUid,
                inGameName = inGameName,
                mobileNumber = mobileNumber,
                whatsAppNumber = whatsAppNumber,
                teamName = teamName.ifBlank { "N/A" },
                captainName = captainName.ifBlank { "N/A" },
                stateRegion = stateRegion,
                paymentScreenshot = paymentScreenshot,
                status = "Pending"
            )
            repository.insertRegistration(registration)
            
            // Add a notification about registration
            repository.insertNotification(
                Notification(
                    title = "New Registration for $tournamentName",
                    message = "Player $playerName (UID: $freeFireUid) has registered for $tournamentName. Status: Pending Admin Approval."
                )
            )
            onSuccess()
        }
    }

    // --- Admin Actions ---

    fun loginAdmin(password: String): Boolean {
        if (password == "admin123" || password == "admin") {
            isAdminLoggedIn = true
            Toast.makeText(getApplication(), "Admin Logged In Successfully!", Toast.LENGTH_SHORT).show()
            return true
        }
        return false
    }

    fun logoutAdmin() {
        isAdminLoggedIn = false
        Toast.makeText(getApplication(), "Admin Logged Out", Toast.LENGTH_SHORT).show()
    }

    fun createTournament(
        name: String,
        map: String,
        mode: String,
        dateTime: String,
        entryFee: Int,
        prizePool: Int,
        totalSlots: Int,
        rules: String
    ) {
        viewModelScope.launch {
            val t = Tournament(
                name = name,
                map = map,
                mode = mode,
                dateTime = dateTime,
                entryFee = entryFee,
                prizePool = prizePool,
                totalSlots = totalSlots,
                remainingSlots = totalSlots,
                rules = rules,
                status = "Open"
            )
            repository.insertTournament(t)
            repository.insertNotification(
                Notification(
                    title = "New Tournament: $name!",
                    message = "Map: $map | Mode: $mode | Prize Pool: ₹$prizePool. Registration is now open!"
                )
            )
            Toast.makeText(getApplication(), "Tournament Created Successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    fun updateTournament(tournament: Tournament) {
        viewModelScope.launch {
            repository.updateTournament(tournament)
            Toast.makeText(getApplication(), "Tournament Updated Successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    fun deleteTournament(tournament: Tournament) {
        viewModelScope.launch {
            repository.deleteTournament(tournament)
            Toast.makeText(getApplication(), "Tournament Deleted Successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    fun setRegistrationStatus(tournament: Tournament, open: Boolean) {
        viewModelScope.launch {
            val updated = tournament.copy(status = if (open) "Open" else "Closed")
            repository.updateTournament(updated)
            Toast.makeText(getApplication(), "Registration status set to ${updated.status}", Toast.LENGTH_SHORT).show()
        }
    }

    fun approveRegistration(reg: Registration) {
        viewModelScope.launch {
            val updated = reg.copy(status = "Approved")
            repository.updateRegistration(updated)
            
            // Notify player
            repository.insertNotification(
                Notification(
                    title = "Registration Approved!",
                    message = "Congratulations ${reg.playerName}, your registration for ${reg.tournamentName} has been APPROVED. Check dashboard for Room details!"
                )
            )
            Toast.makeText(getApplication(), "Registration Approved!", Toast.LENGTH_SHORT).show()
        }
    }

    fun rejectRegistration(reg: Registration) {
        viewModelScope.launch {
            val updated = reg.copy(status = "Rejected")
            repository.updateRegistration(updated)
            
            // Notify player
            repository.insertNotification(
                Notification(
                    title = "Registration Rejected",
                    message = "Sorry ${reg.playerName}, your registration for ${reg.tournamentName} was rejected. Please contact support or upload valid payment details."
                )
            )
            Toast.makeText(getApplication(), "Registration Rejected", Toast.LENGTH_SHORT).show()
        }
    }

    fun publishRoomDetails(tournament: Tournament, roomId: String, roomPass: String) {
        viewModelScope.launch {
            val updated = tournament.copy(roomId = roomId, roomPassword = roomPass)
            repository.updateTournament(updated)
            repository.insertNotification(
                Notification(
                    title = "Room Details Published: ${tournament.name}",
                    message = "Room details are now available for approved players of ${tournament.name}. Room ID: $roomId | Password: $roomPass"
                )
            )
            Toast.makeText(getApplication(), "Room details published successfully!", Toast.LENGTH_SHORT).show()
        }
    }

    fun declareWinner(tournament: Tournament, winnerName: String, winnerUid: String, kills: Int = 10, booyahs: Int = 1) {
        viewModelScope.launch {
            val updated = tournament.copy(
                winnerName = winnerName,
                winnerUid = winnerUid,
                status = "Closed"
            )
            repository.updateTournament(updated)

            // Add points to leaderboard
            val currentLeaderboard = repository.leaderboard.first()
            val existingUser = currentLeaderboard.find { it.freeFireIdEquals(winnerUid) || it.playerName.equals(winnerName, true) }
            if (existingUser != null) {
                repository.insertLeaderboardUser(
                    existingUser.copy(
                        points = existingUser.points + 100, // 100 points for Booyah
                        matchesPlayed = existingUser.matchesPlayed + 1,
                        kills = existingUser.kills + kills,
                        booyahs = existingUser.booyahs + booyahs
                    )
                )
            } else {
                repository.insertLeaderboardUser(
                    LeaderboardUser(
                        playerName = winnerName,
                        inGameId = winnerUid,
                        points = 100 + (kills * 2), // 100 for booyah, 2 points per kill
                        matchesPlayed = 1,
                        kills = kills,
                        booyahs = booyahs
                    )
                )
            }

            repository.insertNotification(
                Notification(
                    title = "Tournament Winner Declared!",
                    message = "Congratulations to $winnerName (UID: $winnerUid) for winning the ${tournament.name}! Booyah!"
                )
            )
            Toast.makeText(getApplication(), "Winner declared and points credited!", Toast.LENGTH_LONG).show()
        }
    }

    private fun LeaderboardUser.freeFireIdEquals(uid: String): Boolean {
        return this.inGameId.equals(uid, true) || this.inGameId.replace("FF-", "").equals(uid.replace("FF-", ""), true)
    }

    fun sendAnnouncement(title: String, message: String) {
        viewModelScope.launch {
            repository.insertNotification(
                Notification(title = "[ANNOUNCEMENT] $title", message = message)
            )
            Toast.makeText(getApplication(), "Announcement published!", Toast.LENGTH_SHORT).show()
        }
    }

    // Helper to export registrations of a tournament as CSV
    fun exportRegistrationsToCsv(context: Context, tournamentId: Int, tournamentName: String) {
        viewModelScope.launch {
            val list = repository.allRegistrations.first().filter { it.tournamentId == tournamentId }
            if (list.isEmpty()) {
                Toast.makeText(context, "No registrations found to export!", Toast.LENGTH_SHORT).show()
                return@launch
            }

            val csvBuilder = StringBuilder()
            csvBuilder.append("ID,Player Name,UID,In-Game Name,Mobile,WhatsApp,Team Name,Captain,State,Status\n")
            for (reg in list) {
                csvBuilder.append("${reg.id},")
                csvBuilder.append("\"${reg.playerName}\",")
                csvBuilder.append("\"${reg.freeFireUid}\",")
                csvBuilder.append("\"${reg.inGameName}\",")
                csvBuilder.append("\"${reg.mobileNumber}\",")
                csvBuilder.append("\"${reg.whatsAppNumber}\",")
                csvBuilder.append("\"${reg.teamName}\",")
                csvBuilder.append("\"${reg.captainName}\",")
                csvBuilder.append("\"${reg.stateRegion}\",")
                csvBuilder.append("\"${reg.status}\"\n")
            }

            val csvString = csvBuilder.toString()
            
            // Share via Android Intent
            val sendIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_TEXT, csvString)
                putExtra(Intent.EXTRA_SUBJECT, "Registrations - $tournamentName")
                type = "text/csv"
            }
            val shareIntent = Intent.createChooser(sendIntent, "Export Player List CSV")
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            context.startActivity(shareIntent)
        }
    }
}

class TournamentViewModelFactory(
    private val application: Application,
    private val repository: AppRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(TournamentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return TournamentViewModel(application, repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
