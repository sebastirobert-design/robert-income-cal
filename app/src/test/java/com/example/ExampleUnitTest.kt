package com.example

import com.example.calculator.HraTableHelper
import com.example.calculator.TaxCalculatorEngine
import com.example.model.TaxYear
import com.example.parser.IfhrmsDataParser
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testTaxCalculationNewRegimeExact() {
        val (months, earnings) = IfhrmsDataParser.createRobertSebastinExactData()
        val totalMonthsGross = months.sumOf { it.totalGross }
        val totalGross = totalMonthsGross + earnings.totalOtherEarnings
        
        val computation = TaxCalculatorEngine.calculateNewRegimeTax(totalGross, months)
        
        assertEquals(1828708.0, computation.totalGrossSalary, 1.0)
        assertEquals(1753708.0, computation.taxableIncome, 1.0)
        assertEquals(75000.0, computation.standardDeduction, 0.0)
        assertEquals(156771.6, computation.totalTaxPayable, 1.0)
        assertEquals(101051.0, computation.tdsPaidSoFar, 1.0)
        assertEquals(55720.6, computation.balanceTaxToDeduct, 1.0)
    }

    @Test
    fun testHraHelper() {
        val hra = HraTableHelper.getSuggestedHra(91700.0, "Grade II", 3200.0)
        assertEquals(3200.0, hra, 0.0)
    }

    @Test
    fun testIfhrmsTextParser() {
        val sample = """
            Employee ID : 43020114172 Employee Name : Robert Sebastin J
            PAN No : AICPR6198J
            Duty Pay 91700
            House Rent Allowance 3200
        """.trimIndent()
        val res = IfhrmsDataParser.parseIfhrmsText(sample)
        assertEquals("Robert Sebastin J", res.name)
        assertEquals("43020114172", res.empId)
        assertEquals("AICPR6198J", res.panNo)
        assertEquals(91700.0, res.basicPay, 0.0)
        assertEquals(3200.0, res.hra, 0.0)
    }
}
