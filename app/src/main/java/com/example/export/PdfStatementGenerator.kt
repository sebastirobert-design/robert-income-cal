package com.example.export

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import com.example.model.EmployeeProfile
import com.example.model.OtherEarnings
import com.example.model.SalaryMonthData
import com.example.model.TaxComputation
import com.example.model.TaxYear
import java.io.File
import java.io.FileOutputStream

object PdfStatementGenerator {

    fun generateA4Pdf(
        context: Context,
        profile: EmployeeProfile,
        taxYear: TaxYear,
        monthsData: List<SalaryMonthData>,
        earnings: OtherEarnings,
        computation: TaxComputation
    ): File {
        // Standard A4 dimensions in points: 595 x 842
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        val titlePaint = Paint().apply {
            color = Color.rgb(15, 23, 42)
            textSize = 12f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }

        val subTitlePaint = Paint().apply {
            color = Color.rgb(71, 85, 105)
            textSize = 9.5f
            isFakeBoldText = true
            textAlign = Paint.Align.CENTER
        }

        val textPaint = Paint().apply {
            color = Color.rgb(30, 41, 59)
            textSize = 7.5f
            textAlign = Paint.Align.LEFT
        }

        val boldPaint = Paint().apply {
            color = Color.rgb(15, 23, 42)
            textSize = 7.5f
            isFakeBoldText = true
            textAlign = Paint.Align.LEFT
        }

        val linePaint = Paint().apply {
            color = Color.rgb(203, 213, 225)
            strokeWidth = 0.8f
            style = Paint.Style.STROKE
        }

        val headerBgPaint = Paint().apply {
            color = Color.rgb(241, 245, 249)
            style = Paint.Style.FILL
        }

        val highlightBgPaint = Paint().apply {
            color = Color.rgb(254, 243, 199)
            style = Paint.Style.FILL
        }

        var y = 30f
        val leftMargin = 30f
        val rightMargin = 565f
        val contentWidth = rightMargin - leftMargin

        // Header
        canvas.drawText("INCOME TAX CALCULATION STATEMENT", 297f, y, titlePaint)
        y += 14f
        canvas.drawText("FINANCIAL YEAR ${taxYear.label.replace("FY ", "")} (${taxYear.assessmentYear}) - ${computation.regime.labelEnglish.uppercase()}", 297f, y, subTitlePaint)
        y += 16f

        // Employee Info Box
        val infoBoxTop = y
        val infoBoxBottom = y + 48f
        canvas.drawRect(leftMargin, infoBoxTop, rightMargin, infoBoxBottom, headerBgPaint)
        canvas.drawRect(leftMargin, infoBoxTop, rightMargin, infoBoxBottom, linePaint)

        val col1 = leftMargin + 8f
        val col2 = leftMargin + 180f
        val col3 = leftMargin + 360f

        canvas.drawText("NAME: ${profile.name}", col1, y + 12f, boldPaint)
        canvas.drawText("IFHRMS NO: ${profile.ifhrmsNo}", col2, y + 12f, boldPaint)
        canvas.drawText("PAN NO: ${profile.panNo}", col3, y + 12f, boldPaint)

        canvas.drawText("DESIGNATION: ${profile.designation}", col1, y + 26f, textPaint)
        canvas.drawText("SCHOOL/OFFICE: ${profile.schoolOffice}", col2, y + 26f, textPaint)
        canvas.drawText("TAN NO: ${profile.beoTanNo}", col3, y + 26f, textPaint)

        canvas.drawText("TPF/CPS NO: ${profile.tpfCpsNo}", col1, y + 40f, textPaint)
        canvas.drawText("MOBILE: ${profile.mobileNo}", col2, y + 40f, textPaint)
        canvas.drawText("DOB / RET: ${profile.dob} / ${profile.retirementDate}", col3, y + 40f, textPaint)

        y = infoBoxBottom + 12f

        // 12-Month Table
        val headers = listOf("MONTH", "BASIC", "PP/SPL", "DA", "HRA", "MA", "GROSS", "IT TDS", "CESS", "NET")
        val colWidths = floatArrayOf(45f, 52f, 40f, 54f, 44f, 35f, 60f, 55f, 45f, 55f)
        var rowHeight = 15f

        // Header row
        canvas.drawRect(leftMargin, y, rightMargin, y + rowHeight, headerBgPaint)
        canvas.drawRect(leftMargin, y, rightMargin, y + rowHeight, linePaint)

        var curX = leftMargin
        for (idx in headers.indices) {
            val title = headers[idx]
            canvas.drawText(title, curX + 4f, y + 11f, boldPaint)
            curX += colWidths[idx]
            if (idx < headers.size - 1) {
                canvas.drawLine(curX, y, curX, y + rowHeight, linePaint)
            }
        }
        y += rowHeight

        // Data rows
        for (m in monthsData) {
            canvas.drawRect(leftMargin, y, rightMargin, y + rowHeight, linePaint)
            curX = leftMargin
            val rowValues = listOf(
                m.monthCode,
                "%,.0f".format(m.basicPay),
                if (m.ppSpl > 0) "%,.0f".format(m.ppSpl) else "-",
                "%,.0f".format(m.daAmount),
                "%,.0f".format(m.hra),
                "%,.0f".format(m.ma),
                "%,.0f".format(m.totalGross),
                if (m.itTds > 0) "%,.0f".format(m.itTds) else "-",
                if (m.itCess > 0) "%,.0f".format(m.itCess) else "-",
                "%,.0f".format(m.netSalary)
            )

            for (idx in rowValues.indices) {
                val value = rowValues[idx]
                canvas.drawText(value, curX + 4f, y + 11f, textPaint)
                curX += colWidths[idx]
                if (idx < rowValues.size - 1) {
                    canvas.drawLine(curX, y, curX, y + rowHeight, linePaint)
                }
            }
            y += rowHeight
        }

        // Total Row
        canvas.drawRect(leftMargin, y, rightMargin, y + rowHeight, headerBgPaint)
        canvas.drawRect(leftMargin, y, rightMargin, y + rowHeight, linePaint)
        curX = leftMargin
        val totalGrossSalary = monthsData.sumOf { it.totalGross }
        val totalItTds = monthsData.sumOf { it.itTds }
        val totalCess = monthsData.sumOf { it.itCess }
        val totalNet = monthsData.sumOf { it.netSalary }

        val totalValues = listOf(
            "TOTAL",
            "%,.0f".format(monthsData.sumOf { it.basicPay }),
            "-",
            "%,.0f".format(monthsData.sumOf { it.daAmount }),
            "%,.0f".format(monthsData.sumOf { it.hra }),
            "%,.0f".format(monthsData.sumOf { it.ma }),
            "%,.0f".format(totalGrossSalary),
            "%,.0f".format(totalItTds),
            "%,.0f".format(totalCess),
            "%,.0f".format(totalNet)
        )
        for (idx in totalValues.indices) {
            canvas.drawText(totalValues[idx], curX + 4f, y + 11f, boldPaint)
            curX += colWidths[idx]
            if (idx < totalValues.size - 1) {
                canvas.drawLine(curX, y, curX, y + rowHeight, linePaint)
            }
        }
        y += rowHeight + 12f

        // Other Earnings & Arrears Section
        canvas.drawRect(leftMargin, y, rightMargin, y + 36f, headerBgPaint)
        canvas.drawRect(leftMargin, y, rightMargin, y + 36f, linePaint)
        canvas.drawText("OTHER EARNINGS & ARREARS:", leftMargin + 6f, y + 12f, boldPaint)
        canvas.drawText("DA Arrear: ₹ %,.0f (${earnings.daArrearMonths} Mo)".format(earnings.daArrear1), leftMargin + 160f, y + 12f, textPaint)
        canvas.drawText("Festival Bonus: ₹ %,.0f".format(earnings.bonus), leftMargin + 320f, y + 12f, textPaint)
        canvas.drawText("Surrender Leave Pay: ₹ %,.0f (${earnings.surrenderDays} Days)".format(earnings.surrenderAmount), leftMargin + 6f, y + 26f, textPaint)
        canvas.drawText("Other Arrears: ₹ %,.0f".format(earnings.otherArrear1 + earnings.otherArrear2), leftMargin + 240f, y + 26f, textPaint)
        canvas.drawText("TOTAL GROSS INCOME: ₹ %,.0f".format(computation.totalGrossSalary), leftMargin + 380f, y + 26f, boldPaint)
        y += 44f

        // Computation Section
        val compWidth = contentWidth / 2f
        val compLeft = leftMargin
        val compRight = leftMargin + compWidth - 4f
        val taxLeft = leftMargin + compWidth + 4f
        val taxRight = rightMargin

        // Left box: Income & Deductions
        canvas.drawRect(compLeft, y, compRight, y + 120f, linePaint)
        canvas.drawText("INCOME & DEDUCTIONS", compLeft + 6f, y + 13f, boldPaint)
        canvas.drawLine(compLeft, y + 18f, compRight, y + 18f, linePaint)

        var cy = y + 32f
        canvas.drawText("1. Total Gross Salary Income:", compLeft + 6f, cy, textPaint)
        canvas.drawText("₹ %,.0f".format(computation.totalGrossSalary), compRight - 70f, cy, boldPaint)
        cy += 14f

        canvas.drawText("2. Less: Standard Deduction u/s 16(ia):", compLeft + 6f, cy, textPaint)
        canvas.drawText("₹ %,.0f".format(computation.standardDeduction), compRight - 70f, cy, textPaint)
        cy += 14f

        if (computation.otherDeductionsChapter6A > 0) {
            canvas.drawText("3. Less: Chapter VI-A (80C, 80CCD, etc.):", compLeft + 6f, cy, textPaint)
            canvas.drawText("₹ %,.0f".format(computation.otherDeductionsChapter6A), compRight - 70f, cy, textPaint)
            cy += 14f
        }

        canvas.drawRect(compLeft, cy - 8f, compRight, cy + 12f, highlightBgPaint)
        canvas.drawText("NET TAXABLE INCOME:", compLeft + 6f, cy + 6f, boldPaint)
        canvas.drawText("₹ %,.0f".format(computation.taxableIncome), compRight - 70f, cy + 6f, boldPaint)

        // Right box: Tax Computation
        canvas.drawRect(taxLeft, y, taxRight, y + 120f, linePaint)
        canvas.drawText("TAX COMPUTATION (${computation.regime.labelEnglish})", taxLeft + 6f, y + 13f, boldPaint)
        canvas.drawLine(taxLeft, y + 18f, taxRight, y + 18f, linePaint)

        var ty = y + 30f
        canvas.drawText("Tax on Taxable Income:", taxLeft + 6f, ty, textPaint)
        canvas.drawText("₹ %,.2f".format(computation.taxBeforeRebate), taxRight - 80f, ty, textPaint)
        ty += 13f

        canvas.drawText("Less: Rebate u/s 87A / Marginal:", taxLeft + 6f, ty, textPaint)
        canvas.drawText("₹ %,.2f".format(computation.rebate87A + computation.marginalRelief), taxRight - 80f, ty, textPaint)
        ty += 13f

        canvas.drawText("Add: Health & Edu Cess @ 4%:", taxLeft + 6f, ty, textPaint)
        canvas.drawText("₹ %,.2f".format(computation.cess4Percent), taxRight - 80f, ty, textPaint)
        ty += 13f

        canvas.drawText("TOTAL TAX PAYABLE:", taxLeft + 6f, ty, boldPaint)
        canvas.drawText("₹ %,.2f".format(computation.totalTaxPayable), taxRight - 80f, ty, boldPaint)
        ty += 14f

        canvas.drawText("Less: TDS Paid (Mar - Oct):", taxLeft + 6f, ty, textPaint)
        canvas.drawText("₹ %,.0f".format(computation.tdsPaidSoFar), taxRight - 80f, ty, textPaint)
        ty += 15f

        canvas.drawRect(taxLeft, ty - 9f, taxRight, ty + 13f, highlightBgPaint)
        canvas.drawText("BALANCE TAX TO DEDUCT:", taxLeft + 6f, ty + 5f, boldPaint)
        canvas.drawText("₹ %,.2f".format(computation.balanceTaxToDeduct), taxRight - 80f, ty + 5f, boldPaint)

        y += 132f

        // Slab summary box
        canvas.drawRect(leftMargin, y, rightMargin, y + 42f, linePaint)
        canvas.drawText("TAX SLABS APPLIED:", leftMargin + 6f, y + 12f, boldPaint)
        var sx = leftMargin + 6f
        var sy = y + 25f
        for (slab in computation.slabBreakdowns) {
            if (slab.taxableInSlab > 0) {
                val str = "${slab.slabRange} @ ${slab.ratePercent.toInt()}% = ₹${"%,.0f".format(slab.taxAmount)}  |  "
                canvas.drawText(str, sx, sy, textPaint)
                sx += textPaint.measureText(str)
                if (sx > rightMargin - 100f) {
                    sx = leftMargin + 6f
                    sy += 11f
                }
            }
        }
        y += 50f

        // Declaration & Signatures
        val declText = "I hereby declare that all details furnished in this statement are true, complete and correct. All income received during the Financial Year is shown without omission."
        canvas.drawText(declText, leftMargin, y, textPaint)
        y += 35f

        canvas.drawText("Signature of the Employee / Govt. Servant", leftMargin + 10f, y, boldPaint)
        canvas.drawText("Signature of the Head of the Office / DDO", rightMargin - 180f, y, boldPaint)
        y += 12f
        canvas.drawText("Name: ${profile.name}", leftMargin + 10f, y, textPaint)
        canvas.drawText("Seal & Date: ________________________", rightMargin - 180f, y, textPaint)

        pdfDocument.finishPage(page)

        // Save PDF to cache directory
        val file = File(context.cacheDir, "IT_Statement_${profile.ifhrmsNo}_${taxYear.label.replace(" ", "_")}.pdf")
        FileOutputStream(file).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()
        return file
    }
}
