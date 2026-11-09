package com.example.weathertracker

import android.Manifest
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

class SettingsFragment : Fragment() {

    companion object {
        fun newInstance(): SettingsFragment = SettingsFragment()
    }

    private lateinit var spinnerLanguage: Spinner
    private lateinit var switchTheme: SwitchCompat
    private lateinit var tvThemeSubtitle: TextView
    private lateinit var rgTemperature: RadioGroup
    private lateinit var rbCelsius: RadioButton
    private lateinit var rbFahrenheit: RadioButton
    private lateinit var switchNotifications: SwitchCompat
    private lateinit var tvNotificationStatus: TextView
    private lateinit var btnTestNotification: Button
    private lateinit var btnSaveSettings: Button

    private val permissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            Toast.makeText(requireContext(), "Разрешение на показ уведомлений получено", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        setupLanguageSpinner()
        loadSavedPreferences()
        setupListeners()
    }

    private fun bindViews(view: View) {
        spinnerLanguage = view.findViewById(R.id.spinnerLanguage)
        switchTheme = view.findViewById(R.id.switchTheme)
        tvThemeSubtitle = view.findViewById(R.id.tvThemeSubtitle)
        rgTemperature = view.findViewById(R.id.rgTemperature)
        rbCelsius = view.findViewById(R.id.rbCelsius)
        rbFahrenheit = view.findViewById(R.id.rbFahrenheit)
        switchNotifications = view.findViewById(R.id.switchNotifications)
        tvNotificationStatus = view.findViewById(R.id.tvNotificationStatus)
        btnTestNotification = view.findViewById(R.id.btnTestNotification)
        btnSaveSettings = view.findViewById(R.id.btnSaveSettings)
    }

    private fun setupLanguageSpinner() {
        val languages = listOf("Русский", "English", "Deutsch")
        val langAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
        langAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerLanguage.adapter = langAdapter
    }

    private fun loadSavedPreferences() {
        switchTheme.isChecked = AppPreferences.isDarkTheme()
        updateThemeSubtitle(switchTheme.isChecked)
        switchNotifications.isChecked = AppPreferences.isNotificationsEnabled()
        updateNotificationSubtitle(switchNotifications.isChecked)
        spinnerLanguage.setSelection(AppPreferences.getLanguageIndex())
        if (AppPreferences.getTemperatureUnit() == "°F") {
            rbFahrenheit.isChecked = true
        } else {
            rbCelsius.isChecked = true
        }
    }

    private fun setupListeners() {
        switchTheme.setOnCheckedChangeListener { _, isChecked ->
            updateThemeSubtitle(isChecked)
        }
        switchNotifications.setOnCheckedChangeListener { _, isChecked ->
            updateNotificationSubtitle(isChecked)
            updatePeriodicWork(isChecked)
        }
        btnTestNotification.setOnClickListener {
            scheduleOneTimeNotification()
        }
        btnSaveSettings.setOnClickListener {
            saveCurrentPreferences()
        }
    }

    private fun scheduleOneTimeNotification() {
        checkNotificationPermission()
        val request = OneTimeWorkRequestBuilder<WeatherNotificationWorker>()
            .setInitialDelay(10, TimeUnit.SECONDS)
            .build()
        WorkManager.getInstance(requireContext()).enqueue(request)
        Toast.makeText(
            requireContext(),
            "Уведомление придет через 10 секунд. Закройте приложение для проверки!",
            Toast.LENGTH_LONG
        ).show()
    }

    private fun updatePeriodicWork(enabled: Boolean) {
        val manager = WorkManager.getInstance(requireContext())
        if (enabled) {
            checkNotificationPermission()
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()
            val periodicRequest = PeriodicWorkRequestBuilder<WeatherNotificationWorker>(2, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()
            manager.enqueueUniquePeriodicWork("weather_sync", ExistingPeriodicWorkPolicy.UPDATE, periodicRequest)
        } else {
            manager.cancelUniqueWork("weather_sync")
        }
    }

    private fun checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!NotificationHelper.hasNotificationPermission(requireContext())) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    private fun updateThemeSubtitle(isChecked: Boolean) {
        tvThemeSubtitle.text = if (isChecked) "Тёмная тема (активна)" else "Светлая тема (активна)"
    }

    private fun updateNotificationSubtitle(isChecked: Boolean) {
        tvNotificationStatus.text = if (isChecked) "Уведомления включены" else "Уведомления выключены"
    }

    private fun saveCurrentPreferences() {
        AppPreferences.saveDarkTheme(switchTheme.isChecked)
        AppPreferences.saveNotifications(switchNotifications.isChecked)
        AppPreferences.saveLanguageIndex(spinnerLanguage.selectedItemPosition)
        val tempUnit = if (rbFahrenheit.isChecked) "°F" else "°C"
        AppPreferences.saveTemperatureUnit(tempUnit)
        Toast.makeText(requireContext(), "Настройки сохранены", Toast.LENGTH_SHORT).show()
    }
}
