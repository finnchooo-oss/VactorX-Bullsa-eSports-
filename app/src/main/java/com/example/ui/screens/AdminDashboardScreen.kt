package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.Registration
import com.example.data.database.Tournament
import com.example.ui.TournamentViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminDashboardScreen(
    viewModel: TournamentViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val tournaments by viewModel.tournaments.collectAsState()
    val registrations by viewModel.registrations.collectAsState()

    if (!viewModel.isAdminLoggedIn) {
        // Show Admin Login
        AdminLoginScreen(viewModel = viewModel)
    } else {
        // Show Admin Dashboard
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(JetBlack)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(GamingRed.copy(alpha = 0.3f), JetBlack)))
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "VACTORX ADMIN PANEL",
                            style = MaterialTheme.typography.labelSmall.copy(color = MetallicGold)
                        )
                        Text(
                            text = "GAME OPERATOR",
                            style = MaterialTheme.typography.titleLarge.copy(color = White, fontWeight = FontWeight.Black)
                        )
                    }

                    Button(
                        onClick = { viewModel.logoutAdmin() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                        shape = RoundedCornerShape(4.dp),
                        modifier = Modifier.height(30.dp).testTag("admin_logout_button")
                    ) {
                        Text("LOGOUT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Tab control
            var selectedTab by remember { mutableStateOf(0) }
            val tabTitles = listOf("Matches", "Registrations", "Broadcast")

            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = CardBackground,
                contentColor = MetallicGold,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = GamingRed
                    )
                }
            ) {
                tabTitles.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                        selectedContentColor = MetallicGold,
                        unselectedContentColor = TextSecondary
                    )
                }
            }

            when (selectedTab) {
                0 -> {
                    // Manage Tournaments Screen
                    ManageTournamentsTab(viewModel = viewModel, tournaments = tournaments)
                }
                1 -> {
                    // Manage Player Registrations Screen
                    ManageRegistrationsTab(viewModel = viewModel, registrations = registrations, tournaments = tournaments)
                }
                2 -> {
                    // Send Broadcast Announcements Screen
                    SendBroadcastTab(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun AdminLoginScreen(viewModel: TournamentViewModel) {
    var password by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(JetBlack)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = MetallicGold, modifier = Modifier.size(64.dp))
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "VACTORX BULLSAÍ ADMIN",
            style = MaterialTheme.typography.titleLarge.copy(color = White, fontWeight = FontWeight.Black)
        )
        Text(
            text = "Enter administrator passcode to access tournament creation and participant approvals.",
            color = TextSecondary,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 8.dp)
        )

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Passcode (admin123)", color = TextSecondary) },
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MetallicGold) },
            visualTransformation = PasswordVisualTransformation(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = MetallicGold,
                unfocusedBorderColor = SurfaceBorder,
                focusedLabelColor = MetallicGold,
                unfocusedLabelColor = TextSecondary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("admin_passcode_input")
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                val success = viewModel.loginAdmin(password)
                if (!success) {
                    showError = true
                } else {
                    showError = false
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = GamingRed),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("admin_login_submit_button")
        ) {
            Text("AUTHENTICATE ADMIN", fontWeight = FontWeight.Bold, color = White)
        }

        if (showError) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Invalid passcode. Please try again.",
                color = NeonRed,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ManageTournamentsTab(viewModel: TournamentViewModel, tournaments: List<Tournament>) {
    var showCreateForm by remember { mutableStateOf(false) }

    // Form inputs
    var name by remember { mutableStateOf("") }
    var map by remember { mutableStateOf("Bermuda") }
    var mode by remember { mutableStateOf("Squad") }
    var dateTime by remember { mutableStateOf("") }
    var entryFee by remember { mutableStateOf("0") }
    var prizePool by remember { mutableStateOf("10000") }
    var totalSlots by remember { mutableStateOf("48") }
    var rules by remember { mutableStateOf("") }

    val mapsList = listOf("Bermuda", "Purgatory", "Kalahari", "Alpine", "Nexterra", "Clash Squad", "Custom Room")
    val modesList = listOf("Solo", "Duo", "Squad", "1v1", "1v6", "4v4")

    if (showCreateForm) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("CREATE NEW LEAGUE", color = MetallicGold, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = { showCreateForm = false }) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            CustomOutlinedTextField(value = name, onValueChange = { name = it }, label = "Tournament Name", icon = Icons.Default.EmojiEvents, tag = "new_t_name")
            CustomOutlinedTextField(value = dateTime, onValueChange = { dateTime = it }, label = "Date & Time (e.g. July 24, 2026 - 06:00 PM)", icon = Icons.Default.AccessTime, tag = "new_t_time")
            CustomOutlinedTextField(value = entryFee, onValueChange = { entryFee = it }, label = "Entry Fee (INR)", icon = Icons.Default.MonetizationOn, keyboardType = KeyboardType.Number, tag = "new_t_fee")
            CustomOutlinedTextField(value = prizePool, onValueChange = { prizePool = it }, label = "Prize Pool (INR)", icon = Icons.Default.Star, keyboardType = KeyboardType.Number, tag = "new_t_prize")
            CustomOutlinedTextField(value = totalSlots, onValueChange = { totalSlots = it }, label = "Total Slots (e.g. 48)", icon = Icons.Default.Groups, keyboardType = KeyboardType.Number, tag = "new_t_slots")

            OutlinedTextField(
                value = rules,
                onValueChange = { rules = it },
                label = { Text("Rules & Guidelines", color = TextSecondary) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedBorderColor = MetallicGold,
                    unfocusedBorderColor = SurfaceBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp)
                    .padding(vertical = 6.dp)
            )

            // Select map and mode
            Text("Select Map Map:", color = MetallicGold, fontSize = 12.sp, modifier = Modifier.padding(top = 12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                mapsList.take(4).forEach { mapChoice ->
                    val isSelected = map == mapChoice
                    Box(
                        modifier = Modifier
                            .background(if (isSelected) GamingRed else CardBackground, RoundedCornerShape(4.dp))
                            .border(1.dp, SurfaceBorder, RoundedCornerShape(4.dp))
                            .clickable { map = mapChoice }
                            .padding(8.dp)
                    ) {
                        Text(mapChoice, color = White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Text("Select Mode Type:", color = MetallicGold, fontSize = 12.sp, modifier = Modifier.padding(top = 12.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                modesList.take(4).forEach { modeChoice ->
                    val isSelected = mode == modeChoice
                    Box(
                        modifier = Modifier
                            .background(if (isSelected) DarkGold else CardBackground, RoundedCornerShape(4.dp))
                            .border(1.dp, SurfaceBorder, RoundedCornerShape(4.dp))
                            .clickable { mode = modeChoice }
                            .padding(8.dp)
                    ) {
                        Text(modeChoice, color = Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (name.isBlank() || dateTime.isBlank() || entryFee.isBlank() || prizePool.isBlank() || totalSlots.isBlank()) {
                        // Validate
                    } else {
                        viewModel.createTournament(
                            name = name,
                            map = map,
                            mode = mode,
                            dateTime = dateTime,
                            entryFee = entryFee.toIntOrNull() ?: 0,
                            prizePool = prizePool.toIntOrNull() ?: 0,
                            totalSlots = totalSlots.toIntOrNull() ?: 48,
                            rules = rules.ifBlank { "Standard competitive play rules." }
                        )
                        showCreateForm = false
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MetallicGold),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("admin_save_tournament_button")
            ) {
                Text("PUBLISH TOURNAMENT", color = Black, fontWeight = FontWeight.Black)
            }
        }
    } else {
        // Show tournaments list with admin controls
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Button(
                    onClick = { showCreateForm = true },
                    colors = ButtonDefaults.buttonColors(containerColor = GamingRed),
                    shape = RoundedCornerShape(6.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_add_tournament_trigger")
                ) {
                    Icon(Icons.Default.Add, contentDescription = null, tint = White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ADD UNLIMITED TOURNAMENT", color = White, fontWeight = FontWeight.Bold)
                }
            }

            items(tournaments) { tournament ->
                AdminTournamentCard(viewModel = viewModel, tournament = tournament)
            }
        }
    }
}

@Composable
fun AdminTournamentCard(viewModel: TournamentViewModel, tournament: Tournament) {
    var publishCredentialsOpen by remember { mutableStateOf(false) }
    var declareWinnerOpen by remember { mutableStateOf(false) }

    var inputRoomId by remember { mutableStateOf("") }
    var inputPass by remember { mutableStateOf("") }

    var winnerName by remember { mutableStateOf("") }
    var winnerUid by remember { mutableStateOf("") }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = tournament.name,
                        style = MaterialTheme.typography.titleMedium.copy(color = White, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Map: ${tournament.map} | Mode: ${tournament.mode}",
                        color = MetallicGold,
                        fontSize = 12.sp
                    )
                }

                Row {
                    IconButton(onClick = { viewModel.setRegistrationStatus(tournament, tournament.status != "Open") }) {
                        Icon(
                            imageVector = if (tournament.status == "Open") Icons.Default.LockOpen else Icons.Default.Lock,
                            contentDescription = "Toggle status",
                            tint = if (tournament.status == "Open") Color.Green else Color.Gray
                        )
                    }

                    IconButton(onClick = { viewModel.deleteTournament(tournament) }) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", tint = NeonRed)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text("Date: ${tournament.dateTime}", color = TextSecondary, fontSize = 12.sp)
            Text("Prizes: ₹${tournament.prizePool} | Fee: ₹${tournament.entryFee}", color = TextSecondary, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(12.dp))

            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { publishCredentialsOpen = !publishCredentialsOpen },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text(if (tournament.roomId.isNotEmpty()) "UPDATE ROOM" else "ROOM CRED", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { declareWinnerOpen = !declareWinnerOpen },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("DECLARE WIN", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            // Credentials Drawer/Panel
            if (publishCredentialsOpen) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = SurfaceBorder)
                Spacer(modifier = Modifier.height(12.dp))

                Text("PUBLISH MATCH ROOM DETAILS", color = MetallicGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                CustomOutlinedTextField(value = inputRoomId, onValueChange = { inputRoomId = it }, label = "ROOM ID", icon = Icons.Default.MeetingRoom, tag = "admin_room_id")
                CustomOutlinedTextField(value = inputPass, onValueChange = { inputPass = it }, label = "PASSWORD", icon = Icons.Default.VpnKey, tag = "admin_room_pass")

                Button(
                    onClick = {
                        if (inputRoomId.isNotEmpty() && inputPass.isNotEmpty()) {
                            viewModel.publishRoomDetails(tournament, inputRoomId, inputPass)
                            publishCredentialsOpen = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = GamingRed),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .testTag("admin_save_room_details")
                ) {
                    Text("SAVE & BROADCAST CREDS", color = White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            // Declare Winner Drawer/Panel
            if (declareWinnerOpen) {
                Spacer(modifier = Modifier.height(12.dp))
                Divider(color = SurfaceBorder)
                Spacer(modifier = Modifier.height(12.dp))

                Text("DECLARE LOBBY WINNER (BOOYAH!)", color = MetallicGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                CustomOutlinedTextField(value = winnerName, onValueChange = { winnerName = it }, label = "WINNER PLAYER NAME", icon = Icons.Default.Person, tag = "winner_name")
                CustomOutlinedTextField(value = winnerUid, onValueChange = { winnerUid = it }, label = "WINNER FREE FIRE UID", icon = Icons.Default.Tag, tag = "winner_uid")

                Button(
                    onClick = {
                        if (winnerName.isNotEmpty() && winnerUid.isNotEmpty()) {
                            viewModel.declareWinner(tournament, winnerName, winnerUid)
                            declareWinnerOpen = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MetallicGold),
                    shape = RoundedCornerShape(4.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .testTag("admin_save_winner")
                ) {
                    Text("CREDIT WINNER & END MATCH", color = Black, fontWeight = FontWeight.Black, fontSize = 12.sp)
                }
            }
        }
    }
}

@Composable
fun ManageRegistrationsTab(
    viewModel: TournamentViewModel,
    registrations: List<Registration>,
    tournaments: List<Tournament>
) {
    val context = LocalContext.current

    if (registrations.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No sign-ups found yet.", color = TextSecondary)
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(registrations) { reg ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp)),
                    colors = CardDefaults.cardColors(containerColor = CardBackground)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = reg.playerName,
                                    style = MaterialTheme.typography.titleMedium.copy(color = White, fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = "UID: ${reg.freeFireUid} | IGN: ${reg.inGameName}",
                                    color = TextSecondary,
                                    fontSize = 12.sp
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .background(
                                        when (reg.status) {
                                            "Approved" -> Color.Green.copy(alpha = 0.15f)
                                            "Rejected" -> NeonRed.copy(alpha = 0.15f)
                                            else -> MetallicGold.copy(alpha = 0.15f)
                                        },
                                        RoundedCornerShape(4.dp)
                                    )
                                    .padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Text(
                                    text = reg.status.uppercase(),
                                    color = when (reg.status) {
                                        "Approved" -> Color.Green
                                        "Rejected" -> NeonRed
                                        else -> MetallicGold
                                    },
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(text = "Tournament: ${reg.tournamentName}", color = MetallicGold, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(text = "Contact: ${reg.mobileNumber} (WhatsApp: ${reg.whatsAppNumber})", color = TextSecondary, fontSize = 12.sp)
                        Text(text = "Region: ${reg.stateRegion} | Team: ${reg.teamName} (Capt: ${reg.captainName})", color = TextSecondary, fontSize = 12.sp)

                        if (reg.paymentScreenshot.isNotEmpty()) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Green, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Simulated Payment Slip Uploaded: ${reg.paymentScreenshot}",
                                    color = Color.Green,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (reg.status == "Pending") {
                                Button(
                                    onClick = { viewModel.approveRegistration(reg) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color.Green),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("APPROVE", color = Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }

                                Button(
                                    onClick = { viewModel.rejectRegistration(reg) },
                                    colors = ButtonDefaults.buttonColors(containerColor = NeonRed),
                                    shape = RoundedCornerShape(4.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("REJECT", color = White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            Button(
                                onClick = { viewModel.exportRegistrationsToCsv(context, reg.tournamentId, reg.tournamentName) },
                                colors = ButtonDefaults.buttonColors(containerColor = MetallicGold),
                                shape = RoundedCornerShape(4.dp),
                                modifier = Modifier.weight(1.1f)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, tint = Black, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("EXPORT CSV", color = Black, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SendBroadcastTab(viewModel: TournamentViewModel) {
    var title by remember { mutableStateOf("") }
    var msg by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text("BROADCAST ANNOUNCEMENT TO ALL LOBBIES", color = MetallicGold, fontWeight = FontWeight.ExtraBold, style = MaterialTheme.typography.titleMedium)
        Text("Published broadcasts appear instantly under player alerts.", color = TextSecondary, fontSize = 12.sp, modifier = Modifier.padding(bottom = 16.dp))

        CustomOutlinedTextField(value = title, onValueChange = { title = it }, label = "Broadcast Title", icon = Icons.Default.Campaign, tag = "announcement_title")

        OutlinedTextField(
            value = msg,
            onValueChange = { msg = it },
            label = { Text("Announcement Message", color = TextSecondary) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = TextPrimary,
                unfocusedTextColor = TextPrimary,
                focusedBorderColor = MetallicGold,
                unfocusedBorderColor = SurfaceBorder
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(130.dp)
                .padding(vertical = 6.dp)
                .testTag("announcement_message_input")
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (title.isNotEmpty() && msg.isNotEmpty()) {
                    viewModel.sendAnnouncement(title, msg)
                    title = ""
                    msg = ""
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = GamingRed),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("publish_announcement_button")
        ) {
            Text("PUBLISH BROADCAST", color = White, fontWeight = FontWeight.Bold)
        }
    }
}
