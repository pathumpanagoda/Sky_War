package com.example.skywar

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.RadioButton
import android.widget.RadioGroup

class OptionActivity : AppCompatActivity() {

    lateinit var backbtn: ImageButton
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_option)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)

        val speedRadioGroup = findViewById<RadioGroup>(R.id.speedRadioGroup)
        val saveButton = findViewById<Button>(R.id.saveButton)
        backbtn = findViewById(R.id.backButton)

        backbtn.setOnClickListener {
            animateButtonOnClick(backbtn)
            finish()
        }

        saveButton.setOnClickListener {
            saveGameSpeed(speedRadioGroup.checkedRadioButtonId)
            loadGameSpeed()
            finish()
        }
    }

    private fun saveGameSpeed(speedId: Int) {
        val editor = sharedPreferences.edit()
        editor.putInt("speed", speedId)
        editor.apply()
    }

    private fun loadGameSpeed() {
        val speedId = sharedPreferences.getInt("speed", R.id.mediumCkBox)
        findViewById<RadioButton>(speedId).isChecked = true
    }

    override fun onResume() {
        super.onResume()
        try {
            loadGameSpeed()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun animateButtonOnClick(button: ImageButton) {
        // Scale up animation
        button.animate()
            .scaleX(1.2f)
            .scaleY(1.2f)
            .setDuration(10)
            .withEndAction {
                // Scale down animation (reversing)
                button.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(10)
                    .start()
            }
            .start()
    }


}