package com.eloop.mobileapp

data class DevicePrice(
    val base: Int,
    val explicitImageUrl: String? = null
) {
    fun getImageUrl(key: String): String {
        if (explicitImageUrl != null) return explicitImageUrl
        val k = key.lowercase()
        return when {
            k.contains("iphone") -> "https://images.unsplash.com/photo-1511707171634-5f897ff02aa9?w=400&q=80"
            k.contains("samsung") -> "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=400&q=80"
            k.contains("macbook") -> "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400&q=80"
            k.contains("dell") || k.contains("hp") || k.contains("lenovo") || k.contains("laptop") -> "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400&q=80"
            k.contains("ipad") || k.contains("tab") || k.contains("tablet") -> "https://images.unsplash.com/photo-1544244015-0df4b3ffc6b0?w=400&q=80"
            k.contains("ps5") || k.contains("xbox") || k.contains("nintendo") -> "https://images.unsplash.com/photo-1605901309584-818e25960b8f?w=400&q=80"
            k.contains("watch") -> "https://images.unsplash.com/photo-1523275335684-37898b6baf30?w=400&q=80"
            k.contains("camera") -> "https://images.unsplash.com/photo-1516035069371-29a1b244cc32?w=400&q=80"
            k.contains("airpods") || k.contains("sony") -> "https://images.unsplash.com/photo-1590658268037-6bf12165a8df?w=400&q=80"
            else -> "https://images.unsplash.com/photo-1550009158-9effb64f4dd6?w=400&q=80" // Generic tech
        }
    }
    
    fun getBrand(key: String): String {
        val k = key.lowercase()
        return when {
            k.contains("iphone") || k.contains("macbook") || k.contains("ipad") || k.contains("apple") || k.contains("airpods") -> "Apple"
            k.contains("samsung") || k.contains("galaxy") -> "Samsung"
            k.contains("dell") -> "Dell"
            k.contains("hp") -> "HP"
            k.contains("lenovo") -> "Lenovo"
            k.contains("sony") || k.contains("ps5") || k.contains("ps4") -> "Sony"
            k.contains("xbox") -> "Microsoft"
            k.contains("nintendo") -> "Nintendo"
            else -> "Unknown Brand"
        }
    }

    fun calculate(condition: String): Int {
        val multiplier = when(condition.lowercase()) {
            "excellent" -> 1.0
            "good" -> 0.85
            "fair" -> 0.65
            "broken" -> 0.35
            else -> 0.85
        }
        return (base * multiplier).toInt()
    }
    fun eloopValue(): Int = (base * 0.25).toInt()
    fun points(): Int = eloopValue() / 2
}

