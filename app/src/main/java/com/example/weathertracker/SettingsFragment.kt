package com.example.weathertracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.RadioGroup
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.SwitchCompat
import androidx.fragment.app.Fragment

class SettingsFragment : Fragment() {

    companion object {
        fun newInstance(): SettingsFragment = SettingsFragment()
    }

    private lateinit var spinnerLanguage: Spinner
    private lateinit var switchTheme: SwitchCompat
    private lateinit var tvThemeSubtitle: TextView
    private lateinit var rgTemperature: RadioGroup
    private lateinit var rgWind: RadioGroup
    private lateinit var rgPressure: RadioGroup
    private lateinit var switchNotifications: SwitchCompat
    private lateinit var tvNotificationStatus: TextView
    private lateinit var btnSaveSettings: Button

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
        setupThemeSwitch()
        setupNotificationsSwitch()
        setupSaveButton()
    }

    private fun bindViews(view: View) {
        spinnerLanguage = view.findViewById(R.id.spinnerLanguage)
        switchTheme = view.findViewById(R.id.switchTheme)
        tvThemeSubtitle = view.findViewById(R.id.tvThemeSubtitle)
        rgTemperature = view.findViewById(R.id.rgTemperature)
        rgWind = view.findViewById(R.id.rgWind)
        rgPressure = view.findViewById(R.id.rgPressure)
        switchNotifications = view.findViewById(R.id.switchNotifications)
        tvNotificationStatus = view.findViewById(R.id.tvNotificationStatus)
        btnSaveSettings = view.findViewById(R.id.btnSaveSettings)
    }

    private fun setupLanguageSpinner() {
        val languages = listOf("Русский", "English", "Deutsch")
        val langAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, languages)
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
            Toast.makeText(requireContext(), "Настройки сохранены", Toast.LENGTH_SHORT).show()
        }
    }
}