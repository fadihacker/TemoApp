package com.eloop.mobileapp.ui.screens

import android.content.Context
import android.content.res.Configuration
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.eloop.mobileapp.R
import com.eloop.mobileapp.LocalAppLanguage
import com.eloop.mobileapp.data.ConversationTurn
import com.eloop.mobileapp.data.GeminiClient
import com.eloop.mobileapp.ui.components.ScreenHeader
import com.eloop.mobileapp.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.Locale

data class ChatMessage(
    val text: String,
    val isUser: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)

// Smart response engine with pattern matching
object EcoBotEngine {
    
    data class BotResponse(
        val text: String,
        val quickReplies: List<String> = emptyList()
    )

    private val greetingPatterns = listOf("hi", "hello", "hey", "مرحبا", "اهلا", "أهلا", "هاي", "السلام", "سلام")
    private val recyclePatterns = listOf("recycle", "recycling", "اعادة", "إعادة", "تدوير", "ريسايكل", "how to")
    private val pricePatterns = listOf("price", "سعر", "كام", "بكام", "ثمن", "قيمة", "تقييم", "valuation", "estimate", "worth")
    private val phonePatterns = listOf("phone", "mobile", "iphone", "samsung", "هاتف", "موبايل", "تليفون", "آيفون", "سامسونج")
    private val laptopPatterns = listOf("laptop", "macbook", "لابتوب", "ماك", "كمبيوتر", "حاسوب")
    private val pointsPatterns = listOf("points", "نقاط", "reward", "مكافآت", "مكافأة", "eco points", "rewards")
    private val pickupPatterns = listOf("pickup", "pick up", "collect", "استلام", "توصيل", "ييجي", "يجي")
    private val locationPatterns = listOf("location", "center", "where", "مكان", "فين", "وين", "مركز", "أقرب", "near")
    private val dataPatterns = listOf("data", "privacy", "safe", "بيانات", "خصوصية", "أمان", "آمن", "secure")
    private val helpPatterns = listOf("help", "مساعدة", "ساعدني", "عايز اعرف", "ازاي", "إزاي", "how")
    private val thanksPatterns = listOf("thanks", "thank", "شكرا", "شكراً", "متشكر", "تسلم")
    private val byePatterns = listOf("bye", "مع السلامة", "سلام", "باي", "goodbye")
    private val devicePatterns = listOf("device", "جهاز", "أجهزة", "اجهزة", "what can", "ايه اللي", "إيه اللي", "devices")
    private val tabletPatterns = listOf("tablet", "ipad", "tab", "تابلت", "آيباد")
    private val cameraPatterns = listOf("camera", "كاميرا", "تصوير")

