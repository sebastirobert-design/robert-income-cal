package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
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
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Gross Income
        KpiCard(
            titleTamil = "மொத்த ஊதியம்",
            titleEnglish = "GROSS INCOME",
            amount = "₹ %,.0f".format(computation.totalGrossSalary),
            subtitle = "12 மாதங்கள் + பிற வரவுகள்",
            accentColor = CardBorderGross,
            testTag = "kpi_gross_income"
        )

        // 2. Taxable Income
        KpiCard(
            titleTamil = "வரிக்குரிய வருமானம்",
            titleEnglish = "TAXABLE",
            amount = "₹ %,.0f".format(computation.taxableIncome),
            subtitle = "நிலையான கழிவு ₹ %,.0f கழித்து".format(computation.standardDeduction),
            accentColor = CardBorderTaxable,
            testTag = "kpi_taxable_income"
        )

        // 3. Total Tax + Cess
        KpiCard(
            titleTamil = "மொத்த வரி + CESS",
            titleEnglish = "TOTAL TAX",
            amount = "₹ %,.1f".format(computation.totalTaxPayable),
            subtitle = "${computation.regime.labelEnglish} (87A தள்ளுபடி)",
            accentColor = CardBorderTax,
            testTag = "kpi_total_tax"
        )

        // 4. TDS Paid (March - Oct)
        KpiCard(
            titleTamil = "பிடித்தம் ஆனது",
            titleEnglish = "MARCH-OCT PAID",
            amount = "₹ %,.0f".format(computation.tdsPaidSoFar),
            subtitle = "8 மாத ஊதியப் பட்டியல் TDS",
            accentColor = CardBorderTds,
            testTag = "kpi_tds_paid"
        )

        // 5. Balance to Deduct
        KpiCard(
            titleTamil = "மீதமுள்ள வரி",
            titleEnglish = "BALANCE TO DEDUCT",
            amount = "₹ %,.1f".format(computation.balanceTaxToDeduct),
            subtitle = "₹ %,.0f / mo".format(computation.monthlyBalanceDeduction),
            accentColor = CardBorderBalance,
            testTag = "kpi_balance_tax"
        )
    }
}

@Composable
private fun KpiCard(
    titleTamil: String,
    titleEnglish: String,
    amount: String,
    subtitle: String,
    accentColor: Color,
    testTag: String
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .height(105.dp)
            .testTag(testTag),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.5.dp, accentColor),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "$titleTamil ($titleEnglish)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = accentColor
                    ),
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = amount,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    maxLines = 1
                )
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontSize = 10.5.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                maxLines = 1
            )
        }
    }
}
