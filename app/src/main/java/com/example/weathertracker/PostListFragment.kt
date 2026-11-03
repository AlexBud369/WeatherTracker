package com.example.weathertracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch
import java.io.IOException

class PostListFragment : Fragment() {

    companion object {
        fun newInstance(): PostListFragment = PostListFragment()
    }

    private lateinit var rvPosts: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var layoutError: LinearLayout
    private lateinit var tvErrorMessage: TextView
    private lateinit var tvErrorDetails: TextView
    private lateinit var btnRetry: Button
    private lateinit var btnRefresh: Button
    private lateinit var tvNetworkStatus: TextView

    private val postAdapter = PostAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_post_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        setupRecyclerView()
        setupListeners()
        loadPosts()
        (activity as? MainActivity)?.updateToolbar("Сетевые данные (Retrofit)", true)
    }

    private fun bindViews(view: View) {
        rvPosts = view.findViewById(R.id.rvPosts)
        progressBar = view.findViewById(R.id.progressBar)
        layoutError = view.findViewById(R.id.layoutError)
        tvErrorMessage = view.findViewById(R.id.tvErrorMessage)
        tvErrorDetails = view.findViewById(R.id.tvErrorDetails)
        btnRetry = view.findViewById(R.id.btnRetry)
        btnRefresh = view.findViewById(R.id.btnRefresh)
        tvNetworkStatus = view.findViewById(R.id.tvNetworkStatus)
    }

    private fun setupRecyclerView() {
        rvPosts.layoutManager = LinearLayoutManager(requireContext())
        rvPosts.adapter = postAdapter
    }

    private fun setupListeners() {
        btnRetry.setOnClickListener { loadPosts() }
        btnRefresh.setOnClickListener { loadPosts() }
    }

    private fun loadPosts() {
        showLoadingState()
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val posts = ApiService.api.getPosts()
                showSuccessState(posts)
            } catch (exception: Exception) {
                showErrorState(exception)
            }
        }
    }

    private fun showLoadingState() {
        progressBar.visibility = View.VISIBLE
        layoutError.visibility = View.GONE
        rvPosts.visibility = View.GONE
        tvNetworkStatus.text = "Загрузка данных из REST API..."
    }

    private fun showSuccessState(posts: List<Post>) {
        progressBar.visibility = View.GONE
        layoutError.visibility = View.GONE
        rvPosts.visibility = View.VISIBLE
        postAdapter.updateList(posts)
        tvNetworkStatus.text = "Успешно загружено: ${posts.size} постов (JSONPlaceholder)"
    }

    private fun showErrorState(exception: Exception) {
        progressBar.visibility = View.GONE
        layoutError.visibility = View.VISIBLE
        rvPosts.visibility = View.GONE
        tvNetworkStatus.text = "Ошибка сети"
        if (exception is IOException) {
            tvErrorMessage.text = "Нет подключения к интернету"
            tvErrorDetails.text = "Проверьте Wi-Fi/мобильную связь или отключите режим «В самолёте»."
        } else {
            tvErrorMessage.text = "Ошибка загрузки: ${exception.localizedMessage ?: "Сбой сервера"}"
            tvErrorDetails.text = "Повторите попытку позже."
        }
    }
}
