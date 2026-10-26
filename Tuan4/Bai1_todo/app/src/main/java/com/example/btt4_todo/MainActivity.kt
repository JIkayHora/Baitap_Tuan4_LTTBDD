package com.example.btt4_todo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.btt4_todo.ui.theme.Btt4_TodoTheme


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Btt4_TodoTheme {
                Surface( modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background){
                    TodoApp()
                }
            }
        }
    }
}

data class StartScreen(
    val route: String,
    val nextRoute: String,
    val logoRes: Int,
    val appName: String,
    val slogan: String,
    val version: String
)

data class Category(
    val tagName: String,
    val textColor: Color,
    val bgColor: Color
)

data class Onboarding(
    val route: String,
    val nextRoute: String,
    val stepIndex: Int,
    val logoRes: Int,
    val appName: String,
    val skipText: String,
    val title: String,
    val description: String,
    val buttonText: String,
    val checklistStates: List<Boolean> = emptyList(),
    val categories: List<Category> = emptyList(),
    val reminderText: String = ""
)

class TodoAppRepository {
    val startData = StartScreen(
        route = "start",
        nextRoute = "step_1",
        logoRes = R.drawable.logo_app,
        appName = "UTH Todo",
        slogan = "Việc học, việc nhà - gọn gàng mỗi ngày",
        version = "Phiên bản 1.0"
    )

    val onboardingList = listOf(
        Onboarding(
            route = "step_1",
            nextRoute = "step_2",
            logoRes = R.drawable.logo_nho,
            stepIndex = 0,
            appName = "UTH Todo",
            skipText = "Bỏ qua",
            title = "Mọi việc, gọn trong một chỗ",
            description = "Ghi lại bài tập, việc nhà, lịch hẹn. Không còn quên việc nào.",
            buttonText = "Tiếp tục",
            checklistStates = listOf(true, false, false)
        ),
        Onboarding(
            route = "step_2",
            nextRoute = "step_3",
            logoRes = R.drawable.logo_nho,
            stepIndex = 1,
            appName = "UTH Todo",
            skipText = "Bỏ qua",
            title = "Chia theo Học tập và Cuộc sống",
            description = "Mỗi việc có nhóm riêng, nhìn là biết hôm nay cần làm gì.",
            buttonText = "Tiếp tục",
            categories = listOf(
                Category("Học tập", Color(0xFF254EDB), Color(0xFFDCE6FF)),
                Category("Cuộc sống", Color(0xFFD97706), Color(0xFFFEF3C7))
            )
        ),
        Onboarding(
            route = "step_3",
            nextRoute = "start",
            stepIndex = 2,
            logoRes = R.drawable.logo_nho,
            appName = "UTH Todo",
            skipText = "Bỏ qua",
            title = "Nhắc đúng lúc",
            description = "Bật thông báo để UTH Todo nhắc bạn trước hạn chót.",
            buttonText = "Bắt đầu ngay",
            reminderText = "Ôn bài Toán lúc 19:00"
        )
    )
}

val appData = TodoAppRepository()

@Composable
fun TodoApp() {
    val navControl = rememberNavController()
    val startScreen = appData.startData
    val pages = appData.onboardingList

    NavHost(navControl, startDestination = startScreen.route) {
        composable(startScreen.route) {
            StartScreen(
                data = startScreen,
                onNavigate = { route -> navControl.navigate(route) }
            )
        }
        composable(pages[0].route) {
            OnboardingScreen(
                data = pages[0],
                totalSteps = pages.size,
                onNavigate = { route -> navControl.navigate(route) }
            )
        }
        composable(pages[1].route) {
            OnboardingScreen(
                data = pages[1],
                totalSteps = pages.size,
                onNavigate = { route -> navControl.navigate(route) }
            )
        }
        composable(pages[2].route) {
            OnboardingScreen(
                data = pages[2],
                totalSteps = pages.size,
                onNavigate = { route ->
                    navControl.navigate(route) {
                        popUpTo(startScreen.route) { inclusive = true }
                    }
                }
            )
        }
    }
}

