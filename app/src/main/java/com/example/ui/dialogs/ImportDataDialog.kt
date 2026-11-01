package com.example.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@Composable
fun ImportDataDialog(
    onDismiss: () -> Unit,
    onPickFileClick: () -> Unit,
    onImportText: (String) -> Unit,
    onLoadSample: () -> Unit
) {
    var rawText by remember { mutableStateOf("") }

    val samplePaySlipText = """
        Pay Drawn Particulars Year Report For the Financial Year 2026-2027
        Employee ID : 43020114172 Employee Name : Robert Sebastin J GBM ID : P0005830318
        Date of Birth : 26-MAY-71 Joining Date : 07-OCT-96 Retirement Date : 31-MAY-31
        PAN No : AICPR6198J Aadhar No : XXXXXXXX6244 Mobile No : 9600892330
        Regular Salary
        House Rent Allowance: 3200
        Medical Allowance: 300
        Dearness Allowance: 53186
        Duty Pay: 91700
        Dearness Allowance Arrear: 7336
        Teachers Provident Fund: 20000
        Income Tax: 11761
        Income Tax Cess: 470
    """.trimIndent()

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(14.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(6.dp)
                .testTag("dialog_import_ifhrms")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "IFHRMS Pay Drawn தரவு இறக்குமதி (Import Pay Drawn PDF)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp)
                )

                Text(
                    text = "சாதனத்தில் உள்ள IFHRMS PDF கோப்பைத் தேர்வு செய்யவும் அல்லது மாதிரி அறிக்கையை ஒரே தொடுதலில் ஏற்றவும்.",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )

                // Option 1: Pick PDF from Storage
                Button(
                    onClick = {
                        onDismiss()
                        onPickFileClick()
                    },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4338CA)),
                    modifier = Modifier.fillMaxWidth().height(42.dp).testTag("btn_pick_pdf_file")
                ) {
                    Icon(imageVector = Icons.Default.FolderOpen, contentDescription = "Pick PDF", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "📁 சாதனத்தில் உள்ள PDF தேர்வு செய் (Choose PDF File)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.5.sp
                    )
                }

                // Option 2: 1-Click Load Sample
                OutlinedButton(
                    onClick = {
                        onLoadSample()
                        onDismiss()
                    },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFFD97706)),
                    modifier = Modifier.fillMaxWidth().height(40.dp).testTag("btn_load_sample_slip")
                ) {
                    Icon(imageVector = Icons.Default.Description, contentDescription = "Sample", tint = Color(0xFFD97706), modifier = Modifier.size(15.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "✨ மாதிரி IFHRMS PDF அறிக்கை ஏற்று (Robert Sebastin J)",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFD97706),
                        fontSize = 11.sp
                    )
                }

                HorizontalDivider(color = Color(0xFFE2E8F0))

                Text(
                    text = "அல்லது உரை / CSV நகலை ஒட்டவும் (Or Paste Text):",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF475569)
                )

                OutlinedTextField(
                    value = rawText,
                    onValueChange = { rawText = it },
                    placeholder = { Text("Employee ID : 43020114172\nEmployee Name : Robert Sebastin J\nDuty Pay: 91700...", fontSize = 11.sp) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .testTag("input_paste_ifhrms_text"),
                    shape = RoundedCornerShape(8.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("ரத்து", fontSize = 12.sp)
                    }
                    Spacer(modifier = Modifier.width(6.dp))
                    Button(
                        onClick = {
                            val txt = rawText.ifBlank { samplePaySlipText }
                            onImportText(txt)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                        modifier = Modifier.height(36.dp).testTag("btn_submit_import")
                    ) {
                        Icon(imageVector = Icons.Default.UploadFile, contentDescription = "Import", modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("இறக்குமதி செய்", fontSize = 11.5.sp)
                    }
                }
            }
        }
    }
}
