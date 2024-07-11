package com.example.skywar
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.view.MotionEvent
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random


class GameActivity : AppCompatActivity() {

    // Constants for time intervals and score values
    private companion object {
        private var GAME_LOOP_INTERVAL = 20L
        private const val BOMB_MOVE_SPEED = 20
        private const val BOMB2_MOVE_SPEED = 25
        private const val BOMB3_MOVE_SPEED = 30
        private const val COIN_MOVE_SPEED = 15
        private const val COIN2_MOVE_SPEED = 18
        private const val BOMB_SCORE = 5
        private const val COIN_SCORE = 20
    }

    private lateinit var collisionSound: MediaPlayer
    private lateinit var failSound: MediaPlayer
    private lateinit var backgroundSound: MediaPlayer
    private lateinit var plane: ImageView
    private lateinit var bomb: ImageView
    private lateinit var bomb2: ImageView
    private lateinit var bomb3: ImageView
    private lateinit var coin: ImageView
    private lateinit var coin2: ImageView
    private lateinit var scoreText: TextView
    private lateinit var highScoreText: TextView
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var toggleButton: ToggleButton
    private lateinit var pauseButton: ImageButton
    private var screenHeight = 0
    private var screenWidth = 0
    private var planeY = 0
    private var bombX = 0
    private var bombY = 0
    private var bomb2X = 0
    private var bomb2Y = 0
    private var bomb3X = 0
    private var bomb3Y = 0
    private var coinX = 0
    private var coinY = 0
    private var coin2X = 0
    private var coin2Y = 0
    private var isGameOver = false
    private var isGamePaused = false
    private var score = 0
    private var highScore = 0
    private lateinit var handler: Handler

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        plane = findViewById(R.id.planeImageView)
        bomb = findViewById(R.id.bomb1)
        bomb2 = findViewById(R.id.bomb2)
        bomb3 = findViewById(R.id.bomb3)
        coin = findViewById(R.id.coin)
        coin2 = findViewById(R.id.coin2)
        scoreText = findViewById(R.id.scoreTextView)
        highScoreText = findViewById(R.id.highScoreTextView)
        collisionSound = MediaPlayer.create(this, R.raw.coincollect2)
        failSound = MediaPlayer.create(this, R.raw.blast)
        backgroundSound = MediaPlayer.create(this, R.raw.backmusic)
        toggleButton = findViewById(R.id.toggleButton)
        pauseButton = findViewById(R.id.pauseButton)

        sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val speedId = sharedPreferences.getInt("speed", R.id.mediumCkBox)

        // Adjust GAME_LOOP_INTERVAL based on speed setting
        GAME_LOOP_INTERVAL = when (speedId) {
            R.id.lawCkBox -> 40L
            R.id.mediumCkBox -> 30L
            R.id.highCkBox -> 10L
            else -> 30L  // Default to medium speed
        }

        //background sound
        backgroundSound.isLooping = true
        backgroundSound.start()


        // Set the initial state of the toggle button based on the background music
        toggleButton.isChecked = false

        toggleButton.setOnCheckedChangeListener { buttonView, isChecked ->
            animateButtonOnClick(toggleButton)
            if (isChecked) {
                // If toggle button is checked, play background music
                muteBackgroundMusic()
            } else {
                // If toggle button is unchecked, pause background music
                unmuteBackgroundMusic()
            }
        }


        //gwt high score from sharedPreferences
        highScore = sharedPreferences.getInt("highScore", 0)
        highScoreText.text = "High Score: $highScore"

        screenHeight = resources.displayMetrics.heightPixels
        screenWidth = resources.displayMetrics.widthPixels

        planeY = (screenHeight / 8) - (plane.height / 4)

        plane.y = planeY.toFloat()


        pauseButton.setOnClickListener {
               pauseGame()
        }

