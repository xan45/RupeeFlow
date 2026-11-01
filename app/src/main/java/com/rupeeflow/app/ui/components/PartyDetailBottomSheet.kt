package com.rupeeflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rupeeflow.app.domain.model.PartySummary
import com.rupeeflow.app.domain.model.Transaction
import com.rupeeflow.app.ui.theme.Emerald400
import com.rupeeflow.app.ui.theme.Indigo500
import com.rupeeflow.app.ui.theme.Indigo600
import com.rupeeflow.app.ui.theme.Rose400
import com.rupeeflow.app.ui.theme.Slate400
import com.rupeeflow.app.ui.theme.Slate700
import com.rupeeflow.app.ui.theme.Slate800
import com.rupeeflow.app.ui.theme.Slate900
import com.rupeeflow.app.util.CurrencyFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PartyDetailBottomSheet(
    party: PartySummary,
    transactions: List<Transaction>,
    onFilterFeed: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Slate900
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 6.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header
            Column {
                Text(
                    text = "SENDER / RECEIVER DRILL-DOWN",
                    color = Indigo500,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Text(
                    text = party.name,
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${party.totalCount} transactions recorded",
                    color = Slate400,
                    fontSize = 11.sp
                )
            }

            // 3 KPI Strip: Received (Cr), Paid (Dr), Net Flow
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Cr
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Emerald400.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                        .border(1.dp, Emerald400.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Received (Cr)", color = Emerald400, fontSize = 10.sp)
                        Text("+${CurrencyFormatter.formatINR(party.creditTotal)}", color = Emerald400, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Dr
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Rose400.copy(alpha = 0.12f), RoundedCornerShape(12.dp))
                        .border(1.dp, Rose400.copy(alpha = 0.25f), RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Paid (Dr)", color = Rose400, fontSize = 10.sp)
                        Text("-${CurrencyFormatter.formatINR(party.debitTotal)}", color = Rose400, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Net
                val net = party.netFlow
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(Slate800, RoundedCornerShape(12.dp))
                        .border(1.dp, Slate700, RoundedCornerShape(12.dp))
                        .padding(10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Net Flow", color = Slate400, fontSize = 10.sp)
                        Text(
                            CurrencyFormatter.formatSignedINR(net),
                            color = if (net >= 0) Emerald400 else Rose400,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Text(
                text = "TRANSACTION HISTORY:",
                color = Slate400,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )

            // Transactions list
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(220.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(transactions) { tx ->
                    TransactionItem(
                        transaction = tx,
                        onPartyClick = {},
                        onDeleteClick = {}
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Action Button
            Button(
                onClick = {
                    onFilterFeed(party.name)
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
            ) {
                Text(
                    text = "Filter Main Feed by This Name",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
            }
        }
    }
}
