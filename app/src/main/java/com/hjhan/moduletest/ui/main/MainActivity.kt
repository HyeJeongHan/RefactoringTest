package com.hjhan.moduletest.ui.main

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import com.hjhan.moduletest.data.local.preferences.AppPreferences
import com.hjhan.moduletest.navigation.AppNavGraph
import com.hjhan.moduletest.ui.theme.ModuleTestTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject lateinit var appPreferences: AppPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContent {
            ModuleTestTheme {
                AppNavGraph(startLoggedIn = appPreferences.isLoggedIn())
            }
        }
    }
}
