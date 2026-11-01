package com.example.weathertracker

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment

class ViewReviewFragment : Fragment() {

    companion object {
        private const val ARG_TITLE = "arg_review_title"
        private const val ARG_CITY = "arg_review_city"
        private const val ARG_DATE = "arg_review_date"
        private const val ARG_STATUS = "arg_review_status"
        private const val ARG_COMMENT = "arg_review_comment"
        private const val ARG_REVIEW_ID = "arg_review_id"

        fun newInstance(review: Review): ViewReviewFragment {
            val fragment = ViewReviewFragment()
            val args = Bundle().apply {
                putString(ARG_TITLE, review.title)
                putString(ARG_CITY, review.city)
                putString(ARG_DATE, review.date)
                putString(ARG_STATUS, review.status)
                putString(ARG_COMMENT, review.comment)
                putLong(ARG_REVIEW_ID, review.id)
            }
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var tvReviewTitle: TextView
    private lateinit var tvReviewCity: TextView
    private lateinit var tvReviewDate: TextView
    private lateinit var tvReviewStatus: TextView
    private lateinit var tvReviewComment: TextView
    private lateinit var btnEditReview: Button
    private lateinit var btnBackToList: Button
    private lateinit var btnBack: ImageButton

    private var currentReview: Review? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_view_review, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        loadReviewFromArguments()
        displayReviewData()
        setupActionButtons()
    }

    private fun bindViews(view: View) {
        tvReviewTitle = view.findViewById(R.id.tvReviewTitle)
        tvReviewCity = view.findViewById(R.id.tvReviewCity)
        tvReviewDate = view.findViewById(R.id.tvReviewDate)
        tvReviewStatus = view.findViewById(R.id.tvReviewStatus)
        tvReviewComment = view.findViewById(R.id.tvReviewComment)
        btnEditReview = view.findViewById(R.id.btnEditReview)
        btnBackToList = view.findViewById(R.id.btnBackToList)
        btnBack = view.findViewById(R.id.btnBack)
    }

    private fun loadReviewFromArguments() {
        val title = arguments?.getString(ARG_TITLE) ?: "Отзыв о погоде"
        val city = arguments?.getString(ARG_CITY) ?: "Город не указан"
        val date = arguments?.getString(ARG_DATE) ?: "Дата не указана"
        val status = arguments?.getString(ARG_STATUS) ?: "Совпало"
        val comment = arguments?.getString(ARG_COMMENT) ?: "Комментарий отсутствует"
        val reviewId = arguments?.getLong(ARG_REVIEW_ID, 0L) ?: 0L
        currentReview = Review(
            id = reviewId,
            title = title,
            city = city,
            date = date,
            status = status,
            comment = comment
        )
    }

    private fun displayReviewData() {
        val review = currentReview ?: return
        tvReviewTitle.text = review.title
        tvReviewCity.text = review.city
        tvReviewDate.text = review.date
        tvReviewStatus.text = review.status
        tvReviewComment.text = review.comment
        applyStatusBadge(review.status)
    }

    private fun applyStatusBadge(status: String) {
        if (status == "Совпало") {
            tvReviewStatus.setBackgroundResource(R.drawable.bg_badge_matched)
            tvReviewStatus.setTextColor(Color.parseColor("#2E7D32"))
        } else {
            tvReviewStatus.setBackgroundResource(R.drawable.bg_badge_mismatched)
            tvReviewStatus.setTextColor(Color.parseColor("#D32F2F"))
        }
    }

    private fun setupActionButtons() {
        btnEditReview.setOnClickListener {
            currentReview?.let { rev ->
                val editFragment = AddReviewFragment.newInstance(review = rev)
                (activity as? MainActivity)?.openFragment(editFragment, addToBackStack = true)
            }
        }
        btnBackToList.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }
}