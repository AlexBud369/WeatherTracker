package com.example.weathertracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class ReviewsListFragment : Fragment() {

    companion object {
        fun newInstance(): ReviewsListFragment = ReviewsListFragment()
    }

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

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reviews_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        rvReviews = view.findViewById(R.id.rvReviews)
        tvEmptyReviews = view.findViewById(R.id.tvEmptyReviews)
        btnAddNewReview = view.findViewById(R.id.btnAddNewReview)
        btnSort = view.findViewById(R.id.btnSort)
        btnFilter = view.findViewById(R.id.btnFilter)
        btnReset = view.findViewById(R.id.btnReset)
        tvFilterStatus = view.findViewById(R.id.tvFilterStatus)
        tvPaginationInfo = view.findViewById(R.id.tvPaginationInfo)
        btnPrevPage = view.findViewById(R.id.btnPrevPage)
        btnNextPage = view.findViewById(R.id.btnNextPage)

        adapter = ReviewCardAdapter(
            reviews = displayedReviews,
            onItemClick = { review ->
                val viewFragment = ViewReviewFragment.newInstance(review)
                (activity as? MainActivity)?.openFragment(viewFragment, addToBackStack = true)
            },
            onEditClick = { review ->
                val editFragment = AddReviewFragment.newInstance(review = review)
                (activity as? MainActivity)?.openFragment(editFragment, addToBackStack = true)
            },
            onDeleteClick = { review ->
                AlertDialog.Builder(requireContext())
                    .setTitle("Удаление отзыва")
                    .setMessage("Вы уверены, что хотите удалить этот отзыв по городу ${review.city}?")
                    .setPositiveButton("Удалить") { _, _ ->
                        ReviewRepository.removeReview(review)
                        Toast.makeText(requireContext(), "Отзыв удален", Toast.LENGTH_SHORT).show()
                        refreshReviewsList()
                    }
                    .setNegativeButton("Отмена", null)
                    .show()
            }
        )

        rvReviews.layoutManager = LinearLayoutManager(requireContext())
        rvReviews.adapter = adapter

        // Слушатель результатов из экрана добавления/редактирования отзыва (Fragment Result API)
        parentFragmentManager.setFragmentResultListener(
            "review_added",
            viewLifecycleOwner
        ) { _, _ ->
            refreshReviewsList()
        }

        // Переход к экрану создания нового отзыва
        btnAddNewReview.setOnClickListener {
            val addFragment = AddReviewFragment.newInstance()
            (activity as? MainActivity)?.openFragment(addFragment, addToBackStack = true)
        }

        // Кнопка Сортировки
        btnSort.setOnClickListener {
            val sortOptions = arrayOf("Сначала новые", "Сначала старые", "По названию города (А-Я)")
            AlertDialog.Builder(requireContext())
                .setTitle("Сортировка отзывов")
                .setSingleChoiceItems(sortOptions, sortMode) { dialog, which ->
                    sortMode = which
                    dialog.dismiss()
                    applyFilterAndSort()
                }
                .show()
        }

        // Кнопка Фильтрации
        btnFilter.setOnClickListener {
            val filterOptions = arrayOf("Все отзывы", "Только «Совпало»", "Только «Не совпало»")
            val currentChecked = when (filterMode) {
                "Совпало" -> 1
                "Не совпало" -> 2
                else -> 0
            }
            AlertDialog.Builder(requireContext())
                .setTitle("Фильтрация по статусу")
                .setSingleChoiceItems(filterOptions, currentChecked) { dialog, which ->
                    filterMode = when (which) {
                        1 -> "Совпало"
                        2 -> "Не совпало"
                        else -> "Все"
                    }
                    dialog.dismiss()
                    applyFilterAndSort()
                }
                .show()
        }

        // Кнопка Сброса фильтров
        btnReset.setOnClickListener {
            filterMode = "Все"
            sortMode = 0
            applyFilterAndSort()
        }

        // Пагинация (на одной странице)
        btnPrevPage.setOnClickListener { }
        btnNextPage.setOnClickListener { }

        refreshReviewsList()
    }

    override fun onResume() {
        super.onResume()
        refreshReviewsList()
    }

    private fun refreshReviewsList() {
        allReviews = ReviewRepository.getReviews()
        applyFilterAndSort()
    }

    private fun applyFilterAndSort() {
        var resultList = allReviews.toList()

        // 1. Фильтрация
        if (filterMode != "Все") {
            resultList = resultList.filter { it.status.equals(filterMode, ignoreCase = true) }
        }

        // 2. Сортировка
        resultList = when (sortMode) {
            1 -> resultList.reversed() // Старые сначала
            2 -> resultList.sortedBy { it.city } // По городу
            else -> resultList // Новые сначала
        }

        displayedReviews.clear()
        displayedReviews.addAll(resultList)
        adapter.updateList(displayedReviews)

        if (displayedReviews.isEmpty()) {
            tvEmptyReviews.visibility = View.VISIBLE
            rvReviews.visibility = View.GONE
        } else {
            tvEmptyReviews.visibility = View.GONE
            rvReviews.visibility = View.VISIBLE
        }

        val sortName = when (sortMode) {
            1 -> "Старые сначала"
            2 -> "По городу (А-Я)"
            else -> "Новые сначала"
        }
        tvFilterStatus.text = "Фильтр: $filterMode | Сортировка: $sortName"
        tvPaginationInfo.text = "Стр. 1 из 1 (Всего: ${displayedReviews.size})"
    }
}