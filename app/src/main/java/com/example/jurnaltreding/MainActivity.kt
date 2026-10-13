package com.example.jurnaltreding

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.jurnaltreding.ui.navigation.JurnalApp
import com.example.jurnaltreding.ui.theme.JurnalTredingTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            JurnalTredingTheme {
                JurnalApp()
            }
        }
    }
}
