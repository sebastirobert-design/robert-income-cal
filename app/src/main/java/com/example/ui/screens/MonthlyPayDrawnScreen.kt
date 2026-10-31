package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SalaryMonthData

@Composable
fun MonthlyPayDrawnScreen(
    monthsData: List<SalaryMonthData>,
    onEditMonthClick: (SalaryMonthData) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .testTag("monthly_pay_drawn_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color(0xFFCBD5E1))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Text(
                text = "12 மாத விரிவான ஊதியப் பட்டியல் (12-Month Pay Drawn Slip)",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = "ஒவ்வொரு மாதத்தின் ஊதியம் மற்றும் பிடித்தங்களை சரிபார்க்கவும், தேவைப்பட்டால் 'திருத்து' பொத்தானை அழுத்தவும்.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = Color(0xFF64748B))
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Horizontally scrollable comprehensive table
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                Column(modifier = Modifier.width(1150.dp)) {
                    // Header row
                    Surface(
                        color = Color(0xFF0F172A),
                        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CellHeader("மாதம் (Month)", 85.dp)
                            CellHeader("அடிப்படை (BP)", 85.dp)
                            CellHeader("DA (%)", 90.dp)
                            CellHeader("HRA", 70.dp)
                            CellHeader("MA", 55.dp)
                            CellHeader("மொத்த ஊதியம்", 95.dp, Color(0xFF93C5FD))
                            CellHeader("TPF/CPS", 80.dp)
                            CellHeader("FBF", 50.dp)
                            CellHeader("NHIS", 60.dp)
                            CellHeader("முன்பணம்", 80.dp)
                            CellHeader("IT TDS", 75.dp)
                            CellHeader("செஸ் 4%", 65.dp)
                            CellHeader("நிகர ஊதியம் (Net)", 95.dp, Color(0xFF86EFAC))
                            CellHeader("நடவடிக்கை", 80.dp)
                        }
                    }

                    // Data rows
                    monthsData.forEachIndexed { idx, m ->
                        val isEven = idx % 2 == 0
                        Surface(
                            color = if (isEven) Color.White else Color(0xFFF8FAFC),
                            border = BorderStroke(0.5.dp, Color(0xFFE2E8F0))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 5.dp, horizontal = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CellData(m.monthCode, 85.dp, isBold = true)
                                CellData("%,.0f".format(m.basicPay), 85.dp)
                                CellData("${"%,.0f".format(m.daAmount)} (${m.daPercent.toInt()}%)", 90.dp)
                                CellData("%,.0f".format(m.hra), 70.dp)
                                CellData("%,.0f".format(m.ma), 55.dp)
                                CellData("%,.0f".format(m.totalGross), 95.dp, isBold = true, color = Color(0xFF1E3A8A))
                                CellData("%,.0f".format(m.tpfCps), 80.dp)
                                CellData("%,.0f".format(m.fbf), 50.dp)
                                CellData(if (m.nhis > 0) "%,.0f".format(m.nhis + m.nhisCorpus) else "-", 60.dp)
                                CellData(if (m.pfAdvRecovery + m.festivalAdvRecovery > 0) "%,.0f".format(m.pfAdvRecovery + m.festivalAdvRecovery) else "-", 80.dp)
                                CellData(if (m.itTds > 0) "%,.0f".format(m.itTds) else "-", 75.dp)
                                CellData(if (m.itCess > 0) "%,.0f".format(m.itCess) else "-", 65.dp)
                                CellData("%,.0f".format(m.netSalary), 95.dp, isBold = true, color = Color(0xFF15803D))

                                Box(modifier = Modifier.width(80.dp), contentAlignment = Alignment.Center) {
                                    IconButton(
                                        onClick = { onEditMonthClick(m) },
                                        modifier = Modifier.size(28.dp).testTag("btn_edit_month_table_${m.monthIndex}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit",
                                            modifier = Modifier.size(14.dp),
                                            tint = Color(0xFF2563EB)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Total row
                    Surface(
                        color = Color(0xFFE2E8F0),
                        shape = RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp),
                        border = BorderStroke(1.dp, Color(0xFF94A3B8))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CellData("மொத்தம்", 85.dp, isBold = true)
                            CellData("%,.0f".format(monthsData.sumOf { it.basicPay }), 85.dp, isBold = true)
                            CellData("%,.0f".format(monthsData.sumOf { it.daAmount }), 90.dp, isBold = true)
                            CellData("%,.0f".format(monthsData.sumOf { it.hra }), 70.dp, isBold = true)
                            CellData("%,.0f".format(monthsData.sumOf { it.ma }), 55.dp, isBold = true)
                            CellData("%,.0f".format(monthsData.sumOf { it.totalGross }), 95.dp, isBold = true, color = Color(0xFF1E3A8A))
                            CellData("%,.0f".format(monthsData.sumOf { it.tpfCps }), 80.dp, isBold = true)
                            CellData("%,.0f".format(monthsData.sumOf { it.fbf }), 50.dp, isBold = true)
                            CellData("%,.0f".format(monthsData.sumOf { it.nhis + it.nhisCorpus }), 60.dp, isBold = true)
                            CellData("%,.0f".format(monthsData.sumOf { it.pfAdvRecovery + it.festivalAdvRecovery }), 80.dp, isBold = true)
                            CellData("%,.0f".format(monthsData.sumOf { it.itTds }), 75.dp, isBold = true)
                            CellData("%,.0f".format(monthsData.sumOf { it.itCess }), 65.dp, isBold = true)
                            CellData("%,.0f".format(monthsData.sumOf { it.netSalary }), 95.dp, isBold = true, color = Color(0xFF15803D))
                            Box(modifier = Modifier.width(80.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CellHeader(text: String, width: androidx.compose.ui.unit.Dp, color: Color = Color.White) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        modifier = Modifier.width(width)
    )
}

@Composable
private fun CellData(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    isBold: Boolean = false,
    color: Color = Color.Unspecified
) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
        color = if (color != Color.Unspecified) color else MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.width(width)
    )
}
