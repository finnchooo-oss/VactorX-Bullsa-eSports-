package com.example.ui.screens

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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.Registration
import com.example.data.database.Tournament
import com.example.data.database.Notification
import com.example.ui.TournamentViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyRegistrationsScreen(
    viewModel: TournamentViewModel,
    modifier: Modifier = Modifier
) {
    val registrations by viewModel.registrations.collectAsState()
    val tournaments by viewModel.tournaments.collectAsState()
    val notifications by viewModel.notifications.collectAsState()

    // Filter registrations to only show those of the currently logged in player UID
    val playerRegistrations = registrations.filter { it.freeFireUid.equals(viewModel.loggedInUserUid, ignoreCase = true) }

    if (!viewModel.isPlayerLoggedIn) {
        // Render Login Dashboard
        PlayerLoginDashboard(viewModel = viewModel)
    } else {
        // Render Player Dashboard
        Column(
            modifier = modifier
                .fillMaxSize()
                .background(JetBlack)
        ) {
            // Profile Summary Header
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
                            text = "PLAYER DASHBOARD",
                            style = MaterialTheme.typography.labelSmall.copy(color = MetallicGold, letterSpacing = 1.sp)
                        )
                        Text(
                            text = viewModel.loggedInUserInGameName,
                            style = MaterialTheme.typography.titleLarge.copy(color = White, fontWeight = FontWeight.Black)
                        )
                        Text(
                            text = "UID: ${viewModel.loggedInUserUid}",
                            style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                        )
                    }

                    Button(
                        onClick = { viewModel.logoutPlayer() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.DarkGray),
                        shape = RoundedCornerShape(4.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                        modifier = Modifier.height(32.dp).testTag("logout_button")
                    ) {
                        Text("LOGOUT", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Tabs for My Registrations vs. Match History vs. Notification feed
            var selectedTab by remember { mutableStateOf(0) }
            val tabTitles = listOf("My Signups", "Match Stats", "Inbox")

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
                    // Signups list
                    if (playerRegistrations.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxWidth(),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(Icons.Default.HourglassEmpty, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(48.dp))
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("No registrations found", color = TextSecondary, fontWeight = FontWeight.Bold)
                                Text("Sign up for an upcoming tournament to see it here!", color = TextSecondary, fontSize = 12.sp)
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.weight(1f),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(playerRegistrations) { reg ->
                                val tournament = tournaments.find { it.id == reg.tournamentId }
                                PlayerRegistrationCard(reg = reg, tournament = tournament)
                            }
                        }
                    }
                }
                1 -> {
                    // Match history / stats
                    PlayerStatsTab(viewModel = viewModel)
                }
                2 -> {
                    // Alerts/Inbox list
                    LazyColumn(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        items(notifications) { notif ->
                            NotificationCard(notif = notif)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PlayerRegistrationCard(reg: Registration, tournament: Tournament?) {
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
                        text = reg.tournamentName,
                        style = MaterialTheme.typography.titleMedium.copy(color = TextPrimary, fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = "Signed up on: 16 July 2026",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                    )
                }

                val statusColor = when (reg.status) {
                    "Approved" -> Color.Green
                    "Rejected" -> NeonRed
                    else -> MetallicGold
                }

                Box(
                    modifier = Modifier
                        .background(statusColor.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = reg.status.uppercase(),
                        color = statusColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Show room credentials ONLY IF APPROVED and tournament details exist
            if (reg.status == "Approved" && tournament != null) {
                if (tournament.roomId.isNotEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(GamingRed.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                            .border(1.dp, GamingRed, RoundedCornerShape(8.dp))
                            .padding(12.dp)
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VpnKey, contentDescription = null, tint = MetallicGold, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "ROOM CREDENTIALS (LIVE)",
                                style = MaterialTheme.typography.labelMedium.copy(color = MetallicGold, fontWeight = FontWeight.Bold)
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text("ROOM ID", color = TextSecondary, fontSize = 10.sp)
                                Text(tournament.roomId, color = White, fontWeight = FontWeight.Bold, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                            }
                            Column {
                                Text("PASSWORD", color = TextSecondary, fontSize = 10.sp)
                                Text(tournament.roomPassword, color = White, fontWeight = FontWeight.Bold, fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Please copy ID and enter Free Fire lobby 10 mins before match starts.",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.DarkGray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LockClock, contentDescription = null, tint = MetallicGold, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                "Room ID & Password will be published here 15 mins before launch.",
                                color = TextSecondary,
                                fontSize = 11.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else if (reg.status == "Pending") {
                Text(
                    text = "Your payment check is underway. Once approved by our VactorX team, Room details will be unlocked here.",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            } else if (reg.status == "Rejected") {
                Text(
                    text = "Registration rejected due to invalid payment verification slip. Kindly contact Support via Contact tab.",
                    color = NeonRed,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun PlayerStatsTab(viewModel: TournamentViewModel) {
    val scrollState = rememberScrollState()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .verticalScroll(scrollState)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "MATCH METRICS & HISTORIC LIFETIME STATS",
            style = MaterialTheme.typography.labelMedium.copy(color = MetallicGold, fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            StatMetricBox(value = "25", label = "Matches", modifier = Modifier.weight(1f))
            StatMetricBox(value = "142", label = "Kills", modifier = Modifier.weight(1f))
            StatMetricBox(value = "8", label = "Booyahs", modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Level progress bar
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
                    Text("PLAYER RANK", color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text("VACTORX LEAGUE: PLATINUM II", color = MetallicGold, fontWeight = FontWeight.Black)
                }
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = { 0.72f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = GamingRed,
                    trackColor = SurfaceBorder
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "720 / 1000 Rank Points to next Rank Tier (Master)",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
fun StatMetricBox(value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.displayMedium.copy(color = MetallicGold, fontWeight = FontWeight.Black)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun NotificationCard(notif: Notification) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp)),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.Top
        ) {
            Icon(
                imageVector = Icons.Default.Campaign,
                contentDescription = null,
                tint = MetallicGold,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = notif.title,
                    color = White,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = notif.message,
                    color = TextSecondary,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlayerLoginDashboard(viewModel: TournamentViewModel) {
    var mobileNo by remember { mutableStateOf("") }
    var userUid by remember { mutableStateOf("") }
    var inGameName by remember { mutableStateOf("") }
    var otpCode by remember { mutableStateOf("") }

    var showError by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(JetBlack)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "VACTORX MEMBER PORTAL",
            style = MaterialTheme.typography.labelMedium.copy(color = MetallicGold, letterSpacing = 2.sp)
        )
        Text(
            text = "ACCESS DASHBOARD",
            style = MaterialTheme.typography.displayMedium.copy(color = White, fontWeight = FontWeight.Black),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Enter your mobile number and Free Fire UID to receive a mock verification code and log into your registrations panel.",
            color = TextSecondary,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(24.dp))

        if (!viewModel.showOtpDialog) {
            // First Stage: Ask details
            CustomOutlinedTextField(
                value = mobileNo,
                onValueChange = { mobileNo = it },
                label = "Mobile Number",
                icon = Icons.Default.Phone,
                keyboardType = KeyboardType.Phone,
                tag = "login_mobile_input"
            )

            CustomOutlinedTextField(
                value = userUid,
                onValueChange = { userUid = it },
                label = "Free Fire UID (ID)",
                icon = Icons.Default.Tag,
                keyboardType = KeyboardType.Number,
                tag = "login_uid_input"
            )

            CustomOutlinedTextField(
                value = inGameName,
                onValueChange = { inGameName = it },
                label = "In-Game Name (IGN)",
                icon = Icons.Default.SportsEsports,
                tag = "login_ign_input"
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (mobileNo.isBlank() || userUid.isBlank() || inGameName.isBlank()) {
                        showError = true
                    } else {
                        showError = false
                        viewModel.sendMockOtp(mobileNo)
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GamingRed),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("send_otp_button")
            ) {
                Text("SEND VERIFICATION CODE (OTP)", fontWeight = FontWeight.Bold, color = White)
            }
        } else {
            // Second Stage: Verify mock OTP
            Text(
                text = "ENTER 6-DIGIT VERIFICATION CODE",
                color = MetallicGold,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(vertical = 8.dp)
            )

            Text(
                text = "Mock Code Sent: ${viewModel.generatedOtp}",
                color = Color.Green,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )

            OutlinedTextField(
                value = otpCode,
                onValueChange = { otpCode = it },
                label = { Text("6-Digit OTP (or 123456)", color = TextSecondary) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = MetallicGold) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
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
                    .padding(vertical = 12.dp)
                    .testTag("otp_code_input")
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    val success = viewModel.verifyMockOtp(otpCode, userUid, inGameName)
                    if (!success) {
                        showError = true
                    } else {
                        showError = false
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MetallicGold),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("verify_otp_button")
            ) {
                Text("VERIFY & SIGN IN", fontWeight = FontWeight.Black, color = Black)
            }

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(onClick = { viewModel.showOtpDialog = false }) {
                Text("Change login details", color = NeonRed)
            }
        }

        if (showError) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Invalid login details or incorrect verification pin code.",
                color = NeonRed,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