    fun getResponse(userMessage: String, context: Context): BotResponse {
        val msg = userMessage.lowercase().trim()
        
        return when {
            greetingPatterns.any { msg.contains(it) } -> BotResponse(
                context.getString(R.string.bot_hello_detailed),
                listOf(
                    context.getString(R.string.quick_reply_device_price),
                    context.getString(R.string.quick_reply_how_to_recycle),
                    context.getString(R.string.quick_reply_nearest_center),
                    context.getString(R.string.quick_reply_points_system)
                )
            )
            
            // Price + Phone
            (pricePatterns.any { msg.contains(it) } && phonePatterns.any { msg.contains(it) }) ||
            phonePatterns.any { msg.contains(it) } -> {
                when {
                    msg.contains("iphone 15") || msg.contains("آيفون 15") -> BotResponse(
                        context.getString(R.string.iphone_15_price),
                        listOf(
                            context.getString(R.string.quick_reply_samsung_price),
                            context.getString(R.string.quick_reply_laptop_price),
                            context.getString(R.string.quick_reply_nearest_center)
                        )
                    )
                    msg.contains("iphone 14") || msg.contains("آيفون 14") -> BotResponse(
                        context.getString(R.string.iphone_14_price),
                        listOf(
                            context.getString(R.string.quick_reply_iphone_15_price),
                            context.getString(R.string.quick_reply_points_system),
                            context.getString(R.string.quick_reply_nearest_center)
                        )
                    )
                    msg.contains("iphone 13") || msg.contains("آيفون 13") -> BotResponse(
                        context.getString(R.string.iphone_13_price),
                        listOf(
                            context.getString(R.string.quick_reply_iphone_14_price),
                            context.getString(R.string.quick_reply_how_to_recycle),
                            context.getString(R.string.quick_reply_points_system)
                        )
                    )
                    msg.contains("samsung") || msg.contains("سامسونج") -> {
                        when {
                            msg.contains("s24") -> BotResponse(
                                context.getString(R.string.samsung_s24_price),
                                listOf(
                                    context.getString(R.string.quick_reply_iphone_price),
                                    context.getString(R.string.quick_reply_laptop_price),
                                    context.getString(R.string.quick_reply_nearest_center)
                                )
                            )
                            msg.contains("s23") -> BotResponse(
                                context.getString(R.string.samsung_s23_price),
                                listOf(
                                    context.getString(R.string.quick_reply_s24_price),
                                    context.getString(R.string.quick_reply_points_system),
                                    context.getString(R.string.quick_reply_nearest_center)
                                )
                            )
                            else -> BotResponse(
                                context.getString(R.string.samsung_general_price),
                                listOf(
                                    context.getString(R.string.quick_reply_s24_price),
                                    context.getString(R.string.quick_reply_s23_price),
                                    context.getString(R.string.quick_reply_iphone_price)
                                )
                            )
                        }
                    }
                    else -> BotResponse(
                        context.getString(R.string.mobile_general_price),
                        listOf(
                            context.getString(R.string.quick_reply_iphone_15_price),
                            context.getString(R.string.quick_reply_s24_price),
                            context.getString(R.string.quick_reply_laptop_price)
                        )
                    )
                }
            }
            
            // Laptop prices
            laptopPatterns.any { msg.contains(it) } -> BotResponse(
                context.getString(R.string.laptop_general_price),
                listOf(
                    context.getString(R.string.quick_reply_mobile_price),
                    context.getString(R.string.quick_reply_nearest_center),
                    context.getString(R.string.quick_reply_points_system)
                )
            )

            // Tablet prices
            tabletPatterns.any { msg.contains(it) } -> BotResponse(
                context.getString(R.string.tablet_general_price),
                listOf(
                    context.getString(R.string.quick_reply_mobile_price),
                    context.getString(R.string.quick_reply_laptop_price),
                    context.getString(R.string.quick_reply_points_system)
                )
            )

            // Camera prices
            cameraPatterns.any { msg.contains(it) } -> BotResponse(
                context.getString(R.string.camera_general_price),
                listOf(
                    context.getString(R.string.quick_reply_mobile_price),
                    context.getString(R.string.quick_reply_how_to_recycle),
                    context.getString(R.string.quick_reply_nearest_center)
                )
            )

            // General price inquiry
            pricePatterns.any { msg.contains(it) } -> BotResponse(
                context.getString(R.string.general_price_inquiry),
                listOf(
                    context.getString(R.string.quick_reply_mobile_price),
                    context.getString(R.string.quick_reply_laptop_price),
                    context.getString(R.string.quick_reply_tablet_price)
                )
            )
            
            // Recycling info
            recyclePatterns.any { msg.contains(it) } -> BotResponse(
                context.getString(R.string.recycling_info),
                listOf(
                    context.getString(R.string.quick_reply_device_price),
                    context.getString(R.string.quick_reply_nearest_center),
                    context.getString(R.string.quick_reply_points_system)
                )
            )
            
            // Points/Rewards
            pointsPatterns.any { msg.contains(it) } -> BotResponse(
                context.getString(R.string.points_rewards_info),
                listOf(
                    context.getString(R.string.quick_reply_device_price),
                    context.getString(R.string.quick_reply_how_to_recycle),
                    context.getString(R.string.quick_reply_nearest_center)
                )
            )
            
            // Pickup
            pickupPatterns.any { msg.contains(it) } -> BotResponse(
                context.getString(R.string.pickup_info),
                listOf(
                    context.getString(R.string.quick_reply_nearest_center),
                    context.getString(R.string.quick_reply_device_price),
                    context.getString(R.string.quick_reply_points_system)
                )
            )
            
            // Location
            locationPatterns.any { msg.contains(it) } -> BotResponse(
                context.getString(R.string.location_info),
                listOf(
                    context.getString(R.string.quick_reply_pickup_service),
                    context.getString(R.string.quick_reply_device_price),
                    context.getString(R.string.quick_reply_how_to_recycle)
                )
            )
            
            // Data/Privacy
            dataPatterns.any { msg.contains(it) } -> BotResponse(
                context.getString(R.string.data_privacy_info),
                listOf(
                    context.getString(R.string.quick_reply_how_to_recycle),
                    context.getString(R.string.quick_reply_nearest_center),
                    context.getString(R.string.quick_reply_device_price)
                )
            )

            // What devices
            devicePatterns.any { msg.contains(it) } -> BotResponse(
                context.getString(R.string.accepted_devices_info),
                listOf(
                    context.getString(R.string.quick_reply_device_price),
                    context.getString(R.string.quick_reply_how_to_recycle),
                    context.getString(R.string.quick_reply_nearest_center)
                )
            )
            
            // Help
            helpPatterns.any { msg.contains(it) } -> BotResponse(
                context.getString(R.string.help_info),
                listOf(
                    context.getString(R.string.quick_reply_device_price),
                    context.getString(R.string.quick_reply_how_to_recycle),
                    context.getString(R.string.quick_reply_nearest_center),
                    context.getString(R.string.quick_reply_points_system)
                )
            )
            
            // Thanks
            thanksPatterns.any { msg.contains(it) } -> BotResponse(
                context.getString(R.string.thanks_info),
                listOf(
                    context.getString(R.string.quick_reply_device_price),
                    context.getString(R.string.quick_reply_how_to_recycle),
                    context.getString(R.string.quick_reply_bye)
                )
            )
            
            // Bye
            byePatterns.any { msg.contains(it) } -> BotResponse(
                context.getString(R.string.bye_info),
                emptyList()
            )
            
            // Default
            else -> BotResponse(
                context.getString(R.string.default_info),
                listOf(
                    context.getString(R.string.quick_reply_device_price),
                    context.getString(R.string.quick_reply_how_to_recycle),
                    context.getString(R.string.quick_reply_nearest_center),
                    context.getString(R.string.quick_reply_points_system)
                )
            )
        }
    }
}

