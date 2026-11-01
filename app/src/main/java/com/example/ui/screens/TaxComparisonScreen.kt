package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.TrendingDown
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
import com.example.model.OldVsNewComparison
import com.example.model.TaxComputation
import com.example.model.TaxRegime

@Composable
fun TaxComparisonScreen(
    selectedRegime: TaxRegime,
    onSelectRegime: (TaxRegime) -> Unit,
    newComputation: TaxComputation,
    oldComputation: TaxComputation,
    comparison: OldVsNewComparison,
    deductions80C: Double,
    deductions80CCD: Double,
    deductions80D: Double,
    onUpdateOldDeductions: (d80C: Double, d80CCD: Double, d80D: Double) -> Unit,
    modifier: Modifier = Modifier
) {
    var text80C by remember(deductions80C) { mutableStateOf(deductions80C.toInt().toString()) }
    var text80CCD by remember(deductions80CCD) { mutableStateOf(deductions80CCD.toInt().toString()) }
    var text80D by remember(deductions80D) { mutableStateOf(deductions80D.toInt().toString()) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(start = 10.dp, end = 20.dp, top = 8.dp, bottom = 14.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Regime Selector Toggle
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, Color(0xFFCBD5E1))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "செயலில் உள்ள வரி முறை தேர்வு (Select Active Regime):",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val isNew = selectedRegime == TaxRegime.NEW_REGIME
                    Button(
                        onClick = { onSelectRegime(TaxRegime.NEW_REGIME) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isNew) Color(0xFF4F46E5) else Color(0xFFE2E8F0),
                            contentColor = if (isNew) Color.White else Color(0xFF334155)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(44.dp).testTag("select_new_regime")
                    ) {
                        Text("புதிய முறை (New)", fontWeight = FontWeight.Bold)
                    }

                    val isOld = selectedRegime == TaxRegime.OLD_REGIME
                    Button(
                        onClick = { onSelectRegime(TaxRegime.OLD_REGIME) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isOld) Color(0xFF4F46E5) else Color(0xFFE2E8F0),
                            contentColor = if (isOld) Color.White else Color(0xFF334155)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.weight(1f).height(44.dp).testTag("select_old_regime")
                    ) {
                        Text("பழைய முறை (Old)", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Recommendation & Savings Banner
        Card(
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDF4)),
            border = BorderStroke(1.5.dp, Color(0xFF86EFAC)),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Savings,
                    contentDescription = "Savings",
                    tint = Color(0xFF16A34A),
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "பரிந்துரை: ${comparison.recommendedRegime.labelTamil} (${comparison.recommendedRegime.labelEnglish})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = Color(0xFF15803D)
                    )
                    Text(
                        text = comparison.explanationTamil,
                        fontSize = 12.sp,
                        color = Color(0xFF166534)
                    )
                }
            }
        }

        // Side-by-Side Comparison Table
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, Color(0xFFCBD5E1))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "இரு முறைகளின் நேரடி ஒப்பீடு (Comparison Table)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF0F172A)
                )
                Divider(color = Color(0xFFE2E8F0))

                // Table Header
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("விவரம் (Particulars)", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, modifier = Modifier.weight(1.3f))
                    Text("புதிய முறை (New)", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = Color(0xFF4F46E5), modifier = Modifier.weight(1f))
                    Text("பழைய முறை (Old)", fontWeight = FontWeight.Bold, fontSize = 11.5.sp, color = Color(0xFFD97706), modifier = Modifier.weight(1f))
                }
                Divider(color = Color(0xFFE2E8F0))

                CompareRow("1. மொத்த வருமானம் (Gross)", "₹ %,.0f".format(newComputation.totalGrossSalary), "₹ %,.0f".format(oldComputation.totalGrossSalary))
                CompareRow("2. நிலையான கழிவு (Std. Ded.)", "₹ %,.0f".format(newComputation.standardDeduction), "₹ %,.0f".format(oldComputation.standardDeduction))
                CompareRow("3. 80C கழிவு (TPF/LIC/GPF)", "-", "₹ %,.0f".format(deductions80C))
                CompareRow("4. 80CCD(1B) கழிவு (CPS/NPS)", "-", "₹ %,.0f".format(deductions80CCD))
                CompareRow("5. 80D மருத்துவக் காப்பீடு (NHIS)", "-", "₹ %,.0f".format(deductions80D))
                Divider(color = Color(0xFFE2E8F0))
                CompareRow("வரிக்குரிய வருமானம் (Taxable)", "₹ %,.0f".format(newComputation.taxableIncome), "₹ %,.0f".format(oldComputation.taxableIncome), isBold = true)
                CompareRow("வரி கணக்கீடு (Tax Before Cess)", "₹ %,.2f".format(newComputation.taxAfterRebate), "₹ %,.2f".format(oldComputation.taxAfterRebate))
                CompareRow("செஸ் @ 4% (Health & Edu Cess)", "₹ %,.2f".format(newComputation.cess4Percent), "₹ %,.2f".format(oldComputation.cess4Percent))
                Divider(color = Color(0xFFCBD5E1))
                CompareRow(
                    "மொத்த வரி + CESS (Total Tax)",
                    "₹ %,.2f".format(newComputation.totalTaxPayable),
                    "₹ %,.2f".format(oldComputation.totalTaxPayable),
                    isBold = true,
                    highlight = true
                )
            }
        }

        // New Tax Slabs Detail Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, Color(0xFFCBD5E1))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "புதிய வரி அடுக்குகள் & கணக்கீடு (New Regime Slabs Breakdown)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp,
                    color = Color(0xFF1E3A8A)
                )
                Divider(color = Color(0xFFE2E8F0))

                for (slab in newComputation.slabBreakdowns) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(slab.slabRangeTamil, fontSize = 11.5.sp, fontWeight = FontWeight.Medium)
                            Text(slab.slabRange, fontSize = 10.sp, color = Color(0xFF64748B))
                        }
                        Text(
                            text = if (slab.taxAmount > 0) "₹ %,.2f".format(slab.taxAmount) else "NIL",
                            fontSize = 12.sp,
                            fontWeight = if (slab.taxAmount > 0) FontWeight.Bold else FontWeight.Normal,
                            color = if (slab.taxAmount > 0) Color(0xFF0F172A) else Color(0xFF64748B)
                        )
                    }
                    Divider(color = Color(0xFFF1F5F9))
                }
            }
        }

        // Old Regime Deduction Adjuster Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, Color(0xFFCBD5E1))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Text(
                    text = "பழைய வரி முறை கழிவுகள் (Old Regime VI-A Deductions)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.5.sp
                )
                Text(
                    text = "80C, 80CCD மற்றும் 80D சேமிப்புகளை மாற்றி பழைய வரி முறையுடன் ஒப்பிட்டுப் பார்க்கலாம்.",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B)
                )

                OutlinedTextField(
                    value = text80C,
                    onValueChange = {
                        text80C = it
                        val v = it.toDoubleOrNull() ?: 0.0
                        onUpdateOldDeductions(v, text80CCD.toDoubleOrNull() ?: 0.0, text80D.toDoubleOrNull() ?: 0.0)
                    },
                    label = { Text("பிரிவு 80C (TPF, LIC, GPF, PPF - உச்சவரம்பு 1.5 லட்சம்)") },
                    prefix = { Text("₹ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = text80CCD,
                    onValueChange = {
                        text80CCD = it
                        val v = it.toDoubleOrNull() ?: 0.0
                        onUpdateOldDeductions(text80C.toDoubleOrNull() ?: 0.0, v, text80D.toDoubleOrNull() ?: 0.0)
                    },
                    label = { Text("பிரிவு 80CCD(1B) (கூடுதல் CPS / NPS - உச்சவரம்பு 50,000)") },
                    prefix = { Text("₹ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = text80D,
                    onValueChange = {
                        text80D = it
                        val v = it.toDoubleOrNull() ?: 0.0
                        onUpdateOldDeductions(text80C.toDoubleOrNull() ?: 0.0, text80CCD.toDoubleOrNull() ?: 0.0, v)
                    },
                    label = { Text("பிரிவு 80D (மருத்துவக் காப்பீடு / NHIS)") },
                    prefix = { Text("₹ ") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }
    }
}

@Composable
private fun CompareRow(
    title: String,
    newVal: String,
    oldVal: String,
    isBold: Boolean = false,
    highlight: Boolean = false
) {
    Surface(
        color = if (highlight) Color(0xFFFEF3C7) else Color.Transparent,
        shape = RoundedCornerShape(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = if (highlight) 6.dp else 2.dp, horizontal = if (highlight) 6.dp else 0.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = title,
                fontSize = 11.5.sp,
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
                modifier = Modifier.weight(1.3f)
            )
            Text(
                text = newVal,
                fontSize = 12.sp,
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
                color = if (highlight) Color(0xFF4338CA) else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
            Text(
                text = oldVal,
                fontSize = 12.sp,
                fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
                color = if (highlight) Color(0xFFB45309) else MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.weight(1f)
            )
        }
    }
}