@Composable
fun StartScreen(
    data: StartScreen,
    onNavigate: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF254EDB))
            .clickable { onNavigate(data.nextRoute) }
            .systemBarsPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.weight(1f))
        Image(
            painter = painterResource(id = data.logoRes),
            contentDescription = "App Logo",
            modifier = Modifier
                .size(100.dp)
                .clip(RoundedCornerShape(22.dp))
        )
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = data.appName,
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = data.slogan,
            fontSize = 13.sp,
            color = Color(0xFFDCE6FF),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.weight(1f))
        Row(
            modifier = Modifier
                .width(64.dp)
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
        ) {
            Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color.White))
            Box(modifier = Modifier.weight(1f).fillMaxHeight().background(Color(0xFF5C7CFA)))
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = data.version,
            fontSize = 11.sp,
            color = Color(0xFFB8C9FF)
        )
    }
}

@Composable
fun OnboardingScreen(
    data: Onboarding,
    totalSteps: Int,
    onNavigate: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .systemBarsPadding()
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = data.logoRes),
                    contentDescription = "Small Logo",
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = data.appName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF254EDB)
                )
            }
            Text(
                text = data.skipText,
                fontSize = 13.sp,
                color = Color.Gray,
                modifier = Modifier.clickable { onNavigate(appData.startData.route) }
            )
        }
        Spacer(modifier = Modifier.height(36.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(230.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(Color(0xFFEEF2FF))
        ) {
            if (data.checklistStates.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp, Alignment.CenterVertically)
                ) {
                    for (isChecked in data.checklistStates) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color.White)
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isChecked) {
                                Box(
                                    modifier = Modifier
                                        .size(18.dp)
                                        .clip(CircleShape)
                                        .background(Color(0xFF254EDB)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Default.Check,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(12.dp))
                                }
                            } else {
                                Box(modifier = Modifier
                                    .size(18.dp)
                                    .border(1.5.dp,
                                    Color(0xFFBDBDBD), CircleShape))
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Box(
                                modifier = Modifier
                                    .width(if (isChecked) 90.dp else 75.dp)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(if (isChecked) Color(0xFF9FA8DA)
                                    else Color(0xFFE0E0E0))
                            )
                        }
                    }
                }
            } else if (data.categories.isNotEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize().padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
                ) {
                    for (cat in data.categories) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White)
                                .padding(14.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(cat.bgColor)
                                    .padding(horizontal = 10.dp, vertical = 4.dp)
                            ) {
                                Text(text = cat.tagName,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = cat.textColor)
                            }
                            Spacer(modifier = Modifier.height(10.dp))
                            Box(
                                modifier = Modifier
                                    .width(110.dp)
                                    .height(8.dp)
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(Color(0xFFE0E0E0))
                            )
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize().padding(20.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Notifications,
                            contentDescription = null, tint = Color(0xFF254EDB),
                            modifier = Modifier.size(30.dp))
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                            .padding(vertical = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = data.reminderText,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1F2937))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        Text(
            text = data.title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF111827),
            lineHeight = 28.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = data.description,
            fontSize = 13.sp,
            color = Color(0xFF6B7280),
            lineHeight = 20.sp
        )
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            for (index in 0 until totalSteps) {
                val isSelected = index == data.stepIndex
                Box(
                    modifier = Modifier
                        .width(if (isSelected) 18.dp else 6.dp)
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(if (isSelected) Color(0xFF254EDB)
                        else Color(0xFFD1D5DB))
                )
            }
        }

        Spacer(modifier = Modifier.weight(1f))
        Button(
            onClick = { onNavigate(data.nextRoute) },
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF254EDB))
        ) {
            Text(
                text = data.buttonText,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun ChecklistLogo(
    dotSize: androidx.compose.ui.unit.Dp,
    lineWidth: androidx.compose.ui.unit.Dp,
    color: Color = Color(0xFF254EDB)
) {
    Column(verticalArrangement = Arrangement.spacedBy(dotSize * 0.7f)) {
        repeat(2) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(modifier = Modifier.size(dotSize).border(1.5.dp, color, CircleShape))
                Spacer(modifier = Modifier.width(dotSize * 0.6f))
                Box(
                    modifier = Modifier
                        .width(lineWidth)
                        .height(dotSize * 0.45f)
                        .clip(RoundedCornerShape(2.dp))
                        .background(color)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    Btt4_TodoTheme {
        TodoApp()
    }
}