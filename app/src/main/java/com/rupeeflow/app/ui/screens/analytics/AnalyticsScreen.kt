package com.rupeeflow.app.ui.screens.analytics

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rupeeflow.app.domain.model.PartyRole
import com.rupeeflow.app.domain.model.PartySummary
import com.rupeeflow.app.domain.model.TimeGranularity
import com.rupeeflow.app.domain.model.TransactionType
import com.rupeeflow.app.ui.components.PartyCard
import com.rupeeflow.app.ui.components.PartyDetailBottomSheet
import com.rupeeflow.app.ui.components.TimeBucketCard
import com.rupeeflow.app.ui.theme.Emerald400
import com.rupeeflow.app.ui.theme.Indigo500
import com.rupeeflow.app.ui.theme.Indigo600
import com.rupeeflow.app.ui.theme.Rose400
import com.rupeeflow.app.ui.theme.Slate400
import com.rupeeflow.app.ui.theme.Slate700
import com.rupeeflow.app.ui.theme.Slate800
import com.rupeeflow.app.ui.theme.Slate900
import com.rupeeflow.app.ui.theme.Slate950
import com.rupeeflow.app.ui.theme.Violet600
import com.rupeeflow.app.util.CurrencyFormatter

@Composable
fun AnalyticsScreen(
    viewModel: AnalyticsViewModel,
    onPartyDrillDown: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val scopedTxs by viewModel.scopedTransactions.collectAsState()
    val parties by viewModel.collatedParties.collectAsState()
    val buckets by viewModel.timeBuckets.collectAsState()

    var selectedPartyForDrill by remember { mutableStateOf<PartySummary?>(null) }

    val filteredCr = scopedTxs.filter { it.type == TransactionType.EARNING }.sumOf { it.amount }
    val filteredDr = scopedTxs.filter { it.type == TransactionType.EXPENSE }.sumOf { it.amount }
    val filteredNet = filteredCr - filteredDr

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "DETAILED STATEMENT ANALYSIS",
                    color = Indigo500,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Day/Week/Month/Year & Collated Counterparties",
                    color = Slate400,
                    fontSize = 10.sp
                )
            }

            IconButton(
                onClick = { viewModel.resetFilters() },
                modifier = Modifier
                    .background(Slate800, RoundedCornerShape(10.dp))
                    .size(36.dp)
            ) {
                Icon(Icons.Default.Refresh, contentDescription = "Reset", tint = Slate400, modifier = Modifier.size(18.dp))
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 4-Stat Filtered Scope Strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Slate900, RoundedCornerShape(16.dp))
                .border(1.dp, Slate800, RoundedCornerShape(16.dp))
                .padding(10.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Text("Filtered Cr", color = Slate400, fontSize = 9.sp)
                Text("+${CurrencyFormatter.formatINR(filteredCr)}", color = Emerald400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Text("Filtered Dr", color = Slate400, fontSize = 9.sp)
                Text("-${CurrencyFormatter.formatINR(filteredDr)}", color = Rose400, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Text("Net Flow", color = Slate400, fontSize = 9.sp)
                Text(
                    CurrencyFormatter.formatSignedINR(filteredNet),
                    color = if (filteredNet >= 0) Emerald400 else Rose400,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.weight(1f)) {
                Text("Unique Names", color = Slate400, fontSize = 9.sp)
                Text("${parties.size}", color = Indigo500, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 3 Sub-Tabs
        TabRow(
            selectedTabIndex = uiState.activeSubTab,
            containerColor = Slate900,
            contentColor = Color.White,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[uiState.activeSubTab]),
                    color = Indigo600
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .background(Slate900, RoundedCornerShape(14.dp))
        ) {
            Tab(
                selected = uiState.activeSubTab == 0,
                onClick = { viewModel.setSubTab(0) },
                text = { Text("📅 Time Granularity", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = uiState.activeSubTab == 1,
                onClick = { viewModel.setSubTab(1) },
                text = { Text("🏆 Top Dr & Cr", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = uiState.activeSubTab == 2,
                onClick = { viewModel.setSubTab(2) },
                text = { Text("👥 All Names", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Tab Content
        when (uiState.activeSubTab) {
            // TAB 0: Time Granularity
            0 -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Granularity Selector
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Slate900, RoundedCornerShape(12.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        TimeGranularity.entries.forEach { gran ->
                            val isSelected = uiState.timeGranularity == gran
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (isSelected) Violet600 else Color.Transparent, RoundedCornerShape(10.dp))
                                    .clickable { viewModel.setGranularity(gran) }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = gran.title,
                                    color = if (isSelected) Color.White else Slate400,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(buckets, key = { it.key }) { bucket ->
                            TimeBucketCard(
                                bucket = bucket,
                                onPartyClick = { partyName ->
                                    val match = parties.firstOrNull { it.name.equals(partyName, ignoreCase = true) }
                                    if (match != null) selectedPartyForDrill = match
                                }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(30.dp)) }
                    }
                }
            }

            // TAB 1: Top Dr & Cr Leaderboards
            1 -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Top Creditors
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Slate900, RoundedCornerShape(16.dp))
                                .border(1.dp, Emerald400.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "↓ TOP CREDITORS / SENDERS (MONEY IN)",
                                color = Emerald400,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            val topCr = parties.filter { it.creditTotal > 0 }
                                .sortedByDescending { it.creditTotal }
                                .take(8)

                            if (topCr.isEmpty()) {
                                Text("No creditor earnings recorded yet.", color = Slate400, fontSize = 11.sp)
                            } else {
                                topCr.forEach { p ->
                                    PartyCard(party = p, onClick = { selectedPartyForDrill = p })
                                }
                            }
                        }
                    }

                    // Top Debitors
                    item {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(Slate900, RoundedCornerShape(16.dp))
                                .border(1.dp, Rose400.copy(alpha = 0.2f), RoundedCornerShape(16.dp))
                                .padding(12.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "↑ TOP DEBITORS / RECEIVERS (MONEY OUT)",
                                color = Rose400,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            val topDr = parties.filter { it.debitTotal > 0 }
                                .sortedByDescending { it.debitTotal }
                                .take(8)

                            if (topDr.isEmpty()) {
                                Text("No debitor expenses recorded yet.", color = Slate400, fontSize = 11.sp)
                            } else {
                                topDr.forEach { p ->
                                    PartyCard(party = p, onClick = { selectedPartyForDrill = p })
                                }
                            }
                        }
                    }

                    item { Spacer(modifier = Modifier.height(30.dp)) }
                }
            }

            // TAB 2: All Names Collated Directory
            2 -> {
                Column(modifier = Modifier.fillMaxSize()) {
                    // Role Filter Buttons
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Slate900, RoundedCornerShape(12.dp))
                            .padding(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        PartyRole.entries.forEach { role ->
                            val isSelected = uiState.partyRoleFilter == role
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .background(if (isSelected) Indigo600 else Color.Transparent, RoundedCornerShape(10.dp))
                                    .clickable { viewModel.setPartyRoleFilter(role) }
                                    .padding(vertical = 6.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = role.displayName,
                                    color = if (isSelected) Color.White else Slate400,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Party Search Box
                    OutlinedTextField(
                        value = uiState.partyDirectorySearch,
                        onValueChange = { viewModel.setPartyDirectorySearch(it) },
                        placeholder = { Text("Filter counterparty name...", color = Slate400, fontSize = 11.sp) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Slate400, modifier = Modifier.size(16.dp)) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Indigo600,
                            unfocusedBorderColor = Slate700,
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedContainerColor = Slate900,
                            unfocusedContainerColor = Slate900
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(parties, key = { it.name }) { party ->
                            PartyCard(
                                party = party,
                                onClick = { selectedPartyForDrill = party }
                            )
                        }
                        item { Spacer(modifier = Modifier.height(30.dp)) }
                    }
                }
            }
        }
    }

    // Party Drill Down Bottom Sheet
    selectedPartyForDrill?.let { party ->
        val partyTxs = scopedTxs.filter { it.title.equals(party.name, ignoreCase = true) }
        PartyDetailBottomSheet(
            party = party,
            transactions = partyTxs,
            onFilterFeed = {
                selectedPartyForDrill = null
                onPartyDrillDown(it)
            },
            onDismiss = { selectedPartyForDrill = null }
        )
    }
}
