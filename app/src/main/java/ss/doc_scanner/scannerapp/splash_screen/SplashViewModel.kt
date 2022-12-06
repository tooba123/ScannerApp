package ss.doc_scanner.scannerapp.splash_screen

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel

class SplashViewModel(app: Application) : AndroidViewModel(app) , SplashViewModelListener{

    override fun onSplashViewModelListenerLoaded(view : SplashView) {
        Handler(Looper.getMainLooper())
            .postDelayed(Runnable {
                          view.launchMainActivity()
        }, 3000)
    }


}

interface SplashViewModelListener{

    public fun onSplashViewModelListenerLoaded(view: SplashView)
}