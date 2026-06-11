package com.eloop.mobileapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.eloop.mobileapp.data.SessionManager
import com.eloop.mobileapp.ui.screens.*
import com.eloop.mobileapp.ui.theme.ELoopTheme
import com.eloop.mobileapp.ui.components.BottomNavigationBar

val LocalAppLanguage = compositionLocalOf { "EN" }

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val sessionManager = remember { SessionManager(context) }
            var themeMode by remember { mutableStateOf(sessionManager.getThemeMode()) }
            var appLanguage by remember { mutableStateOf(sessionManager.getAppLanguage()) }

            CompositionLocalProvider(LocalAppLanguage provides appLanguage) {
                ELoopTheme(themeMode = themeMode) {
                    MainApp(
                        currentThemeMode = themeMode,
                        onThemeChange = { newMode ->
                            sessionManager.setThemeMode(newMode)
                            themeMode = newMode
                        },
                        currentLanguage = appLanguage,
                        onLanguageChange = { newLang ->
                            sessionManager.setAppLanguage(newLang)
                            appLanguage = newLang
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MainApp(
    currentThemeMode: String, 
    onThemeChange: (String) -> Unit,
    currentLanguage: String,
    onLanguageChange: (String) -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val showBottomBar = currentRoute in listOf("home", "categories", "scan", "impact", "profile")

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "splash",
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            composable("splash") { SplashScreen(navController) }
            composable("onboarding") { OnboardingScreen(navController) }
            composable("login") { LoginScreen(navController) }
            composable("signup") { SignupScreen(navController) }
            composable("home") { HomeScreen(navController) }
            composable("categories") { CategoriesScreen(navController) }
            composable(
                route = "scan?hint={hint}",
                arguments = listOf(androidx.navigation.navArgument("hint") { 
                    defaultValue = ""
                    type = androidx.navigation.NavType.StringType 
                })
            ) { backStackEntry ->
                val hint = backStackEntry.arguments?.getString("hint") ?: ""
                ScanScreen(navController, hint)
            }
            composable("rewards") { RewardsScreen(navController) }
            composable("profile") { ProfileScreen(navController) }
            composable("notifications") { NotificationsScreen(navController) }
            composable("offers") { OffersScreen(navController) }
            composable("leaderboard") { LeaderboardScreen(navController) }
            
            // ✅ Updated routes using underscored naming convention
            composable("find") { FindScreen(navController) }
            composable("eco_articles") { EcoArticlesScreen(navController) }
            composable("impact") { ImpactScreen(navController) }
            composable("ai_features") { AIFeaturesScreen(navController) }
            composable(
                route = "condition_selection?category={category}&basePrice={basePrice}&co2Saved={co2Saved}&ecoPoints={ecoPoints}",
                arguments = listOf(
                    androidx.navigation.navArgument("category") { defaultValue = "Smartphone" },
                    androidx.navigation.navArgument("basePrice") { type = androidx.navigation.NavType.IntType; defaultValue = 8000 },
                    androidx.navigation.navArgument("co2Saved") { defaultValue = "4.2" },
                    androidx.navigation.navArgument("ecoPoints") { type = androidx.navigation.NavType.IntType; defaultValue = 820 }
                )
            ) { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: "Smartphone"
                val basePrice = backStackEntry.arguments?.getInt("basePrice") ?: 8000
                val co2Saved = backStackEntry.arguments?.getString("co2Saved")?.toFloatOrNull() ?: 4.2f
                val ecoPoints = backStackEntry.arguments?.getInt("ecoPoints") ?: 820
                ConditionScreen(navController, category, basePrice, co2Saved, ecoPoints)
            }

            composable(
                route = "ai_valuation?category={category}&basePrice={basePrice}&condition={condition}&co2Saved={co2Saved}&ecoPoints={ecoPoints}",
                arguments = listOf(
                    androidx.navigation.navArgument("category") { defaultValue = "Smartphone" },
                    androidx.navigation.navArgument("basePrice") { type = androidx.navigation.NavType.IntType; defaultValue = 8000 },
                    androidx.navigation.navArgument("condition") { defaultValue = "good" },
                    androidx.navigation.navArgument("co2Saved") { defaultValue = "4.2" },
                    androidx.navigation.navArgument("ecoPoints") { type = androidx.navigation.NavType.IntType; defaultValue = 820 }
                )
            ) { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: "Smartphone"
                val basePrice = backStackEntry.arguments?.getInt("basePrice") ?: 8000
                val condition = backStackEntry.arguments?.getString("condition") ?: "good"
                val co2Saved = backStackEntry.arguments?.getString("co2Saved")?.toFloatOrNull() ?: 4.2f
                val ecoPoints = backStackEntry.arguments?.getInt("ecoPoints") ?: 820
                
                // Calculate final price based on condition
                val multiplier = when(condition) {
                    "excellent" -> 1.0f
                    "good" -> 0.85f
                    "fair" -> 0.65f
                    "broken" -> 0.35f
                    else -> 0.85f
                }
                val finalPrice = (basePrice * multiplier).toInt()
                val formattedPrice = "EGP %,d".format(finalPrice)

                AIScreen(navController, category, formattedPrice, co2Saved, ecoPoints)
            }
            composable("help_faq") { HelpFaqScreen(navController) }
            composable("chatbot") { ChatBotScreen(navController) }
            composable("contact_us") { ContactUsScreen(navController) }
            composable("device_journey") { DeviceJourneyScreen(navController) }
            composable("my_history") { MyHistoryScreen(navController) }
            composable("achievements") { AchievementsScreen(navController) }
            composable(
                route = "schedule_pickup?name={name}&price={price}",
                arguments = listOf(
                    androidx.navigation.navArgument("name") { defaultValue = "Your Device" },
                    androidx.navigation.navArgument("price") { defaultValue = "2,450" }
                )
            ) { backStackEntry ->
                val name = backStackEntry.arguments?.getString("name") ?: "Your Device"
                val price = backStackEntry.arguments?.getString("price") ?: "2,450"
                SchedulePickupScreen(navController, name, price)
            }
            composable("order_confirmation") { OrderConfirmationScreen(navController) }
            composable("account_settings") { 
                AccountSettingsScreen(
                    navController = navController,
                    currentThemeMode = currentThemeMode,
                    onThemeChange = onThemeChange,
                    currentLanguage = currentLanguage,
                    onLanguageChange = onLanguageChange
                ) 
            }
            composable("edit_profile") { EditProfileScreen(navController) }
            composable("change_password") { ChangePasswordScreen(navController) }
            composable("privacy_policy") { PrivacyPolicyScreen(navController) }
            composable("terms_conditions") { TermsConditionsScreen(navController) }
            composable("admin_dashboard") { AdminDashboardScreen(navController) }
            
            composable(
                route = "center_details/{id}",
                arguments = listOf(androidx.navigation.navArgument("id") { type = androidx.navigation.NavType.StringType })
            ) { backStackEntry ->
                val centerId = backStackEntry.arguments?.getString("id") ?: ""
                RecyclingCenterDetailScreen(navController = navController, centerId = centerId)
            }

            composable(
                route = "article_detail/{articleId}",
                arguments = listOf(androidx.navigation.navArgument("articleId") { type = androidx.navigation.NavType.IntType })
            ) { backStackEntry ->
                val articleId = backStackEntry.arguments?.getInt("articleId") ?: 1
                ArticleDetailScreen(navController = navController, articleId = articleId)
            }
            
            // Legacy routes (optional, can be kept for compatibility if needed elsewhere)
            // composable("ai") { AIScreen(navController) }
            // composable("help-faq") { HelpFaqScreen(navController) }
            // composable("account-settings") { AccountSettingsScreen(navController) }
        }
    }
}

