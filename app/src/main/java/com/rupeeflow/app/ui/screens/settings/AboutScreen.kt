package com.rupeeflow.app.ui.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rupeeflow.app.ui.theme.Emerald400
import com.rupeeflow.app.ui.theme.Indigo500
import com.rupeeflow.app.ui.theme.Indigo600
import com.rupeeflow.app.ui.theme.Slate400
import com.rupeeflow.app.ui.theme.Slate800
import com.rupeeflow.app.ui.theme.Slate900
import com.rupeeflow.app.ui.theme.Slate950

@Composable
fun AboutScreen(
    onNavigateBack: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // Top Bar
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .background(Slate800, RoundedCornerShape(10.dp))
                    .size(36.dp)
            ) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White, modifier = Modifier.size(18.dp))
            }
            Spacer(modifier = Modifier.size(12.dp))
            Text("About RupeeFlow", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        // App Badge
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Slate900, RoundedCornerShape(20.dp))
                .border(1.dp, Slate800, RoundedCornerShape(20.dp))
                .padding(20.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .background(Indigo600, RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("₹", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text("RupeeFlow", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("v0.1.0-alpha.1 (GitHub Release)", color = Emerald400, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Text("Version Code: 1 • 100% Offline Architecture", color = Slate400, fontSize = 11.sp, modifier = Modifier.padding(top = 4.dp))
            }
        }

        // Architecture Highlights
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Slate900, RoundedCornerShape(16.dp))
                .border(1.dp, Slate800, RoundedCornerShape(16.dp))
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text("NATIVE ARCHITECTURE HIGHLIGHTS", color = Indigo500, fontSize = 11.sp, fontWeight = FontWeight.Bold)

            AboutPoint("🔒 100% Offline & Private", "Zero network permissions requested. No analytics, tracking, or cloud sync.")
            AboutPoint("⚡ Room SQLite Engine", "Full ACID database with indexed queries supporting 100,000+ local transactions.")
            AboutPoint("📂 Storage Access Framework (SAF)", "Direct on-device CSV/TXT/TSV file selection without storage permissions.")
            AboutPoint("🏦 Multi-Bank & UPI Parsers", "Built-in parsing engines for Kotak, HDFC, SBI, ICICI, PhonePe, and Google Pay.")
            AboutPoint("📊 Collated Analytics Engine", "Real-time two-way sender/receiver reconciliation and time-wise aggregations.")
        }
    }
}

@Composable
private fun AboutPoint(title: String, desc: String) {
    Column {
        Text(title, color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Text(desc, color = Slate400, fontSize = 11.sp)
    }
}
