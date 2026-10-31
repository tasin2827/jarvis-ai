package com.jarvis.ai

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.jarvis.ai.ui.theme.JarvisAITheme
import com.jarvis.ai.ui.theme.JarvisCyanPrimary
import com.jarvis.ai.ui.theme.JarvisDarkNavy
import com.jarvis.ai.ui.theme.JarvisObsidian

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JarvisAITheme {
                MainAppScaffold()
            }
        }
    }
}

enum class JarvisScreen(val title: String) {
    DASHBOARD("Dashboard"),
    CHAT("AI Chat"),
    VOICE("Voice HUD"),
    DEVICE_CONTROL("Device Control"),
    CODING("Coding Assistant"),
    SETTINGS("Settings")
}

@Composable
fun MainAppScaffold() {
    var currentScreen by remember { mutableStateOf(JarvisScreen.DASHBOARD) }

    Scaffold(
        containerColor = JarvisObsidian,
        bottomBar = {
            NavigationBar(
                containerColor = JarvisDarkNavy,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = currentScreen == JarvisScreen.DASHBOARD,
                    onClick = { currentScreen = JarvisScreen.DASHBOARD },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("HUD") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = JarvisCyanPrimary,
                        selectedTextColor = JarvisCyanPrimary,
                        indicatorColor = JarvisDarkNavy
                    )
                )
                NavigationBarItem(
                    selected = currentScreen == JarvisScreen.CHAT,
                    onClick = { currentScreen = JarvisScreen.CHAT },
                    icon = { Icon(Icons.Default.Chat, contentDescription = "Chat") },
                    label = { Text("Chat") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = JarvisCyanPrimary,
                        selectedTextColor = JarvisCyanPrimary,
                        indicatorColor = JarvisDarkNavy
                    )
                )
                NavigationBarItem(
                    selected = currentScreen == JarvisScreen.VOICE,
                    onClick = { currentScreen = JarvisScreen.VOICE },
                    icon = { Icon(Icons.Default.Mic, contentDescription = "Voice") },
                    label = { Text("Voice") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = JarvisCyanPrimary,
                        selectedTextColor = JarvisCyanPrimary,
                        indicatorColor = JarvisDarkNavy
                    )
                )
                NavigationBarItem(
                    selected = currentScreen == JarvisScreen.DEVICE_CONTROL,
                    onClick = { currentScreen = JarvisScreen.DEVICE_CONTROL },
                    icon = { Icon(Icons.Default.TouchApp, contentDescription = "Device Control") },
                    label = { Text("Actions") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = JarvisCyanPrimary,
                        selectedTextColor = JarvisCyanPrimary,
                        indicatorColor = JarvisDarkNavy
                    )
                )
                NavigationBarItem(
                    selected = currentScreen == JarvisScreen.SETTINGS,
                    onClick = { currentScreen = JarvisScreen.SETTINGS },
                    icon = { Icon(Icons.Default.Settings, contentDescription = "Settings") },
                    label = { Text("Config") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = JarvisCyanPrimary,
                        selectedTextColor = JarvisCyanPrimary,
                        indicatorColor = JarvisDarkNavy
                    )
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(JarvisObsidian)
        ) {
            when (currentScreen) {
                JarvisScreen.DASHBOARD -> DashboardPlaceholder()
                JarvisScreen.CHAT -> ChatPlaceholder()
                JarvisScreen.VOICE -> VoicePlaceholder()
                JarvisScreen.DEVICE_CONTROL -> DeviceControlPlaceholder()
                JarvisScreen.CODING -> CodingPlaceholder()
                JarvisScreen.SETTINGS -> SettingsPlaceholder()
            }
        }
    }
}

@Composable
fun DashboardPlaceholder() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text("JARVIS AI OPERATING SYSTEM", color = JarvisCyanPrimary, style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Online • Neural Subsystems Synchronized", color = Color.Gray)
    }
}

@Composable
fun ChatPlaceholder() {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("JARVIS Multi-Turn Chat Ready", color = Color.White)
    }
}

@Composable
fun VoicePlaceholder() {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("JARVIS Holographic Voice Arc Ready", color = JarvisCyanPrimary)
    }
}

@Composable
fun DeviceControlPlaceholder() {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("JARVIS Accessibility Automation Ready", color = Color.White)
    }
}

@Composable
fun CodingPlaceholder() {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("JARVIS AI Code Assistant Ready", color = Color.White)
    }
}

@Composable
fun SettingsPlaceholder() {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("JARVIS System Configurations & Keys", color = Color.White)
    }
}
