package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.database.Tournament
import com.example.ui.Screen
import com.example.ui.TournamentViewModel
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrationFormScreen(
    viewModel: TournamentViewModel,
    tournamentId: Int,
    onSuccess: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tournaments by viewModel.tournaments.collectAsState()
    val tournament = tournaments.find { it.id == tournamentId }

    if (tournament == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(JetBlack),
            contentAlignment = Alignment.Center
        ) {
            Text("Tournament not found", color = NeonRed, style = MaterialTheme.typography.titleMedium)
        }
        return
    }

    // Form states
    var playerName by remember { mutableStateOf(viewModel.loggedInUserInGameName.ifBlank { "" }) }
    var freeFireUid by remember { mutableStateOf(viewModel.loggedInUserUid.ifBlank { "" }) }
    var inGameName by remember { mutableStateOf(viewModel.loggedInUserInGameName.ifBlank { "" }) }
    var mobileNumber by remember { mutableStateOf(viewModel.loggedInUserMobile.ifBlank { "" }) }
    var whatsAppNumber by remember { mutableStateOf(viewModel.loggedInUserMobile.ifBlank { "" }) }
    var teamName by remember { mutableStateOf("") }
    var captainName by remember { mutableStateOf("") }
    var stateRegion by remember { mutableStateOf("") }
    var paymentScreenshot by remember { mutableStateOf("") }
    var acceptRules by remember { mutableStateOf(false) }

    var showError by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf("") }

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(JetBlack)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Back Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { viewModel.navigateTo(Screen.Tournaments) }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = MetallicGold)
            }
            Text(
                text = "SECURE ENTRY FORM",
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MetallicGold,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        // Tournament Summary Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 20.dp)
                .border(1.dp, GamingRed.copy(alpha = 0.5f), RoundedCornerShape(12.dp)),
            colors = CardDefaults.cardColors(containerColor = CardBackground)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = tournament.name,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = MetallicGold,
                        fontWeight = FontWeight.Black
                    )
                )
                Text(
                    text = "${tournament.map.uppercase()} • ${tournament.mode.uppercase()} MATCH",
                    color = NeonRed,
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Entry Fee: ₹${tournament.entryFee}", color = TextPrimary, fontWeight = FontWeight.Bold)
                    Text("Prize Pool: ₹${tournament.prizePool}", color = MetallicGold, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Payment QR Mock Section if entry fee > 0
        if (tournament.entryFee > 0) {
            Text(
                text = "STEP 1: SCAN & PAY ENTRY FEE",
                style = MaterialTheme.typography.labelLarge.copy(color = MetallicGold, fontWeight = FontWeight.ExtraBold),
                modifier = Modifier.padding(bottom = 8.dp)
            )

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(12.dp)),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.6f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Simulated QR code using Canvas/Icons
                    Box(
                        modifier = Modifier
                            .size(160.dp)
                            .background(White, RoundedCornerShape(8.dp))
                            .border(4.dp, MetallicGold, RoundedCornerShape(8.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.QrCode2, contentDescription = "Mock QR", tint = Black, modifier = Modifier.size(110.dp))
                            Text("VactorX UPI", color = GamingRed, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "UPI ID: pay.vactorx@upi",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = MetallicGold,
                            fontWeight = FontWeight.Bold
                        )
                    )
                    Text(
                        text = "Scan QR with GPay, PhonePe, or Paytm and pay ₹${tournament.entryFee}",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodySmall,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            paymentScreenshot = "MOCK_PAYMENT_SLIP_${(100000..999999).random()}.PNG"
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = if (paymentScreenshot.isNotEmpty()) Color.DarkGray else GamingRed),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Icon(Icons.Default.CloudUpload, contentDescription = null, tint = White)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (paymentScreenshot.isNotEmpty()) "SLIP UPLOADED ✓" else "SIMULATE RECEIPT UPLOAD",
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (paymentScreenshot.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Selected file: $paymentScreenshot",
                            color = Color.Green,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }

        // Player Details form
        Text(
            text = if (tournament.entryFee > 0) "STEP 2: ENTER PLAYER DETAILS" else "ENTER PLAYER DETAILS",
            style = MaterialTheme.typography.labelLarge.copy(color = MetallicGold, fontWeight = FontWeight.ExtraBold),
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // Player Name
        CustomOutlinedTextField(
            value = playerName,
            onValueChange = { playerName = it },
            label = "Player Full Name",
            icon = Icons.Default.Person,
            tag = "player_name_input"
        )

        // Free Fire UID
        CustomOutlinedTextField(
            value = freeFireUid,
            onValueChange = { freeFireUid = it },
            label = "Free Fire UID",
            icon = Icons.Default.Tag,
            keyboardType = KeyboardType.Number,
            tag = "ff_uid_input"
        )

        // In-Game Name
        CustomOutlinedTextField(
            value = inGameName,
            onValueChange = { inGameName = it },
            label = "In-Game Name (IGN)",
            icon = Icons.Default.SportsEsports,
            tag = "ign_input"
        )

        // Mobile Number
        CustomOutlinedTextField(
            value = mobileNumber,
            onValueChange = { mobileNumber = it },
            label = "Mobile Number",
            icon = Icons.Default.Phone,
            keyboardType = KeyboardType.Phone,
            tag = "mobile_input"
        )

        // WhatsApp Number
        CustomOutlinedTextField(
            value = whatsAppNumber,
            onValueChange = { whatsAppNumber = it },
            label = "WhatsApp Number",
            icon = Icons.Default.Chat,
            keyboardType = KeyboardType.Phone,
            tag = "whatsapp_input"
        )

        // State/Region
        CustomOutlinedTextField(
            value = stateRegion,
            onValueChange = { stateRegion = it },
            label = "State / Region",
            icon = Icons.Default.LocationOn,
            tag = "state_input"
        )

        // Duo/Squad Specific Fields
        if (tournament.mode.equals("Duo", ignoreCase = true) || tournament.mode.equals("Squad", ignoreCase = true)) {
            Text(
                text = "TEAM INFORMATION (${tournament.mode.uppercase()})",
                style = MaterialTheme.typography.labelLarge.copy(color = MetallicGold, fontWeight = FontWeight.ExtraBold),
                modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
            )

            // Team Name
            CustomOutlinedTextField(
                value = teamName,
                onValueChange = { teamName = it },
                label = "Team Name",
                icon = Icons.Default.Groups,
                tag = "team_name_input"
            )

            // Captain Name
            CustomOutlinedTextField(
                value = captainName,
                onValueChange = { captainName = it },
                label = "Captain Name",
                icon = Icons.Default.Star,
                tag = "captain_name_input"
            )
        }

        // Accept Rules Checkbox
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = acceptRules,
                onCheckedChange = { acceptRules = it },
                colors = CheckboxDefaults.colors(
                    checkedColor = GamingRed,
                    uncheckedColor = TextSecondary,
                    checkmarkColor = White
                ),
                modifier = Modifier.testTag("rules_checkbox")
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "I accept the rules of VactorX Bullsaí eSports† and verify that all information provided is accurate.",
                style = MaterialTheme.typography.bodySmall.copy(color = TextSecondary),
                modifier = Modifier.weight(1f)
            )
        }

        if (showError) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                color = NeonRed,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }

        // Submit Button
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = {
                // Validation checks
                if (playerName.isBlank() || freeFireUid.isBlank() || inGameName.isBlank() || mobileNumber.isBlank() || whatsAppNumber.isBlank() || stateRegion.isBlank()) {
                    errorMessage = "Please fill in all player details."
                    showError = true
                } else if (tournament.entryFee > 0 && paymentScreenshot.isEmpty()) {
                    errorMessage = "Please upload payment receipt screenshot."
                    showError = true
                } else if (!acceptRules) {
                    errorMessage = "You must accept the tournament rules."
                    showError = true
                } else {
                    showError = false
                    viewModel.submitRegistration(
                        tournamentId = tournament.id,
                        tournamentName = tournament.name,
                        playerName = playerName,
                        freeFireUid = freeFireUid,
                        inGameName = inGameName,
                        mobileNumber = mobileNumber,
                        whatsAppNumber = whatsAppNumber,
                        teamName = teamName,
                        captainName = captainName,
                        stateRegion = stateRegion,
                        paymentScreenshot = paymentScreenshot,
                        onSuccess = onSuccess
                    )
                }
            },
            colors = ButtonDefaults.buttonColors(containerColor = MetallicGold),
            shape = RoundedCornerShape(6.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("submit_registration_button")
        ) {
            Text(
                text = "SUBMIT REGISTRATION",
                color = Black,
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    keyboardType: KeyboardType = KeyboardType.Text,
    tag: String
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, color = TextSecondary) },
        leadingIcon = { Icon(icon, contentDescription = null, tint = MetallicGold) },
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
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
            .padding(vertical = 6.dp)
            .testTag(tag)
    )
}
