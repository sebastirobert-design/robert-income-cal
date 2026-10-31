package com.example.calculator

import com.example.model.*
import kotlin.math.max
import kotlin.math.min
import kotlin.math.round

object TaxCalculatorEngine {

    val MONTH_LABELS_FY26 = listOf(
        "Mar 26" to "மார்ச் 26",
        "Apr 26" to "ஏப்ரல் 26",
        "May 26" to "மே 26",
        "Jun 26" to "ஜூன் 26",
        "Jul 26" to "ஜூலை 26",
        "Aug 26" to "ஆகஸ்ட் 26",
        "Sep 26" to "செப்டம்பர் 26",
        "Oct 26" to "அக்டோபர் 26",
        "Nov 26" to "நவம்பர் 26",
        "Dec 26" to "டிசம்பர் 26",
        "Jan 27" to "ஜனவரி 27",
        "Feb 27" to "பிப்ரவரி 27"
    )

    val MONTH_LABELS_FY25 = listOf(
        "Mar 25" to "மார்ச் 25",
        "Apr 25" to "ஏப்ரல் 25",
        "May 25" to "மே 25",
        "Jun 25" to "ஜூன் 25",
        "Jul 25" to "ஜூலை 25",
        "Aug 25" to "ஆகஸ்ட் 25",
        "Sep 25" to "செப்டம்பர் 25",
        "Oct 25" to "அக்டோபர் 25",
        "Nov 25" to "நவம்பர் 25",
        "Dec 25" to "டிசம்பர் 25",
        "Jan 26" to "ஜனவரி 26",
        "Feb 26" to "பிப்ரவரி 26"
    )

    fun getMonthLabels(taxYear: TaxYear): List<Pair<String, String>> {
        return if (taxYear == TaxYear.FY_2026_27) MONTH_LABELS_FY26 else MONTH_LABELS_FY25
    }

    /**
     * Auto-generates full 12-month salary data from minimal 1 or 2 entries:
     * - basicPayMarch: e.g. 91700
     * - hraAmount: e.g. 3200 (or calculated from HraTableHelper)
     * - incrementMonth: 0-11, or -1 for none
     * - janDaRate: e.g. 58.0
     * - julDaRate: e.g. 60.0
     */
    fun autoGenerateSalaryData(
        taxYear: TaxYear,
        basicPayMarch: Double,
        hraAmount: Double,
        incrementMonth: Int = 10, // Default Oct or custom
        janDaRate: Double = 58.0,
        julDaRate: Double = 60.0,
        defaultTpf: Double = 20000.0,
        isCps: Boolean = false,
        pfAdvanceRecovery: Double = 25000.0,
        festivalRecovery: Double = 2000.0
    ): List<SalaryMonthData> {
        val labels = getMonthLabels(taxYear)
        val list = mutableListOf<SalaryMonthData>()

        var currentBasic = basicPayMarch
        val incrementAmount = round(basicPayMarch * 0.03 / 100.0) * 100.0 // 3% pay matrix step

        for (i in 0 until 12) {
            val (code, nameTamil) = labels[i]

            // Apply increment on specified month
            if (incrementMonth in 0..11 && i >= incrementMonth) {
                currentBasic = basicPayMarch + incrementAmount
            } else {
                currentBasic = basicPayMarch
            }

            // DA rate: Mar-Jun (months 0-3) Jan rate; Jul-Feb (months 4-11) Jul rate
            // Note: In some pay slips like Robert Sebastin J, Mar-Apr was 58% and May onwards 60%
            val daRate = if (i < 2) janDaRate else julDaRate
            val daVal = round(currentBasic * (daRate / 100.0))
            val hraVal = hraAmount
            val maVal = 300.0 // TN Govt standard ₹300

            // Deductions
            val tpfVal = if (isCps) round((currentBasic + daVal) * 0.10) else defaultTpf
            val fbfVal = 110.0
            val nhisVal = if (i == 6) 375.0 else 0.0 // NHIS in Sep
            val nhisCorpusVal = if (i == 6) 15.0 else 0.0
            val pfAdv = if (i < 5) pfAdvanceRecovery else 0.0
            val festAdv = if (i < 6) festivalRecovery else 0.0

            // Monthly TDS sample estimate based on preliminary bracket
            val estTds = if (i < 2) 11761.0 else if (i < 8) 12274.0 else 0.0
            val estCess = if (i < 2) 470.0 else if (i < 8) 491.0 else 0.0

            list.add(
                SalaryMonthData(
                    monthIndex = i,
                    monthCode = code,
                    monthNameTamil = nameTamil,
                    basicPay = currentBasic,
                    ppSpl = 0.0,
                    daPercent = daRate,
                    daAmount = daVal,
                    hra = hraVal,
                    ma = maVal,
                    cca = 0.0,
                    hillAllowance = 0.0,
                    winterAllowance = 0.0,
                    specialAllowance = 0.0,
                    tpfCps = tpfVal,
                    fbf = fbfVal,
                    nhis = nhisVal,
                    nhisCorpus = nhisCorpusVal,
                    pfAdvRecovery = pfAdv,
                    festivalAdvRecovery = festAdv,
                    itTds = estTds,
                    itCess = estCess
                )
            )
        }

        return list
    }

