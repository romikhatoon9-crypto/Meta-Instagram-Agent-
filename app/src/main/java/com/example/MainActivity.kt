package com.example

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.ui.AppScreen
import com.example.ui.MainScreen
import com.example.ui.SecurityViewModel
import com.example.ui.theme.SufiSecurityTheme

class MainActivity : ComponentActivity() {

    private val viewModel: SecurityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        handleIncomingOAuthIntent(intent)

        setContent {
            SufiSecurityTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    MainScreen(viewModel = viewModel)
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingOAuthIntent(intent)
    }

    private fun handleIncomingOAuthIntent(intent: Intent?) {
        val data: Uri? = intent?.data
        if (data != null && data.scheme == "sufisecurity" && data.host == "oauth-callback") {
            val code = data.getQueryParameter("code")
            if (!code.isNullOrBlank()) {
                viewModel.connectClient()
                viewModel.navigateTo(AppScreen.DASHBOARD)
            }
        }
    }
}
