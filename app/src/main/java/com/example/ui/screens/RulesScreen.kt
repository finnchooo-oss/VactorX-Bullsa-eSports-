package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun RulesScreen(
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(JetBlack)
            .verticalScroll(scrollState)
    ) {
        // Rules Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(GamingRed.copy(alpha = 0.3f), JetBlack)))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "OFFICIAL RULEBOOK",
                    style = MaterialTheme.typography.displayMedium.copy(color = MetallicGold)
                )
                Text(
                    text = "VactorX Fair Play & Tournament Regulations",
                    color = TextSecondary,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "ANTI-CHEAT & INTEGRITY POLICIES",
                style = MaterialTheme.typography.titleMedium.copy(color = NeonRed, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .border(1.dp, GamingRed.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = CardBackground)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    RuleAlertRow(icon = Icons.Default.Security, text = "Zero Tolerance: Any player using modifications, scripting, hacks, or bypass tools will receive an immediate lifetime ban.")
                    Spacer(modifier = Modifier.height(10.dp))
                    RuleAlertRow(icon = Icons.Default.Block, text = "No Teaming Up: Intentional teaming in Solo modes will result in immediate match disqualification and point forfeiture.")
                    Spacer(modifier = Modifier.height(10.dp))
                    RuleAlertRow(icon = Icons.Default.Shield, text = "Device Integrity: Standard mobile matches must be played on standard iOS/Android handhelds. Emulator bypasses are prohibited unless explicitly specified.")
                }
            }

            Text(
                text = "GAMEPLAY CATEGORIES",
                style = MaterialTheme.typography.titleMedium.copy(color = MetallicGold, fontWeight = FontWeight.ExtraBold),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            ExpandableRuleTile(
                title = "Battle Royale (Bermuda/Kalahari/Purgatory)",
                rules = listOf(
                    "Format: Solo, Duo, or Squad standard lobby play.",
                    "Point Distribution: Booyah receives 12 points, 2nd place receives 9 points, 3rd place receives 8 points. 1 point is awarded per kill.",
                    "Discrepancies: All disputes must be filed within 10 minutes of match completion accompanied by raw video capture."
                )
            )

            ExpandableRuleTile(
                title = "Clash Squad (4v4 Brackets)",
                rules = listOf(
                    "Format: Single-elimination tree, best-of-seven rounds (first to 4 round wins).",
                    "Weapon Restrictions: Standard weapons only. Dual Vectors, grenades, and specific character active abilities may be barred depending on seasonal agreement.",
                    "Disconnects: If a player disconnects prior to Round 1 buy phase, the match is restarted. Later disconnects are played out."
                )
            )

            ExpandableRuleTile(
                title = "Custom Room Registration",
                rules = listOf(
                    "Credentials: Room ID and passcode are shared 15 minutes before launch. Players must join their designated lobby slot immediately.",
                    "Spectating: Standard streaming spectators must be pre-approved. Unauthorized specs will be booted from the room.",
                    "Substitutes: Standard squads can declare up to 1 substitute player during sign-up."
                )
            )
        }
    }
}

@Composable
fun RuleAlertRow(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Row(verticalAlignment = Alignment.Top) {
        Icon(icon, contentDescription = null, tint = MetallicGold, modifier = Modifier.size(20.dp))
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            color = TextPrimary,
            fontSize = 13.sp,
            lineHeight = 18.sp
        )
    }
}

@Composable
fun ExpandableRuleTile(title: String, rules: List<String>) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
            .clickable { expanded = !expanded },
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    color = White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Icon(
                    imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    tint = MetallicGold
                )
            }

            if (expanded) {
                Spacer(modifier = Modifier.height(12.dp))
                rules.forEach { rule ->
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            tint = NeonRed,
                            modifier = Modifier
                                .size(14.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = rule,
                            color = TextSecondary,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )
                    }
                }
            }
        }
    }
}