object DevicePriceDatabase {
    val prices = mapOf(
        // ═══ iPhone Series ═══
        "iphone-16-pro-max" to DevicePrice(55000, "https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=400&q=80"),
        "iphone-16-pro" to DevicePrice(50000, "https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=400&q=80"),
        "iphone-16" to DevicePrice(40000, "https://images.unsplash.com/photo-1695048133142-1a20484d2569?w=400&q=80"),
        "iphone-15-pro-max" to DevicePrice(45000, "https://images.unsplash.com/photo-1696446701796-da61225697cc?w=400&q=80"),
        "iphone-15-pro" to DevicePrice(40000),
        "iphone-15" to DevicePrice(32000, "https://images.unsplash.com/photo-1696429150337-1e5b1f93f1f3?w=400&q=80"),
        "iphone-14-pro-max" to DevicePrice(35000),
        "iphone-14-pro" to DevicePrice(30000),
        "iphone-14" to DevicePrice(25000, "https://images.unsplash.com/photo-1663499482523-1c0c1bae4ce1?w=400&q=80"),
        "iphone-13-pro-max" to DevicePrice(28000),
        "iphone-13" to DevicePrice(20000, "https://images.unsplash.com/photo-1632661674596-df8be070a5c5?w=400&q=80"),
        "iphone-12-pro" to DevicePrice(18000),
        "iphone-12" to DevicePrice(15000, "https://images.unsplash.com/photo-1603891128711-11b4b03bb138?w=400&q=80"),
        "iphone-11" to DevicePrice(12000, "https://images.unsplash.com/photo-1591337676887-a217a6970c8a?w=400&q=80"),
        "iphone-x" to DevicePrice(8000),

        // ═══ Samsung Galaxy ═══
        "samsung-s24-ultra" to DevicePrice(45000, "https://images.unsplash.com/photo-1707248310931-e39665476a03?w=400&q=80"),
        "samsung-s24-plus" to DevicePrice(35000),
        "samsung-s24" to DevicePrice(30000),
        "samsung-s23-ultra" to DevicePrice(32000, "https://images.unsplash.com/photo-1678911820864-e2c567c655d7?w=400&q=80"),
        "samsung-s23" to DevicePrice(22000),
        "samsung-s22-ultra" to DevicePrice(25000),
        "samsung-s21" to DevicePrice(12000),
        "samsung-a54" to DevicePrice(10000, "https://images.unsplash.com/photo-1610945265064-0e34e5519bbf?w=400&q=80"),
        "samsung-a34" to DevicePrice(8000),
        "samsung-a14" to DevicePrice(5000),

        // ═══ MacBook Series ═══
        "macbook-pro-m3" to DevicePrice(60000, "https://images.unsplash.com/photo-1517336714731-489689fd1ca8?w=400&q=80"),
        "macbook-pro-m2" to DevicePrice(50000),
        "macbook-air-m2" to DevicePrice(40000),
        "macbook-air-m1" to DevicePrice(30000, "https://images.unsplash.com/photo-1611186871348-b1ec696e523b?w=400&q=80"),

        // ═══ Laptops (Generic) ═══
        "dell-xps" to DevicePrice(35000),
        "hp-spectre" to DevicePrice(30000),
        "lenovo-thinkpad" to DevicePrice(25000),
        "gaming-laptop" to DevicePrice(30000),

        // ═══ iPad & Tablets ═══
        "ipad-pro" to DevicePrice(35000),
        "ipad-air" to DevicePrice(20000),
        "ipad-mini" to DevicePrice(15000),
        "samsung-tab-s9" to DevicePrice(25000),

        // ═══ Gaming ═══
        "ps5" to DevicePrice(25000),
        "ps4" to DevicePrice(10000),
        "xbox-series-x" to DevicePrice(22000),
        "nintendo-switch" to DevicePrice(12000),

        // ═══ Accessories ═══
        "airpods-pro" to DevicePrice(6000),
        "airpods" to DevicePrice(4000),
        "apple-watch-ultra" to DevicePrice(25000),
        "apple-watch" to DevicePrice(12000),
        "sony-wh1000xm5" to DevicePrice(15000),

        // ═══ Generic Fallbacks ═══
        "phone" to DevicePrice(10000),
        "laptop" to DevicePrice(20000),
        "tablet" to DevicePrice(10000),
        "smartwatch" to DevicePrice(5000),
        "camera" to DevicePrice(15000),
        "printer" to DevicePrice(5000)
    )

    fun exists(key: String): Boolean {
        return prices.containsKey(key.lowercase().trim())
    }

    fun findKeyAndPrice(query: String): Pair<String, DevicePrice> {
        val q = query.lowercase().replace(" ", "-").replace("_", "-")
        
        // 1. Exact match
        prices[q]?.let { return q to it }
        
        // 2. Category mapping
        if (q.contains("iphone")) return "iphone-15" to prices["iphone-15"]!!
        if (q.contains("samsung")) return "samsung-s23-ultra" to prices["samsung-s23-ultra"]!!
        if (q.contains("macbook")) return "macbook-air-m1" to prices["macbook-air-m1"]!!
        if (q.contains("laptop") || q.contains("dell") || q.contains("hp") || q.contains("lenovo")) return "laptop" to prices["laptop"]!!
        if (q.contains("tablet") || q.contains("ipad")) return "tablet" to prices["tablet"]!!
        if (q.contains("camera")) return "camera" to prices["camera"]!!
        if (q.contains("watch")) return "smartwatch" to prices["smartwatch"]!!
        if (q.contains("playstation") || q.contains("xbox")) return "ps5" to prices["ps5"]!!
        
        // 3. Partial match
        val partialMatch = prices.entries
            .filter { entry -> 
                val keywords = entry.key.split("-")
                keywords.any { q.contains(it) }
            }
            .maxByOrNull { it.key.length }
            
        if (partialMatch != null) return partialMatch.key to partialMatch.value
        
        return "phone" to prices["phone"]!!
    }

    fun find(query: String): DevicePrice? {
        return findKeyAndPrice(query).second
    }
}

