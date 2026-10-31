package com.example.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog

@Composable
fun AddTeacherDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, designation: String, basicPay: Double, hra: Double, pan: String, ifhrms: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var designation by remember { mutableStateOf("B.T. ASSISTANT") }
    var basicPay by remember { mutableStateOf("50000") }
    var hra by remember { mutableStateOf("2200") }
    var pan by remember { mutableStateOf("") }
    var ifhrms by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("dialog_add_teacher")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "புதிய ஆசிரியர் / பணியாளர் சேர்க்க (Add Teacher)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("ஆசிரியர் பெயர் (Teacher Name)") },
                    placeholder = { Text("e.g. SENTHIL KUMAR M") },
                    modifier = Modifier.fillMaxWidth().testTag("input_new_teacher_name")
                )

                OutlinedTextField(
                    value = designation,
                    onValueChange = { designation = it },
                    label = { Text("பதவி (Designation)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = basicPay,
                        onValueChange = { basicPay = it },
                        label = { Text("அடிப்படை ஊதியம் (BP)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f).testTag("input_new_teacher_bp")
                    )
                    OutlinedTextField(
                        value = hra,
                        onValueChange = { hra = it },
                        label = { Text("HRA") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = pan,
                        onValueChange = { pan = it },
                        label = { Text("PAN எண்") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = ifhrms,
                        onValueChange = { ifhrms = it },
                        label = { Text("IFHRMS எண்") },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("ரத்து")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val bp = basicPay.toDoubleOrNull() ?: 50000.0
                            val h = hra.toDoubleOrNull() ?: 2200.0
                            onAdd(name, designation, bp, h, pan, ifhrms)
                            onDismiss()
                        },
                        modifier = Modifier.testTag("btn_confirm_add_teacher")
                    ) {
                        Text("சேர்க்க (Add)")
                    }
                }
            }
        }
    }
}
