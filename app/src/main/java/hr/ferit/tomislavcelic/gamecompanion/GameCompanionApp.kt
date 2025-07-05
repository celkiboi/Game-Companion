package hr.ferit.tomislavcelic.gamecompanion

import android.app.Application
import android.content.Context
import com.google.firebase.FirebaseApp

class GameCompanionApp : Application() {

    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
        appContext = applicationContext
    }

    companion object {
        lateinit var appContext: Context
            private set
    }
}
