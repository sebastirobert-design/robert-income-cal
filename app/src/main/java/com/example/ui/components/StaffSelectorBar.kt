package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.EmployeeProfile
import com.example.ui.theme.NavyDark

@Composable
fun StaffSelectorBar(
    staffList: List<EmployeeProfile>,
    selectedStaff: EmployeeProfile,
    onSelectStaff: (EmployeeProfile) -> Unit,
    onAddTeacherClick: () -> Unit,
    onEditStaffClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = 1.dp
    ) {
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, end = 22.dp, top = 4.dp, bottom = 4.dp)
        ) {
            val isCompact = maxWidth < 600.dp

            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // Header row with responsive spacing & right clearance
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = if (isCompact) "பணியாளர் தேர்வு (Staff Select):" else "பணியாளர் விரைவுத் தேர்வு (Quick Staff Select):",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = if (isCompact) 10.sp else 10.5.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        onClick = onEditStaffClick,
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                        shape = RoundedCornerShape(6.dp),
                        contentPadding = PaddingValues(horizontal = 7.dp, vertical = 2.dp),
                        modifier = Modifier
                            .height(28.dp)
                            .widthIn(max = if (isCompact) 155.dp else 240.dp)
                            .testTag("btn_edit_staff_data")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit Staff",
                            modifier = Modifier.size(12.dp),
                            tint = Color.White
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = if (isCompact) "தரவு திருத்து (Edit)" else "தரவு உள்ளீடு / திருத்து (Edit Staff & DA)",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Horizontal chips row with margin on end
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                        .padding(end = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    for (staff in staffList) {
                        val isSelected = staff.id == selectedStaff.id
                        val chipBg = if (isSelected) NavyDark else MaterialTheme.colorScheme.surface
                        val chipText = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                        val borderStroke = if (isSelected) null else BorderStroke(1.dp, Color(0xFFCBD5E1))

                        Surface(
                            onClick = { onSelectStaff(staff) },
                            shape = RoundedCornerShape(16.dp),
                            color = chipBg,
                            border = borderStroke,
                            shadowElevation = if (isSelected) 1.dp else 0.dp,
                            modifier = Modifier.testTag("staff_chip_${staff.id}")
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = staff.name,
                                    fontSize = 11.sp,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = chipText
                                )
                                Spacer(modifier = Modifier.width(5.dp))
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isSelected) Color(0xFF1E3A8A) else Color(0xFFE2E8F0)
                                ) {
                                    Text(
                                        text = "₹ %,.0f".format(staff.basicPayMarch),
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (isSelected) Color(0xFF93C5FD) else Color(0xFF334155),
                                        modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Add Custom Teacher chip
                    OutlinedButton(
                        onClick = onAddTeacherClick,
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 3.dp),
                        modifier = Modifier
                            .height(28.dp)
                            .testTag("btn_add_teacher")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            modifier = Modifier.size(13.dp)
                        )
                        Spacer(modifier = Modifier.width(3.dp))
                        Text(
                            text = "ஆசிரியர் சேர்க்க (+)",
                            fontSize = 10.5.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}
