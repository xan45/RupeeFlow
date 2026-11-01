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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rupeeflow.app.domain.model.FinancialHealthSummary
import com.rupeeflow.app.ui.theme.Emerald400
import com.rupeeflow.app.ui.theme.Indigo600
import com.rupeeflow.app.ui.theme.Purple600
import com.rupeeflow.app.ui.theme.Rose400
import com.rupeeflow.app.ui.theme.Violet600
import com.rupeeflow.app.util.CurrencyFormatter

@Composable
fun BalanceCard(
    summary: FinancialHealthSummary,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .background(
                    Brush.horizontalGradient(
                        colors = listOf(Indigo600, Violet600, Purple600)
                    )
                )
                .padding(18.dp)
        ) {
            Column {
                // Top Row: Title + Transaction Count
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "NET CASHFLOW",
                        color = Color.White.copy(alpha = 0.85f),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Box(
                        modifier = Modifier
                            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${summary.transactionCount} entries",
                            color = Color.White,
                            fontSize = 11.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Net Balance & Closing Balance
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = CurrencyFormatter.formatSignedINR(summary.netCashFlow),
                        color = Color.White,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold
                    )
                    if (summary.latestClosingBalance != null) {
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF10B981).copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                                .border(1.dp, Emerald400.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "Closing: ₹${summary.latestClosingBalance}",
                                color = Emerald400,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sub Grid: Total Earnings & Total Expenses
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Earnings Card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFF10B981).copy(alpha = 0.18f), RoundedCornerShape(14.dp))
                            .border(1.dp, Emerald400.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "↓ Earnings",
                                    color = Emerald400,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${summary.earningCount} Cr",
                                    color = Emerald400.copy(alpha = 0.8f),
                                    fontSize = 10.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "+${CurrencyFormatter.formatINR(summary.totalEarnings)}",
                                color = Emerald400,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Expenses Card
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(Color(0xFFF43F5E).copy(alpha = 0.18f), RoundedCornerShape(14.dp))
                            .border(1.dp, Rose400.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
                            .padding(10.dp)
                    ) {
                        Column {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "↑ Expenses",
                                    color = Rose400,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "${summary.expenseCount} Dr",
                                    color = Rose400.copy(alpha = 0.8f),
                                    fontSize = 10.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = "-${CurrencyFormatter.formatINR(summary.totalExpenses)}",
                                color = Rose400,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}