    /**
     * Compute New Tax Regime (Budget 2024 revised slabs):
     * 0 to 4,00,000 : Nil
     * 4,00,001 to 8,00,000 : 5%
     * 8,00,001 to 12,00,000 : 10%
     * 12,00,001 to 16,00,000 : 15%
     * 16,00,001 to 20,00,000 : 20%
     * 20,00,001 to 24,00,000 : 25%
     * Above 24,00,000 : 30%
     *
     * Standard deduction = ₹75,000
     * Section 87A: If taxable <= 12,00,000, 100% rebate (up to ₹60,000)
     * Cess = 4%
     */
    fun calculateNewRegimeTax(
        totalGross: Double,
        monthsData: List<SalaryMonthData>
    ): TaxComputation {
        val standardDeduction = 75000.0
        val taxableIncomeRaw = max(0.0, totalGross - standardDeduction)
        val taxableIncome = taxableIncomeRaw

        val slabs = mutableListOf<SlabDetail>()
        var taxBeforeRebate = 0.0

        // Slab 1: Up to 4,00,000 @ Nil
        val s1Taxable = min(taxableIncome, 400000.0)
        slabs.add(SlabDetail("Upto Rs. 4,00,000", "ரூ. 4,00,000 வரை", 0.0, s1Taxable, 0.0))

        // Slab 2: 4,00,001 to 8,00,000 @ 5%
        if (taxableIncome > 400000.0) {
            val s2Taxable = min(taxableIncome - 400000.0, 400000.0)
            val s2Tax = s2Taxable * 0.05
            taxBeforeRebate += s2Tax
            slabs.add(SlabDetail("Rs. 4,00,001 - 8,00,000", "ரூ. 4,00,001 - 8,00,000", 5.0, s2Taxable, s2Tax))
        } else {
            slabs.add(SlabDetail("Rs. 4,00,001 - 8,00,000", "ரூ. 4,00,001 - 8,00,000", 5.0, 0.0, 0.0))
        }

        // Slab 3: 8,00,001 to 12,00,000 @ 10%
        if (taxableIncome > 800000.0) {
            val s3Taxable = min(taxableIncome - 800000.0, 400000.0)
            val s3Tax = s3Taxable * 0.10
            taxBeforeRebate += s3Tax
            slabs.add(SlabDetail("Rs. 8,00,001 - 12,00,000", "ரூ. 8,00,001 - 12,00,000", 10.0, s3Taxable, s3Tax))
        } else {
            slabs.add(SlabDetail("Rs. 8,00,001 - 12,00,000", "ரூ. 8,00,001 - 12,00,000", 10.0, 0.0, 0.0))
        }

        // Slab 4: 12,00,001 to 16,00,000 @ 15%
        if (taxableIncome > 1200000.0) {
            val s4Taxable = min(taxableIncome - 1200000.0, 400000.0)
            val s4Tax = s4Taxable * 0.15
            taxBeforeRebate += s4Tax
            slabs.add(SlabDetail("Rs. 12,00,001 - 16,00,000", "ரூ. 12,00,001 - 16,00,000", 15.0, s4Taxable, s4Tax))
        } else {
            slabs.add(SlabDetail("Rs. 12,00,001 - 16,00,000", "ரூ. 12,00,001 - 16,00,000", 15.0, 0.0, 0.0))
        }

        // Slab 5: 16,00,001 to 20,00,000 @ 20%
        if (taxableIncome > 1600000.0) {
            val s5Taxable = min(taxableIncome - 1600000.0, 400000.0)
            val s5Tax = s5Taxable * 0.20
            taxBeforeRebate += s5Tax
            slabs.add(SlabDetail("Rs. 16,00,001 - 20,00,000", "ரூ. 16,00,001 - 20,00,000", 20.0, s5Taxable, s5Tax))
        } else {
            slabs.add(SlabDetail("Rs. 16,00,001 - 20,00,000", "ரூ. 16,00,001 - 20,00,000", 20.0, 0.0, 0.0))
        }

        // Slab 6: 20,00,001 to 24,00,000 @ 25%
        if (taxableIncome > 2000000.0) {
            val s6Taxable = min(taxableIncome - 2000000.0, 400000.0)
            val s6Tax = s6Taxable * 0.25
            taxBeforeRebate += s6Tax
            slabs.add(SlabDetail("Rs. 20,00,001 - 24,00,000", "ரூ. 20,00,001 - 24,00,000", 25.0, s6Taxable, s6Tax))
        } else {
            slabs.add(SlabDetail("Rs. 20,00,001 - 24,00,000", "ரூ. 20,00,001 - 24,00,000", 25.0, 0.0, 0.0))
        }

        // Slab 7: Above 24,00,000 @ 30%
        if (taxableIncome > 2400000.0) {
            val s7Taxable = taxableIncome - 2400000.0
            val s7Tax = s7Taxable * 0.30
            taxBeforeRebate += s7Tax
            slabs.add(SlabDetail("Above Rs. 24,00,000", "ரூ. 24,00,000 மேல்", 30.0, s7Taxable, s7Tax))
        } else {
            slabs.add(SlabDetail("Above Rs. 24,00,000", "ரூ. 24,00,000 மேல்", 30.0, 0.0, 0.0))
        }

        // Section 87A Rebate: if taxable <= 12,00,000
        var rebate87A = 0.0
        var marginalRelief = 0.0
        var taxAfterRebate = taxBeforeRebate

        if (taxableIncome <= 1200000.0) {
            rebate87A = taxBeforeRebate
            taxAfterRebate = 0.0
        } else {
            // Marginal relief check: Tax payable should not exceed (Taxable Income - 12,00,000)
            val excessIncome = taxableIncome - 1200000.0
            if (taxBeforeRebate > excessIncome) {
                marginalRelief = taxBeforeRebate - excessIncome
                taxAfterRebate = excessIncome
            }
        }

        val cess = round(taxAfterRebate * 0.04)
        val totalTaxPayable = taxAfterRebate + cess

        // Calculate TDS paid so far from months data
        val tdsPaidSoFar = monthsData.sumOf { it.itTds + it.itCess }
        val balanceTaxToDeduct = max(0.0, totalTaxPayable - tdsPaidSoFar)

        // Remaining months count: count months where itTds == 0.0 or from Nov (monthIndex 8 to 11)
        val remainingMonthsCount = max(1, monthsData.count { it.itTds == 0.0 }.coerceIn(1, 4))
        val monthlyBalance = balanceTaxToDeduct / remainingMonthsCount

        return TaxComputation(
            regime = TaxRegime.NEW_REGIME,
            totalGrossSalary = totalGross,
            standardDeduction = standardDeduction,
            otherDeductionsChapter6A = 0.0,
            taxableIncome = taxableIncome,
            taxBeforeRebate = taxBeforeRebate,
            rebate87A = rebate87A,
            marginalRelief = marginalRelief,
            taxAfterRebate = taxAfterRebate,
            cess4Percent = cess,
            totalTaxPayable = totalTaxPayable,
            tdsPaidSoFar = tdsPaidSoFar,
            balanceTaxToDeduct = balanceTaxToDeduct,
            remainingMonths = remainingMonthsCount,
            monthlyBalanceDeduction = monthlyBalance,
            slabBreakdowns = slabs
        )
    }

