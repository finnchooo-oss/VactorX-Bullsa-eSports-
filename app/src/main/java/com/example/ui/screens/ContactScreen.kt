package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun ContactScreen(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Support Form states
    var name by remember { mutableStateOf("") }
    var uid by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var queryDetails by remember { mutableStateOf("") }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(JetBlack)
            .verticalScroll(scrollState)
    ) {
        // Contact Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.verticalGradient(listOf(GamingRed.copy(alpha = 0.3f), JetBlack)))
                .padding(16.dp)
        ) {
            Column {
                Text(
                    text = "VACTORX HELPDESK",
                    style = MaterialTheme.typography.displayMedium.copy(color = MetallicGold)
                )
                Text(
                    text = "Official Support FAQs & Help Tickets",
                    color = TextSecondary,
                    style = MaterialTheme.typography.titleSmall
                )
            }
        }

        Column(modifier = Modifier.padding(16.dp)) {
            // General support info
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(CardBackground, RoundedCornerShape(8.dp))
                    .border(1.dp, SurfaceBorder, RoundedCornerShape(8.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.SupportAgent, contentDescription = null, tint = MetallicGold, modifier = Modifier.size(32.dp))
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("24/7 WHATSAPP SUPPORT", color = White, fontWeight = FontWeight.Bold)
                    Text("Reach our helpdesk at: +91 98765 43210", color = TextSecondary, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Help Desk Ticket Form
            Text(
                text = "SUBMIT A SUPPORT TICKET",
                style = MaterialTheme.typography.titleMedium.copy(color = MetallicGold, fontWeight = FontWeight.ExtraBold),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            CustomOutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = "Full Name",
                icon = Icons.Default.Person,
                tag = "contact_name"
            )

            CustomOutlinedTextField(
                value = uid,
                onValueChange = { uid = it },
                label = "Free Fire UID",
                icon = Icons.Default.Tag,
                tag = "contact_uid"
            )

            CustomOutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = "Email Address",
                icon = Icons.Default.Email,
                tag = "contact_email"
            )

            OutlinedTextField(
                value = queryDetails,
                onValueChange = { queryDetails = it },
                label = { Text("Query / Concern Details", color = TextSecondary) },
                leadingIcon = { Icon(Icons.Default.Message, contentDescription = null, tint = MetallicGold) },
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
                    .height(110.dp)
                    .padding(vertical = 6.dp)
                    .testTag("contact_message_input")
            )

            Spacer(modifier = Modifier.height(12.dp))

            Button(
                onClick = {
                    if (name.isBlank() || email.isBlank() || queryDetails.isBlank()) {
                        Toast.makeText(context, "Please fill in Name, Email and Query details.", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Ticket submitted successfully! Ticket ID: VTX-${(1000..9999).random()}", Toast.LENGTH_LONG).show()
                        name = ""
                        uid = ""
                        email = ""
                        queryDetails = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = GamingRed),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("submit_ticket_button")
            ) {
                Text("SUBMIT TICKET", color = White, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(24.dp))

            // FAQs Section
            Text(
                text = "FREQUENTLY ASKED QUESTIONS",
                style = MaterialTheme.typography.titleMedium.copy(color = MetallicGold, fontWeight = FontWeight.ExtraBold),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            ExpandableRuleTile(
                title = "How long does payment approval take?",
                rules = listOf("All payment receipt screenshots are checked manually by admins.", "Verification typically takes between 10 to 30 minutes during active peak hours (04:00 PM to 10:00 PM).")
            )

            ExpandableRuleTile(
                title = "Where do I get the Room ID and Password?",
                rules = listOf("Once your registration is APPROVED by our team, navigate to the My Registrations tab.", "If approved, your tournament card will dynamically reveal a glowing credentials container containing the Room ID and Password 15 minutes prior to the scheduled launch.")
            )

            ExpandableRuleTile(
                title = "How are prizes distributed?",
                rules = listOf("After match results are finalized and winners are declared, points are immediately added to the Leaderboard.", "The corresponding monetary prize is transferred directly to the captain's declared GPay/PhonePe or WhatsApp UPI account within 2 hours of match finish.")
            )
        }
    }
}
