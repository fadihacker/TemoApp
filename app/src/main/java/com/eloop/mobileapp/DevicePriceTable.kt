package com.eloop.mobileapp

data class DeviceEstimate(
    val category: String,
    val basePrice: Int,
    val co2Saved: Float,
    val ecoPoints: Int,
    val priority: Int = 0
)

object DevicePriceTable {

    private val headphoneKeywords = listOf("headphone", "earphone", "airpod", "earbud", "buds", "pods", "audio")
    private val laptopKeywords = listOf("laptop", "notebook", "macbook", "computer", "pc", "thinkpad")
    private val smartphoneKeywords = listOf("smartphone", "phone", "iphone", "android", "cell", "galaxy", "pixel")
    private val watchKeywords = listOf("watch", "smartwatch", "wearable", "apple watch", "galaxy watch")

    fun getEstimateByCategory(categoryName: String): DeviceEstimate? {
        val lowerName = categoryName.lowercase().trim()
        return getMatchingEstimate(lowerName) ?: getEstimateByInternalType("Smartphone")
    }

    private fun getMatchingEstimate(query: String): DeviceEstimate? {
        val lowerQuery = query.lowercase().trim()

        return when {
            laptopKeywords.any { lowerQuery.contains(it) } -> getEstimateByInternalType("Laptop")
            headphoneKeywords.any { lowerQuery.contains(it) } -> getEstimateByInternalType("Headphone")
            smartphoneKeywords.any { lowerQuery.contains(it) } -> getEstimateByInternalType("Smartphone")
            watchKeywords.any { lowerQuery.contains(it) } -> getEstimateByInternalType("Smartwatch")
            else -> null
        }
    }

    private fun getEstimateByInternalType(type: String): DeviceEstimate? {
        return when (type) {
            "Laptop" -> DeviceEstimate(
                category = "Laptop",
                basePrice = 18000,
                co2Saved = 7.5f,
                ecoPoints = 1500,
                priority = 90
            )
            "Headphone" -> DeviceEstimate(
                category = "Headphone",
                basePrice = 4500,
                co2Saved = 0.8f,
                ecoPoints = 350,
                priority = 100
            )
            "Smartphone" -> DeviceEstimate(
                category = "Smartphone",
                basePrice = 12000,
                co2Saved = 4.2f,
                ecoPoints = 820,
                priority = 80
            )
            "Smartwatch" -> DeviceEstimate(
                category = "Smartwatch",
                basePrice = 6500,
                co2Saved = 0.5f,
                ecoPoints = 250,
                priority = 70
            )
            else -> null
        }
    }
}

