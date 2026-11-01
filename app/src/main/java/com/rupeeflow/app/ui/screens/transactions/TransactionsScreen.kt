package com.rupeeflow.app.ui.screens.transactions

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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.rupeeflow.app.ui.components.AddTransactionBottomSheet
import com.rupeeflow.app.ui.components.FilterChipRow
import com.rupeeflow.app.ui.components.PartyDetailBottomSheet
import com.rupeeflow.app.ui.components.TransactionItem
import com.rupeeflow.app.ui.theme.Indigo500
import com.rupeeflow.app.ui.theme.Indigo600
import com.rupeeflow.app.ui.theme.Rose400
import com.rupeeflow.app.ui.theme.Slate400
import com.rupeeflow.app.ui.theme.Slate700
import com.rupeeflow.app.ui.theme.Slate800
import com.rupeeflow.app.ui.theme.Slate900
import com.rupeeflow.app.ui.theme.Slate950

@Composable
fun TransactionsScreen(
    viewModel: TransactionsViewModel,
    onPartySelected: (String) -> Unit
) {
    val transactions by viewModel.filteredTransactions.collectAsState()
    val filterState by viewModel.filterState.collectAsState()

    var showAddSheet by remember { mutableStateOf(false) }
    var showClearDialog by remember { mutableStateOf(false) }
    var selectedPartyForDrill by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Slate950)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(14.dp))

            // Screen Title & Clear Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TRANSACTION FEED",
                        color = Indigo500,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "Showing ${transactions.size} records",
                        color = Slate400,
                        fontSize = 11.sp
                    )
                }

                IconButton(
                    onClick = { showClearDialog = true },
                    modifier = Modifier
                        .background(Slate800, RoundedCornerShape(10.dp))
                        .size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Clear All",
                        tint = Rose400,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = filterState.searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                placeholder = { Text("Search merchant (Wipro, Amazon, Sachin)...", color = Slate400, fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Slate400) },
                trailingIcon = {
                    if (filterState.searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Slate400)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
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

            // Filter Chips
            FilterChipRow(
                selectedFilter = filterState.activeFilter,
                onFilterSelected = { viewModel.updateActiveFilter(it) }
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Transactions Feed List
            if (transactions.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Slate900.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .border(1.dp, Slate800, RoundedCornerShape(16.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("No transactions match current filters", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Tap below to reset filters or clear search query", color = Slate400, fontSize = 11.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        TextButton(onClick = { viewModel.resetFilters() }) {
                            Text("Reset Filters", color = Indigo500, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(transactions, key = { it.id }) { tx ->
                        TransactionItem(
                            transaction = tx,
                            onPartyClick = { selectedPartyForDrill = it },
                            onDeleteClick = { viewModel.deleteTransaction(it) }
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }

        // Floating Action Button to Add Entry
        FloatingActionButton(
            onClick = { showAddSheet = true },
            containerColor = Indigo600,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Transaction")
        }
    }

    // Add Transaction Bottom Sheet
    if (showAddSheet) {
        AddTransactionBottomSheet(
            onDismiss = { showAddSheet = false },
            onSave = { viewModel.addTransaction(it) },
            categories = listOf("Food & Dining", "Groceries", "Shopping", "Bills & UPI", "Travel", "Salary / Income", "Other")
        )
    }

    // Clear All Confirmation Dialog
    if (showClearDialog) {
        AlertDialog(
            onDismissRequest = { showClearDialog = false },
            title = { Text("Clear all transactions?", color = Color.White, fontWeight = FontWeight.Bold) },
            text = { Text("This will permanently remove all transactions from local storage.", color = Slate400) },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.clearAllTransactions()
                    showClearDialog = false
                }) {
                    Text("Yes, Clear All", color = Rose400, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearDialog = false }) {
                    Text("Cancel", color = Slate400)
                }
            },
            containerColor = Slate900
        )
    }

    // Party Drill Down Sheet
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
                viewModel.updatePartyFilter(it)
            },
            onDismiss = { selectedPartyForDrill = null }
        )
    }
}
