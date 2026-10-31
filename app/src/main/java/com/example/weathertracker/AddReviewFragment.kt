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
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
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
    private lateinit var btnBack: ImageButton
    private lateinit var tvAddReviewHeader: TextView

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
        btnBack = view.findViewById(R.id.btnBack)
        tvAddReviewHeader = view.findViewById(R.id.tvAddReviewHeader)

        // Подсказки для выпадающего списка городов
        val popularCities = arrayOf(
            "Москва", "Санкт-Петербург", "Новосибирск", "Екатеринбург",
            "Казань", "Нижний Новгород", "Челябинск", "Самара",
            "Уфа", "Ростов-на-Дону", "Краснодар", "Сочи", "Владивосток"
        )
        val cityAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, popularCities)
        actvCity.setAdapter(cityAdapter)

        // Чтение переданных аргументов (Шаг 5 Лаб 9)
        @Suppress("DEPRECATION")
        editingReview = arguments?.getSerializable(ARG_REVIEW) as? Review
        val initialCity = arguments?.getString(ARG_CITY)

        if (editingReview != null) {
            tvAddReviewHeader.text = "Редактировать отзыв"
            etReviewTitle.setText(editingReview!!.title)
            actvCity.setText(editingReview!!.city)
            etReviewComment.setText(editingReview!!.comment)
            if (editingReview!!.status == "Совпало") {
                rbMatched.isChecked = true
            } else {
                rbMismatched.isChecked = true
            }
            val parts = editingReview!!.date.split(" ")
            if (parts.isNotEmpty()) {
                selectedDate = parts[0]
                btnPickDate.text = "📅 $selectedDate"
            }
            if (parts.size > 1) {
                selectedTime = parts[1]
                btnPickTime.text = "⏰ $selectedTime"
            }
        } else {
            val cityToUse = if (!initialCity.isNullOrBlank()) initialCity else WeatherAppState.currentCity
            actvCity.setText(cityToUse)
        }

        // Выбор даты через DatePickerDialog
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

        // Выбор времени через TimePickerDialog
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

        // Кнопка Сохранить
        btnSave.setOnClickListener {
            val title = etReviewTitle.text.toString().trim()
            val city = actvCity.text.toString().trim()
            val comment = etReviewComment.text.toString().trim()
            val status = if (rbMatched.isChecked) "Совпало" else "Не совпало"

            if (title.isEmpty()) {
                etReviewTitle.error = "Введите заголовок отзыва"
                return@setOnClickListener
            }
            if (city.isEmpty()) {
                actvCity.error = "Укажите город"
                return@setOnClickListener
            }
            if (comment.isEmpty()) {
                etReviewComment.error = "Введите текст комментария"
                return@setOnClickListener
            }

            val fullDate = "$selectedDate $selectedTime"

            if (editingReview != null) {
                val updated = editingReview!!.copy(
                    title = title,
                    city = city,
                    date = fullDate,
                    status = status,
                    comment = comment
                )
                ReviewRepository.updateReview(updated)
            } else {
                val newReview = Review(
                    title = title,
                    city = city,
                    date = fullDate,
                    status = status,
                    comment = comment
                )
                ReviewRepository.addReview(newReview)
            }

            // Передача результата через Fragment Result API (Шаг 5 Лаб 9)
            val resultBundle = Bundle().apply {
                putString("title", title)
                putString("city", city)
                putString("status", status)
                putString("comment", comment)
            }
            parentFragmentManager.setFragmentResult("review_added", resultBundle)

            parentFragmentManager.popBackStack()
        }

        btnCancel.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}