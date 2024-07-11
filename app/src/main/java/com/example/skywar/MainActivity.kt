package com.example.skywar

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.media.MediaPlayer

class MainActivity : AppCompatActivity() {

    lateinit var start: Button
    lateinit var setting: Button
    lateinit var quiteGame: Button
    private lateinit var backgroundMusic: MediaPlayer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        backgroundMusic = MediaPlayer.create(this, R.raw.mainmusic)
        backgroundMusic.isLooping = true
        backgroundMusic.start()


        start = findViewById(R.id.startbtn)
        setting = findViewById(R.id.settingbtn)
        quiteGame = findViewById(R.id.quitebtn)

        start.setOnClickListener {
            val intent = Intent(this, GameActivity::class.java)
            startActivity(intent)
            backgroundMusic.stop()
        }

        setting.setOnClickListener{
            val intent = Intent(this,OptionActivity::class.java)
            startActivity(intent)
        }
        quiteGame.setOnClickListener{
            finishAffinity() // Finish all activities in the current task, effectively closing the app
        }

    }
    override fun onDestroy() {
        super.onDestroy()
        backgroundMusic.release()
    }

}
