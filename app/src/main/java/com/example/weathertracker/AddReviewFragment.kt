package com.example.weathertracker

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

class AddReviewFragment : Fragment() {

    companion object {
        private const val ARG_CITY = "arg_city"
        private const val ARG_REVIEW = "arg_review"

        fun newInstance(city: String? = WeatherAppState.currentCity, review: Review? = null): AddReviewFragment {
            val fragment = AddReviewFragment()
            val args = Bundle().apply {
                city?.let { putString(ARG_CITY, it) }
                review?.let { putSerializable(ARG_REVIEW, it) }
            }
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var etReviewTitle: EditText
    private lateinit var actvCity: AutoCompleteTextView
    private lateinit var btnPickDate: Button
    private lateinit var btnPickTime: Button
    private lateinit var rgStatus: RadioGroup
    private lateinit var rbMatched: RadioButton
    private lateinit var rbMismatched: RadioButton
    private lateinit var etReviewComment: EditText
    private lateinit var btnSave: Button
    private lateinit var btnCancel: Button

    private var selectedDate: String = "30.10.2026"
    private var selectedTime: String = "10:45"
    private var editingReview: Review? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        setupCityAutocomplete()
        loadInitialData()
        setupDateTimePickers()
        setupButtons()
    }

    private fun bindViews(view: View) {
        etReviewTitle = view.findViewById(R.id.etReviewTitle)
        actvCity = view.findViewById(R.id.actvCity)
        btnPickDate = view.findViewById(R.id.btnPickDate)
        btnPickTime = view.findViewById(R.id.btnPickTime)
        rgStatus = view.findViewById(R.id.rgStatus)
        rbMatched = view.findViewById(R.id.rbMatched)
        rbMismatched = view.findViewById(R.id.rbMismatched)
        etReviewComment = view.findViewById(R.id.etReviewComment)
        btnSave = view.findViewById(R.id.btnSave)
        btnCancel = view.findViewById(R.id.btnCancel)
    }

    private fun setupCityAutocomplete() {
        val popularCities = arrayOf(
            "Москва", "Санкт-Петербург", "Новосибирск", "Екатеринбург",
            "Казань", "Нижний Новгород", "Челябинск", "Самара",
            "Уфа", "Ростов-на-Дону", "Краснодар", "Сочи", "Владивосток"
        )
        val cityAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, popularCities)
        actvCity.setAdapter(cityAdapter)
    }

    private fun loadInitialData() {
        @Suppress("DEPRECATION")
        editingReview = arguments?.getSerializable(ARG_REVIEW) as? Review
        val initialCity = arguments?.getString(ARG_CITY)
        if (editingReview != null) {
            populateEditingReview(editingReview!!)
        } else {
            val cityToUse = if (!initialCity.isNullOrBlank()) initialCity else WeatherAppState.currentCity
            actvCity.setText(cityToUse)
            (activity as? MainActivity)?.updateToolbar("Оставить отзыв", true)
        }
    }

    private fun populateEditingReview(review: Review) {
        (activity as? MainActivity)?.updateToolbar("Редактировать отзыв", true)
        etReviewTitle.setText(review.title)
        actvCity.setText(review.city)
        etReviewComment.setText(review.comment)
        if (review.status == "Совпало") rbMatched.isChecked = true else rbMismatched.isChecked = true
        val parts = review.date.split(" ")
        if (parts.isNotEmpty()) {
            selectedDate = parts[0]
            btnPickDate.text = "📅 $selectedDate"
        }
        if (parts.size > 1) {
            selectedTime = parts[1]
            btnPickTime.text = "⏰ $selectedTime"
        }
    }

    private fun setupDateTimePickers() {
        setupDatePicker()
        setupTimePicker()
    }

    private fun setupDatePicker() {
        btnPickDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    selectedDate = String.format(Locale.getDefault(), "%02d.%02d.%04d", dayOfMonth, month + 1, year)
                    btnPickDate.text = "📅 $selectedDate"
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupTimePicker() {
        btnPickTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(
                requireContext(),
                { _, hourOfDay, minute ->
                    selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                    btnPickTime.text = "⏰ $selectedTime"
                },
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }
    }

    private fun setupButtons() {
        btnSave.setOnClickListener { onSaveClicked() }
        btnCancel.setOnClickListener { parentFragmentManager.popBackStack() }
    }

    private fun onSaveClicked() {
        val title = etReviewTitle.text.toString().trim()
        val city = actvCity.text.toString().trim()
        val comment = etReviewComment.text.toString().trim()
        val status = if (rbMatched.isChecked) "Совпало" else "Не совпало"

        if (!validateInputs(title, city, comment)) return

        val fullDate = "$selectedDate $selectedTime"
        saveReview(title, city, fullDate, status, comment)
    }

    private fun validateInputs(title: String, city: String, comment: String): Boolean {
        if (title.isEmpty()) {
            etReviewTitle.error = "Введите заголовок отзыва"
            return false
        }
        if (city.isEmpty()) {
            actvCity.error = "Укажите город"
            return false
        }
        if (comment.isEmpty()) {
            etReviewComment.error = "Введите текст комментария"
            return false
        }
        return true
    }

    private fun saveReview(title: String, city: String, fullDate: String, status: String, comment: String) {
        viewLifecycleOwner.lifecycleScope.launch {
            if (editingReview != null) {
                updateExistingReview(title, city, fullDate, status, comment)
            } else {
                createNewReview(title, city, fullDate, status, comment)
            }
            sendFragmentResult(title, city, status, comment)
            parentFragmentManager.popBackStack()
        }
    }

    private suspend fun updateExistingReview(title: String, city: String, fullDate: String, status: String, comment: String) {
        val updated = editingReview!!.copy(title, city, fullDate, status, comment)
        ReviewRepository.updateReview(updated)
    }

    private suspend fun createNewReview(title: String, city: String, fullDate: String, status: String, comment: String) {
        val newReview = Review(0L, city, fullDate, status, comment, title)
        ReviewRepository.addReview(newReview)
    }

    private fun sendFragmentResult(title: String, city: String, status: String, comment: String) {
        val resultBundle = Bundle().apply {
            putString("title", title)
            putString("city", city)
            putString("status", status)
            putString("comment", comment)
        }
        parentFragmentManager.setFragmentResult("review_added", resultBundle)
    }
}