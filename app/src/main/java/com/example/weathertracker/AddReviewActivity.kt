package com.example.weathertracker

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import java.util.Calendar
import java.util.Locale

class AddReviewActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TITLE = "extra_review_title"
        const val EXTRA_CITY = "extra_review_city"
        const val EXTRA_DATE = "extra_review_date"
        const val EXTRA_STATUS = "extra_review_status"
        const val EXTRA_COMMENT = "extra_review_comment"
        const val EXTRA_REVIEW_ID = "extra_review_id"
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

    private var selectedDate: String = "30.10.2026"
    private var selectedTime: String = "10:45"
    private var editReviewId: Long = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_review)
        bindViews()
        setupCityAutocomplete()
        loadIntentData()
        setupDateTimePickers()
        setupButtons()
        NavigationHelper.setupBottomNav(this, NavigationHelper.TAB_REVIEWS)
    }

    private fun bindViews() {
        etReviewTitle = findViewById(R.id.etReviewTitle)
        actvCity = findViewById(R.id.actvCity)
        btnPickDate = findViewById(R.id.btnPickDate)
        btnPickTime = findViewById(R.id.btnPickTime)
        rgStatus = findViewById(R.id.rgStatus)
        rbMatched = findViewById(R.id.rbMatched)
        rbMismatched = findViewById(R.id.rbMismatched)
        etReviewComment = findViewById(R.id.etReviewComment)
        btnSave = findViewById(R.id.btnSave)
        btnCancel = findViewById(R.id.btnCancel)
        btnBack = findViewById(R.id.btnBack)
    }

    private fun setupCityAutocomplete() {
        val cities = listOf(
            "Москва", "Санкт-Петербург", "Новосибирск", "Екатеринбург",
            "Казань", "Нижний Новгород", "Челябинск", "Самара",
            "Омск", "Ростов-на-Дону", "Уфа", "Красноярск",
            "Воронеж", "Пермь", "Волгоград", "Сочи", "Краснодар"
        )
        val adapter = ArrayAdapter(this, android.R.layout.simple_dropdown_item_1line, cities)
        actvCity.setAdapter(adapter)
    }

    private fun loadIntentData() {
        intent.getStringExtra(EXTRA_CITY)?.takeIf { it.isNotBlank() }?.let { actvCity.setText(it) }
        intent.getStringExtra(EXTRA_TITLE)?.takeIf { it.isNotBlank() }?.let { etReviewTitle.setText(it) }
        intent.getStringExtra(EXTRA_COMMENT)?.takeIf { it.isNotBlank() }?.let { etReviewComment.setText(it) }
        parseDateTime(intent.getStringExtra(EXTRA_DATE))
        if (intent.getStringExtra(EXTRA_STATUS) == "Не совпало") {
            rbMismatched.isChecked = true
        } else {
            rbMatched.isChecked = true
        }
        editReviewId = intent.getLongExtra(EXTRA_REVIEW_ID, 0L)
    }

    private fun parseDateTime(dateStr: String?) {
        if (!dateStr.isNullOrBlank()) {
            val parts = dateStr.split(" ")
            if (parts.isNotEmpty()) {
                selectedDate = parts[0]
                btnPickDate.text = selectedDate
            }
            if (parts.size > 1) {
                selectedTime = parts[1]
                btnPickTime.text = selectedTime
            }
        }
    }

    private fun setupDateTimePickers() {
        btnPickDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, dayOfMonth ->
                selectedDate = String.format(Locale.getDefault(), "%02d.%02d.%04d", dayOfMonth, month + 1, year)
                btnPickDate.text = selectedDate
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }
        btnPickTime.setOnClickListener {
            val calendar = Calendar.getInstance()
            TimePickerDialog(this, { _, hourOfDay, minute ->
                selectedTime = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minute)
                btnPickTime.text = selectedTime
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }
    }

    private fun setupButtons() {
        btnSave.setOnClickListener { onSaveClicked() }
        btnCancel.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
        btnBack.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }
    }

    private fun onSaveClicked() {
        val title = etReviewTitle.text.toString().trim()
        val city = actvCity.text.toString().trim()
        val comment = etReviewComment.text.toString().trim()
        val status = if (rbMatched.isChecked) "Совпало" else "Не совпало"
        val fullDateTime = "$selectedDate $selectedTime"

        if (!validateInputs(title, city, comment)) return

        val reviewId = saveReview(title, city, fullDateTime, status, comment)
        returnResult(reviewId, title, city, fullDateTime, status, comment)
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

    private fun saveReview(title: String, city: String, date: String, status: String, comment: String): Long {
        val reviewId = if (editReviewId != 0L) editReviewId else System.currentTimeMillis()
        val review = Review(id = reviewId, title = title, city = city, date = date, status = status, comment = comment)
        if (editReviewId != 0L) {
            ReviewRepository.updateReview(review)
        } else {
            ReviewRepository.addReview(review)
        }
        return reviewId
    }

    private fun returnResult(reviewId: Long, title: String, city: String, date: String, status: String, comment: String) {
        val resultIntent = Intent().apply {
            putExtra(EXTRA_REVIEW_ID, reviewId)
            putExtra(EXTRA_TITLE, title)
            putExtra(EXTRA_CITY, city)
            putExtra(EXTRA_DATE, date)
            putExtra(EXTRA_STATUS, status)
            putExtra(EXTRA_COMMENT, comment)
        }
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }
}