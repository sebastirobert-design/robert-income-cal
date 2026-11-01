package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.TaxComputation
import com.example.ui.theme.*

@Composable
fun SummaryCardsRow(
    computation: TaxComputation,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val isWide = maxWidth >= 720.dp

        if (isWide) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 10.dp, end = 22.dp, top = 4.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // 1. Gross Income
                KpiCard(
                    titleTamil = "மொத்த ஊதியம்",
                    titleEnglish = "GROSS INCOME",
                    amount = "₹ %,.0f".format(computation.totalGrossSalary),
                    subtitle = "12 மாதங்கள் + பிற வரவுகள்",
                    accentColor = CardBorderGross,
                    testTag = "kpi_gross_income",
                    modifier = Modifier.weight(1f).height(74.dp)
                )

                // 2. Taxable Income
                KpiCard(
                    titleTamil = "வரிக்குரிய வருமானம்",
                    titleEnglish = "TAXABLE",
                    amount = "₹ %,.0f".format(computation.taxableIncome),
                    subtitle = "நிலையான கழிவு ₹ %,.0f கழித்து".format(computation.standardDeduction),
                    accentColor = CardBorderTaxable,
                    testTag = "kpi_taxable_income",
                    modifier = Modifier.weight(1f).height(74.dp)
                )

                // 3. Total Tax + Cess
                KpiCard(
                    titleTamil = "மொத்த வரி + CESS",
                    titleEnglish = "TOTAL TAX",
                    amount = "₹ %,.1f".format(computation.totalTaxPayable),
                    subtitle = "${computation.regime.labelEnglish} (87A தள்ளுபடி)",
                    accentColor = CardBorderTax,
                    testTag = "kpi_total_tax",
                    modifier = Modifier.weight(1f).height(74.dp)
                )

                // 4. TDS Paid (March - Oct)
                KpiCard(
                    titleTamil = "பிடித்தம் ஆனது",
                    titleEnglish = "MARCH-OCT PAID",
                    amount = "₹ %,.0f".format(computation.tdsPaidSoFar),
                    subtitle = "8 மாத ஊதியப் பட்டியல் TDS",
                    accentColor = CardBorderTds,
                    testTag = "kpi_tds_paid",
                    modifier = Modifier.weight(1f).height(74.dp)
                )

                // 5. Balance to Deduct
                KpiCard(
                    titleTamil = "மீதமுள்ள வரி",
                    titleEnglish = "BALANCE TO DEDUCT",
                    amount = "₹ %,.1f".format(computation.balanceTaxToDeduct),
                    subtitle = "₹ %,.0f / mo".format(computation.monthlyBalanceDeduction),
                    accentColor = CardBorderBalance,
                    testTag = "kpi_balance_tax",
                    modifier = Modifier.weight(1f).height(74.dp)
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(start = 10.dp, end = 22.dp, top = 4.dp, bottom = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                // 1. Gross Income
                KpiCard(
                    titleTamil = "மொத்த ஊதியம்",
                    titleEnglish = "GROSS INCOME",
                    amount = "₹ %,.0f".format(computation.totalGrossSalary),
                    subtitle = "12 மாதங்கள் + பிற வரவுகள்",
                    accentColor = CardBorderGross,
                    testTag = "kpi_gross_income",
                    modifier = Modifier.width(145.dp).height(74.dp)
                )

                // 2. Taxable Income
                KpiCard(
                    titleTamil = "வரிக்குரிய வருமானம்",
                    titleEnglish = "TAXABLE",
                    amount = "₹ %,.0f".format(computation.taxableIncome),
                    subtitle = "நிலையான கழிவு ₹ %,.0f கழித்து".format(computation.standardDeduction),
                    accentColor = CardBorderTaxable,
                    testTag = "kpi_taxable_income",
                    modifier = Modifier.width(145.dp).height(74.dp)
                )

                // 3. Total Tax + Cess
                KpiCard(
                    titleTamil = "மொத்த வரி + CESS",
                    titleEnglish = "TOTAL TAX",
                    amount = "₹ %,.1f".format(computation.totalTaxPayable),
                    subtitle = "${computation.regime.labelEnglish} (87A தள்ளுபடி)",
                    accentColor = CardBorderTax,
                    testTag = "kpi_total_tax",
                    modifier = Modifier.width(145.dp).height(74.dp)
                )

                // 4. TDS Paid (March - Oct)
                KpiCard(
                    titleTamil = "பிடித்தம் ஆனது",
                    titleEnglish = "MARCH-OCT PAID",
                    amount = "₹ %,.0f".format(computation.tdsPaidSoFar),
                    subtitle = "8 மாத ஊதியப் பட்டியல் TDS",
                    accentColor = CardBorderTds,
                    testTag = "kpi_tds_paid",
                    modifier = Modifier.width(145.dp).height(74.dp)
                )

                // 5. Balance to Deduct
                KpiCard(
                    titleTamil = "மீதமுள்ள வரி",
                    titleEnglish = "BALANCE TO DEDUCT",
                    amount = "₹ %,.1f".format(computation.balanceTaxToDeduct),
                    subtitle = "₹ %,.0f / mo".format(computation.monthlyBalanceDeduction),
                    accentColor = CardBorderBalance,
                    testTag = "kpi_balance_tax",
                    modifier = Modifier.width(145.dp).height(74.dp)
                )
            }
        }
    }
}

@Composable
private fun KpiCard(
    titleTamil: String,
    titleEnglish: String,
    amount: String,
    subtitle: String,
    accentColor: Color,
    testTag: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .testTag(testTag),
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.2.dp, accentColor),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 7.dp, vertical = 5.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "$titleTamil ($titleEnglish)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 9.5.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    ),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(1.dp))
                Text(
                    text = amount,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 15.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    maxLines = 1
                )
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 8.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                maxLines = 1
            )
        }
    }
}
