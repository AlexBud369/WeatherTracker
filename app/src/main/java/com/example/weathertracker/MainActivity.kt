package com.example.weathertracker

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class MainActivity : AppCompatActivity() {

    private lateinit var toolbar: Toolbar
    private var currentTab: Int = NavigationHelper.TAB_WEATHER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupToolbar()
        setupNavigationListeners()
        if (savedInstanceState == null) {
            loadInitialFragment()
        }
    }

    private fun setupToolbar() {
        toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        toolbar.setNavigationOnClickListener {
            supportFragmentManager.popBackStack()
        }
    }

    private fun setupNavigationListeners() {
        NavigationHelper.setupBottomNav(this) { selectedTab ->
            if (selectedTab != currentTab) {
                switchTab(selectedTab)
            }
        }
        supportFragmentManager.addOnBackStackChangedListener {
            updateUIForCurrentFragment()
        }
    }

    private fun loadInitialFragment() {
        val initialFragment = WeatherMainFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, initialFragment)
            .commit()
        currentTab = NavigationHelper.TAB_WEATHER
        NavigationHelper.highlightTab(this, currentTab)
        updateToolbar("Прогноз погоды", false)
    }

    fun openFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.setCustomAnimations(
            R.anim.slide_in_right,
            R.anim.slide_out_left,
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )
        transaction.replace(R.id.fragment_container, fragment)
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }
        transaction.commit()
    }

    private fun switchTab(tab: Int) {
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        val targetFragment = createTabFragment(tab)
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(R.id.fragment_container, targetFragment)
            .commit()
        currentTab = tab
        NavigationHelper.highlightTab(this, currentTab)
    }

    private fun createTabFragment(tab: Int): Fragment {
        return when (tab) {
            NavigationHelper.TAB_WEATHER -> WeatherMainFragment.newInstance()
            NavigationHelper.TAB_FORECAST -> ForecastFragment.newInstance(WeatherAppState.currentCity)
            NavigationHelper.TAB_REVIEWS -> ReviewsListFragment.newInstance()
            NavigationHelper.TAB_SETTINGS -> SettingsFragment.newInstance()
            else -> WeatherMainFragment.newInstance()
        }
    }

    private fun updateUIForCurrentFragment() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        currentTab = resolveTabForFragment(currentFragment)
        NavigationHelper.highlightTab(this, currentTab)
        configureToolbarForFragment(currentFragment)
    }

    private fun resolveTabForFragment(fragment: Fragment?): Int {
        return when (fragment) {
            is WeatherMainFragment -> NavigationHelper.TAB_WEATHER
            is ForecastFragment -> NavigationHelper.TAB_FORECAST
            is ReviewsListFragment, is AddReviewFragment, is ViewReviewFragment -> NavigationHelper.TAB_REVIEWS
            is SettingsFragment -> NavigationHelper.TAB_SETTINGS
            else -> NavigationHelper.TAB_NONE
        }
    }

    private fun configureToolbarForFragment(fragment: Fragment?) {
        when (fragment) {
            is WeatherMainFragment -> updateToolbar("Прогноз погоды", false)
            is ForecastFragment -> updateToolbar("Прогноз: ${WeatherAppState.currentCity}", true)
            is ReviewsListFragment -> updateToolbar("Мои отзывы", false)
            is AddReviewFragment -> updateToolbar("Оставить отзыв", true)
            is ViewReviewFragment -> updateToolbar("Просмотр отзыва", true)
            is SettingsFragment -> updateToolbar("Настройки", false)
            is MapFragment -> updateToolbar("Интерактивная карта", true)
            else -> updateToolbar("Прогноз погоды", false)
        }
    }

    fun updateToolbar(title: String, showBackButton: Boolean) {
        supportActionBar?.title = title
        supportActionBar?.setDisplayHomeAsUpEnabled(showBackButton)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> handleAddAction()
            R.id.action_settings -> handleSettingsAction()
            R.id.action_about -> handleAboutAction()
            android.R.id.home -> handleHomeAction()
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun handleAddAction(): Boolean {
        openFragment(AddReviewFragment.newInstance(), addToBackStack = true)
        return true
    }

    private fun handleSettingsAction(): Boolean {
        openFragment(SettingsFragment.newInstance(), addToBackStack = true)
        return true
    }

    private fun handleAboutAction(): Boolean {
        showAboutDialog()
        return true
    }

    private fun handleHomeAction(): Boolean {
        supportFragmentManager.popBackStack()
        return true
    }

    private fun showAboutDialog() {
        AlertDialog.Builder(this)
            .setTitle("О приложении")
            .setMessage("WeatherTracker — приложение для отслеживания прогноза погоды и отзывов пользователей.")
            .setPositiveButton("Понятно", null)
            .show()
    }
}