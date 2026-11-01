package com.example.ui

import android.content.Context
import android.content.Intent
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.calculator.HraTableHelper
import com.example.calculator.TaxCalculatorEngine
import com.example.export.CsvExportHelper
import com.example.export.PdfStatementGenerator
import com.example.model.*
import com.example.parser.IfhrmsDataParser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.math.round

class TaxViewModel : ViewModel() {

    private val _selectedYear = MutableStateFlow(TaxYear.FY_2026_27)
    val selectedYear: StateFlow<TaxYear> = _selectedYear.asStateFlow()

    private val _selectedRegime = MutableStateFlow(TaxRegime.NEW_REGIME)
    val selectedRegime: StateFlow<TaxRegime> = _selectedRegime.asStateFlow()

    private val _staffList = MutableStateFlow(IfhrmsDataParser.PRESET_STAFF)
    val staffList: StateFlow<List<EmployeeProfile>> = _staffList.asStateFlow()

    private val _currentProfile = MutableStateFlow(IfhrmsDataParser.PRESET_STAFF[0])
    val currentProfile: StateFlow<EmployeeProfile> = _currentProfile.asStateFlow()

    // 12 months salary data
    private val _monthsData = MutableStateFlow<List<SalaryMonthData>>(emptyList())
    val monthsData: StateFlow<List<SalaryMonthData>> = _monthsData.asStateFlow()

    // Other earnings & arrears
    private val _otherEarnings = MutableStateFlow(OtherEarnings())
    val otherEarnings: StateFlow<OtherEarnings> = _otherEarnings.asStateFlow()

    // Old regime Chapter VI-A deductions
    private val _deductions80C = MutableStateFlow(150000.0)
    val deductions80C: StateFlow<Double> = _deductions80C.asStateFlow()

    private val _deductions80CCD = MutableStateFlow(50000.0)
    val deductions80CCD: StateFlow<Double> = _deductions80CCD.asStateFlow()

    private val _deductions80D = MutableStateFlow(25000.0)
    val deductions80D: StateFlow<Double> = _deductions80D.asStateFlow()

    // UI State
    private val _activeTab = MutableStateFlow(0)
    val activeTab: StateFlow<Int> = _activeTab.asStateFlow()

    private val _editingMonth = MutableStateFlow<SalaryMonthData?>(null)
    val editingMonth: StateFlow<SalaryMonthData?> = _editingMonth.asStateFlow()

    private val _showEditStaffDialog = MutableStateFlow(false)
    val showEditStaffDialog: StateFlow<Boolean> = _showEditStaffDialog.asStateFlow()

    private val _showImportDialog = MutableStateFlow(false)
    val showImportDialog: StateFlow<Boolean> = _showImportDialog.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // Computations
    private val _newRegimeComputation = MutableStateFlow<TaxComputation>(
        TaxCalculatorEngine.calculateNewRegimeTax(0.0, emptyList())
    )
    val newRegimeComputation: StateFlow<TaxComputation> = _newRegimeComputation.asStateFlow()

    private val _oldRegimeComputation = MutableStateFlow<TaxComputation>(
        TaxCalculatorEngine.calculateOldRegimeTax(0.0, 0.0, 0.0, 0.0, 0.0, emptyList())
    )
    val oldRegimeComputation: StateFlow<TaxComputation> = _oldRegimeComputation.asStateFlow()

    private val _comparison = MutableStateFlow<OldVsNewComparison>(
        TaxCalculatorEngine.compareRegimes(_newRegimeComputation.value, _oldRegimeComputation.value)
    )
    val comparison: StateFlow<OldVsNewComparison> = _comparison.asStateFlow()

    init {
        // Load default exact Robert Sebastin J profile from model
        loadStaffProfile(IfhrmsDataParser.PRESET_STAFF[0])
    }

    fun selectTab(tabIndex: Int) {
        _activeTab.value = tabIndex
    }

    fun setYear(year: TaxYear) {
        _selectedYear.value = year
        recalculateAll()
    }

    fun setRegime(regime: TaxRegime) {
        _selectedRegime.value = regime
    }

    fun loadStaffProfile(profile: EmployeeProfile) {
        _currentProfile.value = profile
        if (profile.ifhrmsNo == "43020114172") {
            val (months, earnings) = IfhrmsDataParser.createRobertSebastinExactData()
            _monthsData.value = months
            _otherEarnings.value = earnings
        } else {
            // Auto generate from profile data
            autoFillData(
                marchBasic = profile.basicPayMarch,
                fixedHra = profile.fixedHra,
                hraGrade = profile.hraGrade,
                incrementMonth = profile.incrementMonth,
                janDa = _selectedYear.value.defaultJanDa,
                julDa = _selectedYear.value.defaultJulDa,
                daArrear = round(profile.basicPayMarch * 0.02 * 4), // 4 months 2% difference
                bonus = 3000.0,
                surrenderDays = 0
            )
        }
        recalculateAll()
    }

