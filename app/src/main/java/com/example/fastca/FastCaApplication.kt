package com.example.fastca // Или ваш корневой пакет

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class FastCaApplication : Application() {
    // Можно оставить пустым для начала
}