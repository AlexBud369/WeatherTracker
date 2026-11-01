package com.example.weathertracker

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
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
    private var sortMode: Int = 0

    private val addReviewLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            refreshReviewsList()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reviews)
        bindViews()
        setupRecyclerView()
        setupActionButtons()
        refreshReviewsList()
        NavigationHelper.setupBottomNav(this, NavigationHelper.TAB_REVIEWS)
    }

    private fun bindViews() {
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
    }

    private fun setupRecyclerView() {
        adapter = ReviewCardAdapter(
            reviews = displayedReviews,
            onItemClick = { review -> openViewReviewActivity(review) },
            onEditClick = { review -> openEditReviewActivity(review) },
            onDeleteClick = { review -> confirmDeleteReview(review) }
        )
        rvReviews.layoutManager = LinearLayoutManager(this)
        rvReviews.adapter = adapter
    }

    private fun openViewReviewActivity(review: Review) {
        val intent = Intent(this, ViewReviewActivity::class.java).apply {
            putExtra(ViewReviewActivity.EXTRA_REVIEW_ID, review.id)
            putExtra(ViewReviewActivity.EXTRA_TITLE, review.title)
            putExtra(ViewReviewActivity.EXTRA_CITY, review.city)
            putExtra(ViewReviewActivity.EXTRA_DATE, review.date)
            putExtra(ViewReviewActivity.EXTRA_STATUS, review.status)
            putExtra(ViewReviewActivity.EXTRA_COMMENT, review.comment)
        }
        startActivity(intent)
    }

    private fun openEditReviewActivity(review: Review) {
        val intent = Intent(this, AddReviewActivity::class.java).apply {
            putExtra(AddReviewActivity.EXTRA_REVIEW_ID, review.id)
            putExtra(AddReviewActivity.EXTRA_TITLE, review.title)
            putExtra(AddReviewActivity.EXTRA_CITY, review.city)
            putExtra(AddReviewActivity.EXTRA_DATE, review.date)
            putExtra(AddReviewActivity.EXTRA_STATUS, review.status)
            putExtra(AddReviewActivity.EXTRA_COMMENT, review.comment)
        }
        addReviewLauncher.launch(intent)
    }

    private fun confirmDeleteReview(review: Review) {
        AlertDialog.Builder(this)
            .setTitle("Удаление отзыва")
            .setMessage("Вы уверены, что хотите удалить отзыв по городу ${review.city}?")
            .setPositiveButton("Удалить") { _, _ ->
                ReviewRepository.removeReview(review)
                refreshReviewsList()
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun setupActionButtons() {
        btnAddNewReview.setOnClickListener {
            val intent = Intent(this, AddReviewActivity::class.java)
            addReviewLauncher.launch(intent)
        }
        btnSort.setOnClickListener { showSortDialog() }
        btnFilter.setOnClickListener { showFilterDialog() }
        btnReset.setOnClickListener { resetFilters() }
        btnPrevPage.setOnClickListener { }
        btnNextPage.setOnClickListener { }
    }

    private fun showSortDialog() {
        val sortOptions = arrayOf("Новые сначала", "Старые сначала", "По городу (А-Я)")
        AlertDialog.Builder(this)
            .setTitle("Сортировка отзывов")
            .setItems(sortOptions) { _, which ->
                sortMode = which
                applyFiltersAndSorting()
            }
            .show()
    }

    private fun showFilterDialog() {
        val filterOptions = arrayOf("Все", "Только «Совпало»", "Только «Не совпало»")
        AlertDialog.Builder(this)
            .setTitle("Фильтрация отзывов")
            .setItems(filterOptions) { _, which ->
                filterMode = if (which == 1) "Совпало" else if (which == 2) "Не совпало" else "Все"
                applyFiltersAndSorting()
            }
            .show()
    }

    private fun resetFilters() {
        filterMode = "Все"
        sortMode = 0
        applyFiltersAndSorting()
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
        val filtered = filterList(allReviews)
        val sorted = sortList(filtered)
        updateListUI(sorted)
    }

    private fun filterList(list: List<Review>): List<Review> {
        return if (filterMode == "Все") list else list.filter { it.status == filterMode }
    }

    private fun sortList(list: List<Review>): List<Review> {
        return when (sortMode) {
            1 -> list.sortedBy { it.date }
            2 -> list.sortedBy { it.city.lowercase() }
            else -> list.sortedByDescending { it.id }
        }
    }

    private fun updateListUI(newList: List<Review>) {
        displayedReviews.clear()
        displayedReviews.addAll(newList)
        adapter.updateList(displayedReviews)
        tvEmptyReviews.visibility = if (displayedReviews.isEmpty()) View.VISIBLE else View.GONE
        rvReviews.visibility = if (displayedReviews.isEmpty()) View.GONE else View.VISIBLE
        val sortLabel = if (sortMode == 1) "Старые сначала" else if (sortMode == 2) "По городу (А-Я)" else "Новые сначала"
        tvFilterStatus.text = "Фильтр: $filterMode | Сортировка: $sortLabel"
        tvPaginationInfo.text = "Стр. 1 из 1 (Всего: ${displayedReviews.size})"
    }
}