    /**
     * The core feature: With just 1 or 2 entries, auto-fills full year salary, deductions, and arrears!
     */
    fun autoFillData(
        marchBasic: Double,
        fixedHra: Double,
        hraGrade: String,
        incrementMonth: Int,
        janDa: Double,
        julDa: Double,
        janDaArrearMonths: Int = 4,
        janDaArrearAmount: Double = 0.0,
        julDaArrearMonths: Int = 0,
        julDaArrearAmount: Double = 0.0,
        bonus: Double = 3000.0,
        surrenderDays: Int = 0,
        surrenderMonth: String = "None",
        daArrear: Double = 0.0
    ) {
        val hraVal = HraTableHelper.getSuggestedHra(marchBasic, hraGrade, fixedHra)
        val months = TaxCalculatorEngine.autoGenerateSalaryData(
            taxYear = _selectedYear.value,
            basicPayMarch = marchBasic,
            hraAmount = hraVal,
            incrementMonth = incrementMonth,
            janDaRate = janDa,
            julDaRate = julDa
        )

        // Surrender leave pay calculation if days selected
        val surrenderPay = if (surrenderDays > 0) {
            val oneDaySalary = (marchBasic + (marchBasic * (julDa / 100.0)) + hraVal + 300.0) / 30.0
            round(oneDaySalary * surrenderDays)
        } else 0.0

        val finalJanArrear = if (janDaArrearAmount > 0.0) janDaArrearAmount else daArrear

        _monthsData.value = months
        _otherEarnings.value = OtherEarnings(
            daArrear1 = finalJanArrear,
            daArrearMonths1 = janDaArrearMonths,
            daArrear2 = julDaArrearAmount,
            daArrearMonths2 = julDaArrearMonths,
            bonus = bonus,
            surrenderDays = surrenderDays,
            surrenderMonth = surrenderMonth,
            surrenderAmount = surrenderPay
        )

        recalculateAll()
        _snackbarMessage.value = "தரவுகள் வெற்றிகரமாக சேமிக்கப்பட்டன! (Data saved & tax updated successfully!)"
    }

    fun updateMonth(updatedMonth: SalaryMonthData) {
        val currentList = _monthsData.value.toMutableList()
        val index = currentList.indexOfFirst { it.monthIndex == updatedMonth.monthIndex }
        if (index != -1) {
            currentList[index] = updatedMonth
            _monthsData.value = currentList
            recalculateAll()
        }
    }

    fun setEditingMonth(month: SalaryMonthData?) {
        _editingMonth.value = month
    }

    fun updateOtherEarnings(earnings: OtherEarnings) {
        _otherEarnings.value = earnings
        recalculateAll()
    }

    fun updateProfile(profile: EmployeeProfile) {
        _currentProfile.value = profile
        val list = _staffList.value.toMutableList()
        val idx = list.indexOfFirst { it.id == profile.id }
        if (idx != -1) {
            list[idx] = profile
            _staffList.value = list
        }
        recalculateAll()
    }

    fun addNewTeacher(
        name: String,
        designation: String,
        basicPay: Double,
        hra: Double,
        pan: String,
        ifhrms: String
    ) {
        val newStaff = EmployeeProfile(
            id = "staff_${System.currentTimeMillis()}",
            name = name.ifBlank { "TEACHER" },
            ifhrmsNo = ifhrms.ifBlank { "43020999999" },
            designation = designation.ifBlank { "BT ASSISTANT" },
            schoolOffice = _currentProfile.value.schoolOffice,
            schoolCode = _currentProfile.value.schoolCode,
            beoTanNo = _currentProfile.value.beoTanNo,
            panNo = pan.ifBlank { "ABCDE1234F" },
            tpfCpsNo = "TPF 998877",
            basicPayMarch = basicPay,
            fixedHra = hra
        )
        val list = _staffList.value.toMutableList()
        list.add(newStaff)
        _staffList.value = list
        loadStaffProfile(newStaff)
    }

    fun updateOldRegimeDeductions(d80C: Double, d80CCD: Double, d80D: Double) {
        _deductions80C.value = d80C
        _deductions80CCD.value = d80CCD
        _deductions80D.value = d80D
        recalculateAll()
    }

    fun setEditStaffDialog(show: Boolean) {
        _showEditStaffDialog.value = show
    }

