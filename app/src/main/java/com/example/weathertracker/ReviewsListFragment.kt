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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class ReviewsListFragment : Fragment() {

    companion object {
        fun newInstance(): ReviewsListFragment {
            return ReviewsListFragment()
        }
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
    private var sortMode: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_reviews_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        setupRecyclerView()
        setupResultListener()
        setupActionButtons()
        observeReviewsFromDatabase()
    }

    private fun bindViews(view: View) {
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
    }

    private fun setupRecyclerView() {
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
                confirmDeleteReview(review)
            }
        )
        rvReviews.layoutManager = LinearLayoutManager(requireContext())
        rvReviews.adapter = adapter
    }

    private fun observeReviewsFromDatabase() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                ReviewRepository.getReviewsFlow().collect { reviewsList ->
                    allReviews = reviewsList.toMutableList()
                    applyFilterAndSort()
                }
            }
        }
    }

    private fun confirmDeleteReview(review: Review) {
        AlertDialog.Builder(requireContext())
            .setTitle("Удаление отзыва")
            .setMessage("Вы уверены, что хотите удалить этот отзыв по городу ${review.city}?")
            .setPositiveButton("Удалить") { _, _ ->
                deleteReviewFromDatabase(review)
            }
            .setNegativeButton("Отмена", null)
            .show()
    }

    private fun deleteReviewFromDatabase(review: Review) {
        viewLifecycleOwner.lifecycleScope.launch {
            ReviewRepository.removeReview(review)
            Toast.makeText(requireContext(), "Отзыв удален из базы данных Room", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupResultListener() {
        parentFragmentManager.setFragmentResultListener("review_added", viewLifecycleOwner) { _, _ ->
            refreshReviewsList()
        }
    }

    private fun setupActionButtons() {
        btnAddNewReview.setOnClickListener {
            val addFragment = AddReviewFragment.newInstance()
            (activity as? MainActivity)?.openFragment(addFragment, addToBackStack = true)
        }
        btnSort.setOnClickListener { showSortDialog() }
        btnFilter.setOnClickListener { showFilterDialog() }
        btnReset.setOnClickListener { resetFilters() }
        btnPrevPage.setOnClickListener { }
        btnNextPage.setOnClickListener { }
    }

    private fun showSortDialog() {
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

    private fun showFilterDialog() {
        val filterOptions = arrayOf("Все отзывы", "Только «Совпало»", "Только «Не совпало»")
        val currentChecked = if (filterMode == "Совпало") 1 else if (filterMode == "Не совпало") 2 else 0
        AlertDialog.Builder(requireContext())
            .setTitle("Фильтрация по статусу")
            .setSingleChoiceItems(filterOptions, currentChecked) { dialog, which ->
                filterMode = if (which == 1) "Совпало" else if (which == 2) "Не совпало" else "Все"
                dialog.dismiss()
                applyFilterAndSort()
            }
            .show()
    }

    private fun resetFilters() {
        filterMode = "Все"
        sortMode = 0
        applyFilterAndSort()
    }

    override fun onResume() {
        super.onResume()
        refreshReviewsList()
    }

    private fun refreshReviewsList() {
        viewLifecycleOwner.lifecycleScope.launch {
            allReviews = ReviewRepository.getReviews().toMutableList()
            applyFilterAndSort()
        }
    }

    private fun applyFilterAndSort() {
        val filteredList = filterReviews(allReviews)
        val sortedList = sortReviews(filteredList)
        updateListUI(sortedList)
    }

    private fun filterReviews(list: List<Review>): List<Review> {
        return if (filterMode == "Все") {
            list
        } else {
            list.filter { it.status.equals(filterMode, ignoreCase = true) }
        }
    }

    private fun sortReviews(list: List<Review>): List<Review> {
        return when (sortMode) {
            1 -> list.reversed()
            2 -> list.sortedBy { it.city }
            else -> list
        }
    }

    private fun updateListUI(newList: List<Review>) {
        displayedReviews.clear()
        displayedReviews.addAll(newList)
        adapter.updateList(displayedReviews)
        tvEmptyReviews.visibility = if (displayedReviews.isEmpty()) View.VISIBLE else View.GONE
        rvReviews.visibility = if (displayedReviews.isEmpty()) View.GONE else View.VISIBLE
        val sortName = if (sortMode == 1) "Старые сначала" else if (sortMode == 2) "По городу (А-Я)" else "Новые сначала"
        tvFilterStatus.text = "Фильтр: $filterMode | Сортировка: $sortName"
        tvPaginationInfo.text = "Стр. 1 из 1 (Всего: ${displayedReviews.size})"
    }
}
