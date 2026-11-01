package com.rupeeflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rupeeflow.app.domain.model.Transaction
import com.rupeeflow.app.domain.model.TransactionSource
import com.rupeeflow.app.domain.model.TransactionType
import com.rupeeflow.app.ui.theme.Emerald400
import com.rupeeflow.app.ui.theme.Rose400
import com.rupeeflow.app.ui.theme.Slate400
import com.rupeeflow.app.ui.theme.Slate700
import com.rupeeflow.app.ui.theme.Slate800
import com.rupeeflow.app.util.CurrencyFormatter
import com.rupeeflow.app.util.DateUtils

@Composable
fun TransactionItem(
    transaction: Transaction,
    onPartyClick: (String) -> Unit,
    onDeleteClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val isEarning = transaction.type == TransactionType.EARNING
    val arrowColor = if (isEarning) Emerald400 else Rose400
    val arrowBg = if (isEarning) Emerald400.copy(alpha = 0.15f) else Rose400.copy(alpha = 0.15f)
    val amountPrefix = if (isEarning) "+" else "-"

    val sourceBadgeColor = when (transaction.source) {
        TransactionSource.BANK -> Color(0xFFF59E0B) // Amber
        TransactionSource.GPAY -> Color(0xFF3B82F6) // Blue
        TransactionSource.PHONEPE -> Color(0xFF8B5CF6) // Purple
        TransactionSource.MAILBOX -> Color(0xFF10B981) // Emerald
        TransactionSource.MANUAL -> Color(0xFF64748B) // Slate
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Slate800.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
            .border(1.dp, Slate700.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .background(arrowBg, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isEarning) "↓" else "↑",
                    color = arrowColor,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Details
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = transaction.title,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.clickable { onPartyClick(transaction.title) }
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.padding(top = 2.dp)
                ) {
                    // Source badge
                    Box(
                        modifier = Modifier
                            .background(sourceBadgeColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .border(1.dp, sourceBadgeColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                            .padding(horizontal = 6.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = transaction.source.code,
                            color = sourceBadgeColor,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = transaction.category,
                        color = Slate400,
                        fontSize = 11.sp
                    )

                    Text(
                        text = "• ${DateUtils.formatDisplayDate(transaction.date)}",
                        color = Slate400.copy(alpha = 0.8f),
                        fontSize = 11.sp
                    )
                }
            }
        }

        // Amount & Delete
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "$amountPrefix${CurrencyFormatter.formatINR(transaction.amount)}",
                color = arrowColor,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold
            )

            IconButton(
                onClick = { onDeleteClick(transaction.id) },
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Delete",
                    tint = Slate400.copy(alpha = 0.7f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}