    /**
     * Compute Old Tax Regime with Chapter VI-A deductions
     */
    fun calculateOldRegimeTax(
        totalGross: Double,
        deductions80C: Double = 150000.0,
        deductions80CCD: Double = 50000.0, // CPS / NPS
        deductions80D: Double = 25000.0,   // Health insurance
        hraExemption: Double = 0.0,
        monthsData: List<SalaryMonthData>
    ): TaxComputation {
        val standardDeduction = 50000.0
        val capped80C = min(deductions80C, 150000.0)
        val capped80CCD = min(deductions80CCD, 50000.0)
        val capped80D = min(deductions80D, 25000.0)
        val totalChapter6A = capped80C + capped80CCD + capped80D + hraExemption

        val taxableIncomeRaw = max(0.0, totalGross - standardDeduction - totalChapter6A)
        val taxableIncome = round(taxableIncomeRaw / 10.0) * 10.0

        val slabs = mutableListOf<SlabDetail>()
        var taxBeforeRebate = 0.0

        // Slab 1: Up to 2,50,000 @ Nil
        val s1Taxable = min(taxableIncome, 250000.0)
        slabs.add(SlabDetail("Upto Rs. 2,50,000", "ரூ. 2,50,000 வரை", 0.0, s1Taxable, 0.0))

        // Slab 2: 2,50,001 - 5,00,000 @ 5%
        if (taxableIncome > 250000.0) {
            val s2Taxable = min(taxableIncome - 250000.0, 250000.0)
            val s2Tax = s2Taxable * 0.05
            taxBeforeRebate += s2Tax
            slabs.add(SlabDetail("Rs. 2,50,001 - 5,00,000", "ரூ. 2,50,001 - 5,00,000", 5.0, s2Taxable, s2Tax))
        } else {
            slabs.add(SlabDetail("Rs. 2,50,001 - 5,00,000", "ரூ. 2,50,001 - 5,00,000", 5.0, 0.0, 0.0))
        }

        // Slab 3: 5,00,001 - 10,00,000 @ 20%
        if (taxableIncome > 500000.0) {
            val s3Taxable = min(taxableIncome - 500000.0, 500000.0)
            val s3Tax = s3Taxable * 0.20
            taxBeforeRebate += s3Tax
            slabs.add(SlabDetail("Rs. 5,00,001 - 10,00,000", "ரூ. 5,00,001 - 10,00,000", 20.0, s3Taxable, s3Tax))
        } else {
            slabs.add(SlabDetail("Rs. 5,00,001 - 10,00,000", "ரூ. 5,00,001 - 10,00,000", 20.0, 0.0, 0.0))
        }

        // Slab 4: Above 10,00,000 @ 30%
        if (taxableIncome > 1000000.0) {
            val s4Taxable = taxableIncome - 1000000.0
            val s4Tax = s4Taxable * 0.30
            taxBeforeRebate += s4Tax
            slabs.add(SlabDetail("Above Rs. 10,00,000", "ரூ. 10,00,000 மேல்", 30.0, s4Taxable, s4Tax))
        } else {
            slabs.add(SlabDetail("Above Rs. 10,00,000", "ரூ. 10,00,000 மேல்", 30.0, 0.0, 0.0))
        }

        // Section 87A in Old Regime: up to 5,00,000 (max ₹12,500)
        var rebate87A = 0.0
        var taxAfterRebate = taxBeforeRebate
        if (taxableIncome <= 500000.0) {
            rebate87A = min(taxBeforeRebate, 12500.0)
            taxAfterRebate = max(0.0, taxBeforeRebate - rebate87A)
        }

        val cess = taxAfterRebate * 0.04
        val totalTaxPayable = taxAfterRebate + cess

        val tdsPaidSoFar = monthsData.sumOf { it.itTds + it.itCess }
        val balanceTaxToDeduct = max(0.0, totalTaxPayable - tdsPaidSoFar)
        val remainingMonthsCount = max(1, monthsData.count { it.itTds == 0.0 }.coerceIn(1, 4))
        val monthlyBalance = balanceTaxToDeduct / remainingMonthsCount

        return TaxComputation(
            regime = TaxRegime.OLD_REGIME,
            totalGrossSalary = totalGross,
            standardDeduction = standardDeduction,
            otherDeductionsChapter6A = totalChapter6A,
            taxableIncome = taxableIncome,
            taxBeforeRebate = taxBeforeRebate,
            rebate87A = rebate87A,
            marginalRelief = 0.0,
            taxAfterRebate = taxAfterRebate,
            cess4Percent = cess,
            totalTaxPayable = totalTaxPayable,
            tdsPaidSoFar = tdsPaidSoFar,
            balanceTaxToDeduct = balanceTaxToDeduct,
            remainingMonths = remainingMonthsCount,
            monthlyBalanceDeduction = monthlyBalance,
            slabBreakdowns = slabs
        )
    }

