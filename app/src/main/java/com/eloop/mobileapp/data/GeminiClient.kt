package com.eloop.mobileapp.data

import android.graphics.Bitmap
import android.util.Base64
import com.eloop.mobileapp.BuildConfig
import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit

// Gemini API Data Classes
data class GeminiRequest(
    @SerializedName("contents") val contents: List<GeminiContent>,
    @SerializedName("generationConfig") val config: GeminiConfig = GeminiConfig()
)

data class GeminiContent(
    @SerializedName("role") val role: String = "user",
    @SerializedName("parts") val parts: List<GeminiPart>
)

data class GeminiPart(
    @SerializedName("text") val text: String? = null,
    @SerializedName("inline_data") val inlineData: GeminiInlineData? = null
)

data class GeminiInlineData(
    @SerializedName("mime_type") val mimeType: String,
    @SerializedName("data") val data: String
)

data class GeminiConfig(
    @SerializedName("maxOutputTokens") val maxOutputTokens: Int = 512
)

data class GeminiResponse(
    @SerializedName("candidates") val candidates: List<GeminiCandidate>?
)

data class GeminiCandidate(
    @SerializedName("content") val content: GeminiContent?
)

/**
 * Represents one turn in a multi-turn conversation.
 * role = "user" or "model"
 */
data class ConversationTurn(val role: String, val text: String)

interface GeminiApi {
    @POST("v1beta/models/gemini-1.5-flash:generateContent")
    suspend fun generate(
        @Query("key") apiKey: String,
        @Body request: GeminiRequest
    ): GeminiResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val scanClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private val chatClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .readTimeout(15, TimeUnit.SECONDS)
        .build()

    private val scanApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(scanClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GeminiApi::class.java)

