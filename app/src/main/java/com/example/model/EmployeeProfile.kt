package com.example.model

enum class TaxYear(val label: String, val assessmentYear: String, val defaultJanDa: Double, val defaultJulDa: Double) {
    FY_2026_27("FY 2026-27", "AY 2027-28", 58.0, 60.0),
    FY_2025_26("FY 2025-26", "AY 2026-27", 53.0, 55.0)
}

enum class TaxRegime(val labelTamil: String, val labelEnglish: String) {
    NEW_REGIME("புதிய வரி முறை", "New Tax Regime"),
    OLD_REGIME("பழைய வரி முறை", "Old Tax Regime")
}

data class EmployeeProfile(
    val id: String,
    val name: String,
    val ifhrmsNo: String,
    val designation: String,
    val schoolOffice: String,
    val schoolCode: String = "",
    val beoTanNo: String,
    val panNo: String,
    val aadhaarNo: String = "",
    val tpfCpsNo: String,
    val dob: String = "",
    val joiningDate: String = "",
    val retirementDate: String = "",
    val mobileNo: String = "",
    val bankAccount: String = "",
    val basicPayMarch: Double,
    val hraGrade: String = "Grade II",
    val fixedHra: Double = 3200.0,
    val incrementMonth: Int = 10 // Month index 0-11 (e.g. 7 for Oct, 1 for Apr, etc. or -1 for none)
)
