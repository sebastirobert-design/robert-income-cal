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
import androidx.compose.material.icons.filled.PictureAsPdf
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
        shadowElevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            // Row 1: Logo, App Title, Year Badge & School info
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Gold Rupee Icon
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(GoldAccent, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "₹",
                        color = Color.Black,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "தமிழ்நாடு எளிய வருமான வரி செயலி",
                            color = Color.White,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        )
                        // Year Toggle Badge
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = BluePrimary,
                            modifier = Modifier
                                .clickable { onYearToggle() }
                                .testTag("year_toggle_badge")
                        ) {
                            Text(
                                text = taxYear.label,
                                color = Color.White,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = 7.dp, vertical = 2.dp)
                            )
                        }
                    }
                    Text(
                        text = "${profile.schoolOffice} (குறியீடு: ${profile.schoolCode.ifBlank { "33230500103" }})",
                        color = Color(0xFF94A3B8),
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Row 2: Action buttons (Import PDF, A4 Print, Export CSV, WhatsApp)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Button 1: Import Pay Drawn PDF
                Button(
                    onClick = onImportPdfClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("btn_import_pdf")
                ) {
                    Icon(
                        imageVector = Icons.Default.UploadFile,
                        contentDescription = "Import",
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("Import Pay Drawn PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // Button 2: A4 Print / PDF
                Button(
                    onClick = onPrintPdfClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("btn_a4_print")
                ) {
                    Icon(
                        imageVector = Icons.Default.Print,
                        contentDescription = "Print",
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("A4 Print / PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // Button 3: Export CSV
                Button(
                    onClick = onExportCsvClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("btn_export_csv")
                ) {
                    Icon(
                        imageVector = Icons.Default.FileDownload,
                        contentDescription = "Export CSV",
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("Export CSV", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                // Button 4: WhatsApp
                Button(
                    onClick = onWhatsAppClick,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF16A34A)),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    modifier = Modifier
                        .height(36.dp)
                        .testTag("btn_whatsapp_share")
                ) {
                    Icon(
                        imageVector = Icons.Default.Share,
                        contentDescription = "WhatsApp",
                        modifier = Modifier.size(16.dp),
                        tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text("WhatsApp", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
