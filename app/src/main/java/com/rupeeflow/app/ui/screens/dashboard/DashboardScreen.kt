package com.rupeeflow.app.ui.screens.dashboard

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rupeeflow.app.ui.components.AddTransactionBottomSheet
import com.rupeeflow.app.ui.components.BalanceCard
import com.rupeeflow.app.ui.components.PartyDetailBottomSheet
import com.rupeeflow.app.ui.components.TransactionItem
import com.rupeeflow.app.ui.theme.Emerald400
import com.rupeeflow.app.ui.theme.Indigo500
import com.rupeeflow.app.ui.theme.Indigo600
import com.rupeeflow.app.ui.theme.Slate400
import com.rupeeflow.app.ui.theme.Slate700
import com.rupeeflow.app.ui.theme.Slate800
import com.rupeeflow.app.ui.theme.Slate900
import com.rupeeflow.app.ui.theme.Slate950

@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToImport: () -> Unit,
    onNavigateToTransactions: () -> Unit,
    onPartyDrillDown: (String) -> Unit
) {
    val summary by viewModel.financialSummary.collectAsState()
    val transactions by viewModel.allTransactions.collectAsState()

    var showAddSheet by remember { mutableStateOf(false) }
    var selectedPartyForDrill by remember { mutableStateOf<String?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Slate950)
                .padding(horizontal = 16.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(Indigo600, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("₹", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.size(10.dp))
                Column {
                    Text("RupeeFlow", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    Text("100% Offline • Alpha 1", color = Emerald400, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Box(
                modifier = Modifier
                    .background(Slate900, RoundedCornerShape(12.dp))
                    .border(1.dp, Slate800, RoundedCornerShape(12.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lock, contentDescription = null, tint = Emerald400, modifier = Modifier.size(12.dp))
                    Spacer(modifier = Modifier.size(4.dp))
                    Text("Private", color = Emerald400, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Check if database is empty (Fresh install state)
        if (transactions.isEmpty()) {
            // First Launch / Onboarding Empty State Prompt
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(Indigo600.copy(alpha = 0.2f), RoundedCornerShape(22.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.UploadFile,
                        contentDescription = "Import",
                        tint = Indigo500,
                        modifier = Modifier.size(38.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = "Welcome to RupeeFlow",
                    color = Color.White,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Your private, 100% offline expense and earnings tracker. No cloud accounts, no internet required. Your financial data stays entirely on your device.",
                    color = Slate400,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 18.sp,
                    modifier = Modifier.padding(horizontal = 12.dp)
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Primary Action: Import Statement
                Button(
                    onClick = onNavigateToImport,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.UploadFile, contentDescription = null, tint = Color.White)
                        Spacer(modifier = Modifier.size(8.dp))
                        Text(
                            text = "Import Your First Statement",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Secondary Action: Add Manual Entry
                OutlinedButton(
                    onClick = { showAddSheet = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Slate700)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Add, contentDescription = null, tint = Slate400)
                        Spacer(modifier = Modifier.size(6.dp))
                        Text(
                            text = "+ Add Manual Transaction",
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 13.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Supported Banks Note
                Text(
                    text = "Supported statement formats:\nKotak • HDFC • SBI • ICICI • PhonePe • Google Pay • Generic UPI",
                    color = Slate400.copy(alpha = 0.7f),
                    fontSize = 10.sp,
                    textAlign = TextAlign.Center,
                    lineHeight = 16.sp
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Main Balance Card
                item {
                    BalanceCard(summary = summary)
                }

                // Financial Health Score & Quick Actions
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Health Score Box
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(Slate900, RoundedCornerShape(16.dp))
                                .border(1.dp, Slate800, RoundedCornerShape(16.dp))
                                .padding(12.dp)
                        ) {
                            Column {
                                Text("FINANCIAL HEALTH", color = Slate400, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(verticalAlignment = Alignment.Bottom) {
                                    Text(
                                        text = "${summary.healthScore}",
                                        color = if (summary.healthScore >= 70) Emerald400 else Color(0xFFFBBF24),
                                        fontSize = 22.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text("/100", color = Slate400, fontSize = 11.sp, modifier = Modifier.padding(bottom = 2.dp))
                                }
                                Text(
                                    text = "Savings Rate: ${String.format("%.1f", summary.savingsRate)}%",
                                    color = Emerald400,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        // Quick Add Button
                        Button(
                            onClick = { showAddSheet = true },
                            modifier = Modifier
                                .weight(1f)
                                .height(72.dp),
                            shape = RoundedCornerShape(16.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Add, contentDescription = null, tint = Color.White)
                                Spacer(modifier = Modifier.size(6.dp))
                                Text("+ Add Entry", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }
                    }
                }

                // Recent Transactions Header
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "RECENT TRANSACTIONS",
                            color = Slate400,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                        Text(
                            text = "View All (${transactions.size})",
                            color = Indigo500,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier.padding(4.dp)
                        )
                    }
                }

                // Recent transactions list
                val recent = transactions.take(10)
                items(recent, key = { it.id }) { tx ->
                    TransactionItem(
                        transaction = tx,
                        onPartyClick = { selectedPartyForDrill = it },
                        onDeleteClick = { viewModel.deleteTransaction(it) }
                    )
                }

                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
            }
        }
    }

    // Add Sheet
    if (showAddSheet) {
        AddTransactionBottomSheet(
            onDismiss = { showAddSheet = false },
            onSave = { newTx ->
                viewModel.addTransaction(newTx)
            },
            categories = listOf("Food & Dining", "Groceries", "Shopping", "Bills & UPI", "Travel", "Salary / Income", "Other")
        )
    }

    // Party Drill Bottom Sheet
    selectedPartyForDrill?.let { partyName ->
        val partyTxs = transactions.filter { it.title.equals(partyName, ignoreCase = true) }
        val cr = partyTxs.filter { it.type == com.rupeeflow.app.domain.model.TransactionType.EARNING }.sumOf { it.amount }
        val dr = partyTxs.filter { it.type == com.rupeeflow.app.domain.model.TransactionType.EXPENSE }.sumOf { it.amount }
        val summaryModel = com.rupeeflow.app.domain.model.PartySummary(
            name = partyName.uppercase(),
            creditTotal = cr,
            debitTotal = dr,
            creditCount = partyTxs.count { it.type == com.rupeeflow.app.domain.model.TransactionType.EARNING },
            debitCount = partyTxs.count { it.type == com.rupeeflow.app.domain.model.TransactionType.EXPENSE },
            totalCount = partyTxs.size,
            firstDate = partyTxs.minOfOrNull { it.date } ?: 0L,
            lastDate = partyTxs.maxOfOrNull { it.date } ?: 0L,
            category = partyTxs.firstOrNull()?.category ?: "Other"
        )

        PartyDetailBottomSheet(
            party = summaryModel,
            transactions = partyTxs,
            onFilterFeed = {
                selectedPartyForDrill = null
                onPartyDrillDown(it)
            },
            onDismiss = { selectedPartyForDrill = null }
        )
    }
}
