package com.example.weathertracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ReviewsActivity : AppCompatActivity() {

    private lateinit var rvReviews: RecyclerView
    private lateinit var tvEmptyReviews: TextView
    private lateinit var btnAddNewReview: Button
    private lateinit var btnSort: Button
    private lateinit var btnFilter: Button
    private lateinit var btnReset: Button
    private lateinit var tvFilterStatus: TextView
    private lateinit var tvPaginationInfo: TextView
    private lateinit var btnPrevPage: Button
    private lateinit var btnNextPage: Button

    private lateinit var adapter: ReviewCardAdapter
    private var allReviews = mutableListOf<Review>()
    private var displayedReviews = mutableListOf<Review>()

    private var filterMode: String = "Все"
    private var sortMode: Int = 0 // 0: Новые, 1: Старые, 2: По городу

    // ActivityResultLauncher для получения данных из AddReviewActivity (повышенный уровень Лаб 8)
    private val addReviewLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val title = data?.getStringExtra(AddReviewActivity.EXTRA_TITLE) ?: ""
            val city = data?.getStringExtra(AddReviewActivity.EXTRA_CITY) ?: ""
            Toast.makeText(
                this,
                "Получен результат: «$title» ($city)",
                Toast.LENGTH_SHORT
            ).show()
            refreshReviewsList()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews)

        rvReviews = findViewById(R.id.rvReviews)
        tvEmptyReviews = findViewById(R.id.tvEmptyReviews)
        btnAddNewReview = findViewById(R.id.btnAddNewReview)
        btnSort = findViewById(R.id.btnSort)
        btnFilter = findViewById(R.id.btnFilter)
        btnReset = findViewById(R.id.btnReset)
        tvFilterStatus = findViewById(R.id.tvFilterStatus)
        tvPaginationInfo = findViewById(R.id.tvPaginationInfo)
        btnPrevPage = findViewById(R.id.btnPrevPage)
        btnNextPage = findViewById(R.id.btnNextPage)

        adapter = ReviewCardAdapter(
            reviews = displayedReviews,
            onItemClick = { review ->
                // Переход к экрану детального просмотра отзыва с передачей данных через Intent (Шаг 3 Лаб 8)
                val intent = Intent(this, ViewReviewActivity::class.java).apply {
                    putExtra(ViewReviewActivity.EXTRA_REVIEW_ID, review.id)
                    putExtra(ViewReviewActivity.EXTRA_TITLE, review.title)
                    putExtra(ViewReviewActivity.EXTRA_CITY, review.city)
                    putExtra(ViewReviewActivity.EXTRA_DATE, review.date)
                    putExtra(ViewReviewActivity.EXTRA_STATUS, review.status)
                    putExtra(ViewReviewActivity.EXTRA_COMMENT, review.comment)
                }
                startActivity(intent)
            },
            onEditClick = { review ->
                // Переход в экран редактирования отзыва через ActivityResultLauncher
                val intent = Intent(this, AddReviewActivity::class.java).apply {
                    putExtra(AddReviewActivity.EXTRA_REVIEW_ID, review.id)
                    putExtra(AddReviewActivity.EXTRA_TITLE, review.title)
                    putExtra(AddReviewActivity.EXTRA_CITY, review.city)
                    putExtra(AddReviewActivity.EXTRA_DATE, review.date)
                    putExtra(AddReviewActivity.EXTRA_STATUS, review.status)
                    putExtra(AddReviewActivity.EXTRA_COMMENT, review.comment)
                }
                addReviewLauncher.launch(intent)
            },
            onDeleteClick = { review ->
                // Удаление отзыва с подтверждением
                AlertDialog.Builder(this)
                    .setTitle("Удаление отзыва")
                    .setMessage("Вы уверены, что хотите удалить отзыв по городу ${review.city}?")
                    .setPositiveButton("Удалить") { _, _ ->
                        ReviewRepository.removeReview(review)
                        refreshReviewsList()
                        Toast.makeText(this, "Отзыв удален", Toast.LENGTH_SHORT).show()
                    }
                    .setNegativeButton("Отмена", null)
                    .show()
            }
        )

        rvReviews.layoutManager = LinearLayoutManager(this)
        rvReviews.adapter = adapter

        // Кнопка добавления отзыва - запуск дочерней Activity через Launcher (Шаг 4 Лаб 8)
        btnAddNewReview.setOnClickListener {
            val intent = Intent(this, AddReviewActivity::class.java)
            addReviewLauncher.launch(intent)
        }

        btnSort.setOnClickListener {
            val sortOptions = arrayOf("Новые сначала", "Старые сначала", "По городу (А-Я)")
            AlertDialog.Builder(this)
                .setTitle("Сортировка отзывов")
                .setItems(sortOptions) { _, which ->
                    sortMode = which
                    applyFiltersAndSorting()
                }
                .show()
        }

        btnFilter.setOnClickListener {
            val filterOptions = arrayOf("Все", "Только «Совпало»", "Только «Не совпало»")
            AlertDialog.Builder(this)
                .setTitle("Фильтрация отзывов")
                .setItems(filterOptions) { _, which ->
                    filterMode = when (which) {
                        1 -> "Совпало"
                        2 -> "Не совпало"
                        else -> "Все"
                    }
                    applyFiltersAndSorting()
                }
                .show()
        }

        btnReset.setOnClickListener {
            filterMode = "Все"
            sortMode = 0
            applyFiltersAndSorting()
            Toast.makeText(this, "Фильтры сброшены", Toast.LENGTH_SHORT).show()
        }

        btnPrevPage.setOnClickListener {
            Toast.makeText(this, "Первая страница", Toast.LENGTH_SHORT).show()
        }

        btnNextPage.setOnClickListener {
            Toast.makeText(this, "Последняя страница", Toast.LENGTH_SHORT).show()
        }

        refreshReviewsList()
        NavigationHelper.setupBottomNav(this, NavigationHelper.TAB_REVIEWS)
    }

    override fun onResume() {
        super.onResume()
        refreshReviewsList()
    }

    private fun refreshReviewsList() {
        allReviews = ReviewRepository.getReviews()
        applyFiltersAndSorting()
    }

    private fun applyFiltersAndSorting() {
        var list = allReviews.toList()

        // Применяем фильтр
        if (filterMode != "Все") {
            list = list.filter { it.status == filterMode }
        }

        // Применяем сортировку
        list = when (sortMode) {
            1 -> list.sortedBy { it.date }
            2 -> list.sortedBy { it.city.lowercase() }
            else -> list.sortedByDescending { it.id }
        }

        displayedReviews.clear()
        displayedReviews.addAll(list)
        adapter.updateList(displayedReviews)

        if (displayedReviews.isEmpty()) {
            tvEmptyReviews.visibility = View.VISIBLE
            rvReviews.visibility = View.GONE
        } else {
            tvEmptyReviews.visibility = View.GONE
            rvReviews.visibility = View.VISIBLE
        }

        val sortLabel = when (sortMode) {
            1 -> "Старые сначала"
            2 -> "По городу (А-Я)"
            else -> "Новые сначала"
        }
        tvFilterStatus.text = "Фильтр: $filterMode | Сортировка: $sortLabel"
        tvPaginationInfo.text = "Стр. 1 из 1 (Всего: ${displayedReviews.size})"
    }
}
