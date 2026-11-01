package com.rupeeflow.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.rupeeflow.app.ui.navigation.RupeeFlowNavigation
import com.rupeeflow.app.ui.theme.RupeeFlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RupeeFlowTheme {
                RupeeFlowNavigation()
            }
        }
    }
}
