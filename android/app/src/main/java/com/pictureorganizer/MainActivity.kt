package com.pictureorganizer

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.pictureorganizer.navigation.PictureOrganizerNavHost
import com.pictureorganizer.ui.theme.PictureOrganizerTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PictureOrganizerTheme {
                PictureOrganizerNavHost()
            }
        }
    }
}
