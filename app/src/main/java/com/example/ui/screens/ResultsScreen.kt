package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.Tournament
import com.example.ui.TournamentViewModel
import com.example.ui.theme.*

@Composable
fun ResultsScreen(
    viewModel: TournamentViewModel,
    modifier: Modifier = Modifier
) {
    val tournaments by viewModel.tournaments.collectAsState()
    
    // We get closed tournaments, or we can supplement with mock finished ones if none are closed yet
    val finishedTournaments = tournaments.filter { it.status == "Closed" || it.winnerName.isNotEmpty() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(JetBlack)
    ) {
        // Results Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(GamingRed.copy(alpha = 0.3f), JetBlack)))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "BATTLE RESULTS",
                    style = MaterialTheme.typography.displayMedium.copy(color = MetallicGold)
                )
                Text(
                    text = "Lobby Finishes & Crowned Champions",
                    color = TextSecondary,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }

        if (finishedTournaments.isEmpty()) {
            // We can show a nice empty state or a few mock results so the screen is instantly functional!
            val sampleFinished = listOf(
                Tournament(
                    name = "VactorX Launch Cup 2026",
                    map = "Bermuda",
                    mode = "Squad",
                    dateTime = "July 10, 2026 - 08:00 PM",
                    entryFee = 50,
                    prizePool = 25000,
                    totalSlots = 48,
                    remainingSlots = 0,
                    rules = "",
                    winnerName = "VactorX_Boss",
                    winnerUid = "FF-9938827",
                    status = "Closed"
                ),
                Tournament(
                    name = "Free Fire Kalahari Solo Clash",
                    map = "Kalahari",
                    mode = "Solo",
                    dateTime = "July 12, 2026 - 06:00 PM",
                    entryFee = 10,
                    prizePool = 8000,
                    totalSlots = 100,
                    remainingSlots = 0,
                    rules = "",
                    winnerName = "Bullsai_Pro",
                    winnerUid = "FF-1029348",
                    status = "Closed"
                )
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(sampleFinished) { tournament ->
                    ResultCard(tournament = tournament)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(finishedTournaments) { tournament ->
                    ResultCard(tournament = tournament)
                }
            }
        }
    }
}

@Composable
fun ResultCard(tournament: Tournament) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(2.dp, Brush.horizontalGradient(listOf(DarkGold, GamingRed)), RoundedCornerShape(12.dp)),
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
                        text = "${tournament.map.uppercase()} • ${tournament.mode.uppercase()} • Finished",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                    )
                }

                Box(
                    modifier = Modifier
                        .background(MetallicGold.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = "CONCLUDED",
                        color = MetallicGold,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Divider(color = SurfaceBorder)
            Spacer(modifier = Modifier.height(12.dp))

            // Champion banner
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(listOf(Color.Black, GamingRed.copy(alpha = 0.3f))),
                        RoundedCornerShape(8.dp)
                    )
                    .border(1.dp, MetallicGold.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(MetallicGold, RoundedCornerShape(22.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.EmojiEvents,
                        contentDescription = "Champion Trophy",
                        tint = Black,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "CHAMPION BOOYAH!",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MetallicGold,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 1.sp
                        )
                    )
                    Text(
                        text = tournament.winnerName.ifBlank { "Unassigned Winner" },
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = White,
                            fontWeight = FontWeight.Black
                        )
                    )
                    Text(
                        text = "UID: ${tournament.winnerUid.ifBlank { "N/A" }}",
                        style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total Season Prize: ₹${tournament.prizePool}",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MetallicGold, fontWeight = FontWeight.Bold)
                )
                Text(
                    text = "Date: ${tournament.dateTime.split("-").first().trim()}",
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary)
                )
            }
        }
    }
}
