package me.hodders.hitt

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import me.hodders.hitt.ui.navigation.AppNavigation
import me.hodders.hitt.ui.theme.HiitTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HiitTheme {
                AppNavigation()
            }
        }
    }
}
