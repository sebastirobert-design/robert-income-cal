package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.UploadFile
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EmployeeProfile
import com.example.model.TaxYear
import com.example.ui.theme.*

@Composable
fun TopAppBarHeader(
    taxYear: TaxYear,
    profile: EmployeeProfile,
    onYearToggle: () -> Unit,
    onImportPdfClick: () -> Unit,
    onPrintPdfClick: () -> Unit,
    onExportCsvClick: () -> Unit,
    onWhatsAppClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = NavyDark,
        shadowElevation = 3.dp
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 22.dp, top = 6.dp, bottom = 6.dp)
        ) {
            val isWide = maxWidth >= 720.dp

            if (isWide) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    BrandingContent(
                        taxYear = taxYear,
                        profile = profile,
                        onYearToggle = onYearToggle,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    ActionButtonsRow(
                        onImportPdfClick = onImportPdfClick,
                        onPrintPdfClick = onPrintPdfClick,
                        onExportCsvClick = onExportCsvClick,
                        onWhatsAppClick = onWhatsAppClick
                    )
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    BrandingContent(
                        taxYear = taxYear,
                        profile = profile,
                        onYearToggle = onYearToggle,
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    ActionButtonsRow(
                        onImportPdfClick = onImportPdfClick,
                        onPrintPdfClick = onPrintPdfClick,
                        onExportCsvClick = onExportCsvClick,
                        onWhatsAppClick = onWhatsAppClick,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

@Composable
private fun BrandingContent(
    taxYear: TaxYear,
    profile: EmployeeProfile,
    onYearToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Gold Rupee Icon
        Box(
            modifier = Modifier
                .size(32.dp)
                .background(GoldAccent, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "₹",
                color = Color.Black,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text(
                    text = "தமிழ்நாடு எளிய வருமான வரி செயலி",
                    color = Color.White,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.5.sp
                    )
                )
                // Year Toggle Badge
                Surface(
                    shape = RoundedCornerShape(4.dp),
                    color = BluePrimary,
                    modifier = Modifier
                        .clickable { onYearToggle() }
                        .testTag("year_toggle_badge")
                ) {
                    Text(
                        text = taxYear.label,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 1.dp)
                    )
                }
            }
            Text(
                text = "${profile.schoolOffice} (குறியீடு: ${profile.schoolCode.ifBlank { "33230500103" }})",
                color = Color(0xFF94A3B8),
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ActionButtonsRow(
    onImportPdfClick: () -> Unit,
    onPrintPdfClick: () -> Unit,
    onExportCsvClick: () -> Unit,
    onWhatsAppClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .horizontalScroll(rememberScrollState())
            .padding(end = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Button 1: Import Pay Drawn PDF
        Button(
            onClick = onImportPdfClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4338CA)),
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(horizontal = 9.dp, vertical = 2.dp),
            modifier = Modifier
                .height(30.dp)
                .testTag("btn_import_pdf")
        ) {
            Icon(
                imageVector = Icons.Default.UploadFile,
                contentDescription = "Import",
                modifier = Modifier.size(13.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Import Pay Drawn PDF", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
        }

        // Button 2: A4 Print / PDF
        Button(
            onClick = onPrintPdfClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC2410C)),
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(horizontal = 9.dp, vertical = 2.dp),
            modifier = Modifier
                .height(30.dp)
                .testTag("btn_a4_print")
        ) {
            Icon(
                imageVector = Icons.Default.Print,
                contentDescription = "Print",
                modifier = Modifier.size(13.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("A4 Print / PDF", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
        }

        // Button 3: Export CSV
        Button(
            onClick = onExportCsvClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0369A1)),
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(horizontal = 9.dp, vertical = 2.dp),
            modifier = Modifier
                .height(30.dp)
                .testTag("btn_export_csv")
        ) {
            Icon(
                imageVector = Icons.Default.FileDownload,
                contentDescription = "Export CSV",
                modifier = Modifier.size(13.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Export CSV", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
        }

        // Button 4: WhatsApp
        Button(
            onClick = onWhatsAppClick,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF15803D)),
            shape = RoundedCornerShape(6.dp),
            contentPadding = PaddingValues(horizontal = 9.dp, vertical = 2.dp),
            modifier = Modifier
                .height(30.dp)
                .testTag("btn_whatsapp_share")
        ) {
            Icon(
                imageVector = Icons.Default.Share,
                contentDescription = "WhatsApp",
                modifier = Modifier.size(13.dp),
                tint = Color.White
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("WhatsApp", fontSize = 10.5.sp, fontWeight = FontWeight.Bold)
        }
    }
}
