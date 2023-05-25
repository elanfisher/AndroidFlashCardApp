package com.example.flashcardapp

import android.util.Log

class Flashcards {
    // declare variables
    // array of flashcards with key as question and value as answer
    private var flashcards : ArrayList<Card>
    var sizeOfDeck : Int
    private var currentFlashcardIndex : Int = 0
    var init : Boolean = false

    // constructor
    constructor() {
        flashcards = ArrayList<Card>()
        sizeOfDeck = 0
        currentFlashcardIndex = 0
    }

    // getters and setters
    fun getFlashcards() : ArrayList<Card> {
        return flashcards
    }

    fun getCurrentFlashcardIndex() : Int {
        return currentFlashcardIndex
    }

    fun setCurrentFlashcardIndex(currentFlashcardIndex : Int) {
        this.currentFlashcardIndex = currentFlashcardIndex
    }

    // function to add a flashcard to the hashmap
    fun addFlashcard(question : String, answer : String) {
        // create a new card object
        var newCard = Card(question, answer)

        // add the card to the hashmap
        flashcards.add(newCard)

        // increment the current flashcard index
        sizeOfDeck += 1
        Log.w("Flashcards", "Added card - Sizeofdeck: $sizeOfDeck")
    }

    // function to remove a flashcard from the hashmap
    fun removeFlashcard(index : Int) {
        // remove the flashcard at that index
        flashcards.removeAt(index)
        sizeOfDeck -= 1
        Log.w("Flashcards", "Removed card - Sizeofdeck: $sizeOfDeck")
    }

    // function to get the next flashcard
    fun getNextFlashcard() : Card {
        Log.w("Flashcards", "[GetNextFlashcard] currentFlashcardIndex $currentFlashcardIndex")

        if (currentFlashcardIndex >= 0 && sizeOfDeck > 0 && currentFlashcardIndex < sizeOfDeck) {
            Log.w("Flashcards","Get Flashcard: "+ flashcards[currentFlashcardIndex].getQuestion())

            currentFlashcardIndex += 1

            // return the next flashcard
            return flashcards[currentFlashcardIndex - 1]!!
        } else {
            Log.w("Flashcards","Cannot get flashcard: "+ currentFlashcardIndex.toString())
            return Card("GET NEXT FLASHCARD CREATED ME", "I AM INVALID")
        }
    }

    // inner class to define a card
    inner class Card {
        // declare variables
        private var question : String
        private var answer : String

        // constructor
        constructor(question : String, answer : String) {
            this.question = question
            this.answer = answer
        }

        // getters and setters
        fun getQuestion() : String {
            return question
        }

        fun setQuestion(question : String) {
            this.question = question
        }

        fun getAnswer() : String {
            return answer
        }

        fun setAnswer(answer : String) {
            this.answer = answer
        }

        override fun toString(): String {
            return question + " : " + answer
        }
    }
}
