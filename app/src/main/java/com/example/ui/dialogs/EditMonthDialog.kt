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
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.model.SalaryMonthData

@Composable
fun EditMonthDialog(
    month: SalaryMonthData,
    onDismiss: () -> Unit,
    onSave: (SalaryMonthData) -> Unit
) {
    var basicPay by remember { mutableStateOf(month.basicPay.toInt().toString()) }
    var ppSpl by remember { mutableStateOf(month.ppSpl.toInt().toString()) }
    var daPercent by remember { mutableStateOf(month.daPercent.toInt().toString()) }
    var hra by remember { mutableStateOf(month.hra.toInt().toString()) }
    var ma by remember { mutableStateOf(month.ma.toInt().toString()) }
    var cca by remember { mutableStateOf(month.cca.toInt().toString()) }
    var tpfCps by remember { mutableStateOf(month.tpfCps.toInt().toString()) }
    var fbf by remember { mutableStateOf(month.fbf.toInt().toString()) }
    var nhis by remember { mutableStateOf(month.nhis.toInt().toString()) }
    var pfAdv by remember { mutableStateOf(month.pfAdvRecovery.toInt().toString()) }
    var festAdv by remember { mutableStateOf(month.festivalAdvRecovery.toInt().toString()) }
    var itTds by remember { mutableStateOf(month.itTds.toInt().toString()) }
    var itCess by remember { mutableStateOf(month.itCess.toInt().toString()) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("dialog_edit_month")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "${month.monthCode} (${month.monthNameTamil}) - ஊதியம் திருத்து",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Text(
                    text = "ஊதிய வரவுகள் (Earnings):",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = basicPay,
                        onValueChange = { basicPay = it },
                        label = { Text("அடிப்படை (BP)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = ppSpl,
                        onValueChange = { ppSpl = it },
                        label = { Text("PP / Spl Pay") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = daPercent,
                        onValueChange = { daPercent = it },
                        label = { Text("DA %") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
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
                        value = ma,
                        onValueChange = { ma = it },
                        label = { Text("MA (மருத்துவம்)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = cca,
                        onValueChange = { cca = it },
                        label = { Text("CCA") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Divider()

                Text(
                    text = "மாத பிடித்தங்கள் & வருமான வரி (Deductions & TDS):",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = itTds,
                        onValueChange = { itTds = it },
                        label = { Text("வருமான வரி (IT TDS)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = itCess,
                        onValueChange = { itCess = it },
                        label = { Text("செஸ் (IT Cess 4%)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = tpfCps,
                        onValueChange = { tpfCps = it },
                        label = { Text("TPF / CPS") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = fbf,
                        onValueChange = { fbf = it },
                        label = { Text("FBF") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = nhis,
                        onValueChange = { nhis = it },
                        label = { Text("NHIS காப்பீடு") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = pfAdv,
                        onValueChange = { pfAdv = it },
                        label = { Text("PF முன்பணம் பிடித்தம்") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("ரத்து (Cancel)")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val bpVal = basicPay.toDoubleOrNull() ?: month.basicPay
                            val daPVal = daPercent.toDoubleOrNull() ?: month.daPercent
                            val updated = month.copy(
                                basicPay = bpVal,
                                ppSpl = ppSpl.toDoubleOrNull() ?: 0.0,
                                daPercent = daPVal,
                                daAmount = kotlin.math.round(bpVal * (daPVal / 100.0)),
                                hra = hra.toDoubleOrNull() ?: month.hra,
                                ma = ma.toDoubleOrNull() ?: month.ma,
                                cca = cca.toDoubleOrNull() ?: month.cca,
                                itTds = itTds.toDoubleOrNull() ?: month.itTds,
                                itCess = itCess.toDoubleOrNull() ?: month.itCess,
                                tpfCps = tpfCps.toDoubleOrNull() ?: month.tpfCps,
                                fbf = fbf.toDoubleOrNull() ?: month.fbf,
                                nhis = nhis.toDoubleOrNull() ?: month.nhis,
                                pfAdvRecovery = pfAdv.toDoubleOrNull() ?: month.pfAdvRecovery,
                                festivalAdvRecovery = festAdv.toDoubleOrNull() ?: month.festivalAdvRecovery
                            )
                            onSave(updated)
                        },
                        modifier = Modifier.testTag("btn_save_month_edit")
                    ) {
                        Text("சேமி (Save)")
                    }
                }
            }
        }
    }
}
