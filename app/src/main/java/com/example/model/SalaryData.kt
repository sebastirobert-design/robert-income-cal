package com.example.model

data class SalaryMonthData(
    val monthIndex: Int,          // 0 = Mar, 1 = Apr, ..., 11 = Feb
    val monthCode: String,        // "Mar 26", "Apr 26", etc.
    val monthNameTamil: String,   // "மார்ச் 26", "ஏப்ரல் 26", etc.
    var basicPay: Double = 0.0,
    var ppSpl: Double = 0.0,
    var daPercent: Double = 0.0,
    var daAmount: Double = 0.0,
    var hra: Double = 0.0,
    var ma: Double = 300.0,       // TN standard Medical Allowance ₹300
    var cca: Double = 0.0,
    var hillAllowance: Double = 0.0,
    var winterAllowance: Double = 0.0,
    var specialAllowance: Double = 0.0,
    
    // Deductions
    var tpfCps: Double = 0.0,
    var fbf: Double = 110.0,      // Family Benefit Fund ₹110
    var nhis: Double = 0.0,       // New Health Insurance Scheme ₹375 (in Sep)
    var nhisCorpus: Double = 0.0, // NHIS Corpus ₹15 (in Sep)
    var pfAdvRecovery: Double = 0.0,
    var festivalAdvRecovery: Double = 0.0,
    var itTds: Double = 0.0,      // Monthly Income tax deducted
    var itCess: Double = 0.0,     // 4% IT Cess
    var otherDeductions: Double = 0.0
) {
    val totalGross: Double
        get() = basicPay + ppSpl + daAmount + hra + ma + cca + hillAllowance + winterAllowance + specialAllowance

    val totalDeduction: Double
        get() = tpfCps + fbf + nhis + nhisCorpus + pfAdvRecovery + festivalAdvRecovery + itTds + itCess + otherDeductions

    val netSalary: Double
        get() = totalGross - totalDeduction
}

data class OtherEarnings(
    var daArrear1: Double = 7336.0,          // Jan DA Arrear Amount (ரூபாய்)
    var daArrearMonths1: Int = 4,            // Jan DA Arrear Months (நிலுவை மாதங்கள்)
    var daArrear2: Double = 0.0,             // Jul DA Arrear Amount (ரூபாய்)
    var daArrearMonths2: Int = 0,            // Jul DA Arrear Months (நிலுவை மாதங்கள்)
    var daArrearMonths: Int = 4,             // Backward-compatible alias
    var bonus: Double = 3000.0,              // Standard festival bonus
    var surrenderDays: Int = 0,              // 0, 15, or 30 days
    var surrenderMonth: String = "None",     // Month surrendered
    var surrenderAmount: Double = 0.0,
    var otherArrear1: Double = 0.0,
    var otherArrear2: Double = 0.0
) {
    val totalOtherEarnings: Double
        get() = daArrear1 + daArrear2 + bonus + surrenderAmount + otherArrear1 + otherArrear2
}

data class SlabDetail(
    val slabRange: String,
    val slabRangeTamil: String,
    val ratePercent: Double,
    val taxableInSlab: Double,
    val taxAmount: Double
)

data class TaxComputation(
    val regime: TaxRegime,
    val totalGrossSalary: Double,
    val standardDeduction: Double,
    val otherDeductionsChapter6A: Double, // For Old Regime (80C, 80CCD, 80D, etc.)
    val taxableIncome: Double,
    val taxBeforeRebate: Double,
    val rebate87A: Double,
    val marginalRelief: Double,
    val taxAfterRebate: Double,
    val cess4Percent: Double,
    val totalTaxPayable: Double,
    val tdsPaidSoFar: Double,             // Mar to Oct or actual sum of itTds + itCess
    val balanceTaxToDeduct: Double,
    val remainingMonths: Int,             // Remaining months (e.g. 4 for Nov, Dec, Jan, Feb)
    val monthlyBalanceDeduction: Double,
    val slabBreakdowns: List<SlabDetail>
)

data class OldVsNewComparison(
    val newRegimeTax: Double,
    val oldRegimeTax: Double,
    val recommendedRegime: TaxRegime,
    val netSavings: Double,
    val explanationTamil: String,
    val explanationEnglish: String
)
