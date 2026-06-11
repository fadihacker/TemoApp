package com.eloop.mobileapp.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.graphics.Color

data class EcoArticle(
    val id: Int,
    val title: String,
    val category: String, // "Safety", "DIY", "News", "Tips", "Tech"
    val readTime: String,
    val summary: String,
    val content: String,
    var isBookmarked: Boolean = false,
    val imageUrl: String = "" // Optional, but used for UI
)

object EcoData {
    val bookmarkedIds = mutableStateListOf<Int>()

    fun isBookmarked(id: Int) = bookmarkedIds.contains(id)
    
    fun toggleBookmark(id: Int) {
        if (bookmarkedIds.contains(id)) {
            bookmarkedIds.remove(id)
        } else {
            bookmarkedIds.add(id)
        }
    }

    val articles = listOf(
        EcoArticle(
            id = 1,
            title = "How to safely dispose of Lithium batteries",
            category = "Safety",
            readTime = "3 min read",
            summary = "Swollen batteries are dangerous. Learn how to handle them safely.",
            content = "Have you ever noticed your phone screen popping out? That's a swollen lithium-ion battery and it can be extremely dangerous. As batteries age, chemical reactions break down and produce gas. NEVER puncture a swollen battery — it can catch fire immediately. Stop using the device, place it in a cool dry place, and use ELoop to schedule a safe pickup. By recycling with ELoop, dangerous chemicals are neutralized and valuable materials like cobalt and lithium are recovered for new batteries!",
            imageUrl = "https://images.unsplash.com/photo-1609091839311-d5365f9ff1c5?w=800"
        ),
        EcoArticle(
            id = 2,
            title = "The hidden gold in your old laptop",
            category = "Tech",
            readTime = "5 min read",
            summary = "Your old devices contain real gold, silver and copper.",
            content = "Did you know there is more gold in a ton of smartphones than in a ton of gold ore? Every phone contains gold in circuit boards, silver in connections, and copper throughout. Recycling 1 million phones recovers 35,000 lbs of copper, 772 lbs of silver, and 75 lbs of gold! By recycling with ELoop, you reduce destructive mining operations and earn rewards. You are holding a tiny gold mine — recycle it!",
            imageUrl = "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=800"
        ),
        EcoArticle(
            id = 3,
            title = "10 ways to reduce electronic waste",
            category = "Tips",
            readTime = "6 min read",
            summary = "Simple daily habits that make a big difference.",
            content = "1. Repair before replacing. 2. Donate old devices to schools. 3. Buy refurbished electronics. 4. Use ELoop to recycle old phones. 5. Remove batteries before disposal. 6. Never throw electronics in regular trash. 7. Buy products with longer warranties. 8. Use cloud storage instead of physical drives. 9. Properly store devices to extend lifespan. 10. Spread awareness about e-waste to friends and family.",
            imageUrl = "https://images.unsplash.com/photo-1532996122724-e3c354a0b15b?w=800"
        ),
        EcoArticle(
            id = 4,
            title = "DIY: Clean your old PC before recycling",
            category = "DIY",
            readTime = "4 min read",
            summary = "Wipe your data safely before handing over your device.",
            content = "Before recycling your PC, always perform a factory reset to protect your personal data. For Windows: Go to Settings → Update & Security → Recovery → Reset this PC. For Mac: Restart in Recovery Mode and use Disk Utility to erase. Remove the SIM card and memory card from phones. ELoop also offers professional data wiping for all devices — military-grade secure erasure before any dismantling occurs.",
            imageUrl = "https://images.unsplash.com/photo-1518770660439-4636190af475?w=800"
        ),
        EcoArticle(
            id = 5,
            title = "Why E-Waste is the fastest growing waste stream",
            category = "News",
            readTime = "3 min read",
            summary = "50 million tons of e-waste generated every year globally.",
            content = "The world generates over 50 million tons of electronic waste every year — and only 20% is properly recycled. The rest ends up in landfills, releasing lead, mercury, and cadmium into soil and water. Egypt alone generates over 350,000 tons annually. The main driver is rapid technology replacement cycles — people upgrade phones every 2 years on average. Platforms like ELoop are part of the solution, making responsible recycling easy and rewarding.",
            imageUrl = "https://images.unsplash.com/photo-1550751827-4bd374c3f58b?w=800"
        ),
        EcoArticle(
            id = 6,
            title = "How ELoop calculates your device value",
            category = "Tips",
            readTime = "2 min read",
            summary = "Understanding how we price your old electronics.",
            content = "ELoop uses a smart valuation system combining real market data with device condition. We check brand, model, storage capacity, and physical condition (Excellent, Good, Fair, Broken). The ELoop trade-in value is typically 20% of the current used market price — this is the minimum you receive. You also earn Eco Points: every 100 EGP = 50 points, redeemable for vouchers and rewards. The more you recycle, the more you earn!",
            imageUrl = "https://images.unsplash.com/photo-1611532736597-de2d4265fba3?w=800"
        )
    )

    fun getCategoryColor(category: String): Color {
        return when (category) {
            "Safety" -> Color(0xFFE57373)
            "Tech" -> Color(0xFF64B5F6)
            "Tips" -> Color(0xFFFFB74D)
            "DIY" -> Color(0xFF81C784)
            "News" -> Color(0xFF90A4AE)
            else -> Color(0xFF84A582)
        }
    }
}

