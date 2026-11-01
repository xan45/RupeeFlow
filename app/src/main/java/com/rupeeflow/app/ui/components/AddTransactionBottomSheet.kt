package com.rupeeflow.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rupeeflow.app.domain.model.Transaction
import com.rupeeflow.app.domain.model.TransactionSource
import com.rupeeflow.app.domain.model.TransactionType
import com.rupeeflow.app.ui.theme.Emerald400
import com.rupeeflow.app.ui.theme.Indigo600
import com.rupeeflow.app.ui.theme.Rose400
import com.rupeeflow.app.ui.theme.Slate400
import com.rupeeflow.app.ui.theme.Slate700
import com.rupeeflow.app.ui.theme.Slate800
import com.rupeeflow.app.ui.theme.Slate900
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionBottomSheet(
    onDismiss: () -> Unit,
    onSave: (Transaction) -> Unit,
    categories: List<String>
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var type by remember { mutableStateOf(TransactionType.EXPENSE) }
    var title by remember { mutableStateOf("") }
    var amountText by remember { mutableStateOf("") }
    var source by remember { mutableStateOf(TransactionSource.BANK) }
    var category by remember { mutableStateOf(if (categories.isNotEmpty()) categories.first() else "Food & Dining") }
    var dateString by remember { mutableStateOf(SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(Date())) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Slate900
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .padding(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // Header with Type Toggle
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Add Transaction",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                // Toggle
                Row(
                    modifier = Modifier
                        .background(Slate800, RoundedCornerShape(12.dp))
                        .padding(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                if (type == TransactionType.EXPENSE) Rose400 else Color.Transparent,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { type = TransactionType.EXPENSE }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Expense (-₹)",
                            color = if (type == TransactionType.EXPENSE) Color.White else Slate400,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(
                                if (type == TransactionType.EARNING) Emerald400 else Color.Transparent,
                                RoundedCornerShape(10.dp)
                            )
                            .clickable { type = TransactionType.EARNING }
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "Earning (+₹)",
                            color = if (type == TransactionType.EARNING) Color.White else Slate400,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Description / Merchant
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Description / Merchant (e.g. Swiggy, Salary)") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Indigo600,
                    unfocusedBorderColor = Slate700,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp)
            )

            // Amount
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("Amount (₹)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Indigo600,
                    unfocusedBorderColor = Slate700,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                shape = RoundedCornerShape(14.dp)
            )

            // Source Selector
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TransactionSource.entries.forEach { src ->
                    val isSelected = source == src
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .background(if (isSelected) Indigo600 else Slate800, RoundedCornerShape(10.dp))
                            .clickable { source = src }
                            .padding(vertical = 8.dp),
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        Text(
                            text = src.code,
                            color = if (isSelected) Color.White else Slate400,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            // Save Button
            Spacer(modifier = Modifier.height(4.dp))
            Button(
                onClick = {
                    val amt = amountText.toDoubleOrNull() ?: 0.0
                    if (title.isNotBlank() && amt > 0.0) {
                        val parsedDate = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH)
                            .parse(dateString)?.time ?: System.currentTimeMillis()

                        onSave(
                            Transaction(
                                originalId = "manual-${System.currentTimeMillis()}",
                                date = parsedDate,
                                title = title.trim(),
                                rawDescription = title.trim(),
                                refNumber = "Manual",
                                amount = amt,
                                type = type,
                                source = source,
                                category = category
                            )
                        )
                        onDismiss()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Indigo600)
            ) {
                Text(
                    text = if (type == TransactionType.EXPENSE) "+ Add Expense" else "+ Add Earning",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
