package com.example.btt4_apputh

import android.R.attr.singleLine
import android.R.attr.textStyle
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.btt4_apputh.ui.theme.Btt4_apputhTheme
import androidx.compose.foundation.Image
import androidx.compose.foundation.text.input.InputTransformation.Companion.keyboardOptions
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.CheckboxDefaults.colors
import androidx.compose.ui.focus.focusRequester

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Btt4_apputhTheme {
                Surface( modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background){
                    SmartApp()
                }
            }
        }
    }
}

@Composable
fun SmartApp() {
    val navController = rememberNavController()
    var email by remember { mutableStateOf("") }
    var otp by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isSubmitted by remember { mutableStateOf(false) }
    NavHost(navController = navController, startDestination = "forget_password") {
        composable("forget_password") {
            ForgetPasswordScreen(
                email = email,
                onEmailChange = { email = it },
                isSubmitted = isSubmitted,
                submittedEmail = email,
                submittedOtp = otp,
                submittedPass = password,
                onNext = {
                    isSubmitted = false
                    navController.navigate("verify_code")
                }
            )
        }
        composable("verify_code") {
            VerifyCodeScreen(
                onBack = { navController.popBackStack() },
                onNext = { enteredOtp ->
                    otp = enteredOtp
                    navController.navigate("reset_password")
                }
            )
        }
        composable("reset_password") {
            ResetPasswordScreen(
                onBack = { navController.popBackStack() },
                onNext = { enteredPass ->
                    password = enteredPass
                    navController.navigate("confirm")
                }
            )
        }
        composable("confirm") {
            ConfirmScreen(
                email = email,
                otp = otp,
                password = password,
                onBack = { navController.popBackStack() },
                onSubmit = {
                    isSubmitted = true
                    navController.navigate("forget_password") {
                        popUpTo("forget_password") { inclusive = true }
                    }
                }
            )
        }
    }
}
@Composable
fun ToppHeader(showBackButton: Boolean = false, onBack: () -> Unit = {}) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (showBackButton) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF3897F0))
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Back",
                        tint = Color.White
                    )
                }
            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Image( modifier = Modifier.size(150.dp),
                painter = painterResource(id = R.drawable.logo),
                contentDescription = null
        )
    }
}
@Composable
fun ForgetPasswordScreen(
    email: String,
    onEmailChange: (String) -> Unit,
    isSubmitted: Boolean,
    submittedEmail: String,
    submittedOtp: String,
    submittedPass: String,
    onNext: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        ToppHeader(showBackButton = false)
        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = "Forget Password?",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Enter your Email, we will send you a verification code.",
            fontSize = 13.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            placeholder = { Text("Your Email",
                color = Color.Gray,
                fontSize = 14.sp) },
            leadingIcon = {
                Icon(Icons.Default.Email,
                    contentDescription = null,
                    tint = Color.Gray)
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedBorderColor = Color(0xFF2196F3),
                unfocusedContainerColor = Color(0xFFFAFAFA),
                focusedContainerColor = Color(0xFFFAFAFA)
            )
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onNext,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
        ) {
            Text("Next", fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White)
        }
        if (isSubmitted) {
            Spacer(modifier = Modifier.height(32.dp))
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFDCEBFB))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Email: $submittedEmail",
                        color = Color(0xFFD32F2F),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "OTP: $submittedOtp",
                        color = Color(0xFFD32F2F),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Pass: $submittedPass",
                        color = Color(0xFFD32F2F),
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        }
    }
}
@Composable
fun VerifyCodeScreen(
    onBack: () -> Unit,
    onNext: (String) -> Unit
) {
    val otpValues = remember { mutableStateListOf("", "", "", "", "") }
    val focusRequesters = remember { List(5) { FocusRequester() } }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        ToppHeader(showBackButton = true, onBack = onBack)
        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = "Verify Code",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Enter the the code\nwe just sent you on your registered Email",
            fontSize = 13.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            for (i in 0 until 5) {
                OutlinedTextField(
                    value = otpValues[i],
                    onValueChange = { newValue ->
                        if (newValue.length <= 1 && newValue.all { it.isDigit() }) {
                            otpValues[i] = newValue
                            if (newValue.isNotEmpty() && i < 4) {
                                focusRequesters[i + 1].requestFocus()
                            }
                            else if (newValue.isEmpty() && i > 0) {
                                focusRequesters[i - 1].requestFocus()
                            }
                        }
                    },
                    singleLine = true,
                    textStyle = LocalTextStyle.current.copy(
                        textAlign = TextAlign.Center,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .width(54.dp)
                        .height(56.dp)
                        .focusRequester(focusRequesters[i]),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color(0xFFE0E0E0),
                        focusedBorderColor = Color(0xFF2196F3),
                        unfocusedContainerColor = Color(0xFFF5F5F5),
                        focusedContainerColor = Color(0xFFF5F5F5)
                    )
                )
            }
        }
        Spacer(modifier = Modifier.height(28.dp))
        Button(
            onClick = {
                val combinedOtp = otpValues.joinToString("")
                onNext(combinedOtp)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
        ) {
            Text("Next", fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White)
        }
    }
}
@Composable
fun ResetPasswordScreen(
    onBack: () -> Unit,
    onNext: (String) -> Unit
) {
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        ToppHeader(showBackButton = true, onBack = onBack)
        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = "Create new password",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Your new password must be different form\npreviously used password",
            fontSize = 13.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            placeholder = { Text("Password",
                color = Color.Gray,
                fontSize = 14.sp) },
            leadingIcon = {
                Icon(Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color.Gray)
            },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedBorderColor = Color(0xFF2196F3),
                unfocusedContainerColor = Color(0xFFFAFAFA),
                focusedContainerColor = Color(0xFFFAFAFA)
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            placeholder = { Text("Confirm Password",
                color = Color.Gray,
                fontSize = 14.sp) },
            leadingIcon = {
                Icon(Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color.Gray)
            },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE0E0E0),
                focusedBorderColor = Color(0xFF2196F3),
                unfocusedContainerColor = Color(0xFFFAFAFA),
                focusedContainerColor = Color(0xFFFAFAFA)
            )
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = { onNext(password) },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
        ) {
            Text("Next", fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White)
        }
    }
}
@Composable
fun ConfirmScreen(
    email: String,
    otp: String,
    password: String,
    onBack: () -> Unit,
    onSubmit: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(20.dp))
        ToppHeader(showBackButton = true, onBack = onBack)
        Spacer(modifier = Modifier.height(28.dp))
        Text(
            text = "Confirm",
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "We are here to help you!",
            fontSize = 13.sp,
            color = Color.Gray,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(24.dp))
        OutlinedTextField(
            value = email,
            onValueChange = {},
            readOnly = true,
            leadingIcon = {
                Icon(Icons.Default.Person,
                    contentDescription = null,
                    tint = Color.Gray)
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE0E0E0),
                unfocusedContainerColor = Color(0xFFFAFAFA)
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = otp,
            onValueChange = {},
            readOnly = true,
            leadingIcon = {
                Icon(Icons.Default.Email,
                    contentDescription = null,
                    tint = Color.Gray)
            },
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE0E0E0),
                unfocusedContainerColor = Color(0xFFFAFAFA)
            )
        )
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedTextField(
            value = password,
            onValueChange = {},
            readOnly = true,
            leadingIcon = {
                Icon(Icons.Default.Lock,
                    contentDescription = null,
                    tint = Color.Gray)
            },
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true,
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                unfocusedBorderColor = Color(0xFFE0E0E0),
                unfocusedContainerColor = Color(0xFFFAFAFA)
            )
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(24.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
        ) {
            Text("Summit", fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White)
        }
    }
}
@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Btt4_apputhTheme {
        SmartApp()
    }
}