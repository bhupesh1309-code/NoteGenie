package com.example.notegenie

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.notegenie.navigation.NavGraph
import com.example.notegenie.ui.theme.NoteGenieTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NoteGenieTheme {
                NavGraph()
                }
            }
        }
    }


