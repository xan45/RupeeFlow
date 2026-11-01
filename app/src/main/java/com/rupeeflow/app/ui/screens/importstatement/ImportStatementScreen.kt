package com.rupeeflow.app.ui.screens.importstatement

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rupeeflow.app.ui.theme.Emerald400
import com.rupeeflow.app.ui.theme.Emerald500
import com.rupeeflow.app.ui.theme.Indigo500
import com.rupeeflow.app.ui.theme.Indigo600
import com.rupeeflow.app.ui.theme.Rose400
import com.rupeeflow.app.ui.theme.Slate400
import com.rupeeflow.app.ui.theme.Slate700
import com.rupeeflow.app.ui.theme.Slate800
import com.rupeeflow.app.ui.theme.Slate900
import com.rupeeflow.app.ui.theme.Slate950

@Composable
fun ImportStatementScreen(
    viewModel: ImportStatementViewModel
) {
    val uiState by viewModel.uiState.collectAsState()

    // Storage Access Framework Document Picker Launcher
    val documentPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            val fileName = uri.lastPathSegment?.substringAfterLast('/') ?: "Selected Statement"
            viewModel.importFromUri(uri, fileName)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
            .padding(horizontal = 16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        Spacer(modifier = Modifier.height(14.dp))

        // Title
        Column {
            Text(
                text = "IMPORT BANK / UPI STATEMENT",
                color = Indigo500,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
            Text(
                text = "100% Local Device Processing • Kotak, HDFC, SBI, ICICI, PhonePe, GPay",
                color = Slate400,
                fontSize = 10.sp
            )
        }

        // Status / Toast Banner
        uiState.statusMessage?.let { msg ->
            val isError = msg.contains("failed", ignoreCase = true) || msg.contains("error", ignoreCase = true)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        if (isError) Rose400.copy(alpha = 0.15f) else Emerald400.copy(alpha = 0.15f),
                        RoundedCornerShape(12.dp)
                    )
                    .border(
                        1.dp,
                        if (isError) Rose400.copy(alpha = 0.3f) else Emerald400.copy(alpha = 0.3f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = msg,
                    color = if (isError) Rose400 else Emerald400,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f)
                )
                IconButton(
                    onClick = { viewModel.dismissStatusMessage() },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = Slate400, modifier = Modifier.size(14.dp))
                }
            }
        }

        // Storage Access Framework File Picker
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Slate900, RoundedCornerShape(18.dp))
                .border(1.dp, Slate800, RoundedCornerShape(18.dp))
                .clickable {
                    documentPickerLauncher.launch(
                        arrayOf(
                            "text/*",
                            "text/csv",
                            "text/plain",
                            "application/vnd.ms-excel",
                            "application/octet-stream"
                        )
                    )
                }
                .padding(22.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .background(Indigo600.copy(alpha = 0.2f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.FolderOpen,
                        contentDescription = "Pick Document",
                        tint = Indigo500,
                        modifier = Modifier.size(28.dp)
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "Select Statement from Device",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Opens Android Storage Access Framework (SAF) to securely pick your .CSV, .TXT, or .TSV file",
                    color = Slate400,
                    fontSize = 11.sp,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        }

        // Paste Statement Content Textarea
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("OR PASTE STATEMENT CSV CONTENT:", color = Slate400, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                if (uiState.statementText.isNotEmpty()) {
                    TextButton(onClick = { viewModel.clearStatementText() }) {
                        Text("Clear", color = Slate400, fontSize = 11.sp)
                    }
                }
            }

            OutlinedTextField(
                value = uiState.statementText,
                onValueChange = { viewModel.updateStatementText(it) },
                placeholder = { Text("Paste exported bank or UPI statement CSV rows here...", color = Slate400, fontSize = 11.sp) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                shape = RoundedCornerShape(14.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Indigo600,
                    unfocusedBorderColor = Slate700,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Slate900,
                    unfocusedContainerColor = Slate900
                ),
                textStyle = androidx.compose.ui.text.TextStyle(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp
                )
            )
        }

        // Row Summary & Replace Checkbox
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Slate900, RoundedCornerShape(14.dp))
                .border(1.dp, Slate800, RoundedCornerShape(14.dp))
                .padding(horizontal = 14.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Detected: ${uiState.previewCount} rows",
                color = Slate400,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium
            )

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = uiState.replaceExisting,
                    onCheckedChange = { viewModel.updateReplaceExisting(it) },
                    colors = CheckboxDefaults.colors(checkedColor = Indigo600)
                )
                Text(
                    text = "Replace existing data",
                    color = Color.White,
                    fontSize = 11.sp
                )
            }
        }

        // Parse & Import Button
        Button(
            onClick = { viewModel.parseAndImportText() },
            enabled = !uiState.isProcessing && uiState.statementText.isNotBlank(),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Emerald500)
        ) {
            if (uiState.isProcessing) {
                CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
            } else {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.UploadFile, contentDescription = null, tint = Color.White)
                    Spacer(modifier = Modifier.size(6.dp))
                    Text("⚡ Parse & Import Statement", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
