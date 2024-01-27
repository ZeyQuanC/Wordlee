package com.example.wordle

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var guessEditText: EditText
    private lateinit var submitButton: Button
    private lateinit var correctnessTextViews: List<TextView>

    private val wordToGuess = FourLetterWordList.getRandomFourLetterWord()
    private var remainingGuesses = 3
    private val correctnessList = mutableListOf<String>()
    private val guessesList = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        guessEditText = findViewById(R.id.text_input)
        submitButton = findViewById(R.id.button)
        correctnessTextViews = listOf(
            findViewById(R.id.correctivenessTextView6),
            findViewById(R.id.correctivenessTextView4),
            findViewById(R.id.correctivenessTextView8)
        )

        submitButton.setOnClickListener {
            handleGuess()
            hideKeyboard()
        }
    }
    @SuppressLint("SetTextI18n")
    private fun handleGuess() {
        val userGuess = guessEditText.text.toString().uppercase()
        val correctness = checkGuess(userGuess)

        // Store guesses and correctness information
        guessesList.add(userGuess)
        correctnessList.add(correctness)

        // Update correctness display for each TextView
        for (i in correctnessTextViews.indices) {
            val guessText = if (i < guessesList.size) "Guess ${i + 1}: ${guessesList[i]}" else ""
            val correctnessText = if (i < correctnessList.size) "Correctness: ${correctnessList[i]}" else ""

            correctnessTextViews[i].text = "$guessText\n$correctnessText"
        }

        // Decrease remaining guesses
        remainingGuesses--

        // Check if the user has exceeded the number of guesses
        if (remainingGuesses == 0) {
            // Disable the submit button
            submitButton.isEnabled = false

            // Show a 'Reset' button instead of 'Submit'
            val resetButton = findViewById<Button>(R.id.button)
            resetButton.text = getString(R.string.reset_button)
            resetButton.setOnClickListener {
                resetGame()
            }

            // Show a Toast to inform the user
            showToast("You've exceeded the number of guesses!")

            // Show the correct answer
            val correctAnswerTextView = findViewById<TextView>(R.id.correct_answer_textview)
            correctAnswerTextView.text = "Correct Answer: $wordToGuess"
        }

        // Clear the input field after handling the guess
        guessEditText.text.clear()
    }

    // Function to reset the game
    private fun resetGame() {
        remainingGuesses = 3
        guessesList.clear()
        correctnessList.clear()
        submitButton.isEnabled = true
        val resetButton = findViewById<Button>(R.id.button)
        resetButton.text = getString(R.string.submit_button)
        val correctAnswerTextView = findViewById<TextView>(R.id.correct_answer_textview)
        correctAnswerTextView.text = ""
        clearTextViews()
    }

    // Function to show a Toast
    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    // Function to clear correctness TextViews
    private fun clearTextViews() {
        for (textView in correctnessTextViews) {
            textView.text = ""
        }
    }

    private fun checkGuess(guess: String): String {
        var result = ""
        for (i in guess.indices) {
            result += when {
                guess[i] == wordToGuess[i] -> "O"
                guess[i] in wordToGuess -> "+"
                else -> "X"
            }
        }
        return result
    }

    // Inside your activity or fragment
    private fun hideKeyboard() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(guessEditText.windowToken, 0)
    }

    // Inside your activity or fragment
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        currentFocus?.let {
            hideKeyboard()
        }
        return super.onTouchEvent(event)
    }
}
