package com.example.weathertracker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager

class MainActivity : AppCompatActivity() {

    private var currentTab: Int = NavigationHelper.TAB_WEATHER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupNavigationListeners()
        if (savedInstanceState == null) {
            loadInitialFragment()
        }
    }

    private fun setupNavigationListeners() {
        NavigationHelper.setupBottomNav(this) { selectedTab ->
            if (selectedTab != currentTab) {
                switchTab(selectedTab)
            }
        }
        supportFragmentManager.addOnBackStackChangedListener {
            updateTabForTopFragment()
        }
    }

    private fun loadInitialFragment() {
        val initialFragment = WeatherMainFragment.newInstance()
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, initialFragment)
            .commit()
        currentTab = NavigationHelper.TAB_WEATHER
        NavigationHelper.highlightTab(this, currentTab)
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

    private fun updateTabForTopFragment() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        currentTab = resolveTabForFragment(currentFragment)
        NavigationHelper.highlightTab(this, currentTab)
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
}