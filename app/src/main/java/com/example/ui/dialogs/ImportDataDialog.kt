package com.example.ui.dialogs

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
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
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("dialog_import_ifhrms")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "IFHRMS Pay Drawn தரவு இறக்குமதி (Import IFHRMS)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Text(
                    text = "IFHRMS-லிருந்து பதிவிறக்கிய ஊதிய அறிக்கை (Pay Drawn Particulars PDF) விவரங்களை கீழே ஒட்டவும் அல்லது ஒரு தொடுதலில் மாதிரியை ஏற்றவும்.",
                    fontSize = 11.5.sp,
                    color = Color(0xFF64748B)
                )

                // Quick One-tap Sample Button
                OutlinedButton(
                    onClick = {
                        onLoadSample()
                        onDismiss()
                    },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, Color(0xFF4F46E5)),
                    modifier = Modifier.fillMaxWidth().testTag("btn_load_sample_slip")
                ) {
                    Icon(imageVector = Icons.Default.Description, contentDescription = "Sample", tint = Color(0xFF4F46E5))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        "மாதிரி IFHRMS ஊதிய சீட்டு ஏற்று (Robert Sebastin J)",
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4F46E5),
                        fontSize = 11.5.sp
                    )
                }

                Divider()

                OutlinedTextField(
                    value = rawText,
                    onValueChange = { rawText = it },
                    label = { Text("IFHRMS உரை / CSV வரிகளை ஒட்டவும் (Paste text)") },
                    placeholder = { Text("Employee ID : 43020114172\nEmployee Name : Robert Sebastin J\nDuty Pay: 91700...") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .testTag("input_paste_ifhrms_text"),
                    shape = RoundedCornerShape(8.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("ரத்து (Cancel)")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val txt = rawText.ifBlank { samplePaySlipText }
                            onImportText(txt)
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                        modifier = Modifier.testTag("btn_submit_import")
                    ) {
                        Icon(imageVector = Icons.Default.UploadFile, contentDescription = "Import")
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("இறக்குமதி செய் (Import)")
                    }
                }
            }
        }
    }
}
