package com.dastageer.rento

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import com.dastageer.rento.presentation.shared.theme.LocalRentoColors
import com.dastageer.rento.presentation.shared.theme.RentoTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            RentoTheme {
                val colors = LocalRentoColors.current
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = colors.bg0,
                ) {
                    // RentoNavGraph() — placeholder, replaced in Module 02
                }
            }
        }
    }
}