@Composable
fun ChatBotScreen(navController: NavController) {
    val context = LocalContext.current
    val currentLang = LocalAppLanguage.current.lowercase()
    val locale = Locale(currentLang)
    val configuration = Configuration(context.resources.configuration).apply {
        setLocale(locale)
    }
    val localizedContext = context.createConfigurationContext(configuration)
    
    var messageText by remember { mutableStateOf("") }
    var isTyping by remember { mutableStateOf(false) }
    var quickReplies by remember { 
        mutableStateOf(
            listOf(
                localizedContext.getString(R.string.quick_reply_device_price),
                localizedContext.getString(R.string.quick_reply_how_to_recycle),
                localizedContext.getString(R.string.quick_reply_nearest_center),
                localizedContext.getString(R.string.quick_reply_points_system)
            )
        ) 
    }
    val messages = remember {
        mutableStateListOf(
            ChatMessage(localizedContext.getString(R.string.chatbot_initial_msg), false)
        )
    }

    // Stores the real conversation turns for Gemini multi-turn context (excludes initial greeting)
    val conversationHistory = remember { mutableStateListOf<ConversationTurn>() }
    
    // Update locale on language change
    LaunchedEffect(currentLang) {
        if (messages.size == 1 && !messages[0].isUser) {
            messages[0] = ChatMessage(localizedContext.getString(R.string.chatbot_initial_msg), false)
            quickReplies = listOf(
                localizedContext.getString(R.string.quick_reply_device_price),
                localizedContext.getString(R.string.quick_reply_how_to_recycle),
                localizedContext.getString(R.string.quick_reply_nearest_center),
                localizedContext.getString(R.string.quick_reply_points_system)
            )
        }
    }

    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            scrollState.animateScrollToItem(messages.size - 1)
        }
    }

    fun sendMessage(text: String) {
        if (text.isBlank()) return
        messages.add(ChatMessage(text, true))
        isTyping = true
        quickReplies = emptyList()

        // Add a placeholder bubble while the bot is thinking
        val placeholderTimestamp = System.currentTimeMillis()
        messages.add(ChatMessage("", false, placeholderTimestamp))

        coroutineScope.launch {
            val lang = if (currentLang == "ar") "ar" else "en"

            // Try Gemini first with full conversation history
            val geminiReply = GeminiClient.getChatBotResponse(
                newMessage = text,
                history = conversationHistory.toList(),
                language = lang
            )

            val botReply: String
            val newQuickReplies: List<String>

            if (!geminiReply.isNullOrBlank() && !geminiReply.startsWith("عذراً، لا يوجد") && !geminiReply.startsWith("Sorry, no internet")) {
                // ✅ Gemini succeeded — use its response
                botReply = geminiReply
                newQuickReplies = emptyList() // Gemini handles its own follow-ups naturally

                // Save turns to history
                conversationHistory.add(ConversationTurn(role = "user", text = text))
                conversationHistory.add(ConversationTurn(role = "model", text = botReply))
            } else {
                // ⚡ Offline fallback — use local EcoBotEngine
                delay(600L + (text.length * 15L).coerceAtMost(900L))
                val localResponse = EcoBotEngine.getResponse(text, localizedContext)
                botReply = localResponse.text
                newQuickReplies = localResponse.quickReplies
            }

            // Replace placeholder with real message
            messages[messages.lastIndex] = ChatMessage(botReply, false, placeholderTimestamp)
            quickReplies = newQuickReplies
            isTyping = false
        }
    }

    Scaffold(
        topBar = {
            ScreenHeader(
                title = localizedContext.getString(R.string.chatbot_title),
                onBack = { navController.popBackStack() }
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            LazyColumn(
                state = scrollState,
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                items(messages) { message ->
                    AnimatedContent(
                        targetState = message.text,
                        transitionSpec = { fadeIn() togetherWith fadeOut() },
                        label = "MessageAnim"
                    ) { text ->
                        if (text.isEmpty() && !message.isUser) {
                            TypingIndicator()
                        } else {
                            ChatBubble(message.copy(text = text))
                        }
                    }
                }
            }

            // Quick Reply Chips
            AnimatedVisibility(
                visible = quickReplies.isNotEmpty() && !isTyping,
                enter = fadeIn() + slideInVertically { it },
                exit = fadeOut() + slideOutVertically { it }
            ) {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(quickReplies) { reply ->
                        SuggestionChip(
                            onClick = {
                                messageText = ""
                                sendMessage(reply)
                            },
                            label = { Text(reply, fontSize = 13.sp) },
                            shape = RoundedCornerShape(20.dp),
                            colors = SuggestionChipDefaults.suggestionChipColors(
                                containerColor = PrimaryGreen.copy(alpha = 0.1f),
                                labelColor = PrimaryDarkGreen
                            ),
                            border = SuggestionChipDefaults.suggestionChipBorder(
                                enabled = true,
                                borderColor = PrimaryGreen.copy(alpha = 0.3f)
                            )
                        )
                    }
                }
            }

            // Message Input Area
            Surface(
                tonalElevation = 8.dp,
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextField(
                        value = messageText,
                        onValueChange = { messageText = it },
                        placeholder = { Text(localizedContext.getString(R.string.chatbot_input_placeholder)) },
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(24.dp),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                        ),
                        maxLines = 4,
                        enabled = !isTyping
                    )
                    
                    Spacer(modifier = Modifier.width(8.dp))
                    
                    IconButton(
                        onClick = {
                            if (messageText.isNotBlank() && !isTyping) {
                                val userMsg = messageText
                                messageText = ""
                                sendMessage(userMsg)
                            }
                        },
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = if (isTyping) PrimaryGreen.copy(alpha = 0.5f) else PrimaryGreen,
                            contentColor = Color.White
                        ),
                        modifier = Modifier.size(48.dp),
                        enabled = !isTyping
                    ) {
                        Icon(Icons.AutoMirrored.Rounded.Send, contentDescription = localizedContext.getString(R.string.chatbot_send_button))
                    }
                }
            }
        }
    }
}

