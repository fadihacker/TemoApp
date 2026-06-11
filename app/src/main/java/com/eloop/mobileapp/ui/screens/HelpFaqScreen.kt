package com.eloop.mobileapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.HelpCenter
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import androidx.core.net.toUri
import androidx.navigation.NavController
import com.eloop.mobileapp.ui.components.*
import com.eloop.mobileapp.ui.theme.*
import com.eloop.mobileapp.LocalAppLanguage

@Composable
fun HelpFaqScreen(navController: NavController) {
    val language = LocalAppLanguage.current
    val isAr = language == "AR"
    
    val faqs = if (isAr) {
        listOf(
            "كيف يمكنني إعادة تدوير جهازي؟" to "اختر جهازك من الفئات، امسحه ضوئياً أو أدخل التفاصيل يدوياً، واحصل على تقييم بالذكاء الاصطناعي، ثم اختر البيع أو التسليم في أحد المراكز.",
            "كيف يتم تقدير السعر؟" to "يقوم الذكاء الاصطناعي لدينا بتحليل العلامة التجارية والطراز والحالة وبيانات السوق الحالية لتقديم تقدير سوقي عادل.",
            "هل النقاط البيئية حقيقية؟" to "نعم! يتم كسب النقاط البيئية مع كل عملية إعادة تدوير ويمكن استبدالها بخصومات وقسائم من شركائنا.",
            "كم من الوقت يستغرق الاستلام؟" to "عادةً ما يتم تحديد موعد الاستلام في غضون ٢٤-٤٨ ساعة من طلبك، حسب موقعك وتوافر الخدمة.",
            "هل بياناتي آمنة؟" to "بالتأكيد. يستخدم ELoop التشفير التام ولا يشارك بياناتك مع أطراف ثالثة دون موافقتك.",
            "ما هي الأجهزة التي يمكن إعادة تدويرها؟" to "الهواتف الذكية، أجهزة الكمبيوتر المحمولة، الأجهزة اللوحية، أجهزة التلفزيون، الطابعات، الكاميرات، الملحقات، ومعظم الإلكترونيات المنزلية.",
        )
    } else {
        listOf(
            "How do I recycle my device?" to "Select your device from Categories, scan it or enter details manually, get an AI valuation, then choose to sell or drop off at a center.",
            "How is the price estimated?" to "Our AI analyzes the brand, model, condition, and current market data to provide a fair market estimate.",
            "Are Eco Points real?" to "Yes! Eco Points are earned with every recycling action and can be redeemed for discounts and vouchers from our partners.",
            "How long does pickup take?" to "Pickups are usually scheduled within 24-48 hours of your request, depending on your location and availability.",
            "Is my data safe?" to "Absolutely. ELoop uses end-to-end encryption and does not share your data with third parties without your consent.",
            "What devices can be recycled?" to "Smartphones, laptops, tablets, TVs, printers, cameras, accessories, and most household electronics.",
        )
    }

    var searchQuery by remember { mutableStateOf("") }
    val filteredFaqs = faqs.filter { it.first.contains(searchQuery, ignoreCase = true) }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate("chatbot") },
                containerColor = PrimaryGreen,
                contentColor = Color.White,
                        icon = { Icon(Icons.Rounded.SmartToy, contentDescription = null) },
                text = { Text(if (isAr) "بوت بيئي" else "EcoBot") }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
        ) {
            ScreenHeader(title = if (isAr) "المساعدة والأسئلة الشائعة" else "Help & FAQ", onBack = { navController.popBackStack() })

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                item {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
                        placeholder = { Text(if (isAr) "ابحث في مواضيع المساعدة..." else "Search help topics...") },
                        leadingIcon = { Icon(Icons.Rounded.Search, contentDescription = null) },
                        shape = RoundedCornerShape(24.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = PrimaryGreen,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline
                        )
                    )
                }

                if (filteredFaqs.isEmpty()) {
                    item {
                        EmptyState(
                            icon = Icons.AutoMirrored.Rounded.HelpCenter,
                            title = if (isAr) "لا توجد نتائج" else "No results found",
                            subtitle = if (isAr) "لم نتمكن من العثور على إجابة. جرب البوت البيئي!" else "We couldn't find an answer. Try the chat bot!"
                        )
                    }
                } else {
                    items(filteredFaqs) { (q, a) ->
                        ExpandableFaqCard(question = q, answer = a)
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    Text(if (isAr) "الدعم المباشر" else "Direct Support", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(12.dp))
                    val context = LocalContext.current
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        ContactChip(
                            icon = Icons.Rounded.Email,
                            label = if (isAr) "اتصل بنا" else "Contact Us",
                            modifier = Modifier.weight(1f),
                            onClick = { navController.navigate("contact_us") }
                        )
                        ContactChip(
                            icon = Icons.Rounded.Call,
                            label = if (isAr) "مركز الاتصال" else "Call Center",
                            modifier = Modifier.weight(1f),
                            onClick = { 
                                val intent = android.content.Intent(android.content.Intent.ACTION_DIAL).apply {
                                    data = "tel:+201223456789".toUri()
                                }
                                context.startActivity(intent)
                            }
                        )
                    }
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
        }
    }
}

@Composable
fun ExpandableFaqCard(question: String, answer: String) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        onClick = { expanded = !expanded }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(question, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f))
                Icon(
                    if (expanded) Icons.Rounded.ExpandLess else Icons.Rounded.ExpandMore,
                    contentDescription = null,
                    tint = PrimaryGreen
                )
            }
            if (expanded) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(answer, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
    }
}

@Composable
fun ContactChip(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = PrimaryGreen)
            Text(label, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        }
    }
}



