package ss.doc_scanner.scannerapp.app

import android.app.Application
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener

class ScannerApplication : Application() {


    override public fun onCreate() {
        super.onCreate()
        initializeAdMobSDK()
    }

    private fun initializeAdMobSDK(){
        MobileAds.initialize(this, OnInitializationCompleteListener {

        })
    }


}