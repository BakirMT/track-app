package com.example.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

val AuthGreen = Color(0xFF48B575)
val AuthBg = Color(0xFFF2F2F7)

@Composable
fun AuthScreen(viewModel: FinanceViewModel, onAuthSuccess: (String) -> Unit) {
    var isLogin by remember { mutableStateOf(true) }
    var isForgotPassword by remember { mutableStateOf(false) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = AuthBg
    ) {
        if (isForgotPassword) {
            ForgotPasswordScreen(
                viewModel = viewModel,
                onBack = { isForgotPassword = false }
            )
        } else if (isLogin) {
            LoginScreen(
                viewModel = viewModel,
                onAuthSuccess = onAuthSuccess,
                onGoToSignup = { isLogin = false },
                onForgotPassword = { isForgotPassword = true }
            )
        } else {
            SignupScreen(
                viewModel = viewModel,
                onAuthSuccess = onAuthSuccess,
                onGoToLogin = { isLogin = true }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = { Text(placeholder, color = Color.Gray, fontSize = 14.sp) },
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        singleLine = true,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            disabledContainerColor = Color.White,
            focusedBorderColor = AuthGreen,
            unfocusedBorderColor = Color.Transparent,
            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,
            cursorColor = AuthGreen
        )
    )
}

@Composable
fun AuthButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        shape = RoundedCornerShape(25.dp),
        colors = ButtonDefaults.buttonColors(containerColor = AuthGreen)
    ) {
        Text(text, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
    }
}

@Composable
fun LoginScreen(
    viewModel: FinanceViewModel,
    onAuthSuccess: (String) -> Unit,
    onGoToSignup: () -> Unit,
    onForgotPassword: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        IconButton(onClick = { /* no op for root */ }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            "Log in",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Enter your email and password to securely access your account and manage your services.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        AuthTextField(
            value = email,
            onValueChange = { email = it; error = "" },
            placeholder = "Email address",
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color.Gray) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        AuthTextField(
            value = password,
            onValueChange = { password = it; error = "" },
            placeholder = "Password",
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray) },
            trailingIcon = {
                val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(image, contentDescription = null, tint = Color.Gray)
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = rememberMe,
                    onCheckedChange = { rememberMe = it },
                    colors = CheckboxDefaults.colors(checkedColor = AuthGreen, checkmarkColor = Color.White)
                )
                Text("Remember me", fontSize = 12.sp, color = Color.Gray)
            }
            Text(
                "Forgot Password",
                fontSize = 12.sp,
                color = Color.Gray,
                modifier = Modifier.clickable { onForgotPassword() }
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (error.isNotEmpty()) {
            Text(error, color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.height(8.dp))
        }

        AuthButton("Login") {
            if (email.isNotBlank() && password.isNotBlank()) {
                viewModel.signin(email, password) { success, msg ->
                    if (success) onAuthSuccess(email) else error = msg
                }
            } else {
                error = "Please fill all fields"
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Don't have an account? ", fontSize = 14.sp, color = Color.Gray)
            Text(
                "Sign Up here",
                fontSize = 14.sp,
                color = AuthGreen,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onGoToSignup() }
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun SignupScreen(
    viewModel: FinanceViewModel,
    onAuthSuccess: (String) -> Unit,
    onGoToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        IconButton(onClick = onGoToLogin) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            "Create Account",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Create a new account to get started and enjoy seamless access to our features.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(24.dp))

        AuthTextField(
            value = name,
            onValueChange = { name = it; error = "" },
            placeholder = "Name",
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray) }
        )
        Spacer(modifier = Modifier.height(12.dp))

        AuthTextField(
            value = email,
            onValueChange = { email = it; error = "" },
            placeholder = "Email address",
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color.Gray) },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(12.dp))
        
        AuthTextField(
            value = password,
            onValueChange = { password = it; error = "" },
            placeholder = "Password",
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray) },
            trailingIcon = {
                val image = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(image, contentDescription = null, tint = Color.Gray)
                }
            },
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation()
        )
        Spacer(modifier = Modifier.height(12.dp))

        AuthTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it; error = "" },
            placeholder = "Confirm Password",
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null, tint = Color.Gray) },
            trailingIcon = {
                val image = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff
                IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Icon(image, contentDescription = null, tint = Color.Gray)
                }
            },
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation()
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        if (error.isNotEmpty()) {
            Text(error, color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
            Spacer(modifier = Modifier.height(8.dp))
        }

        AuthButton("Create Account") {
            if (email.isNotBlank() && password.isNotBlank() && confirmPassword.isNotBlank()) {
                if (password == confirmPassword) {
                    viewModel.signup(email, password) { success, msg ->
                        if (success) onAuthSuccess(email) else error = msg
                    }
                } else {
                    error = "Passwords do not match"
                }
            } else {
                error = "Please fill all fields"
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Row(
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ) {
            Text("Already have an account? ", fontSize = 14.sp, color = Color.Gray)
            Text(
                "Sign In here",
                fontSize = 14.sp,
                color = AuthGreen,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clickable { onGoToLogin() }
            )
        }
        
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
fun ForgotPasswordScreen(
    viewModel: FinanceViewModel,
    onBack: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var step by remember { mutableStateOf(1) }
    var error by remember { mutableStateOf("") }
    var message by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        IconButton(onClick = onBack) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.Black)
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            "Forgot Password",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Enter your email address to receive a reset link and regain access to your account.",
            fontSize = 14.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        )
        
        Spacer(modifier = Modifier.height(32.dp))

        if (step == 1) {
            AuthTextField(
                value = email,
                onValueChange = { email = it; error = ""; message = "" },
                placeholder = "Email address",
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null, tint = Color.Gray) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            if (error.isNotEmpty()) {
                Text(error, color = Color.Red, fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(modifier = Modifier.height(8.dp))
            }
            if (message.isNotEmpty()) {
                Text(message, color = AuthGreen, fontSize = 12.sp, modifier = Modifier.align(Alignment.CenterHorizontally))
                Spacer(modifier = Modifier.height(8.dp))
            }

            AuthButton("Send Reset Link") {
                if (email.isNotBlank()) {
                    viewModel.sendPasswordResetLink(email) { success, msg ->
                        if (success) {
                            message = msg
                            // Firebase sends a link, no OTP step needed anymore.
                            // We stay on this screen to show success message, or they can click Back.
                        } else {
                            error = msg
                        }
                    }
                } else {
                    error = "Please enter an email"
                }
            }
        }
    }
}
