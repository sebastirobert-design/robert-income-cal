package com.example.calculator

object HraTableHelper {
    data class HraSlab(val minPay: Double, val maxPay: Double, val grade1a: Double, val grade1b: Double, val grade2: Double, val grade3: Double, val grade4: Double)

    private val slabs = listOf(
        HraSlab(0.0, 13700.0, 700.0, 600.0, 400.0, 250.0, 250.0),
        HraSlab(13701.0, 17300.0, 1000.0, 700.0, 450.0, 300.0, 300.0),
        HraSlab(17301.0, 21100.0, 1200.0, 800.0, 500.0, 350.0, 350.0),
        HraSlab(21101.0, 24000.0, 1400.0, 1000.0, 700.0, 400.0, 400.0),
        HraSlab(24001.0, 27300.0, 1700.0, 1200.0, 800.0, 400.0, 400.0),
        HraSlab(27301.0, 30700.0, 2000.0, 1500.0, 1000.0, 450.0, 450.0),
        HraSlab(30701.0, 35500.0, 2300.0, 1700.0, 1200.0, 500.0, 500.0),
        HraSlab(35501.0, 37400.0, 2600.0, 1800.0, 1500.0, 550.0, 550.0),
        HraSlab(37401.0, 41200.0, 3000.0, 2300.0, 1700.0, 600.0, 600.0),
        HraSlab(41201.0, 44600.0, 3300.0, 2600.0, 1900.0, 650.0, 650.0),
        HraSlab(44601.0, 50300.0, 3600.0, 2900.0, 2000.0, 650.0, 650.0),
        HraSlab(50301.0, 51700.0, 3800.0, 3100.0, 2200.0, 700.0, 700.0),
        HraSlab(51701.0, 54100.0, 4100.0, 3200.0, 2200.0, 750.0, 750.0),
        HraSlab(54101.0, 55600.0, 4300.0, 3200.0, 2200.0, 800.0, 800.0),
        HraSlab(55601.0, 57000.0, 4300.0, 3200.0, 2200.0, 850.0, 850.0),
        HraSlab(57001.0, 64300.0, 4300.0, 3200.0, 2200.0, 850.0, 850.0),
        HraSlab(64301.0, 999999.0, 4300.0, 3200.0, 2200.0, 850.0, 850.0)
    )

    fun getSuggestedHra(basicPay: Double, grade: String, fixedHra: Double): Double {
        if (fixedHra > 0) return fixedHra
        val slab = slabs.firstOrNull { basicPay >= it.minPay && basicPay <= it.maxPay } ?: slabs.last()
        return when (grade) {
            "Grade 1(a)" -> slab.grade1a
            "Grade 1(b)" -> slab.grade1b
            "Grade II" -> slab.grade2
            "Grade III" -> slab.grade3
            "Grade IV" -> slab.grade4
            else -> fixedHra
        }
    }
}
