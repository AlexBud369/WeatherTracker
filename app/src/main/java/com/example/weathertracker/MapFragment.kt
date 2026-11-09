package com.example.weathertracker

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
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

    private lateinit var webViewMap: WebView
    private lateinit var tvMapPinCity: TextView
    private lateinit var btnCloseMap: Button
    private lateinit var btnZoomIn: FloatingActionButton
    private lateinit var btnZoomOut: FloatingActionButton

    private var zoomLevel: Int = 11
    private var currentCity: String = WeatherAppState.currentCity
    private var latitude: Double = WeatherAppState.currentLatitude
    private var longitude: Double = WeatherAppState.currentLongitude

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
        loadCityData()
        setupButtons()
        setupWebView()
    }

    private fun bindViews(view: View) {
        webViewMap = view.findViewById(R.id.webViewMap)
        tvMapPinCity = view.findViewById(R.id.tvMapPinCity)
        btnCloseMap = view.findViewById(R.id.btnCloseMap)
        btnZoomIn = view.findViewById(R.id.btnZoomIn)
        btnZoomOut = view.findViewById(R.id.btnZoomOut)
    }

    private fun loadCityData() {
        currentCity = arguments?.getString(ARG_CITY)?.takeIf { it.isNotBlank() } ?: WeatherAppState.currentCity
        latitude = WeatherAppState.currentLatitude
        longitude = WeatherAppState.currentLongitude
        (activity as? MainActivity)?.updateToolbar("Карта: $currentCity", true)
        updatePinText()
    }

    private fun setupButtons() {
        btnCloseMap.setOnClickListener { parentFragmentManager.popBackStack() }
        btnZoomIn.setOnClickListener { zoomIn() }
        btnZoomOut.setOnClickListener { zoomOut() }
    }

    @SuppressLint("SetJavaScriptEnabled")
    private fun setupWebView() {
        webViewMap.settings.javaScriptEnabled = true
        webViewMap.settings.domStorageEnabled = true
        webViewMap.settings.userAgentString = "WeatherTrackerApp/1.0 (Android Mobile)"
        webViewMap.webViewClient = WebViewClient()
        val temp = getCurrentTempString()
        val html = buildMapHtml(latitude, longitude, zoomLevel, currentCity, temp)
        webViewMap.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null)
    }

    private fun getCurrentTempString(): String {
        return WeatherRepository.latestForecast?.current?.temperature?.let {
            WeatherMapper.formatTemperature(it)
        } ?: "+18°C"
    }

    private fun buildMapHtml(lat: Double, lon: Double, zoom: Int, city: String, temp: String): String {
        return """
            <!DOCTYPE html>
            <html>
            <head>
                <meta name="viewport" content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" />
                <link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" />
                <script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js"></script>
                <style>
                    html, body, #map { height: 100%; margin: 0; padding: 0; background: #0F172A; }
                    .leaflet-control-attribution { display: none; }
                </style>
            </head>
            <body>
                <div id="map"></div>
                <script>
                    var map = L.map('map', { zoomControl: false }).setView([$lat, $lon], $zoom);
                    L.tileLayer('https://server.arcgisonline.com/ArcGIS/rest/services/World_Street_Map/MapServer/tile/{z}/{y}/{x}', {
                        maxZoom: 19
                    }).addTo(map);
                    L.marker([$lat, $lon]).addTo(map).bindPopup('<b>$city</b><br>$temp').openPopup();
                </script>
            </body>
            </html>
        """.trimIndent()
    }

    private fun zoomIn() {
        if (zoomLevel < 18) {
            zoomLevel++
            webViewMap.evaluateJavascript("map.setZoom($zoomLevel);", null)
            updatePinText()
        }
    }

    private fun zoomOut() {
        if (zoomLevel > 3) {
            zoomLevel--
            webViewMap.evaluateJavascript("map.setZoom($zoomLevel);", null)
            updatePinText()
        }
    }

    private fun updatePinText() {
        val temp = getCurrentTempString()
        tvMapPinCity.text = "$currentCity: $temp (${latitude.format(2)}, ${longitude.format(2)}) [${zoomLevel}x]"
    }

    private fun Double.format(digits: Int): String = "%.${digits}f".format(this)
}
