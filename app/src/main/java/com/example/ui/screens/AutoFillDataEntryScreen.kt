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
import androidx.compose.material.icons.filled.Save
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
        janDaArrearMonths: Int,
        janDaArrearAmount: Double,
        julDaArrearMonths: Int,
        julDaArrearAmount: Double,
        bonus: Double,
        surrenderDays: Int,
        surrenderMonth: String
    ) -> Unit,
    onPasteIfhrmsClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var basicPayText by remember(profile.basicPayMarch) { mutableStateOf(profile.basicPayMarch.toInt().toString()) }
    var hraText by remember(profile.fixedHra) { mutableStateOf(profile.fixedHra.toInt().toString()) }
    var selectedGrade by remember(profile.hraGrade) { mutableStateOf(profile.hraGrade) }
    var selectedIncMonth by remember(profile.incrementMonth) { mutableStateOf(profile.incrementMonth) }

    // DA January
    var janDaText by remember(taxYear.defaultJanDa) { mutableStateOf(taxYear.defaultJanDa.toInt().toString()) }
    var janDaArrearMonthsText by remember(earnings.daArrearMonths1) { mutableStateOf(earnings.daArrearMonths1.toString()) }
    var janDaArrearAmountText by remember(earnings.daArrear1) { mutableStateOf(earnings.daArrear1.toInt().toString()) }

    // DA July
    var julDaText by remember(taxYear.defaultJulDa) { mutableStateOf(taxYear.defaultJulDa.toInt().toString()) }
    var julDaArrearMonthsText by remember(earnings.daArrearMonths2) { mutableStateOf(earnings.daArrearMonths2.toString()) }
    var julDaArrearAmountText by remember(earnings.daArrear2) { mutableStateOf(earnings.daArrear2.toInt().toString()) }

    // Bonus & Surrender
    var bonusText by remember(earnings.bonus) { mutableStateOf(earnings.bonus.toInt().toString()) }
    var surrenderDays by remember(earnings.surrenderDays) { mutableStateOf(earnings.surrenderDays) }
    var surrenderMonth by remember(earnings.surrenderMonth) {
        mutableStateOf(if (earnings.surrenderMonth.isNotBlank()) earnings.surrenderMonth else "இல்லை (None)")
    }
    var surrenderMonthExpanded by remember { mutableStateOf(false) }

    val hraGrades = listOf("Grade 1(a)", "Grade 1(b)", "Grade II", "Grade III", "Grade IV", "Custom")
    val incMonthOptions = listOf(
        -1 to "இல்லை (None)",
        1 to "ஏப்ரல் (April)",
        4 to "ஜூலை (July)",
        7 to "அக்டோபர் (October)",
        10 to "ஜனவரி (January)"
    )

    val surrenderMonths = listOf(
        "இல்லை (None)",
        "மார்ச் (March)",
        "ஏப்ரல் (April)",
        "மே (May)",
        "ஜூன் (June)",
        "ஜூலை (July)",
        "ஆகஸ்ட் (August)",
        "செப்டம்பர் (September)",
        "அக்டோபர் (October)",
        "நவம்பர் (November)",
        "டிசம்பர் (December)",
        "ஜனவரி (January)",
        "பிப்ரவரி (February)"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 10.dp, end = 20.dp, top = 8.dp, bottom = 14.dp),
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
                        text = "மார்ச் அடிப்படை ஊதியம் மற்றும் HRA உள்ளிட்ட பிறகு, 'சேமித்து கணக்கிடு' பொத்தானை அழுத்துங்கள்.",
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

                    var gradeExpanded by remember { mutableStateOf(false) }
                    ExposedDropdownMenuBox(
                        expanded = gradeExpanded,
                        onExpandedChange = { gradeExpanded = !gradeExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = selectedGrade,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("HRA நிலை (Grade)") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = gradeExpanded) },
                            modifier = Modifier
                                .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                                .fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        )
                        ExposedDropdownMenu(
                            expanded = gradeExpanded,
                            onDismissRequest = { gradeExpanded = false }
                        ) {
                            hraGrades.forEach { grade ->
                                DropdownMenuItem(
                                    text = { Text(grade) },
                                    onClick = {
                                        selectedGrade = grade
                                        val bp = basicPayText.toDoubleOrNull() ?: 0.0
                                        val suggested = com.example.calculator.HraTableHelper.getSuggestedHra(bp, grade, hraText.toDoubleOrNull() ?: 3200.0)
                                        hraText = suggested.toInt().toString()
                                        gradeExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Increment Month
                var incExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = incExpanded,
                    onExpandedChange = { incExpanded = !incExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = incMonthOptions.find { it.first == selectedIncMonth }?.second ?: "இல்லை",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("வருடாந்திர ஊதிய உயர்வு மாதம் (Annual Increment Month)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = incExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = incExpanded,
                        onDismissRequest = { incExpanded = false }
                    ) {
                        incMonthOptions.forEach { (m, label) ->
                            DropdownMenuItem(
                                text = { Text(label) },
                                onClick = {
                                    selectedIncMonth = m
                                    incExpanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        // Section: DA January 2026 Details
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, Color(0xFFCBD5E1))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "ஜனவரி 2026 அகவிலைப்படி (January 2026 DA & Arrears)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                )

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
                        value = janDaArrearMonthsText,
                        onValueChange = { janDaArrearMonthsText = it },
                        label = { Text("நிலுவை மாதங்கள் (Months)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                OutlinedTextField(
                    value = janDaArrearAmountText,
                    onValueChange = { janDaArrearAmountText = it },
                    label = { Text("ஜனவரி DA நிலுவைத்தொகை (Jan Arrear Amount)") },
                    prefix = { Text("₹ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        // Section: DA July 2026 Details
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, Color(0xFFCBD5E1))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "ஜூலை 2026 அகவிலைப்படி (July 2026 DA & Arrears)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = julDaText,
                        onValueChange = { julDaText = it },
                        label = { Text("ஜூலை DA %") },
                        suffix = { Text("%") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                    OutlinedTextField(
                        value = julDaArrearMonthsText,
                        onValueChange = { julDaArrearMonthsText = it },
                        label = { Text("நிலுவை மாதங்கள் (Months)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    )
                }

                OutlinedTextField(
                    value = julDaArrearAmountText,
                    onValueChange = { julDaArrearAmountText = it },
                    label = { Text("ஜூலை DA நிலுவைத்தொகை (Jul Arrear Amount)") },
                    prefix = { Text("₹ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        // Section: Surrender Leave & Festival Bonus
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
                    text = "ஈட்டிய விடுப்பு ஒப்படைப்பு & போனஸ் (Surrender & Bonus)",
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF0F172A))
                )

                // Surrender Leave Days
                Column {
                    Text(
                        text = "ஈட்டிய விடுப்பு ஒப்படைப்பு நாட்கள் (Surrender Days):",
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

                // Surrender Month Dropdown
                ExposedDropdownMenuBox(
                    expanded = surrenderMonthExpanded,
                    onExpandedChange = { surrenderMonthExpanded = !surrenderMonthExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = surrenderMonth,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("ஒப்படைப்பு மாதம் (Surrender Month)") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = surrenderMonthExpanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable, true)
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = surrenderMonthExpanded,
                        onDismissRequest = { surrenderMonthExpanded = false }
                    ) {
                        surrenderMonths.forEach { m ->
                            DropdownMenuItem(
                                text = { Text(m) },
                                onClick = {
                                    surrenderMonth = m
                                    surrenderMonthExpanded = false
                                }
                            )
                        }
                    }
                }

                // Bonus Input
                OutlinedTextField(
                    value = bonusText,
                    onValueChange = { bonusText = it },
                    label = { Text("பண்டிகை முன்பணம் / போனஸ் (Festival Bonus)") },
                    prefix = { Text("₹ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        // Primary Commit / Save Button
        Button(
            onClick = {
                val bp = basicPayText.toDoubleOrNull() ?: 91700.0
                val hra = hraText.toDoubleOrNull() ?: 3200.0
                val janDa = janDaText.toDoubleOrNull() ?: 58.0
                val julDa = julDaText.toDoubleOrNull() ?: 60.0
                val janArrMonths = janDaArrearMonthsText.toIntOrNull() ?: 4
                val janArrAmount = janDaArrearAmountText.toDoubleOrNull() ?: 7336.0
                val julArrMonths = julDaArrearMonthsText.toIntOrNull() ?: 0
                val julArrAmount = julDaArrearAmountText.toDoubleOrNull() ?: 0.0
                val bonus = bonusText.toDoubleOrNull() ?: 3000.0

                onAutoFill(
                    bp, hra, selectedGrade, selectedIncMonth,
                    janDa, julDa,
                    janArrMonths, janArrAmount,
                    julArrMonths, julArrAmount,
                    bonus, surrenderDays, surrenderMonth
                )
            },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1D4ED8)),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("btn_save_and_calculate_tax")
        ) {
            Icon(imageVector = Icons.Default.Save, contentDescription = "Save")
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "💾 சேமித்து கணக்கிடு (Save & Calculate Tax)",
                fontSize = 14.sp,
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
