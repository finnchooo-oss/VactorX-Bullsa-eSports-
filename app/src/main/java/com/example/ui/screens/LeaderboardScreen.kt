package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
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
import com.example.data.database.LeaderboardUser
import com.example.ui.TournamentViewModel
import com.example.ui.theme.*

@Composable
fun LeaderboardScreen(
    viewModel: TournamentViewModel,
    modifier: Modifier = Modifier
) {
    val leaderboard by viewModel.leaderboard.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(JetBlack)
    ) {
        // Leaderboard Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(GamingRed.copy(alpha = 0.3f), JetBlack)))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "HALL OF FAME",
                    style = MaterialTheme.typography.displayMedium.copy(color = MetallicGold)
                )
                Text(
                    text = "VactorX Elite Season Standings",
                    color = TextSecondary,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }

        if (leaderboard.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = MetallicGold)
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Top 3 Podium
                item {
                    PodiumSection(leaderboard = leaderboard)
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Table Header
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(CardBackground, RoundedCornerShape(4.dp))
                            .padding(vertical = 8.dp, horizontal = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "Rk", color = TextSecondary, fontWeight = FontWeight.Bold, modifier = Modifier.width(30.dp), fontSize = 12.sp)
                        Text(text = "Player / IGN", color = TextSecondary, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), fontSize = 12.sp)
                        Text(text = "Mat", color = TextSecondary, fontWeight = FontWeight.Bold, modifier = Modifier.width(40.dp), textAlign = TextAlign.Center, fontSize = 12.sp)
                        Text(text = "Kills", color = TextSecondary, fontWeight = FontWeight.Bold, modifier = Modifier.width(45.dp), textAlign = TextAlign.Center, fontSize = 12.sp)
                        Text(text = "Pts", color = MetallicGold, fontWeight = FontWeight.Bold, modifier = Modifier.width(55.dp), textAlign = TextAlign.End, fontSize = 12.sp)
                    }
                }

                // Other ranks
                itemsIndexed(leaderboard) { index, player ->
                    LeaderboardRow(rank = index + 1, player = player)
                }
            }
        }
    }
}

@Composable
fun PodiumSection(leaderboard: List<LeaderboardUser>) {
    val top1 = leaderboard.getOrNull(0)
    val top2 = leaderboard.getOrNull(1)
    val top3 = leaderboard.getOrNull(2)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.Bottom
    ) {
        // Rank 2 (Left)
        if (top2 != null) {
            PodiumColumn(player = top2, rank = 2, height = 110.dp, badgeColor = Color(0xFFC0C0C0), modifier = Modifier.weight(1f))
        }

        // Rank 1 (Center - Tallest)
        if (top1 != null) {
            PodiumColumn(player = top1, rank = 1, height = 140.dp, badgeColor = MetallicGold, modifier = Modifier.weight(1.1f))
        }

        // Rank 3 (Right)
        if (top3 != null) {
            PodiumColumn(player = top3, rank = 3, height = 95.dp, badgeColor = Color(0xFFCD7F32), modifier = Modifier.weight(1f))
        }
    }
}

@Composable
fun PodiumColumn(
    player: LeaderboardUser,
    rank: Int,
    height: androidx.compose.ui.unit.Dp,
    badgeColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Trophy Icon with Crown/Badge color
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(badgeColor.copy(alpha = 0.15f), RoundedCornerShape(21.dp))
                .border(2.dp, badgeColor, RoundedCornerShape(21.dp)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "#$rank",
                color = badgeColor,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = player.playerName,
            color = White,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            textAlign = TextAlign.Center,
            maxLines = 1
        )
        Text(
            text = "Kills: ${player.kills}",
            color = TextSecondary,
            fontSize = 10.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Visual podium block
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(badgeColor.copy(alpha = 0.3f), CardBackground)
                    ),
                    RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                )
                .border(1.dp, SurfaceBorder, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .padding(8.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "${player.points}",
                    style = MaterialTheme.typography.titleLarge.copy(color = badgeColor, fontWeight = FontWeight.Black)
                )
                Text(
                    text = "POINTS",
                    style = MaterialTheme.typography.labelSmall.copy(color = TextSecondary, fontSize = 9.sp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .background(GamingRed.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${player.booyahs} BOOYAH",
                        color = NeonRed,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun LeaderboardRow(rank: Int, player: LeaderboardUser) {
    val highlightColor = when (rank) {
        1 -> MetallicGold
        2 -> Color(0xFFC0C0C0)
        3 -> Color(0xFFCD7F32)
        else -> TextPrimary
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardBackground, RoundedCornerShape(8.dp))
            .border(1.dp, if (rank <= 3) highlightColor.copy(alpha = 0.3f) else SurfaceBorder, RoundedCornerShape(8.dp))
            .padding(vertical = 12.dp, horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Rank No
        Text(
            text = rank.toString().padStart(2, '0'),
            color = highlightColor,
            fontWeight = FontWeight.Black,
            modifier = Modifier.width(30.dp),
            fontSize = 14.sp
        )

        // Player Name/UID
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = player.playerName,
                color = White,
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold)
            )
            Text(
                text = "UID: ${player.inGameId}",
                color = TextSecondary,
                style = MaterialTheme.typography.labelSmall
            )
        }

        // Matches Played
        Text(
            text = player.matchesPlayed.toString(),
            color = TextPrimary,
            modifier = Modifier.width(40.dp),
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        // Kills
        Text(
            text = player.kills.toString(),
            color = TextPrimary,
            modifier = Modifier.width(45.dp),
            textAlign = TextAlign.Center,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        // Total Points
        Text(
            text = "${player.points} Pts",
            color = MetallicGold,
            modifier = Modifier.width(55.dp),
            textAlign = TextAlign.End,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black
        )
    }
}