        handler = Handler()
        startGameLoop()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isGameOver && !isGamePaused) {
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    plane.y = event.y - (plane.height / 2)
                }
                MotionEvent.ACTION_MOVE -> {
                    plane.y = event.y - (plane.height / 2)
                }
            }
        }
        return true
    }

    private fun startGameLoop() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (!isGameOver && !isGamePaused) {
                    moveBomb()
                    moveBomb2()
                    moveBomb3()
                    moveCoin()
                    moveCoin2()
                    checkCollision()
                    handler.postDelayed(this, GAME_LOOP_INTERVAL)
                }
            }
        }, GAME_LOOP_INTERVAL)
    }

    private fun pauseGame() {
        handler.removeCallbacksAndMessages(null)
        showPauseDialog()
    }

    private fun resumeGame() {
        startGameLoop()
        // You can add UI changes for resume state here
    }

    private fun moveBomb() {
        val previousX = bombX
        bombX -= BOMB_MOVE_SPEED
        if (bombX < 0) {
            bombX = screenWidth
            bombY = Random.nextInt(0, screenHeight - bomb.height)
        }
        bomb.x = bombX.toFloat()
        bomb.y = bombY.toFloat()

        if (previousX > plane.x + plane.width && bombX <= plane.x + plane.width) {
            score += BOMB_SCORE
            scoreText.text = "Score: $score"
        }
    }

    private fun moveBomb2() {
        val previousX = bomb2X
        bomb2X -= BOMB2_MOVE_SPEED
        if (bomb2X < 0) {
            bomb2X = screenWidth
            bomb2Y = Random.nextInt(0, screenHeight - bomb2.height)
        }
        bomb2.x = bomb2X.toFloat()
        bomb2.y = bomb2Y.toFloat()

        if (previousX > plane.x + plane.width && bomb2X <= plane.x + plane.width) {
            score += BOMB_SCORE
            scoreText.text = "Score: $score"
        }
    }

    private fun moveBomb3() {
        val previousX = bomb3X
        bomb3X -= BOMB3_MOVE_SPEED
        if (bomb3X < 0) {
            bomb3X = screenWidth
            bomb3Y = Random.nextInt(0, screenHeight - bomb3.height)
        }
        bomb3.x = bomb3X.toFloat()
        bomb3.y = bomb3Y.toFloat()

        if (previousX > plane.x + plane.width && bomb3X <= plane.x + plane.width) {
            score += BOMB_SCORE
            scoreText.text = "Score: $score"
        }
    }

    private fun moveCoin() {
        coinX -= COIN_MOVE_SPEED
        if (coinX < 0 || isCoinColliding()) {
            coinX = screenWidth
            coinY = Random.nextInt(0, screenHeight - coin.height)
        }
        coin.x = coinX.toFloat()
        coin.y = coinY.toFloat()
    }

    private fun moveCoin2() {
        coin2X -= COIN2_MOVE_SPEED
        if (coin2X < 0 || isCoin2Colliding()) {
            coin2X = screenWidth
            coin2Y = Random.nextInt(0, screenHeight - coin2.height)
        }
        coin2.x = coin2X.toFloat()
        coin2.y = coin2Y.toFloat()
    }


    //check 1st coin collision
    private fun isCoinColliding(): Boolean {
        val planeLeft = plane.x
        val planeTop = plane.y
        val planeRight = plane.x + plane.width
        val planeBottom = plane.y + plane.height

        val coinLeft = coin.x
        val coinTop = coin.y
        val coinRight = coin.x + coin.width
        val coinBottom = coin.y + coin.height

        val isCollidingCoin1 = planeLeft < coinRight && planeRight > coinLeft && planeTop < coinBottom && planeBottom > coinTop


        if (isCollidingCoin1) {
            score += COIN_SCORE
            scoreText.text = "Score: $score"
            playCollisionSound()
        }

        return isCollidingCoin1
    }

    //check coin 2 collision
    private fun isCoin2Colliding(): Boolean {
        val planeLeft = plane.x
        val planeTop = plane.y
        val planeRight = plane.x + plane.width
        val planeBottom = plane.y + plane.height

        val coin2Left = coin2.x
        val coin2Top = coin2.y
        val coin2Right = coin2.x + coin2.width
        val coin2Bottom = coin2.y + coin2.height

        val isCollidingCoin2 = planeLeft < coin2Right && planeRight > coin2Left && planeTop < coin2Bottom && planeBottom > coin2Top

        if (isCollidingCoin2) {
            score += COIN_SCORE
            scoreText.text = "Score: $score"
            playCollisionSound()
        }

        return isCollidingCoin2
    }

    //check bombs collision
    private fun checkCollision() {
        val planeLeft = plane.x
        val planeTop = plane.y
        val planeRight = plane.x + plane.width
        val planeBottom = plane.y + plane.height

        val bombLeft = bomb.x
        val bombTop = bomb.y
        val bombRight = bomb.x + bomb.width
        val bombBottom = bomb.y + bomb.height

        val bomb2Left = bomb2.x
        val bomb2Top = bomb2.y
        val bomb2Right = bomb2.x + bomb2.width
        val bomb2Bottom = bomb2.y + bomb2.height

        val bomb3Left = bomb3.x
        val bomb3Top = bomb3.y
        val bomb3Right = bomb3.x + bomb3.width
        val bomb3Bottom = bomb3.y + bomb3.height

        if (planeLeft < bombRight && planeRight > bombLeft && planeTop < bombBottom && planeBottom > bombTop) {
            gameOver()
            playFailSound()
        }

        if (planeLeft < bomb2Right && planeRight > bomb2Left && planeTop < bomb2Bottom && planeBottom > bomb2Top) {
            gameOver()
            playFailSound()
        }

        if (planeLeft < bomb3Right && planeRight > bomb3Left && planeTop < bomb3Bottom && planeBottom > bomb3Top) {
            gameOver()
            playFailSound()
        }
    }


    private fun playCollisionSound() {
        collisionSound.start()
    }

    private fun playFailSound() {
        failSound.start()
    }


    private fun muteBackgroundMusic() {
        if (backgroundSound.isPlaying) {
            backgroundSound.pause()
        }
    }

    private fun unmuteBackgroundMusic() {
        if (!backgroundSound.isPlaying) {
            backgroundSound.start()
        }
    }

    private fun gameOver() {
        isGameOver = true
        checkAndUpdateHighScore()

        // Show game over dialog
        showGameOverDialog()
    }


    private fun showPauseDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_game_paused, null)

        val resumeButton = dialogView.findViewById<Button>(R.id.resumeButton)
        val quitButton = dialogView.findViewById<Button>(R.id.quitButton2)
        val closeButton = dialogView.findViewById<ImageButton>(R.id.closeButton2)

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)

        val dialog = dialogBuilder.create()

        resumeButton.setOnClickListener {
            // Resume the game
            dialog.dismiss()
            resumeGame()

        }

        quitButton.setOnClickListener {
            // Stop background music
            backgroundSound.stop()
            // Quit the game
            finish()
        }

        dialog.show()
    }


    private fun showGameOverDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_game_over, null)
        val restartButton = dialogView.findViewById<Button>(R.id.restartButton)
        val quitButton = dialogView.findViewById<Button>(R.id.quitButton)
        val closeButton = dialogView.findViewById<ImageButton>(R.id.closeButton)

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)

        val dialog = dialogBuilder.create()

        restartButton.setOnClickListener {
            // Restart the game
            restartGame()
            dialog.dismiss()
        }

        quitButton.setOnClickListener {
            // Stop background music
            backgroundSound.stop()
            // Quit the game
            finish() // Close the GameActivity
        }

        dialog.show()
    }


    private fun restartGame() {
        // Reset game variables
        isGameOver = false
        isGamePaused = false
        score = 0
        scoreText.text = "Score: $score"

        // Reset bomb, coin, and plane positions
        resetGameElements()

        // Restart game loop
        startGameLoop()
    }

    private fun resetGameElements() {
        // Reset bomb positions
        bombX = screenWidth
        bombY = Random.nextInt(0, screenHeight - bomb.height)
        bomb.x = bombX.toFloat()
        bomb.y = bombY.toFloat()

        bomb2X = screenWidth
        bomb2Y = Random.nextInt(0, screenHeight - bomb2.height)
        bomb2.x = bomb2X.toFloat()
        bomb2.y = bomb2Y.toFloat()

        bomb3X = screenWidth
        bomb3Y = Random.nextInt(0, screenHeight - bomb3.height)
        bomb3.x = bomb3X.toFloat()
        bomb3.y = bomb3Y.toFloat()

        // Reset coin position
        coinX = screenWidth
        coinY = Random.nextInt(0, screenHeight - coin.height)
        coin.x = coinX.toFloat()
        coin.y = coinY.toFloat()

        //Reset coin 2 position
        coin2X = screenWidth
        coin2Y = Random.nextInt(0, screenHeight - coin2.height)
        coin2.x = coin2X.toFloat()
        coin2.y = coin2Y.toFloat()

        // Reset plane position
        planeY = (screenHeight / 2) - (plane.height / 2)
        plane.y = planeY.toFloat()
    }


    private fun checkAndUpdateHighScore() {
        if (score > highScore) {
            highScore = score
            highScoreText.text = "High Score: $highScore"
        }
    }

    override fun onPause() {
        super.onPause()
        with(sharedPreferences.edit()) {
            putInt("highScore", highScore)
            apply()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        // Release media players to free up resources
        releaseMediaPlayers()
    }

    private fun releaseMediaPlayers() {
        collisionSound.release()
        failSound.release()
        backgroundSound.release()
    }

    private fun animateButtonOnClick(button: ToggleButton) {
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
