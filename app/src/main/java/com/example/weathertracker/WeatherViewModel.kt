package com.example.weathertracker

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WeatherViewModel(
    private val repository: WeatherRepository = WeatherRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(WeatherUiState())
    val uiState: StateFlow<WeatherUiState> = _uiState.asStateFlow()

    fun loadWeather(cityName: String, force: Boolean = false) {
        val currentCity = _uiState.value.city?.name
        if (!force && currentCity.equals(cityName, ignoreCase = true) && _uiState.value.forecast != null) {
            return
        }
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        viewModelScope.launch {
            val result = repository.loadWeather(cityName)
            handleLoadResult(result)
        }
    }

    private fun handleLoadResult(result: Result<Pair<GeocodingCity, ForecastResponse>>) {
        result.onSuccess { pair ->
            _uiState.value = WeatherUiState(
                isLoading = false,
                city = pair.first,
                forecast = pair.second,
                errorMessage = null
            )
        }.onFailure { error ->
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = error.message ?: "Ошибка загрузки данных"
            )
        }
    }
}
