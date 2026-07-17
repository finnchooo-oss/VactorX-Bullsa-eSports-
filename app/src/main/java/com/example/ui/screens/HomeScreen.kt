package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.database.Tournament
import com.example.data.database.Notification
import com.example.ui.Screen
import com.example.ui.TournamentViewModel
import com.example.ui.theme.*
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    viewModel: TournamentViewModel,
    onRegisterClick: (Tournament) -> Unit,
    modifier: Modifier = Modifier
) {
    val tournaments by viewModel.tournaments.collectAsState()
    val notifications by viewModel.notifications.collectAsState()

    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(JetBlack),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        // Hero Image Section
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_hero_banner),
                    contentDescription = "VactorX Bullsai Hero Banner",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Red/Gold Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(
                                    Color.Transparent,
                                    JetBlack.copy(alpha = 0.5f),
                                    JetBlack
                                )
                            )
                        )
                )

                // Title overlay
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(16.dp)
                ) {
                    Text(
                        text = "VactorX Bullsaí eSports†",
                        style = MaterialTheme.typography.displayMedium.copy(color = MetallicGold),
                        modifier = Modifier.shadow(8.dp, RoundedCornerShape(4.dp))
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Compete. Conquer. Become a Champion.",
                        style = MaterialTheme.typography.titleMedium.copy(
                            color = NeonRed,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        // Live Tournament Countdown Widget
        item {
            Spacer(modifier = Modifier.height(16.dp))
            LiveCountdownWidget(
                tournamentName = "VactorX Pro League - Season 1",
                onRegisterNowClick = {
                    val target = tournaments.find { it.name.contains("Pro League", true) }
                    if (target != null) {
                        viewModel.navigateTo(Screen.RegistrationForm(target.id))
                    } else {
                        viewModel.navigateTo(Screen.Tournaments)
                    }
                }
            )
        }

        // Season Prize Pool Card
        item {
            Spacer(modifier = Modifier.height(16.dp))
            SeasonPrizePoolWidget()
        }

        // Upcoming Tournaments Showcase
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "UPCOMING BATTLES",
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MetallicGold,
                        fontWeight = FontWeight.ExtraBold
                    )
                )
                Text(
                    text = "View All",
                    color = NeonRed,
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .clickable { viewModel.navigateTo(Screen.Tournaments) }
                        .padding(4.dp)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
        }

        // Show top 3 open tournaments
        val openTournaments = tournaments.filter { it.status == "Open" }.take(3)
        if (openTournaments.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .background(CardBackground, RoundedCornerShape(12.dp))
                        .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(Icons.Default.SportsEsports, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(48.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("No active registrations open.", color = TextSecondary, style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        } else {
            items(openTournaments) { tournament ->
                TournamentShowcaseRow(
                    tournament = tournament,
                    onRegisterClick = { onRegisterClick(tournament) }
                )
            }
        }

        // Announcements Section
        item {
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = "LATEST ANNOUNCEMENTS",
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MetallicGold,
                    fontWeight = FontWeight.ExtraBold
                ),
                modifier = Modifier.padding(horizontal = 16.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        val activeAnnouncements = notifications.take(4)
        if (activeAnnouncements.isEmpty()) {
            item {
                Text(
                    text = "No recent announcements.",
                    color = TextSecondary,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        } else {
            items(activeAnnouncements) { notification ->
                AnnouncementItem(notification = notification)
            }
        }

        // Social Media Channels Links
        item {
            Spacer(modifier = Modifier.height(32.dp))
            SocialChannelsSection()
        }
    }
}

@Composable
fun LiveCountdownWidget(
    tournamentName: String,
    onRegisterNowClick: () -> Unit
) {
    var timeLeftSeconds by remember { mutableStateOf(432000) } // 5 days in seconds
    LaunchedEffect(Unit) {
        while (timeLeftSeconds > 0) {
            delay(1000)
            timeLeftSeconds--
        }
    }

    val days = timeLeftSeconds / 86400
    val hours = (timeLeftSeconds % 86400) / 3600
    val minutes = (timeLeftSeconds % 3600) / 60
    val seconds = timeLeftSeconds % 60

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(CardBackground, RoundedCornerShape(12.dp))
            .border(2.dp, Brush.horizontalGradient(listOf(GamingRed, MetallicGold)), RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "LIVE TOURNAMENT COUNTDOWN",
            style = MaterialTheme.typography.labelMedium.copy(
                color = MetallicGold,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.5.sp
            )
        )
        Text(
            text = tournamentName,
            style = MaterialTheme.typography.titleMedium.copy(
                color = TextPrimary,
                fontWeight = FontWeight.ExtraBold
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 4.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Countdown digits
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            CountdownBox(value = days.toString().padStart(2, '0'), label = "Days")
            CountdownBox(value = hours.toString().padStart(2, '0'), label = "Hours")
            CountdownBox(value = minutes.toString().padStart(2, '0'), label = "Mins")
            CountdownBox(value = seconds.toString().padStart(2, '0'), label = "Secs")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onRegisterNowClick,
            colors = ButtonDefaults.buttonColors(containerColor = NeonRed),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .testTag("register_now_countdown_button")
        ) {
            Icon(Icons.Default.SportsEsports, contentDescription = null, tint = White)
            Spacer(modifier = Modifier.width(8.dp))
            Text("REGISTER NOW", color = White, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun CountdownBox(value: String, label: String) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .width(50.dp)
    ) {
        Text(
            text = value,
            style = MaterialTheme.typography.displayMedium.copy(
                color = MetallicGold,
                fontWeight = FontWeight.Black
            )
        )
        Text(
            text = label.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                color = TextSecondary,
                fontSize = 10.sp
            )
        )
    }
}

@Composable
fun SeasonPrizePoolWidget() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(
                Brush.linearGradient(
                    colors = listOf(GamingRed.copy(alpha = 0.3f), Color.Black)
                ),
                RoundedCornerShape(12.dp)
            )
            .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(52.dp)
                .background(MetallicGold.copy(alpha = 0.1f), RoundedCornerShape(26.dp)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.EmojiEvents,
                contentDescription = "Prize Pool",
                tint = MetallicGold,
                modifier = Modifier.size(32.dp)
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(
                text = "SEASON PRIZE POOL",
                style = MaterialTheme.typography.labelMedium.copy(
                    color = MetallicGold,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = "₹1,50,000+ INR",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = White,
                    fontWeight = FontWeight.Black
                )
            )
            Text(
                text = "Compete in Bermuda, Kalahari & Custom rooms to win big!",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
            )
        }
    }
}

@Composable
fun TournamentShowcaseRow(
    tournament: Tournament,
    onRegisterClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp)),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Map icon circle
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .background(GamingRed.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Map,
                    contentDescription = null,
                    tint = MetallicGold,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = tournament.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = TextPrimary
                    )
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Gamepad,
                        contentDescription = null,
                        tint = NeonRed,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${tournament.map} | ${tournament.mode}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.MonetizationOn,
                        contentDescription = null,
                        tint = MetallicGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Prize: ₹${tournament.prizePool} | Entry: ₹${tournament.entryFee}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = MetallicGold,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${tournament.remainingSlots}/${tournament.totalSlots}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (tournament.remainingSlots < 10) NeonRed else MetallicGold,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "Slots left",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary, fontSize = 10.sp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = onRegisterClick,
                    colors = ButtonDefaults.buttonColors(containerColor = GamingRed),
                    shape = RoundedCornerShape(4.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(30.dp)
                ) {
                    Text("JOIN", color = White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AnnouncementItem(notification: Notification) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .background(CardBackground, RoundedCornerShape(8.dp))
            .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = Icons.Default.Campaign,
            contentDescription = "Announcement",
            tint = NeonRed,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = notification.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = MetallicGold
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = notification.message,
                style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
            )
        }
    }
}

@Composable
fun SocialChannelsSection() {
    var showDialog by remember { mutableStateOf(false) }
    var clickedPlatform by remember { mutableStateOf("") }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Connect on $clickedPlatform", color = MetallicGold) },
            text = {
                Text(
                    "You are now opening the official VactorX Bullsaí eSports† $clickedPlatform channel. Get ready to interact with millions of gamers, view official live streams, and catch dynamic highlights!",
                    color = TextPrimary
                )
            },
            confirmButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("OK", color = NeonRed)
                }
            },
            containerColor = CardBackground,
            titleContentColor = MetallicGold,
            textContentColor = TextPrimary
        )
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Text(
            text = "CONNECT WITH US",
            style = MaterialTheme.typography.titleMedium.copy(
                color = MetallicGold,
                fontWeight = FontWeight.ExtraBold
            )
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            SocialButton(
                icon = Icons.Default.Chat,
                label = "Discord",
                color = Color(0xFF7289DA),
                onClick = {
                    clickedPlatform = "Discord"
                    showDialog = true
                }
            )
            SocialButton(
                icon = Icons.Default.PhotoCamera,
                label = "Instagram",
                color = Color(0xFFE1306C),
                onClick = {
                    clickedPlatform = "Instagram"
                    showDialog = true
                }
            )
            SocialButton(
                icon = Icons.Default.PlayCircle,
                label = "YouTube",
                color = Color(0xFFFF0000),
                onClick = {
                    clickedPlatform = "YouTube"
                    showDialog = true
                }
            )
        }
    }
}

@Composable
fun SocialButton(
    icon: ImageVector,
    label: String,
    color: Color,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .background(CardBackground, RoundedCornerShape(8.dp))
            .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 10.dp)
            .width(90.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(icon, contentDescription = label, tint = color, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
