package com.rupeeflow.app.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.rupeeflow.app.domain.model.TimeBucket
import com.rupeeflow.app.ui.theme.Emerald400
import com.rupeeflow.app.ui.theme.Indigo500
import com.rupeeflow.app.ui.theme.Rose400
import com.rupeeflow.app.ui.theme.Slate400
import com.rupeeflow.app.ui.theme.Slate800
import com.rupeeflow.app.ui.theme.Slate900
import com.rupeeflow.app.ui.theme.Slate950
import com.rupeeflow.app.util.CurrencyFormatter

@Composable
fun TimeBucketCard(
    bucket: TimeBucket,
    onPartyClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val net = bucket.netFlow

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Slate900.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
            .border(1.dp, Slate800, RoundedCornerShape(16.dp))
    ) {
        // Main Clickable Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { expanded = !expanded }
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = bucket.label,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "${bucket.subLabel} • ${bucket.totalCount} txns",
                    color = Slate400,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(top = 1.dp)
                )

                // Visual Cr & Dr Progress Bar
                Spacer(modifier = Modifier.height(8.dp))
                val totalVolume = Math.max(bucket.creditTotal + bucket.debitTotal, 1.0)
                val crFraction = (bucket.creditTotal / totalVolume).toFloat()
                val drFraction = (bucket.debitTotal / totalVolume).toFloat()

                Row(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(5.dp)
                        .background(Slate950, RoundedCornerShape(3.dp))
                ) {
                    if (crFraction > 0f) {
                        Box(
                            modifier = Modifier
                                .weight(crFraction)
                                .height(5.dp)
                                .background(Emerald400, RoundedCornerShape(3.dp))
                        )
                    }
                    if (drFraction > 0f) {
                        Box(
                            modifier = Modifier
                                .weight(drFraction)
                                .height(5.dp)
                                .background(Rose400, RoundedCornerShape(3.dp))
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(10.dp))

            // Totals
            Column(horizontalAlignment = Alignment.End) {
                if (bucket.creditTotal > 0) {
                    Text(
                        text = "+${CurrencyFormatter.formatINR(bucket.creditTotal)}",
                        color = Emerald400,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                if (bucket.debitTotal > 0) {
                    Text(
                        text = "-${CurrencyFormatter.formatINR(bucket.debitTotal)}",
                        color = Rose400,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Text(
                    text = "Net: ${CurrencyFormatter.formatSignedINR(net)}",
                    color = if (net >= 0) Emerald400 else Rose400,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Expandable Detail of Parties inside this bucket
        AnimatedVisibility(visible = expanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate950)
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "COLLATED SENDER / RECEIVERS IN THIS PERIOD:",
                    color = Indigo500,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )

                bucket.collatedParties.forEach { party ->
                    PartyCard(
                        party = party,
                        onClick = { onPartyClick(party.name) }
                    )
                }
            }
        }
    }
}
