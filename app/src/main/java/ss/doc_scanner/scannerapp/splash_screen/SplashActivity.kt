package ss.doc_scanner.scannerapp.splash_screen

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ss.doc_scanner.scannerapp.R
import ss.doc_scanner.scannerapp.main.MainActivity


class SplashActivity : AppCompatActivity(), SplashView {

    lateinit var splashViewModelListener : SplashViewModelListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_splash_screen)

        initView()
        initListener()

        splashViewModelListener.onSplashViewModelListenerLoaded(this@SplashActivity)


    }

    private fun initView(){

    }

    private fun initListener(){
        var splashViewModel = ViewModelProvider(this).get(SplashViewModel::class.java)
        splashViewModelListener = splashViewModel
    }

    override fun launchMainActivity() {
        startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        finish()
    }


}