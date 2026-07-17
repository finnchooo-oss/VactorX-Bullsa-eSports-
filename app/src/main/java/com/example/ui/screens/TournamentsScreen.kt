package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.Tournament
import com.example.ui.TournamentViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TournamentsScreen(
    viewModel: TournamentViewModel,
    onRegisterClick: (Tournament) -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredTournaments by viewModel.filteredTournaments.collectAsState()
    
    // Quick categories mapping
    val mapsList = listOf("All", "Bermuda", "Purgatory", "Kalahari", "Alpine", "Nexterra", "Clash Squad", "Custom Room")
    val modesList = listOf("All", "Solo", "Duo", "Squad", "1v1", "1v6", "4v4")

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(JetBlack)
    ) {
        // Custom Search Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(GamingRed.copy(alpha = 0.4f), JetBlack)))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "TOURNAMENT LOBBY",
                    style = MaterialTheme.typography.displayMedium.copy(color = MetallicGold),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // Search field
                TextField(
                    value = viewModel.searchQuery,
                    onValueChange = { viewModel.searchQuery = it },
                    placeholder = { Text("Search tournaments...", color = TextSecondary) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = MetallicGold) },
                    trailingIcon = {
                        if (viewModel.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.searchQuery = "" }) {
                                Icon(Icons.Default.Clear, contentDescription = null, tint = TextSecondary)
                            }
                        }
                    },
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = CardBackground,
                        unfocusedContainerColor = CardBackground,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedIndicatorColor = MetallicGold,
                        unfocusedIndicatorColor = SurfaceBorder
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .testTag("tournament_search_input")
                )
            }
        }

        // Horizontal Map Categories
        Text(
            text = "FILTER BY MAP",
            style = MaterialTheme.typography.labelSmall.copy(color = MetallicGold),
            modifier = Modifier.padding(start = 16.dp, top = 12.dp, bottom = 6.dp)
        )
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(mapsList) { mapCategory ->
                val isSelected = viewModel.filterMap == mapCategory
                Box(
                    modifier = Modifier
                        .background(
                            if (isSelected) GamingRed else CardBackground,
                            RoundedCornerShape(6.dp)
                        )
                        .border(1.dp, if (isSelected) MetallicGold else SurfaceBorder, RoundedCornerShape(6.dp))
                        .clickable { viewModel.filterMap = mapCategory }
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        text = mapCategory.uppercase(),
                        color = if (isSelected) White else TextSecondary,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        // Horizontal Mode Categories
        Text(
            text = "FILTER BY MODE",
            style = MaterialTheme.typography.labelSmall.copy(color = MetallicGold),
            modifier = Modifier.padding(start = 16.dp, top = 4.dp, bottom = 6.dp)
        )
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(modesList) { modeCategory ->
                val isSelected = viewModel.filterMode == modeCategory
                Box(
                    modifier = Modifier
                        .background(
                            if (isSelected) DarkGold else CardBackground,
                            RoundedCornerShape(6.dp)
                        )
                        .border(1.dp, if (isSelected) MetallicGold else SurfaceBorder, RoundedCornerShape(6.dp))
                        .clickable { viewModel.filterMode = modeCategory }
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = modeCategory,
                        color = if (isSelected) Black else TextSecondary,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }
            }
        }

        // Tournaments List
        if (filteredTournaments.isEmpty()) {
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.FilterListOff,
                        contentDescription = "Empty filter results",
                        tint = TextSecondary,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No Tournaments Match Your Filters",
                        color = TextPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Try adjusting your search query or filters above.",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 32.dp, vertical = 4.dp)
                    )
                    Button(
                        onClick = {
                            viewModel.searchQuery = ""
                            viewModel.filterMap = "All"
                            viewModel.filterMode = "All"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GamingRed),
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Reset All Filters")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                items(filteredTournaments) { tournament ->
                    TournamentCard(
                        tournament = tournament,
                        onRegisterClick = { onRegisterClick(tournament) }
                    )
                }
            }
        }
    }
}

@Composable
fun TournamentCard(
    tournament: Tournament,
    onRegisterClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp))
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Card Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(GamingRed.copy(alpha = 0.1f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = when(tournament.map.lowercase()) {
                            "bermuda" -> Icons.Default.Landscape
                            "kalahari" -> Icons.Default.WbSunny
                            "clash squad" -> Icons.Default.ElectricBolt
                            else -> Icons.Default.Gamepad
                        },
                        contentDescription = null,
                        tint = MetallicGold,
                        modifier = Modifier.size(24.dp)
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
                    Text(
                        text = "${tournament.map.uppercase()} • ${tournament.mode.uppercase()}",
                        style = MaterialTheme.typography.bodyMedium.copy(color = TextSecondary)
                    )
                }

                Box(
                    modifier = Modifier
                        .background(
                            if (tournament.status == "Open") GamingRed.copy(alpha = 0.15f) else Color.Gray.copy(alpha = 0.15f),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = tournament.status.uppercase(),
                        color = if (tournament.status == "Open") NeonRed else Color.Gray,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Info Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                InfoColumn(label = "PRIZE POOL", value = "₹${tournament.prizePool}", tint = MetallicGold)
                InfoColumn(label = "ENTRY FEE", value = if (tournament.entryFee == 0) "FREE" else "₹${tournament.entryFee}", tint = TextPrimary)
                InfoColumn(
                    label = "SLOTS REMAINING", 
                    value = "${tournament.remainingSlots} / ${tournament.totalSlots}", 
                    tint = if (tournament.remainingSlots < 10) NeonRed else MetallicGold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(Icons.Default.AccessTime, contentDescription = null, tint = TextSecondary, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = tournament.dateTime,
                    color = TextPrimary,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // Expandable details (Rules, Map, Register Button)
            if (expanded) {
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = SurfaceBorder)
                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "TOURNAMENT RULES & REGULATIONS",
                    style = MaterialTheme.typography.labelMedium.copy(color = MetallicGold, fontWeight = FontWeight.Bold),
                    modifier = Modifier.padding(bottom = 4.dp)
                )
                Text(
                    text = tournament.rules,
                    style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (tournament.status == "Open" && tournament.remainingSlots > 0) {
                    Button(
                        onClick = onRegisterClick,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonRed),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("register_now_card_button")
                    ) {
                        Text("REGISTER NOW FOR ₹${tournament.entryFee}", color = White, fontWeight = FontWeight.Bold)
                    }
                } else if (tournament.remainingSlots <= 0 && tournament.status == "Open") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.DarkGray.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("TOURNAMENT FULL", color = NeonRed, fontWeight = FontWeight.Bold)
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.DarkGray.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("REGISTRATION CLOSED", color = TextSecondary, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "Expand",
                        tint = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
fun InfoColumn(label: String, value: String, tint: Color) {
    Column {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                color = TextSecondary,
                fontSize = 10.sp
            )
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge.copy(
                color = tint,
                fontWeight = FontWeight.Bold
            )
        )
    }
}
