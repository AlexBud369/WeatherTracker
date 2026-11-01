package com.example.weathertracker

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat

class SettingsActivity : AppCompatActivity() {

    private lateinit var spinnerLanguage: Spinner
    private lateinit var switchTheme: SwitchCompat
    private lateinit var tvThemeSubtitle: TextView
    private lateinit var rgTemperature: RadioGroup
    private lateinit var rgWind: RadioGroup
    private lateinit var rgPressure: RadioGroup
    private lateinit var switchNotifications: SwitchCompat
    private lateinit var tvNotificationStatus: TextView
    private lateinit var btnSaveSettings: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        bindViews()
        setupLanguageSpinner()
        setupThemeSwitch()
        setupNotificationsSwitch()
        setupSaveButton()
        NavigationHelper.setupBottomNav(this, NavigationHelper.TAB_SETTINGS)
    }

    private fun bindViews() {
        spinnerLanguage = findViewById(R.id.spinnerLanguage)
        switchTheme = findViewById(R.id.switchTheme)
        tvThemeSubtitle = findViewById(R.id.tvThemeSubtitle)
        rgTemperature = findViewById(R.id.rgTemperature)
        rgWind = findViewById(R.id.rgWind)
        rgPressure = findViewById(R.id.rgPressure)
        switchNotifications = findViewById(R.id.switchNotifications)
        tvNotificationStatus = findViewById(R.id.tvNotificationStatus)
        btnSaveSettings = findViewById(R.id.btnSaveSettings)
    }

    private fun setupLanguageSpinner() {
        val languages = listOf("Русский", "English", "Deutsch")
        val langAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, languages)
        langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLanguage.adapter = langAdapter
    }

    private fun setupThemeSwitch() {
        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            tvThemeSubtitle.text = if (isChecked) "Тёмная тема (активна)" else "Светлая тема (активна)"
        }
    }

    private fun setupNotificationsSwitch() {
        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            tvNotificationStatus.text = if (isChecked) "Уведомления включены" else "Уведомления выключены"
        }
    }

    private fun setupSaveButton() {
        btnSaveSettings.setOnClickListener {
            Toast.makeText(this, "Настройки сохранены", Toast.LENGTH_SHORT).show()
        }
    }
}