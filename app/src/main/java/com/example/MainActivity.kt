package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.TaxYear
import com.example.ui.TaxViewModel
import com.example.ui.components.StaffSelectorBar
import com.example.ui.components.SummaryCardsRow
import com.example.ui.components.TopAppBarHeader
import com.example.ui.dialogs.AddTeacherDialog
import com.example.ui.dialogs.EditMonthDialog
import com.example.ui.dialogs.EditStaffDialog
import com.example.ui.dialogs.ImportDataDialog
import com.example.ui.screens.AutoFillDataEntryScreen
import com.example.ui.screens.MonthlyPayDrawnScreen
import com.example.ui.screens.OnePageStatementScreen
import com.example.ui.screens.TaxComparisonScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NavyDark
import com.example.ui.theme.BluePrimary

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                TaxCalculatorApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaxCalculatorApp(
    viewModel: TaxViewModel = viewModel()
) {
    val context = LocalContext.current
    val taxYear by viewModel.selectedYear.collectAsStateWithLifecycle()
    val selectedRegime by viewModel.selectedRegime.collectAsStateWithLifecycle()
    val currentProfile by viewModel.currentProfile.collectAsStateWithLifecycle()
    val staffList by viewModel.staffList.collectAsStateWithLifecycle()
    val monthsData by viewModel.monthsData.collectAsStateWithLifecycle()
    val otherEarnings by viewModel.otherEarnings.collectAsStateWithLifecycle()
    val newComp by viewModel.newRegimeComputation.collectAsStateWithLifecycle()
    val oldComp by viewModel.oldRegimeComputation.collectAsStateWithLifecycle()
    val comparison by viewModel.comparison.collectAsStateWithLifecycle()
    val activeTab by viewModel.activeTab.collectAsStateWithLifecycle()
    val editingMonth by viewModel.editingMonth.collectAsStateWithLifecycle()
    val showEditStaffDialog by viewModel.showEditStaffDialog.collectAsStateWithLifecycle()
    val showImportDialog by viewModel.showImportDialog.collectAsStateWithLifecycle()
    val snackbarMessage by viewModel.snackbarMessage.collectAsStateWithLifecycle()
    val deductions80C by viewModel.deductions80C.collectAsStateWithLifecycle()
    val deductions80CCD by viewModel.deductions80CCD.collectAsStateWithLifecycle()
    val deductions80D by viewModel.deductions80D.collectAsStateWithLifecycle()

    var showAddTeacherDialog by remember { mutableStateOf(false) }
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMessage) {
        snackbarMessage?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    val activeComp = viewModel.getActiveComputation()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBarHeader(
                taxYear = taxYear,
                profile = currentProfile,
                onYearToggle = {
                    val nextYear = if (taxYear == TaxYear.FY_2026_27) TaxYear.FY_2025_26 else TaxYear.FY_2026_27
                    viewModel.setYear(nextYear)
                },
                onImportPdfClick = { viewModel.setImportDialog(true) },
                onPrintPdfClick = { viewModel.sharePdf(context) },
                onExportCsvClick = { viewModel.shareCsv(context) },
                onWhatsAppClick = { viewModel.shareWhatsApp(context) },
                modifier = Modifier.statusBarsPadding()
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            // Summary Cards Row
            SummaryCardsRow(computation = activeComp)

            // Tabs Row
            val tabs = listOf(
                "1 பக்க அதிகாரப்பூர்வ அறிக்கை (1-Page IT Statement)" to Icons.Default.Description,
                "தரவு உள்ளீடு / அமைப்புகள் (Data Entry & Setup)" to Icons.Default.Settings,
                "12 மாத ஊதியப் பட்டியல் (Monthly Pay Drawn)" to Icons.Default.TableChart,
                "வரி கணக்கீடு & ஒப்பீடு (Tax Slabs & Old vs New)" to Icons.Default.Balance
            )

            ScrollableTabRow(
                selectedTabIndex = activeTab,
                edgePadding = 12.dp,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("main_tabs_row"),
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = BluePrimary
            ) {
                tabs.forEachIndexed { index, (title, icon) ->
                    Tab(
                        selected = activeTab == index,
                        onClick = { viewModel.selectTab(index) },
                        text = {
                            Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                                Icon(imageVector = icon, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = title,
                                    fontSize = 11.5.sp,
                                    fontWeight = if (activeTab == index) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        },
                        modifier = Modifier.testTag("tab_item_$index")
                    )
                }
            }

            // Quick Staff Selector
            StaffSelectorBar(
                staffList = staffList,
                selectedStaff = currentProfile,
                onSelectStaff = { viewModel.loadStaffProfile(it) },
                onAddTeacherClick = { showAddTeacherDialog = true },
                onEditStaffClick = { viewModel.setEditStaffDialog(true) }
            )

            // Content according to active tab
            Box(modifier = Modifier.weight(1f)) {
                when (activeTab) {
                    0 -> OnePageStatementScreen(
                        profile = currentProfile,
                        taxYear = taxYear,
                        monthsData = monthsData,
                        earnings = otherEarnings,
                        computation = activeComp,
                        onEditMonthClick = { viewModel.setEditingMonth(it) },
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    )
                    1 -> AutoFillDataEntryScreen(
                        profile = currentProfile,
                        taxYear = taxYear,
                        earnings = otherEarnings,
                        onAutoFill = { bp, hra, grade, incMonth, janDa, julDa, daArr, bonus, surrenderDays ->
                            viewModel.autoFillData(bp, hra, grade, incMonth, janDa, julDa, daArr, bonus, surrenderDays)
                            viewModel.selectTab(0) // Automatically jump to 1-Page statement to show results!
                        },
                        onPasteIfhrmsClick = { viewModel.setImportDialog(true) }
                    )
                    2 -> MonthlyPayDrawnScreen(
                        monthsData = monthsData,
                        onEditMonthClick = { viewModel.setEditingMonth(it) },
                        modifier = Modifier.verticalScroll(rememberScrollState())
                    )
                    3 -> TaxComparisonScreen(
                        selectedRegime = selectedRegime,
                        onSelectRegime = { viewModel.setRegime(it) },
                        newComputation = newComp,
                        oldComputation = oldComp,
                        comparison = comparison,
                        deductions80C = deductions80C,
                        deductions80CCD = deductions80CCD,
                        deductions80D = deductions80D,
                        onUpdateOldDeductions = { c, ccd, d -> viewModel.updateOldRegimeDeductions(c, ccd, d) }
                    )
                }
            }
        }
    }

    // Dialogs
    editingMonth?.let { month ->
        EditMonthDialog(
            month = month,
            onDismiss = { viewModel.setEditingMonth(null) },
            onSave = {
                viewModel.updateMonth(it)
                viewModel.setEditingMonth(null)
            }
        )
    }

    if (showEditStaffDialog) {
        EditStaffDialog(
            profile = currentProfile,
            onDismiss = { viewModel.setEditStaffDialog(false) },
            onSave = {
                viewModel.updateProfile(it)
                viewModel.setEditStaffDialog(false)
            }
        )
    }

    if (showImportDialog) {
        ImportDataDialog(
            onDismiss = { viewModel.setImportDialog(false) },
            onImportText = { viewModel.parseAndLoadText(it) },
            onLoadSample = {
                viewModel.loadStaffProfile(com.example.parser.IfhrmsDataParser.PRESET_STAFF[0])
            }
        )
    }

    if (showAddTeacherDialog) {
        AddTeacherDialog(
            onDismiss = { showAddTeacherDialog = false },
            onAdd = { name, desig, bp, hra, pan, ifhrms ->
                viewModel.addNewTeacher(name, desig, bp, hra, pan, ifhrms)
                showAddTeacherDialog = false
            }
        )
    }
}