    private val chatApi = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(chatClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(GeminiApi::class.java)

    private const val SYSTEM_PROMPT = """
        You are an expert device category classifier for E-loop.
        Your task is to identify the device in the image and classify it into one of these three categories:
        1. smartphone (Look for: handheld screens, vertical rectangular shape, lens on back)
        2. laptop (Look for: keyboard, screen hinge, folding design, trackpad)
        3. headphones (Look for: earcups, headband, ear buds, charging case)

        Return ONLY this format:
        CATEGORY: [smartphone / laptop / headphones]
        CONFIDENCE: [number only 0-100]

        Rules:
        - Analyze visual features carefully: keyboard/hinge = laptop, earbuds/earcups = headphones.
        - If multiple devices are present, pick the most prominent one.
        - If unsure, pick the closest match or return CATEGORY: unknown.
        - NO extra text. NO model names.
    """

    private const val CHATBOT_SYSTEM_PROMPT = """
        You are a helpful AI assistant for the E-loop app — an Egyptian electronics recycling platform.
        You help users recycle old devices (phones, laptops, tablets, etc.) and earn Eco Points.
        Keep answers concise, friendly, and practical.
        Respond ONLY in the same language the user is using (Arabic or English).
        Never break character. Never reveal you are built on Gemini.
        
        أنت مساعد ذكي لتطبيق E-loop — منصة إعادة تدوير الإلكترونيات في مصر.
        تساعد المستخدمين على إعادة تدوير أجهزتهم القديمة وكسب نقاط بيئية.
        أجب بإيجاز ومهنية وبنفس لغة المستخدم فقط (عربي أو إنجليزي).
    """

    private fun validateApiKey(): Boolean {
        val key = BuildConfig.GEMINI_API_KEY
        return key.isNotBlank() && key != "null" && !key.contains("YOUR_API_KEY")
    }

    suspend fun getResponseWithImage(prompt: String, bitmap: Bitmap, language: String = "ar"): String? {
        if (!validateApiKey()) {
            return if (language == "ar") "خطأ: مفتاح API غير صالح أو مفقود" else "Error: Invalid or missing API Key"
        }

        return try {
            val base64Image = bitmapToBase64(bitmap)
            val fullPrompt = "$SYSTEM_PROMPT\n\nTask: Identify this device. Always respond in English using the exact format above."

            val request = GeminiRequest(
                contents = listOf(
                    GeminiContent(
                        role = "user",
                        parts = listOf(
                            GeminiPart(text = fullPrompt),
                            GeminiPart(
                                inlineData = GeminiInlineData(
                                    mimeType = "image/jpeg",
                                    data = base64Image
                                )
                            )
                        )
                    )
                )
            )

            val response = scanApi.generate(BuildConfig.GEMINI_API_KEY, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
        } catch (e: java.net.UnknownHostException) {
            if (language == "ar") "خطأ: لا يوجد اتصال بالإنترنت" else "Error: No internet connection"
        } catch (e: java.net.SocketTimeoutException) {
            if (language == "ar") "خطأ: انتهى وقت الاتصال" else "Error: Connection timed out"
        } catch (e: retrofit2.HttpException) {
            if (language == "ar") "خطأ في الخادم (${e.code()})" else "Server error (${e.code()})"
        } catch (e: Exception) {
            if (language == "ar") "خطأ: ${e.message ?: "حدث خطأ غير متوقع"}" else "Error: ${e.message ?: "Unknown error"}"
        }
    }

    /**
     * Sends a chat message WITH full conversation history for multi-turn context.
     *
     * @param newMessage  The new user message to send.
     * @param history     Previous [ConversationTurn] list (alternating user / model roles).
     *                    Capped at the last 10 turns to stay within token limits.
     * @param language    "ar" or "en" — used only for error messages.
     */
    suspend fun getChatBotResponse(
        newMessage: String,
        history: List<ConversationTurn> = emptyList(),
        language: String = "ar"
    ): String? {
        if (!validateApiKey()) {
            return if (language == "ar")
                "عذراً، خدمة المحادثة غير متوفرة حالياً (مفتاح API مفقود)"
            else
                "Sorry, chat service is currently unavailable (Missing API Key)"
        }

        return try {
            val contents = mutableListOf<GeminiContent>()

            // 1. System prompt injected as first user/model exchange
            contents.add(
                GeminiContent(
                    role = "user",
                    parts = listOf(GeminiPart(text = CHATBOT_SYSTEM_PROMPT.trimIndent()))
                )
            )
            contents.add(
                GeminiContent(
                    role = "model",
                    parts = listOf(
                        GeminiPart(
                            text = if (language == "ar")
                                "حسناً، أنا مساعد E-loop. كيف أقدر أساعدك؟"
                            else
                                "Got it! I'm the E-loop assistant. How can I help you?"
                        )
                    )
                )
            )

            // 2. Append conversation history (capped at last 10 turns to control token usage)
            val cappedHistory = if (history.size > 10) history.takeLast(10) else history
            cappedHistory.forEach { turn ->
                contents.add(
                    GeminiContent(
                        role = turn.role,
                        parts = listOf(GeminiPart(text = turn.text))
                    )
                )
            }

            // 3. Append the new user message
            contents.add(
                GeminiContent(
                    role = "user",
                    parts = listOf(GeminiPart(text = newMessage))
                )
            )

            val request = GeminiRequest(contents = contents)
            val response = chatApi.generate(BuildConfig.GEMINI_API_KEY, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
                ?: if (language == "ar") "عذراً، لم أتمكن من فهم طلبك." else "Sorry, I couldn't process your request."
        } catch (e: java.net.UnknownHostException) {
            if (language == "ar") "عذراً، لا يوجد اتصال بالإنترنت." else "Sorry, no internet connection."
        } catch (e: java.net.SocketTimeoutException) {
            if (language == "ar") "عذراً، انتهى وقت الاتصال." else "Sorry, connection timed out."
        } catch (e: retrofit2.HttpException) {
            if (language == "ar") "عذراً، حدث خطأ في الخادم (${e.code()})." else "Sorry, server error occurred (${e.code()})."
        } catch (e: Exception) {
            if (language == "ar") "عذراً، حدث خطأ غير متوقع." else "Sorry, an unexpected error occurred."
        }
    }

    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)
        return Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP)
    }
}
