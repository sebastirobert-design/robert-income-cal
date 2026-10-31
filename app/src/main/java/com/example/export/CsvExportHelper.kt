package com.example.export

import android.content.Context
import com.example.model.EmployeeProfile
import com.example.model.OtherEarnings
import com.example.model.SalaryMonthData
import com.example.model.TaxComputation
import com.example.model.TaxYear
import java.io.File
import java.io.FileWriter

object CsvExportHelper {

    fun generateCsv(
        context: Context,
        profile: EmployeeProfile,
        taxYear: TaxYear,
        monthsData: List<SalaryMonthData>,
        earnings: OtherEarnings,
        computation: TaxComputation
    ): File {
        val file = File(context.cacheDir, "IT_Form_${profile.ifhrmsNo}_${taxYear.label.replace(" ", "_")}.csv")
        FileWriter(file).use { writer ->
            writer.append("INCOME TAX CALCULATION STATEMENT FOR THE\n")
            writer.append("FINANCIAL YEAR ${taxYear.label.replace("FY ", "")} (${taxYear.assessmentYear}) - ${computation.regime.labelEnglish}\n\n")

            writer.append("NAME,${profile.name},IFHRMS NO,${profile.ifhrmsNo},PAN NO,${profile.panNo}\n")
            writer.append("DESIGNATION,${profile.designation},SCHOOL,${profile.schoolOffice},TAN NO,${profile.beoTanNo}\n")
            writer.append("TPF/CPS NO,${profile.tpfCpsNo},MOBILE,${profile.mobileNo},DOB,${profile.dob}\n\n")

            writer.append("PAY DRAWN PARTICULARS FOR THE FINANCIAL YEAR ${taxYear.label.replace("FY ", "")}\n")
            writer.append("MONTH,BASIC PAY,PP/SPL,DA,HRA,MA,CCA,TOTAL GROSS,IT TDS,IT CESS,NET SALARY\n")

            for (m in monthsData) {
                writer.append("${m.monthCode},%.0f,%.0f,%.0f,%.0f,%.0f,%.0f,%.0f,%.0f,%.0f,%.0f\n".format(
                    m.basicPay, m.ppSpl, m.daAmount, m.hra, m.ma, m.cca, m.totalGross, m.itTds, m.itCess, m.netSalary
                ))
            }

            val totalGrossSalary = monthsData.sumOf { it.totalGross }
            val totalItTds = monthsData.sumOf { it.itTds }
            val totalCess = monthsData.sumOf { it.itCess }
            val totalNet = monthsData.sumOf { it.netSalary }

            writer.append("TOTAL,%.0f,0,%.0f,%.0f,%.0f,0,%.0f,%.0f,%.0f,%.0f\n\n".format(
                monthsData.sumOf { it.basicPay },
                monthsData.sumOf { it.daAmount },
                monthsData.sumOf { it.hra },
                monthsData.sumOf { it.ma },
                totalGrossSalary, totalItTds, totalCess, totalNet
            ))

            writer.append("OTHER ARREARS & EARNINGS\n")
            writer.append("DA Arrear (1),%.0f\n".format(earnings.daArrear1))
            writer.append("Bonus,%.0f\n".format(earnings.bonus))
            writer.append("Surrender Leave Pay,%.0f\n".format(earnings.surrenderAmount))
            writer.append("Other Arrears,%.0f\n".format(earnings.otherArrear1 + earnings.otherArrear2))
            writer.append("TOTAL GROSS INCOME,%.0f\n\n".format(computation.totalGrossSalary))

            writer.append("TAX COMPUTATION\n")
            writer.append("Total Gross Income,%.0f\n".format(computation.totalGrossSalary))
            writer.append("Less: Standard Deduction,%.0f\n".format(computation.standardDeduction))
            writer.append("NET TAXABLE INCOME,%.0f\n".format(computation.taxableIncome))
            writer.append("Tax on Taxable Income,%.2f\n".format(computation.taxBeforeRebate))
            writer.append("Less: 87A Tax Rebate / Marginal Relief,%.2f\n".format(computation.rebate87A + computation.marginalRelief))
            writer.append("Add: 4% Health & Education Cess,%.2f\n".format(computation.cess4Percent))
            writer.append("TOTAL TAX PAYABLE,%.2f\n".format(computation.totalTaxPayable))
            writer.append("Less: TDS Already Paid,%.0f\n".format(computation.tdsPaidSoFar))
            writer.append("BALANCE TAX TO DEDUCT,%.2f\n".format(computation.balanceTaxToDeduct))
            writer.append("Per Month TDS for remaining months,%.2f\n".format(computation.monthlyBalanceDeduction))
        }
        return file
    }
}
