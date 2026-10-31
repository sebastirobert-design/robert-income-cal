package com.example.ui.dialogs

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.example.model.EmployeeProfile

@Composable
fun EditStaffDialog(
    profile: EmployeeProfile,
    onDismiss: () -> Unit,
    onSave: (EmployeeProfile) -> Unit
) {
    var name by remember { mutableStateOf(profile.name) }
    var ifhrmsNo by remember { mutableStateOf(profile.ifhrmsNo) }
    var designation by remember { mutableStateOf(profile.designation) }
    var schoolOffice by remember { mutableStateOf(profile.schoolOffice) }
    var beoTanNo by remember { mutableStateOf(profile.beoTanNo) }
    var panNo by remember { mutableStateOf(profile.panNo) }
    var tpfCpsNo by remember { mutableStateOf(profile.tpfCpsNo) }
    var mobileNo by remember { mutableStateOf(profile.mobileNo) }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .testTag("dialog_edit_staff")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "பணியாளர் விவரங்கள் திருத்து (Edit Staff Details)",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("பணியாளர் பெயர் (Name)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = ifhrmsNo,
                        onValueChange = { ifhrmsNo = it },
                        label = { Text("IFHRMS எண்") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = panNo,
                        onValueChange = { panNo = it },
                        label = { Text("PAN எண்") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = designation,
                    onValueChange = { designation = it },
                    label = { Text("பதவி (Designation)") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = schoolOffice,
                    onValueChange = { schoolOffice = it },
                    label = { Text("பள்ளி / அலுவலகம் (School / Office)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = beoTanNo,
                        onValueChange = { beoTanNo = it },
                        label = { Text("BEO / TAN எண்") },
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = tpfCpsNo,
                        onValueChange = { tpfCpsNo = it },
                        label = { Text("TPF / CPS எண்") },
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = mobileNo,
                    onValueChange = { mobileNo = it },
                    label = { Text("கைபேசி எண் (Mobile No)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = onDismiss) {
                        Text("ரத்து")
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(
                        onClick = {
                            val updated = profile.copy(
                                name = name,
                                ifhrmsNo = ifhrmsNo,
                                designation = designation,
                                schoolOffice = schoolOffice,
                                beoTanNo = beoTanNo,
                                panNo = panNo,
                                tpfCpsNo = tpfCpsNo,
                                mobileNo = mobileNo
                            )
                            onSave(updated)
                        },
                        modifier = Modifier.testTag("btn_save_staff_details")
                    ) {
                        Text("சேமி (Save)")
                    }
                }
            }
        }
    }
}
