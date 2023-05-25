package com.example.flashcardapp

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.InitializationStatus
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class DoneActivity : AppCompatActivity() {

    // initialize every View
    lateinit var msgTV : TextView
    lateinit var restartButon : Button
    lateinit var newCardsButton : Button
    lateinit var uri : Uri

    var questionSide = true
    lateinit var currentCard : Flashcards.Card

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.done)

        // find the views
        msgTV = findViewById(R.id.msg)
        restartButon = findViewById(R.id.restart)
        newCardsButton = findViewById(R.id.new_cards)

        //FOR THE AD
        var initializer : AdInitializer = AdInitializer()
        MobileAds.initialize( this, initializer )

        // build the Adview
        var adView: AdView = AdView( this )
        var adSize : AdSize = AdSize( AdSize.FULL_WIDTH, AdSize.AUTO_HEIGHT )
        adView.setAdSize( adSize )
        var adUnitId : String = "ca-app-pub-3940256099942544/6300978111"
        adView.adUnitId = adUnitId

        // build the AdRequest
        var builder : AdRequest.Builder = AdRequest.Builder( )
        builder.addKeyword( "workout" )
        builder.addKeyword( "fitness" )
        var request : AdRequest = builder.build()

        // add adView to LinearLayout
        var adLayout : LinearLayout = findViewById<LinearLayout>( R.id.ad_view )
        adLayout.addView( adView )

        // load the ad
        try {
            Log.w("MainActivity", "Trying to load ad")
            adView.loadAd( request )
            Log.w("MainActivity", "Ad loaded fine!")

        } catch( e : Exception ) {
            Log.w( "MainActivity", "Ad failed tom load" )
        }

        // Email stuff
        var file : File = createFile()
        uri = FileProvider.getUriForFile(this, "com.example.flashcardapp.myprovider", file)
        //sendEmail()

        // update the view based on the model
        updateView()
    }

    fun createFile() : File {
        var sdf : SimpleDateFormat = SimpleDateFormat("yyyyMMdd_HHmmss")
        var timestamp : String = sdf.format(Date())
        var dir : File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile("Image" + timestamp, ".png", dir)
    }

    fun sendEmail() {
        var intent : Intent = Intent(Intent.ACTION_SEND)
        intent.setType("text/octet-stream")
        intent.putExtra(Intent.EXTRA_SUBJECT, "Congrats on flashcards! 2")
        var recipients : Array<String> = arrayOf("elanfisherj@gmail.com", "ejfisher@umd.edu", "kevin.liao2003@gmail.com")
        intent.putExtra(Intent.EXTRA_EMAIL, recipients)
        intent.putExtra(Intent.EXTRA_STREAM, uri)

        startActivity(Intent.createChooser(intent, "Pick One"))
    }

    override fun onStart() {
        super.onStart()

        Log.w("LearnCards", "Inside LearnCards onStart")

        updateView()
    }

    // update view will update the view based on the model
    fun updateView() {
        Log.w("DoneActivity", "Inside LearnCards updateView")
    }

    // add the onClick methods from done.xml
    fun addCards( v : View ) {
        // reset the current index
        MainActivity.flashcards.setCurrentFlashcardIndex(0)

        Log.w("DoneActivity", "Email sent fine!")

        // Test to pop this off the stack
        finish()

        // set the intent to MainActivity
        var myIntent : Intent = Intent( this, MainActivity::class.java )

        // push the intent onto the stack
        this.startActivity( myIntent )

        // transition
        overridePendingTransition( R.anim.fade_in_and_scale, 0 )
    }

    fun restart ( v : View ) {
        // go back to LearnCards if we have them
        if( MainActivity.flashcards.sizeOfDeck > 0 ) {
            // reset the current index
            MainActivity.flashcards.setCurrentFlashcardIndex(0)

            // TODO: Fix finish when restarting, load another on the stack.
            Log.w("DoneActivity", "Finish done activity to go back to MainActivity.")
            finish()

            var myIntent : Intent = Intent( this, LearnCards::class.java )
            // push the intent onto the stack
            this.startActivity( myIntent )
            // transition
            overridePendingTransition( R.anim.fade_in_and_scale, 0 )
        }
    }

    class AdInitializer : OnInitializationCompleteListener {
        override fun onInitializationComplete(p0: InitializationStatus) {
            Log.w( "MainActivty", "ad initialization complete" )
        }
    }
}