    fun compareRegimes(newComp: TaxComputation, oldComp: TaxComputation): OldVsNewComparison {
        val diff = oldComp.totalTaxPayable - newComp.totalTaxPayable
        return if (diff > 0) {
            OldVsNewComparison(
                newRegimeTax = newComp.totalTaxPayable,
                oldRegimeTax = oldComp.totalTaxPayable,
                recommendedRegime = TaxRegime.NEW_REGIME,
                netSavings = diff,
                explanationTamil = "புதிய வரி முறையில் உங்களுக்கு ₹ %,.0f மிச்சமாகும். வரி சலுகைகள் ஆவணங்கள் தேவையில்லை!".format(diff),
                explanationEnglish = "New Tax Regime saves you ₹ %,.0f. No proof of investment needed!".format(diff)
            )
        } else {
            val oldSavings = -diff
            OldVsNewComparison(
                newRegimeTax = newComp.totalTaxPayable,
                oldRegimeTax = oldComp.totalTaxPayable,
                recommendedRegime = TaxRegime.OLD_REGIME,
                netSavings = oldSavings,
                explanationTamil = "பழைய வரி முறையில் (80C, 80CCD கழிவுகளுடன்) உங்களுக்கு ₹ %,.0f மிச்சமாகும்.".format(oldSavings),
                explanationEnglish = "Old Tax Regime saves you ₹ %,.0f with 80C & 80CCD deductions.".format(oldSavings)
            )
        }
    }
}
