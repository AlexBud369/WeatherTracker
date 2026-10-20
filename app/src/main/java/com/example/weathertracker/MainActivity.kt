package com.example.weathertracker

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.weathertracker.R

class MainActivity : AppCompatActivity() {

    private lateinit var etCity: EditText
    private lateinit var btnShowWeather: Button
    private lateinit var tvWeatherOutput: TextView
    private lateinit var tvCreateCount: TextView
    private var createCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Log.d("Lifecycle", "onCreate")

        etCity = findViewById(R.id.etCity)
        btnShowWeather = findViewById(R.id.btnShowWeather)
        tvWeatherOutput = findViewById(R.id.tvWeatherOutput)
        tvCreateCount = findViewById(R.id.tvCreateCount)

        createCount = savedInstanceState?.getInt("createCount", 0) ?: createCount
        createCount++
        tvCreateCount.text = "onCreate вызван $createCount раз(а)"

        if (savedInstanceState != null) {
            etCity.setText(savedInstanceState.getString("userInput", ""))
            tvWeatherOutput.text = savedInstanceState.getString("weatherOutput", "Результат появится здесь")
        }

        btnShowWeather.setOnClickListener {
            val city = etCity.text.toString().trim()
            if (city.isNotEmpty()) {
                tvWeatherOutput.text = "Погода для города: $city"
            } else {
                tvWeatherOutput.text = "Введите город"
            }
        }

        etCity.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tvWeatherOutput.text = "Введён город: ${s.toString()}"
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onStart() {
        super.onStart()
        Log.d("Lifecycle", "onStart")
    }

    override fun onResume() {
        super.onResume()
        Log.d("Lifecycle", "onResume")
    }

    override fun onPause() {
        super.onPause()
        Log.d("Lifecycle", "onPause")
    }

    override fun onStop() {
        super.onStop()
        Log.d("Lifecycle", "onStop")
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("Lifecycle", "onDestroy")
    }

    override fun onRestart() {
        super.onRestart()
        Log.d("Lifecycle", "onRestart")
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString("userInput", etCity.text.toString())
        outState.putString("weatherOutput", tvWeatherOutput.text.toString())
        outState.putInt("createCount", createCount)
        Log.d("Lifecycle", "onSaveInstanceState")
    }
}