@Composable
fun TypingIndicator() {
    val infiniteTransition = rememberInfiniteTransition()
    
    val offsets = List(3) { index ->
        infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = -6f,
            animationSpec = infiniteRepeatable(
                animation = tween(300, delayMillis = index * 150, easing = EaseInOutSine),
                repeatMode = RepeatMode.Reverse
            ),
            label = "dotBounce"
        )
    }
    
    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.CenterStart) {
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            shape = RoundedCornerShape(16.dp, 16.dp, 16.dp, 2.dp),
            tonalElevation = 2.dp,
            shadowElevation = 1.dp,
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(3) { index ->
                    Box(
                        modifier = Modifier
                            .offset(y = offsets[index].value.dp)
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(message: ChatMessage) {
    val alignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart
    val color = if (message.isUser) PrimaryGreen else MaterialTheme.colorScheme.surfaceVariant
    val contentColor = if (message.isUser) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
    val shape = if (message.isUser) {
        RoundedCornerShape(16.dp, 16.dp, 2.dp, 16.dp)
    } else {
        RoundedCornerShape(16.dp, 16.dp, 16.dp, 2.dp)
    }

    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = alignment) {
        Surface(
            color = color,
            contentColor = contentColor,
            shape = shape,
            tonalElevation = 2.dp,
            shadowElevation = 1.dp,
            modifier = Modifier.widthIn(max = 300.dp)
        ) {
            Text(
                text = message.text,
                modifier = Modifier.padding(12.dp),
                fontSize = 15.sp,
                lineHeight = 22.sp
            )
        }
    }
}


