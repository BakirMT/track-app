package com.example

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import com.example.data.FinanceDatabase
import com.example.data.FinanceRepository
import com.example.ui.FinanceViewModel
import com.example.ui.FinanceViewModelFactory
import com.example.ui.TrackerApp
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.ThemeOption

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = FinanceDatabase.getDatabase(this)
        val repository = FinanceRepository(database.financeDao())
        val viewModelFactory = FinanceViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, viewModelFactory)[FinanceViewModel::class.java]

        val prefs = getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val savedThemeStr = prefs.getString("theme", ThemeOption.DARK.name) ?: ThemeOption.DARK.name
        val savedTheme = try { ThemeOption.valueOf(savedThemeStr) } catch (e: Exception) { ThemeOption.DARK }

        val savedUser = prefs.getString("current_user", null)
        if (savedUser != null) {
            viewModel.login(savedUser)
        }

        setContent {
            var currentTheme by remember { mutableStateOf(savedTheme) }

            MyApplicationTheme(themeOption = currentTheme) {
                TrackerApp(
                    viewModel = viewModel,
                    currentTheme = currentTheme,
                    onThemeChange = { newTheme ->
                        currentTheme = newTheme
                        prefs.edit().putString("theme", newTheme.name).apply()
                    },
                    onLogout = {
                        viewModel.logout()
                        prefs.edit().remove("current_user").apply()
                    },
                    onLogin = { email ->
                        prefs.edit().putString("current_user", email).apply()
                    }
                )
            }
        }
    }
}
