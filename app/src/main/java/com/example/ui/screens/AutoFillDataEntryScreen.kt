package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentPaste
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EmployeeProfile
import com.example.model.OtherEarnings
import com.example.model.TaxYear
import com.example.ui.theme.BluePrimary
import com.example.ui.theme.GoldAccent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AutoFillDataEntryScreen(
    profile: EmployeeProfile,
    taxYear: TaxYear,
    earnings: OtherEarnings,
    onAutoFill: (
        marchBasic: Double,
        fixedHra: Double,
        hraGrade: String,
        incrementMonth: Int,
        janDa: Double,
        julDa: Double,
        daArrear: Double,
        bonus: Double,
        surrenderDays: Int
    ) -> Unit,
    onPasteIfhrmsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var basicPayText by remember(profile.basicPayMarch) { mutableStateOf(profile.basicPayMarch.toInt().toString()) }
    var hraText by remember(profile.fixedHra) { mutableStateOf(profile.fixedHra.toInt().toString()) }
    var selectedGrade by remember(profile.hraGrade) { mutableStateOf(profile.hraGrade) }
    var selectedIncMonth by remember(profile.incrementMonth) { mutableStateOf(profile.incrementMonth) }
    var janDaText by remember(taxYear.defaultJanDa) { mutableStateOf(taxYear.defaultJanDa.toInt().toString()) }
    var julDaText by remember(taxYear.defaultJulDa) { mutableStateOf(taxYear.defaultJulDa.toInt().toString()) }
    var daArrearText by remember(earnings.daArrear1) { mutableStateOf(earnings.daArrear1.toInt().toString()) }
    var bonusText by remember(earnings.bonus) { mutableStateOf(earnings.bonus.toInt().toString()) }
    var surrenderDays by remember(earnings.surrenderDays) { mutableStateOf(earnings.surrenderDays) }

    val hraGrades = listOf("Grade 1(a)", "Grade 1(b)", "Grade II", "Grade III", "Grade IV", "Custom")
    val incMonthOptions = listOf(
        -1 to "இல்லை (None)",
        1 to "ஏப்ரல் (April)",
        4 to "ஜூலை (July)",
        7 to "அக்டோபர் (October)",
        10 to "ஜனவரி (January)"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Quick Explanation Banner
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFEFF6FF)),
            border = BorderStroke(1.dp, Color(0xFFBFDBFE)),
            shape = RoundedCornerShape(10.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Auto Fill",
                    tint = BluePrimary,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "1 அல்லது 2 பதிவுகளில் முழு 12 மாத ஊதியம் & வரி கணக்கீடு!",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        color = Color(0xFF1E3A8A)
                    )
                    Text(
                        text = "மார்ச் அடிப்படை ஊதியம் மற்றும் HRA மட்டும் உள்ளிட்டால் போதும்; DA, நிலுவை, போனஸ், CPS/TPF மற்றும் வரி தானாக பூர்த்தி செய்யப்படும்.",
                        fontSize = 11.5.sp,
                        color = Color(0xFF3B82F6)
                    )
                }
            }
        }

        // Primary 1-2 Entry Inputs Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.5.dp, Color(0xFF6366F1)),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "முக்கிய விவரங்கள் (Mandatory 1-2 Inputs)",
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4F46E5)
                    )
                )

                // Entry 1: Basic Pay in March
                OutlinedTextField(
                    value = basicPayText,
                    onValueChange = { basicPayText = it },
                    label = { Text("1. மார்ச் மாத அடிப்படை ஊதியம் (March Basic Pay)") },
                    prefix = { Text("₹ ", fontWeight = FontWeight.Bold) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_march_basic_pay"),
                    shape = RoundedCornerShape(8.dp)
                )

                // Entry 2: HRA Amount / Grade
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedTextField(
                        value = hraText,
                        onValueChange = { hraText = it },
                        label = { Text("2. வீட்டு வாடகைப்படி (HRA)") },
                        prefix = { Text("₹ ", fontWeight = FontWeight.Bold) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("input_hra"),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "HRA தரம் (HRA Grade):",
                            fontSize = 11.5.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF475569)
                        )
                        var gradeExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = gradeExpanded,
                            onExpandedChange = { gradeExpanded = !gradeExpanded }
                        ) {
                            OutlinedTextField(
                                value = selectedGrade,
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = gradeExpanded) },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = gradeExpanded,
                                onDismissRequest = { gradeExpanded = false }
                            ) {
                                hraGrades.forEach { g ->
                                    DropdownMenuItem(
                                        text = { Text(g) },
                                        onClick = {
                                            selectedGrade = g
                                            gradeExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                // Increment Month
                Column {
                    Text(
                        text = "ஆண்டு ஊதிய உயர்வு மாதம் (Annual Increment Month):",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF475569)
                    )
                    var incExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = incExpanded,
                        onExpandedChange = { incExpanded = !incExpanded }
                    ) {
                        OutlinedTextField(
                            value = incMonthOptions.firstOrNull { it.first == selectedIncMonth }?.second ?: "அக்டோபர் (October)",
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = incExpanded) },
                            modifier = Modifier
                                .menuAnchor()
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = incExpanded,
                            onDismissRequest = { incExpanded = false }
                        ) {
                            incMonthOptions.forEach { opt ->
                                DropdownMenuItem(
                                    text = { Text(opt.second) },
                                    onClick = {
                                        selectedIncMonth = opt.first
                                        incExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        // Secondary Details & Rates Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, Color(0xFFCBD5E1))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "அகவிலைப்படி & பிற வரவுகள் (DA Rates & Arrears)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                )

                // DA Rates
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = janDaText,
                        onValueChange = { janDaText = it },
                        label = { Text("ஜனவரி DA %") },
                        suffix = { Text("%") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = julDaText,
                        onValueChange = { julDaText = it },
                        label = { Text("ஜூலை DA %") },
                        suffix = { Text("%") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                // DA Arrear & Bonus
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = daArrearText,
                        onValueChange = { daArrearText = it },
                        label = { Text("DA நிலுவை (DA Arrear)") },
                        prefix = { Text("₹ ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = bonusText,
                        onValueChange = { bonusText = it },
                        label = { Text("பண்டிகை முன்பணம் / போனஸ்") },
                        prefix = { Text("₹ ") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                // Surrender Leave Days
                Column {
                    Text(
                        text = "ஈட்டிய விடுப்பு ஒப்படைப்பு (Surrender Leave Days):",
                        fontSize = 11.5.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF475569)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(0 to "இல்லை (0)", 15 to "15 நாட்கள்", 30 to "30 நாட்கள்").forEach { (days, label) ->
                            FilterChip(
                                selected = surrenderDays == days,
                                onClick = { surrenderDays = days },
                                label = { Text(label, fontSize = 12.sp) }
                            )
                        }
                    }
                }
            }
        }

        // Action Buttons Row
        Button(
            onClick = {
                val bp = basicPayText.toDoubleOrNull() ?: 91700.0
                val hra = hraText.toDoubleOrNull() ?: 3200.0
                val janDa = janDaText.toDoubleOrNull() ?: 58.0
                val julDa = julDaText.toDoubleOrNull() ?: 60.0
                val daArrear = daArrearText.toDoubleOrNull() ?: 7336.0
                val bonus = bonusText.toDoubleOrNull() ?: 3000.0

                onAutoFill(bp, hra, selectedGrade, selectedIncMonth, janDa, julDa, daArrear, bonus, surrenderDays)
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("btn_trigger_auto_fill")
        ) {
            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = "Auto Fill")
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "தானியங்கி முறையில் கணக்கிடு (Auto-Generate 12 Months & Tax)",
                fontSize = 13.5.sp,
                fontWeight = FontWeight.Bold
            )
        }

        // Import Quick Card
        OutlinedButton(
            onClick = onPasteIfhrmsClick,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .testTag("btn_paste_ifhrms")
        ) {
            Icon(imageVector = Icons.Default.ContentPaste, contentDescription = "Paste")
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "IFHRMS Pay Drawn நகலெடுத்து ஒட்ட (Paste / Quick Import)",
                fontSize = 12.5.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
