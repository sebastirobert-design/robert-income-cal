package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import com.example.model.*
import com.example.ui.theme.NavyDark

@Composable
fun OnePageStatementScreen(
    profile: EmployeeProfile,
    taxYear: TaxYear,
    monthsData: List<SalaryMonthData>,
    earnings: OtherEarnings,
    computation: TaxComputation,
    onEditMonthClick: (SalaryMonthData) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(12.dp)
            .testTag("one_page_statement_card"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header
            Text(
                text = "INCOME TAX CALCULATION STATEMENT FOR THE",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Black,
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Text(
                text = "FINANCIAL YEAR ${taxYear.label.replace("FY ", "")} (${taxYear.assessmentYear}) - ${computation.regime.labelEnglish.uppercase()}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = Color(0xFF475569)
                ),
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Metadata Grid Box
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFF8FAFC),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFE2E8F0))
            ) {
                Column(modifier = Modifier.padding(10.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        MetaItem("NAME", profile.name, Modifier.weight(1.2f))
                        MetaItem("IFHRMS NO", profile.ifhrmsNo, Modifier.weight(1f))
                        MetaItem("DESIGNATION", profile.designation, Modifier.weight(1.2f))
                        MetaItem("PAN NO", profile.panNo, Modifier.weight(0.9f))
                    }
                    Divider(color = Color(0xFFE2E8F0), thickness = 0.5.dp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        MetaItem("SCHOOL", profile.schoolOffice, Modifier.weight(1.5f))
                        MetaItem("BEO / TAN NO", profile.beoTanNo, Modifier.weight(1f))
                        MetaItem("TPF / CPS NO", profile.tpfCpsNo, Modifier.weight(1f))
                        MetaItem("DA RATES", "Jan: ${taxYear.defaultJanDa.toInt()}% | Jul: ${taxYear.defaultJulDa.toInt()}%", Modifier.weight(1.1f))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // 12-Month Table (Horizontal scrollable for responsive fit)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
            ) {
                Column(modifier = Modifier.width(920.dp)) {
                    // Table Header
                    Surface(
                        color = Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp),
                        border = BorderStroke(1.dp, Color(0xFFCBD5E1))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp, horizontal = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            TableHeaderCell("MONTH", 90.dp)
                            TableHeaderCell("BASIC PAY", 100.dp)
                            TableHeaderCell("PP / SPL", 75.dp)
                            TableHeaderCell("DA", 120.dp)
                            TableHeaderCell("HRA", 80.dp)
                            TableHeaderCell("MA", 65.dp)
                            TableHeaderCell("TOTAL GROSS", 120.dp)
                            TableHeaderCell("IT", 90.dp)
                            TableHeaderCell("IT CESS", 80.dp)
                            TableHeaderCell("திருத்து", 90.dp)
                        }
                    }

                    // Month Rows
                    monthsData.forEachIndexed { index, m ->
                        val isEven = index % 2 == 0
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
                                TableDataCell(m.monthCode, 90.dp, isBold = true)
                                TableDataCell("%,.0f".format(m.basicPay), 100.dp)
                                TableDataCell(if (m.ppSpl > 0) "%,.0f".format(m.ppSpl) else "-", 75.dp)
                                TableDataCell("${"%,.0f".format(m.daAmount)} (${m.daPercent.toInt()}%)", 120.dp)
                                TableDataCell("%,.0f".format(m.hra), 80.dp)
                                TableDataCell("%,.0f".format(m.ma), 65.dp)
                                TableDataCell("%,.0f".format(m.totalGross), 120.dp, isBold = true, textColor = Color(0xFF1E3A8A))
                                TableDataCell(if (m.itTds > 0) "%,.0f".format(m.itTds) else "-", 90.dp)
                                TableDataCell(if (m.itCess > 0) "%,.0f".format(m.itCess) else "-", 80.dp)

                                Box(modifier = Modifier.width(90.dp), contentAlignment = Alignment.Center) {
                                    OutlinedButton(
                                        onClick = { onEditMonthClick(m) },
                                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp),
                                        modifier = Modifier.height(28.dp).testTag("btn_edit_month_${m.monthIndex}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "Edit",
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text("திருத்து", fontSize = 11.sp)
                                    }
                                }
                            }
                        }
                    }

                    // Total Row
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
                            TableDataCell("TOTAL", 90.dp, isBold = true)
                            TableDataCell("%,.0f".format(monthsData.sumOf { it.basicPay }), 100.dp, isBold = true)
                            TableDataCell("-", 75.dp)
                            TableDataCell("%,.0f".format(monthsData.sumOf { it.daAmount }), 120.dp, isBold = true)
                            TableDataCell("%,.0f".format(monthsData.sumOf { it.hra }), 80.dp, isBold = true)
                            TableDataCell("%,.0f".format(monthsData.sumOf { it.ma }), 65.dp, isBold = true)
                            TableDataCell("%,.0f".format(monthsData.sumOf { it.totalGross }), 120.dp, isBold = true, textColor = Color(0xFF1E3A8A))
                            TableDataCell("%,.0f".format(monthsData.sumOf { it.itTds }), 90.dp, isBold = true)
                            TableDataCell("%,.0f".format(monthsData.sumOf { it.itCess }), 80.dp, isBold = true)
                            Box(modifier = Modifier.width(90.dp))
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Other Arrears Bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = Color(0xFFFEF3C7),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, Color(0xFFFCD34D))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "பிற வரவுகள் (Other Arrears & Bonus):  DA Arrear: ₹ %,.0f  |  Bonus: ₹ %,.0f  |  Surrender: ₹ %,.0f".format(
                            earnings.daArrear1,
                            earnings.bonus,
                            earnings.surrenderAmount
                        ),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF92400E)
                    )
                    Text(
                        text = "மொத்த ஊதியம்: ₹ %,.0f".format(computation.totalGrossSalary),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = Color(0xFF78350F)
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Income / Tax Computation Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Left Column: Income & Deductions
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "வருமானம் & நிலையான கழிவு (INCOME PARTICULARS)",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFF1E40AF))
                        )
                        Divider(color = Color(0xFFE2E8F0))
                        CompRow("மொத்த ஊதிய வருமானம் (Total Gross Salary)", "₹ %,.0f".format(computation.totalGrossSalary))
                        CompRow("கழிக்க: நிலையான கழிவு (Standard Deduction)", "₹ %,.0f".format(computation.standardDeduction))
                        if (computation.otherDeductionsChapter6A > 0) {
                            CompRow("கழிக்க: அத்தியாயம் VI-A (80C, 80CCD)", "₹ %,.0f".format(computation.otherDeductionsChapter6A))
                        }
                        Divider(color = Color(0xFFCBD5E1))
                        CompRow(
                            "வரிக்குரிய வருமானம் (NET TAXABLE INCOME)",
                            "₹ %,.0f".format(computation.taxableIncome),
                            isBold = true,
                            color = Color(0xFF7C3AED)
                        )
                    }
                }

                // Right Column: Tax Slabs & Balance
                Card(
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF8FAFC)),
                    border = BorderStroke(1.dp, Color(0xFFCBD5E1)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "வரி கணக்கீடு (COMPUTATION OF TAX)",
                            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold, color = Color(0xFFD97706))
                        )
                        Divider(color = Color(0xFFE2E8F0))
                        CompRow("வரிக்குரிய வருமானத்திற்கான வரி", "₹ %,.2f".format(computation.taxBeforeRebate))
                        CompRow("பிரிவு 87A வரி தள்ளுபடி (Rebate)", "₹ %,.2f".format(computation.rebate87A + computation.marginalRelief))
                        CompRow("கூட்டுக: 4% செஸ் (Health & Edu Cess)", "₹ %,.2f".format(computation.cess4Percent))
                        Divider(color = Color(0xFFE2E8F0))
                        CompRow("மொத்த வரி + CESS (Total Tax Payable)", "₹ %,.2f".format(computation.totalTaxPayable), isBold = true)
                        CompRow("கழிக்க: பிடித்தம் ஆனது (TDS Paid)", "₹ %,.0f".format(computation.tdsPaidSoFar), color = Color(0xFF059669))
                        Divider(color = Color(0xFFCBD5E1))
                        Surface(color = Color(0xFFFFE4E6), shape = RoundedCornerShape(6.dp)) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 8.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = "மீதமுள்ள வரி (Balance):",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = Color(0xFFBE123C)
                                )
                                Text(
                                    text = "₹ %,.2f (₹ %,.0f/mo)".format(computation.balanceTaxToDeduct, computation.monthlyBalanceDeduction),
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 12.5.sp,
                                    color = Color(0xFFBE123C)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Signature & Declaration
            Text(
                text = "இவ்வறிக்கையில் வழங்கப்பட்டுள்ள அனைத்து தகவல்களும் உண்மை மற்றும் சரியானவை. பெறப்பட்ட அனைத்து வருமானங்களும் விடுபடலின்றி காண்பிக்கப்பட்டுள்ளன.",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp, color = Color(0xFF64748B))
            )
            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("அரசு ஊழியரின் கையொப்பம்", fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                    Text("பணியாளர் பெயர்: ${profile.name}", fontSize = 11.sp, color = Color(0xFF475569))
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("அலுவலகத் தலைவர் / வரைவு அலுவலர் கையொப்பம்", fontWeight = FontWeight.Bold, fontSize = 11.5.sp)
                    Text("முத்திரை மற்றும் நாள்", fontSize = 11.sp, color = Color(0xFF475569))
                }
            }
        }
    }
}

@Composable
private fun MetaItem(label: String, value: String, modifier: Modifier = Modifier) {
    Column(modifier = modifier) {
        Text(text = label, fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color(0xFF64748B))
        Text(text = value, fontSize = 11.5.sp, fontWeight = FontWeight.ExtraBold, color = Color(0xFF0F172A), maxLines = 1)
    }
}

@Composable
private fun TableHeaderCell(text: String, width: androidx.compose.ui.unit.Dp) {
    Text(
        text = text,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF334155),
        modifier = Modifier.width(width)
    )
}

@Composable
private fun TableDataCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    isBold: Boolean = false,
    textColor: Color = Color.Unspecified
) {
    Text(
        text = text,
        fontSize = 11.5.sp,
        fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
        color = if (textColor != Color.Unspecified) textColor else MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.width(width)
    )
}

@Composable
private fun CompRow(
    label: String,
    value: String,
    isBold: Boolean = false,
    color: Color = Color.Unspecified
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (color != Color.Unspecified) color else Color(0xFF334155)
        )
        Text(
            text = value,
            fontSize = 11.5.sp,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.SemiBold,
            color = if (color != Color.Unspecified) color else Color(0xFF0F172A)
        )
    }
}
