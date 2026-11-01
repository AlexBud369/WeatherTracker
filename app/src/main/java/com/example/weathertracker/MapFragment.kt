package com.example.weathertracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MapFragment : Fragment() {

    companion object {
        private const val ARG_CITY = "arg_city"

        fun newInstance(city: String = WeatherAppState.currentCity): MapFragment {
            val fragment = MapFragment()
            val args = Bundle().apply {
                putString(ARG_CITY, city)
            }
            fragment.arguments = args
            return fragment
        }
    }

    private lateinit var tvMapPinCity: TextView
    private lateinit var btnCloseMap: Button
    private lateinit var btnZoomIn: FloatingActionButton
    private lateinit var btnZoomOut: FloatingActionButton

    private var zoomLevel: Int = 10
    private var currentCity: String = WeatherAppState.currentCity

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_map, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindViews(view)
        loadCity()
        setupButtons()
    }

    private fun bindViews(view: View) {
        tvMapPinCity = view.findViewById(R.id.tvMapPinCity)
        btnCloseMap = view.findViewById(R.id.btnCloseMap)
        btnZoomIn = view.findViewById(R.id.btnZoomIn)
        btnZoomOut = view.findViewById(R.id.btnZoomOut)
    }

    private fun loadCity() {
        currentCity = arguments?.getString(ARG_CITY)?.takeIf { it.isNotBlank() } ?: WeatherAppState.currentCity
        (activity as? MainActivity)?.updateToolbar("Карта: $currentCity", true)
        updatePinText()
    }

    private fun setupButtons() {
        btnCloseMap.setOnClickListener { parentFragmentManager.popBackStack() }
        btnZoomIn.setOnClickListener { zoomIn() }
        btnZoomOut.setOnClickListener { zoomOut() }
    }

    private fun zoomIn() {
        if (zoomLevel < 18) {
            zoomLevel++
            updatePinText()
        }
    }

    private fun zoomOut() {
        if (zoomLevel > 3) {
            zoomLevel--
            updatePinText()
        }
    }

    private fun updatePinText() {
        tvMapPinCity.text = "$currentCity: +18°C, Масштаб ${zoomLevel}x"
    }
}