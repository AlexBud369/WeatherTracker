package com.example.weathertracker

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ViewReviewActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TITLE = "extra_review_title"
        const val EXTRA_CITY = "extra_review_city"
        const val EXTRA_DATE = "extra_review_date"
        const val EXTRA_STATUS = "extra_review_status"
        const val EXTRA_COMMENT = "extra_review_comment"
        const val EXTRA_REVIEW_ID = "extra_review_id"
    }

    private lateinit var tvReviewTitle: TextView
    private lateinit var tvReviewCity: TextView
    private lateinit var tvReviewDate: TextView
    private lateinit var tvReviewStatus: TextView
    private lateinit var tvReviewComment: TextView
    private lateinit var btnEditReview: Button
    private lateinit var btnBackToList: Button
    private lateinit var btnBack: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_review)

        tvReviewTitle = findViewById(R.id.tvReviewTitle)
        tvReviewCity = findViewById(R.id.tvReviewCity)
        tvReviewDate = findViewById(R.id.tvReviewDate)
        tvReviewStatus = findViewById(R.id.tvReviewStatus)
        tvReviewComment = findViewById(R.id.tvReviewComment)
        btnEditReview = findViewById(R.id.btnEditReview)
        btnBackToList = findViewById(R.id.btnBackToList)
        btnBack = findViewById(R.id.btnBack)

        // Получение переданных через Intent данных (Шаг 3 лаб 8)
        val title = intent.getStringExtra(EXTRA_TITLE) ?: "Отзыв о погоде"
        val city = intent.getStringExtra(EXTRA_CITY) ?: "Город не указан"
        val date = intent.getStringExtra(EXTRA_DATE) ?: "Дата не указана"
        val status = intent.getStringExtra(EXTRA_STATUS) ?: "Совпало"
        val comment = intent.getStringExtra(EXTRA_COMMENT) ?: "Комментарий отсутствует"
        val reviewId = intent.getLongExtra(EXTRA_REVIEW_ID, 0L)

        tvReviewTitle.text = title
        tvReviewCity.text = city
        tvReviewDate.text = date
        tvReviewStatus.text = status
        tvReviewComment.text = comment

        if (status == "Совпало") {
            tvReviewStatus.setBackgroundResource(R.drawable.bg_badge_matched)
            tvReviewStatus.setTextColor(Color.parseColor("#2E7D32"))
        } else {
            tvReviewStatus.setBackgroundResource(R.drawable.bg_badge_mismatched)
            tvReviewStatus.setTextColor(Color.parseColor("#D32F2F"))
        }

        btnBack.setOnClickListener {
            finish()
        }

        btnBackToList.setOnClickListener {
            finish()
        }

        btnEditReview.setOnClickListener {
            val intent = Intent(this, AddReviewActivity::class.java).apply {
                putExtra(AddReviewActivity.EXTRA_REVIEW_ID, reviewId)
                putExtra(AddReviewActivity.EXTRA_TITLE, title)
                putExtra(AddReviewActivity.EXTRA_CITY, city)
                putExtra(AddReviewActivity.EXTRA_DATE, date)
                putExtra(AddReviewActivity.EXTRA_STATUS, status)
                putExtra(AddReviewActivity.EXTRA_COMMENT, comment)
            }
            startActivity(intent)
            finish()
        }

        NavigationHelper.setupBottomNav(this, NavigationHelper.TAB_REVIEWS)
    }
}
