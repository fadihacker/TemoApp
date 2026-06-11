package com.eloop.mobileapp.data

import org.osmdroid.util.GeoPoint

data class RecyclingCenterInfo(
    val id: String,
    val name: String,
    val area: String,
    val distance: String,
    val rating: Float,
    val hours: String,
    val location: GeoPoint,
    val acceptedWaste: List<String>
)

object RecyclingCentersData {
    val centers = listOf(
        RecyclingCenterInfo(
            id = "eco_cairo",
            name = "EcoRecycle Cairo",
            area = "Nasr City",
            distance = "2.3 km", // Example mock distance
            rating = 4.8f,
            hours = "10:00 AM - 8:00 PM",
            location = GeoPoint(30.0626, 31.3419),
            acceptedWaste = listOf("Electronics", "Metal")
        ),
        RecyclingCenterInfo(
            id = "green_hub",
            name = "GreenTech Hub",
            area = "Maadi",
            distance = "5.1 km",
            rating = 4.7f,
            hours = "9:00 AM - 6:00 PM",
            location = GeoPoint(29.9602, 31.2569),
            acceptedWaste = listOf("Electronics", "Metal", "Plastic", "Paper") // All types
        ),
        RecyclingCenterInfo(
            id = "cairo_ewaste",
            name = "Cairo E-Waste Center",
            area = "Heliopolis",
            distance = "3.4 km",
            rating = 4.5f,
            hours = "8:00 AM - 5:00 PM",
            location = GeoPoint(30.0911, 31.3424),
            acceptedWaste = listOf("Electronics")
        ),
        RecyclingCenterInfo(
            id = "cycle_now",
            name = "RecycleNow",
            area = "Dokki",
            distance = "4.0 km",
            rating = 4.9f,
            hours = "10:00 AM - 7:00 PM",
            location = GeoPoint(30.0392, 31.2113),
            acceptedWaste = listOf("Paper", "Plastic")
        ),
        RecyclingCenterInfo(
            id = "smart_recycle",
            name = "SmartRecycle",
            area = "6th of October",
            distance = "15.5 km",
            rating = 4.6f,
            hours = "24/7",
            location = GeoPoint(29.9748, 30.9277),
            acceptedWaste = listOf("Metal", "Plastic")
        )
    )
}

