package com.lunnaris.clicky

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.lunnaris.clicky.ui.theme.ClickyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ClickyTheme {
                Scaffold { padding ->
                    Column(
                        modifier = Modifier.padding(padding)
                    ) {
                        App()
                    }

                }
            }
        }
    }
}

data class NavRoute(val label: String, val route: String, val icon: Int)

@Composable
fun App() {
    val navController = rememberNavController()
    Global.allowQR = true
    NavHost(navController, startDestination = "main") {
        composable("main") {
            UseNavBar(navController) {
                Home(navController)
            }
        }
        composable("settings") {
            UseNavBar(navController) {
                Settings(navController)
            }
        }
        composable("qrscanner") {
            QrScannerScreen(navController)
        }
        composable("input") {
            UseNavBar(navController) {
                KeyboardScreen()
            }
        }
    }
}

@Composable
fun UseNavBar(navController: NavController, content: @Composable () -> Unit) {
    val routes = listOf(
        NavRoute(stringResource(R.string.home), "main", R.drawable.home),
        NavRoute(stringResource(R.string.keyboard), "input", R.drawable.keyboard),
        NavRoute(stringResource(R.string.connect), "qrscanner", R.drawable.qrcode_scan),
        NavRoute(stringResource((R.string.settings)), "settings", R.drawable.tune),
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    Column {
        Row(
            modifier = Modifier.weight(1f)
        ) {
            content()
        }
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            routes.forEach {
                val selected = currentRoute == it.route
                val color = if (selected) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurface
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            navController.navigate(it.route)
                        }
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        verticalArrangement = Arrangement.spacedBy(5.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(it.icon),
                            contentDescription = it.label,
                            tint = color
                        )
                        Text(
                            it.label, color = color, fontWeight = if (selected) {
                                FontWeight.Bold
                            } else {
                                FontWeight.Normal
                            },
                            fontSize = 14.sp
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    App()
}