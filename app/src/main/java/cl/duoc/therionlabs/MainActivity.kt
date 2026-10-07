package cl.duoc.therionlabs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import cl.duoc.therionlabs.ui.screens.FormScreen
import cl.duoc.therionlabs.ui.theme.TherionLabsAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TherionLabsAppTheme {
                FormScreen()
            }
        }
    }
}