    fun setImportDialog(show: Boolean) {
        _showImportDialog.value = show
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun parseAndLoadText(rawText: String) {
        val res = IfhrmsDataParser.parseIfhrmsText(rawText)
        val updatedProfile = _currentProfile.value.copy(
            name = res.name,
            ifhrmsNo = res.empId,
            panNo = res.panNo,
            aadhaarNo = res.aadharNo,
            mobileNo = res.mobileNo,
            dob = res.dob,
            joiningDate = res.joiningDate,
            retirementDate = res.retirementDate,
            basicPayMarch = res.basicPay,
            fixedHra = res.hra
        )
        _currentProfile.value = updatedProfile
        autoFillData(
            marchBasic = res.basicPay,
            fixedHra = res.hra,
            hraGrade = updatedProfile.hraGrade,
            incrementMonth = updatedProfile.incrementMonth,
            janDa = _selectedYear.value.defaultJanDa,
            julDa = _selectedYear.value.defaultJulDa,
            daArrear = res.daArrear,
            bonus = 3000.0,
            surrenderDays = 0
        )
        _snackbarMessage.value = "IFHRMS Pay Drawn விவரங்கள் வெற்றிகரமாக இறக்கப்பட்டது!"
    }

    fun recalculateAll() {
        val totalMonthsGross = _monthsData.value.sumOf { it.totalGross }
        val totalGross = totalMonthsGross + _otherEarnings.value.totalOtherEarnings

        val newComp = TaxCalculatorEngine.calculateNewRegimeTax(totalGross, _monthsData.value)
        val oldComp = TaxCalculatorEngine.calculateOldRegimeTax(
            totalGross = totalGross,
            deductions80C = _deductions80C.value,
            deductions80CCD = _deductions80CCD.value,
            deductions80D = _deductions80D.value,
            hraExemption = 0.0,
            monthsData = _monthsData.value
        )

        _newRegimeComputation.value = newComp
        _oldRegimeComputation.value = oldComp
        _comparison.value = TaxCalculatorEngine.compareRegimes(newComp, oldComp)
    }

    fun getActiveComputation(): TaxComputation {
        return if (_selectedRegime.value == TaxRegime.NEW_REGIME) _newRegimeComputation.value else _oldRegimeComputation.value
    }

    fun sharePdf(context: Context) {
        try {
            val file = PdfStatementGenerator.generateA4Pdf(
                context = context,
                profile = _currentProfile.value,
                taxYear = _selectedYear.value,
                monthsData = _monthsData.value,
                earnings = _otherEarnings.value,
                computation = getActiveComputation()
            )
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "application/pdf"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "Income Tax Statement - ${_currentProfile.value.name} (${_selectedYear.value.label})")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "A4 Print / Share PDF"))
        } catch (e: Exception) {
            _snackbarMessage.value = "PDF உருவாக்குவதில் பிழை: ${e.message}"
        }
    }

    fun shareCsv(context: Context) {
        try {
            val file = CsvExportHelper.generateCsv(
                context = context,
                profile = _currentProfile.value,
                taxYear = _selectedYear.value,
                monthsData = _monthsData.value,
                earnings = _otherEarnings.value,
                computation = getActiveComputation()
            )
            val uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/csv"
                putExtra(Intent.EXTRA_STREAM, uri)
                putExtra(Intent.EXTRA_SUBJECT, "IT Form CSV - ${_currentProfile.value.name}")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(Intent.createChooser(intent, "Export CSV"))
        } catch (e: Exception) {
            _snackbarMessage.value = "CSV ஏற்றுமதியில் பிழை: ${e.message}"
        }
    }

    fun shareWhatsApp(context: Context) {
        try {
            val comp = getActiveComputation()
            val text = """
                *தமிழ்நாடு எளிய வருமான வரி அறிக்கை* (${_selectedYear.value.label})
                பணியாளர்: ${_currentProfile.value.name} (${_currentProfile.value.designation})
                IFHRMS: ${_currentProfile.value.ifhrmsNo} | PAN: ${_currentProfile.value.panNo}
                பள்ளி: ${_currentProfile.value.schoolOffice}
                -----------------------------------
                • மொத்த ஊதியம் (Gross): ₹ %,.0f
                • நிலையான கழிவு: ₹ %,.0f
                • வரிக்குரிய வருமானம்: ₹ %,.0f
                • மொத்த வரி + CESS (${_selectedRegime.value.labelTamil}): ₹ %,.2f
                • பிடித்தம் ஆனது (TDS Paid): ₹ %,.0f
                • மீதமுள்ள வரி (Balance): ₹ %,.2f
                • மாதந்தோறும் பிடித்தம்: ₹ %,.2f / மாதம் (${comp.remainingMonths} மாதங்கள்)
                -----------------------------------
                TN Simple Income Tax Calculator App
            """.trimIndent().format(
                comp.totalGrossSalary,
                comp.standardDeduction,
                comp.taxableIncome,
                comp.totalTaxPayable,
                comp.tdsPaidSoFar,
                comp.balanceTaxToDeduct,
                comp.monthlyBalanceDeduction
            )

            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, text)
                setPackage("com.whatsapp")
            }
            try {
                context.startActivity(intent)
            } catch (ex: Exception) {
                // Fallback to general share chooser
                val fallbackIntent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_TEXT, text)
                }
                context.startActivity(Intent.createChooser(fallbackIntent, "Share Tax Summary"))
            }
        } catch (e: Exception) {
            _snackbarMessage.value = "பகிர்வதில் பிழை: ${e.message}"
        }
    }
}
