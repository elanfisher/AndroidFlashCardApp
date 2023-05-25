package com.example.flashcardapp

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.AnalogClock
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener


class LearnCards : AppCompatActivity() {

    // initialize every View
    lateinit var cardButton : Button
    lateinit var currentCardTV : TextView

    lateinit var countdownTextView : TextView
    private lateinit var countdownTimer: CountDownTimer

    lateinit var correctButton : Button
    lateinit var incorrectBUtton : Button
    lateinit var progressBar : ProgressBar
    lateinit var progressMin : TextView
    lateinit var progressMax : TextView

    var questionSide = true
    lateinit var currentCard : Flashcards.Card
    var currentCardIdx : Int = 0

    lateinit var timer : Timer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_learn)

        // find the views
        cardButton = findViewById(R.id.card)
        currentCardTV = findViewById(R.id.current_card)
        countdownTextView = findViewById(R.id.countdown_text_view)
        correctButton = findViewById(R.id.correct)
        incorrectBUtton = findViewById(R.id.incorrect)
        progressBar = findViewById(R.id.progress)
        progressMin = findViewById(R.id.progress_min)
        progressMax = findViewById(R.id.progress_max)

        // Count down timer

        // Initialize the countdown timer with a total time of 10 seconds and a tick interval of 1 second
        countdownTimer = object : CountDownTimer(10000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                // Calculate the remaining time in seconds
                val remainingSeconds = millisUntilFinished / 1000

                // Update the countdown text view with the remaining time in seconds
                countdownTextView.text = String.format(Locale.getDefault(), "%d", remainingSeconds)
            }

            override fun onFinish() {
                // Update the countdown text view with "Done"
                countdownTextView.text = "Done!!!"
            }
        }

        //TODO: Start the timer every time a card is moved!

        // Start the countdown timer
        countdownTimer.start()

        // update the view based on the model
        updateView()
    }

    override fun onDestroy() {
        super.onDestroy()
        // Cancel the countdown timer to avoid memory leaks
        countdownTimer.cancel()
    }

    override fun onStart() {
        super.onStart()

        Log.w("LearnCards", "Inside LearnCards onStart")

        updateView()
    }

    override fun onResume() {
        super.onResume()

        updateView()
    }

    // update view will update the view based on the model
    fun updateView() {
        Log.w("LearnCards", "Inside LearnCards updateView")

        currentCardIdx = MainActivity.flashcards.getCurrentFlashcardIndex()
        Log.w("LearnCards", "[LearnCards] CurrentCardIndex: $currentCardIdx")


        // set the text of the current card
        currentCardTV.text = "Current Card: " + (currentCardIdx + 1)

        // set the text of the progress bar
        progressMin.text = 0.toString()
        progressMax.text = MainActivity.flashcards.sizeOfDeck.toString()

        Log.w("LearnCards", "Size of Deck: "+MainActivity.flashcards.sizeOfDeck)
        // TODO "Fix progress bar not updating properly!"
        var calc : Int = ((currentCardIdx.toFloat() / MainActivity.flashcards.sizeOfDeck.toFloat()) * 100).toInt()
        Log.w("LearnCards", "Prog %: " + calc)
        progressBar.setProgress( calc )

        // set the text of the progress min
        progressMin.text = "1"

        // set the text of the progress max
        progressMax.text = MainActivity.flashcards.sizeOfDeck.toString()

        currentCard = MainActivity.flashcards.getFlashcards()[currentCardIdx]
        cardButton.text = currentCard.getQuestion()
    }

    // flip the current card, if
    fun flip ( v : View ) {
        if ( questionSide ) {
            // we are currently a question, we need to replace with the answer text
            cardButton.text = currentCard.getAnswer()

            questionSide = false
        } else {
            // replace with question text
            cardButton.text = currentCard.getQuestion()

            questionSide = true
        }
    }

    fun correctCard ( v : View ) {
        // check if we can remove a card, if not throw an exception
        if (MainActivity.flashcards.sizeOfDeck > 0) {

            Log.w("LearnCards", "[LC/correct] CurrentFlashcardIdx: $currentCardIdx")

            // remove the current card
            MainActivity.flashcards.removeFlashcard(currentCardIdx)

            // if there are no more cards left go to DoneActivity (start intent)
            if (currentCardIdx > MainActivity.flashcards.sizeOfDeck - 1) {
                Log.w("LearnCards", "[LC/correct] LEAVE THIS VIEW: $currentCardIdx")

                // test finish
                finish()

                var myIntent: Intent = Intent(this, DoneActivity::class.java)

                // push the intent onto the stack
                this.startActivity(myIntent)

                // transition
                overridePendingTransition(R.anim.slide_from_left, 0)
            } else {
                Log.w("LearnCards", "[LC/correct] GO TO NEXT CARD!: $currentCardIdx")

                // restart the timer
                countdownTimer.start()

                // go to the next card
                currentCard = MainActivity.flashcards.getNextFlashcard()
                currentCardIdx = MainActivity.flashcards.getCurrentFlashcardIndex()
                updateView()
            }
        }
    }

    fun incorrectCard ( v : View ) {
        // if there are no more cards left go to DoneActivity (start intent)
        var deckSize : Int = MainActivity.flashcards.sizeOfDeck

        Log.w("LearnCards", "[LC/incorrect] CurrentFlashcardIdx: $currentCardIdx")

        if (currentCardIdx >= MainActivity.flashcards.sizeOfDeck - 1) {
            Log.w("LearnCards", "[LC/incorrect] LEAVE THIS VIEW: $currentCardIdx")

            // test finish
            finish()

            var myIntent: Intent = Intent(this, DoneActivity::class.java)

            // push the intent onto the stack
            this.startActivity(myIntent)

            // transition
            overridePendingTransition(R.anim.slide_from_left, 0)
        } else {
            Log.w("LearnCards", "[LC/incorrect] GO TO NEXT CARD!: $currentCardIdx")

            // restart the timer
            countdownTimer.start()

            // go to the next card
            currentCard = MainActivity.flashcards.getNextFlashcard()
            currentCardIdx = MainActivity.flashcards.getCurrentFlashcardIndex()
            updateView()
        }
    }

    private fun timeStringFromLong(time : Long) : String {
        var hours = (time / 1000 * 60 * 60) % 24
        var minutes = (time / 1000 * 60) % 60
        var seconds = (time / 1000) % 60

        //converts time to String format
        return timeString(hours, minutes, seconds)
    }

    //converts time to String format
    private fun timeString(hours : Long, minutes : Long, seconds : Long) : String {
        return String.format("%02d:%02d:%02d", hours, minutes, seconds)
    }
}