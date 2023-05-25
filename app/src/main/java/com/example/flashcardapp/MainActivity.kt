package com.example.flashcardapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.values
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.app

class MainActivity : AppCompatActivity() {

    lateinit var database : DatabaseReference

    // initialize every View
    lateinit var questionButton : EditText
    lateinit var answerButton : EditText
    lateinit var doneButton : Button
    lateinit var newCardButton : Button
    lateinit var numCardsText : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.flash_add)

        Log.w("MainActivity", "Inside onCreate MainActivity")

        // find the views
        questionButton = findViewById(R.id.question)
        answerButton = findViewById(R.id.answer)
        doneButton = findViewById(R.id.done)
        newCardButton = findViewById(R.id.addCard)
        numCardsText = findViewById(R.id.number)

        // handle firebase stuff
        database = Firebase.database.getReferenceFromUrl("https://flashcardapp-17acc-default-rtdb.firebaseio.com/")
        Log.w("MainActivity", "Firebase Instance: "+database.toString())

        if( flashcards.init == false ) {
            getServerFlashcards()
            flashcards.init = true
        }

        // update the view based on the model
        updateView()
    }

    override fun onStart() {
        super.onStart()

        Log.w("MainActivity", "Inside MainActivity onStart")

        updateView()
    }

    override fun onResume() {
        super.onResume()

        Log.w("MainActivity", "Inside MainActivity onResume")

        updateView()
    }

    // update view will update the view based on the model
    fun updateView() {
        Log.w("MainActivity", "Inside MainActivity updateView")

        var numcards = flashcards.sizeOfDeck
        Log.w("MainActivity", "[MA] NumOfCards: $numcards, raw size: ${flashcards.getFlashcards().size}")

        // for loop print out all the cards with their question and answer with index
        for (i in 0 until flashcards.getFlashcards().size) {
            Log.w("MainActivity", "[MA] Card $i: ${flashcards.getFlashcards()[i].getQuestion()} : ${flashcards.getFlashcards()[i].getAnswer()}")
        }

        // set the text of the number of cards
        numCardsText.text = "Number of Cards: " + numcards
    }

    // pushes the current flashcards to firebase
    fun postServerFlashcards(question : String, answer : String) {
        // post to the flashcards
        // get the flashcards from the server, then update our model.
        val db = Firebase.database
        val cardsRef = db.getReference("cards")
        Log.w("MainActivity", "[POST] Got the cards object: " + cardsRef)

        val currentCardIdx = (flashcards.sizeOfDeck - 1).toString()
        Log.w("MainActivity", "[POST] Card Key: "+currentCardIdx)

        var cardData = HashMap<String, String>()
        cardData["back"] = answer
        cardData["front"] = question

        cardsRef.child(currentCardIdx).setValue(cardData)
            .addOnSuccessListener {
                // Write was successful!
                Log.w("MainActivity", "[POST] Write Success for: "+question + " : " + answer)
            }
            .addOnFailureListener {
                // Write failed
                Log.w("MainActivity", "[POST] Write Failed for: "+question + " : " + answer)
            }
    }

    // gets current flashcards from firebase
    fun getServerFlashcards() {
        // get the flashcards from the server, then update our model.
        val db = Firebase.database

        val cardsRef = db.getReference("cards")

        Log.w("MainActivity", "[GET] Got the cards object: " + cardsRef)

        var i = 0
        var go = true
        while(i < 100 && go == true) {
            var cardNum : String = i.toString()
            cardsRef.child(cardNum).get().addOnSuccessListener {
                val res = it.value
                if( res != null ) {

                    val resStr = res.toString()
                    Log.w("MainActivity", "Got value: ${resStr}, i = $i")

                    // Extract the values of 'back' and 'front' using regex and string manipulation
                    val back = Regex("back=(.+), front=").find(resStr)?.groupValues?.get(1)
                    val front = Regex("front=(.+)\\}").find(resStr)?.groupValues?.get(1)

                    Log.w("MainActivity", "back: "+back)
                    Log.w("MainActivity", "front: "+front)

                    // add it to our flashcard list.
                    flashcards.addFlashcard(front.toString(), back.toString())

                    val numcards = flashcards.sizeOfDeck
                    Log.w("MainActivity", "UPDATE THE VIEW WITH THE SERVER FLASHCARDS: "+numcards)
                    numCardsText.text = "Number of Cards: " + numcards

                } else {
                    go = false
                }

            }.addOnFailureListener{
                Log.w("MainActivity", "Error getting data", it)
                go = false
            }

            i += 1
        }
    }

    // update flashcards will update the model based on the view
    fun updateFlashcards() {
        // get the question and answer from the view
        var question = questionButton.text.toString()
        var answer = answerButton.text.toString()

        // add the flashcard to the model
        flashcards.addFlashcard(question, answer)

        // update the firebase server
        postServerFlashcards(question, answer)
    }

    // if the question and answer EditText views are not null or empty add their strings to the flashcard hashmap with currentFlashcardIndex += 1
    fun addNewCard(view : View) {
        // if the question and answer are not empty
        if (!questionButton.text.toString().isEmpty() && !answerButton.text.toString().isEmpty()) {
            // update the flashcards
            updateFlashcards()

            // update the view
            updateView()
        }
    }

    // Check if the the flashcards hashmap is not empty and if it is not finish() and then transition to MainActivity
    fun doneAdding ( v : View) {
        Log.w("Flashcards", "Called Done! Check if we have any cards before finishing.")

        // if the flashcards hashmap is not empty
        if (flashcards.sizeOfDeck > 0) {

            // Test finish
            // TODO: Fix that we completely loose our array of cards when we finish b/c it will do an onCreate call.
            finish()

            // go to DataActivity
            var myIntent : Intent = Intent( this, LearnCards::class.java )

            // push the intent onto the stack
            this.startActivity( myIntent )

            // transition
            overridePendingTransition( R.anim.slide_from_left, 0 )
        }
    }

    companion object {
        var flashcards : Flashcards = Flashcards()
    }
}