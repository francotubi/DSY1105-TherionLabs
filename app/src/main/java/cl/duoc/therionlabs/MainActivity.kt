package cl.duoc.therionlabs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import cl.duoc.therionlabs.ui.screens.FormScreen
import cl.duoc.therionlabs.ui.screens.HistoryScreen
import cl.duoc.therionlabs.ui.theme.TherionLabsAppTheme

import cl.duoc.therionlabs.ui.screens.SelectorSedeScreen
import java.text.Normalizer

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TherionLabsAppTheme {
                AppNavigation()
            }
        }
    }
}

@Composable
fun AppNavigation(){
    val navController = rememberNavController()

    // Define cual pantalla abre primero (startDestination)
    NavHost(navController = navController, startDestination = "form_screen") {
        composable(route = "form_screen") {
            FormScreen(navController = navController)
        }
        composable(route = "selector_sede") {
            SelectorSedeScreen(navController = navController)
        }
        composable(route = "history_screen") {
            HistoryScreen(navController = navController)
        }
    }
}
