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

        // Инициализация нижней навигации
        NavigationHelper.setupBottomNav(this) { selectedTab ->
            if (selectedTab != currentTab) {
                switchTab(selectedTab)
            }
        }

        // Отслеживание изменений в стеке фрагментов для обновления подсветки нижней панели
        supportFragmentManager.addOnBackStackChangedListener {
            updateTabForTopFragment()
        }

        // При первом запуске загружаем главный экран погоды
        if (savedInstanceState == null) {
            val initialFragment = WeatherMainFragment.newInstance()
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, initialFragment)
                .commit()
            currentTab = NavigationHelper.TAB_WEATHER
            NavigationHelper.highlightTab(this, currentTab)
        }
    }

    /**
     * Динамическая замена фрагмента в контейнере с транзакцией и анимацией (Шаг 4 и 6 Лаб 9)
     */
    fun openFragment(fragment: Fragment, addToBackStack: Boolean = true) {
        val transaction = supportFragmentManager.beginTransaction()

        // Настройка анимаций переходов (Лабораторная работа №9, Шаг 6)
        transaction.setCustomAnimations(
            R.anim.slide_in_right,
            R.anim.slide_out_left,
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )

        // Замена фрагмента в контейнере
        transaction.replace(R.id.fragment_container, fragment)

        // Добавление в BackStack при необходимости
        if (addToBackStack) {
            transaction.addToBackStack(null)
        }

        transaction.commit()
    }

    /**
     * Переключение основных вкладок нижней навигации
     */
    private fun switchTab(tab: Int) {
        // Очищаем предыдущий стек возврата при переключении между основными разделами
        supportFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)

        val targetFragment: Fragment = when (tab) {
            NavigationHelper.TAB_WEATHER -> WeatherMainFragment.newInstance()
            NavigationHelper.TAB_FORECAST -> ForecastFragment.newInstance(WeatherAppState.currentCity)
            NavigationHelper.TAB_REVIEWS -> ReviewsListFragment.newInstance()
            NavigationHelper.TAB_SETTINGS -> SettingsFragment.newInstance()
            else -> WeatherMainFragment.newInstance()
        }

        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(R.id.fragment_container, targetFragment)
            .commit()

        currentTab = tab
        NavigationHelper.highlightTab(this, currentTab)
    }

    /**
     * Определение активной вкладки по текущему верхнему фрагменту в контейнере
     */
    private fun updateTabForTopFragment() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        currentTab = when (currentFragment) {
            is WeatherMainFragment -> NavigationHelper.TAB_WEATHER
            is ForecastFragment -> NavigationHelper.TAB_FORECAST
            is ReviewsListFragment, is AddReviewFragment, is ViewReviewFragment -> NavigationHelper.TAB_REVIEWS
            is SettingsFragment -> NavigationHelper.TAB_SETTINGS
            else -> NavigationHelper.TAB_NONE
        }
        NavigationHelper.highlightTab(this, currentTab)
    }
}