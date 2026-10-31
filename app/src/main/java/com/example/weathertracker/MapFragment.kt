package com.example.weathertracker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
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

    private lateinit var tvMapHeader: TextView
    private lateinit var tvMapPinCity: TextView
    private lateinit var btnCloseMap: Button
    private lateinit var btnBackFromMap: ImageButton
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

        tvMapHeader = view.findViewById(R.id.tvMapHeader)
        tvMapPinCity = view.findViewById(R.id.tvMapPinCity)
        btnCloseMap = view.findViewById(R.id.btnCloseMap)
        btnBackFromMap = view.findViewById(R.id.btnBackFromMap)
        btnZoomIn = view.findViewById(R.id.btnZoomIn)
        btnZoomOut = view.findViewById(R.id.btnZoomOut)

        currentCity = arguments?.getString(ARG_CITY)?.takeIf { it.isNotBlank() } ?: WeatherAppState.currentCity
        tvMapHeader.text = "Интерактивная карта: $currentCity"
        updatePinText()

        btnBackFromMap.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnCloseMap.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        btnZoomIn.setOnClickListener {
            if (zoomLevel < 18) {
                zoomLevel++
                updatePinText()
            }
        }

        btnZoomOut.setOnClickListener {
            if (zoomLevel > 3) {
                zoomLevel--
                updatePinText()
            }
        }
    }

    private fun updatePinText() {
        tvMapPinCity.text = "$currentCity: +18°C, Масштаб ${zoomLevel}x"
    }
}