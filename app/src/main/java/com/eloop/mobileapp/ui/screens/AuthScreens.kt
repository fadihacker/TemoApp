package com.eloop.mobileapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.*
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.eloop.mobileapp.data.FirebaseRepository
import com.eloop.mobileapp.data.SessionManager
import com.eloop.mobileapp.ui.components.*
import com.eloop.mobileapp.ui.theme.*
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import com.eloop.mobileapp.LocalAppLanguage
import kotlinx.coroutines.tasks.await

@Composable
fun LoginScreen(navController: NavController) {
    val language = LocalAppLanguage.current
    val isAr = language == "AR"
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val sessionManager = remember { SessionManager(context) }
    
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }

    fun validate(): Boolean {
        var isValid = true
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = if (isAr) "عنوان بريد إلكتروني غير صالح" else "Invalid email address"
            isValid = false
        } else {
            emailError = null
        }
        
        if (password.length < 8) {
            passwordError = if (isAr) "٨ أحرف على الأقل" else "Minimum 8 characters"
            isValid = false
        } else {
            passwordError = null
        }
        return isValid
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 24.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Sync,
                contentDescription = null,
                modifier = Modifier.size(64.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = if (isAr) "مرحباً بعودتك!" else "Welcome Back!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isAr) "سجل الدخول لمتابعة رحلتك البيئية" else "Sign in to continue your eco-journey",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isAr) "تسجيل الدخول" else "Sign In",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.align(Alignment.Start)
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it; emailError = null },
                        label = { Text(if (isAr) "البريد الإلكتروني" else "Email") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        isError = emailError != null,
                        supportingText = emailError?.let { { Text(it) } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it; passwordError = null },
                        label = { Text(if (isAr) "كلمة المرور" else "Password") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        isError = passwordError != null,
                        supportingText = passwordError?.let { { Text(it) } },
                        visualTransformation = if (isPasswordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                                Icon(
                                    imageVector = if (isPasswordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility,
                                    contentDescription = null
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true
                    )

                    TextButton(
                        onClick = { /* Navigate to forgot password */ },
                        modifier = Modifier.align(Alignment.Start)
                    ) {
                        Text(if (isAr) "هل نسيت كلمة المرور؟" else "Forgot Password?", color = MaterialTheme.colorScheme.primary)
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    LoadingButton(
                        text = if (isAr) "دخول" else "Login",
                        isLoading = isLoading,
                        onClick = {
                            if (validate()) {
                                scope.launch {
                                    isLoading = true
                                    try {
                                        auth.signInWithEmailAndPassword(email, password).await()
                                        
                                        // Update SessionManager
                                        sessionManager.setLoggedIn(
                                            true, 
                                            email, 
                                            auth.currentUser?.displayName ?: ""
                                        )

                                        navController.navigate("home") {
                                            popUpTo("login") { inclusive = true }
                                        }
                                    } catch (e: Exception) {
                                        snackbarHostState.showSnackbar(e.message ?: "Login failed")
                                    } finally {
                                        isLoading = false
                                    }
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(if (isAr) "ليس لديك حساب؟ " else "Don't have an account? ", style = MaterialTheme.typography.bodyMedium)
                        TextButton(onClick = { navController.navigate("signup") }) {
                            Text(if (isAr) "إنشاء حساب" else "Sign up", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
fun SignupScreen(navController: NavController) {
    val language = LocalAppLanguage.current
    val isAr = language == "AR"
    val context = LocalContext.current
    val auth = remember { FirebaseAuth.getInstance() }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val sessionManager = remember { SessionManager(context) }
    
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var isTermsAccepted by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmError by remember { mutableStateOf<String?>(null) }

    fun validate(): Boolean {
        var isValid = true
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailError = if (isAr) "بريد إلكتروني غير صالح" else "Invalid email"
            isValid = false
        } else { emailError = null }
        
        if (password.length < 8) {
            passwordError = if (isAr) "٨ أحرف على الأقل" else "Minimum 8 characters"
            isValid = false
        } else { passwordError = null }
        
        if (password != confirmPassword) {
            confirmError = if (isAr) "كلمات المرور غير متطابقة" else "Passwords do not match"
            isValid = false
        } else { confirmError = null }
        
        return isValid
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            @OptIn(ExperimentalMaterial3Api::class)
            CenterAlignedTopAppBar(
                title = { Text(if (isAr) "إنشاء حساب" else "Create Account", style = MaterialTheme.typography.titleMedium) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = null)
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = fullName,
                onValueChange = { fullName = it },
                label = { Text(if (isAr) "الاسم الكامل" else "Full Name") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text(if (isAr) "رقم الهاتف (اختياري)" else "Phone Number (Optional)") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone)
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it; emailError = null },
                label = { Text(if (isAr) "البريد الإلكتروني" else "Email") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                isError = emailError != null,
                supportingText = emailError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it; passwordError = null },
                label = { Text(if (isAr) "كلمة المرور" else "Password") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                isError = passwordError != null,
                supportingText = passwordError?.let { { Text(it) } },
                visualTransformation = if (isPasswordVisible) androidx.compose.ui.text.input.VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Icon(imageVector = if (isPasswordVisible) Icons.Rounded.VisibilityOff else Icons.Rounded.Visibility, contentDescription = null)
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it; confirmError = null },
                label = { Text(if (isAr) "تأكيد كلمة المرور" else "Confirm Password") },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                isError = confirmError != null,
                supportingText = confirmError?.let { { Text(it) } },
                visualTransformation = PasswordVisualTransformation()
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Checkbox(
                    checked = isTermsAccepted,
                    onCheckedChange = { isTermsAccepted = it }
                )
                Text(
                    text = if (isAr) "أوافق على الشروط والأحكام" else "I agree to the Terms & Conditions",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            LoadingButton(
                text = if (isAr) "إنشاء الحساب" else "Sign Up",
                isLoading = isLoading,
                enabled = isTermsAccepted,
                onClick = {
                    if (validate()) {
                        scope.launch {
                            isLoading = true
                            try {
                                auth.createUserWithEmailAndPassword(email, password).await()
                                FirebaseRepository.createUserProfile(fullName, email)
                                
                                // Update SessionManager
                                sessionManager.setLoggedIn(true, email, fullName)

                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            } catch (e: Exception) {
                                snackbarHostState.showSnackbar(e.message ?: "Signup failed")
                            } finally {
                                isLoading = false
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(if (isAr) "لديك حساب بالفعل؟ " else "Already have an account? ", style = MaterialTheme.typography.bodyMedium)
                TextButton(onClick = { navController.navigate("login") }) {
                    Text(if (isAr) "دخول" else "Login", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}


