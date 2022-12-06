package ss.doc_scanner.scannerapp.pdf_viewer

import android.os.Bundle
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import ss.doc_scanner.scannerapp.R


class PDFViewActivity : AppCompatActivity(), PDFView {

    lateinit var webView : WebView
    companion object{
        var KEY_PDF_FILE_PATH = "key_pdf_file_path"
    }
    lateinit var pdfViewListener : PDFViewListener

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)


        setContentView(R.layout.layout_pdf_view)
        init()
        pdfViewListener.onViewLoaded(this@PDFViewActivity)

    }

    private fun init(){
        var PDFViewModel = ViewModelProvider(this).get(PDFViewModel::class.java)
        pdfViewListener = PDFViewModel

        initView()
        initListener()
    }

    private fun initView(){
        webView = findViewById(R.id.wv_pdf_view)
    }

    private fun initListener(){

    }



    override fun getPDFFilePath() : String?{
        var filePath : String? = null
        if(intent.hasExtra(KEY_PDF_FILE_PATH)){
            filePath = intent.getStringExtra(KEY_PDF_FILE_PATH).toString()
        }
        return filePath
    }

    override fun showPDFFile(path : String) {
        webView.webViewClient = object : WebViewClient(){
            override fun shouldOverrideUrlLoading(view: WebView, url: String?): Boolean {
                view.loadUrl(url!!)
                return true
            }
        }
        webView.settings.setSupportZoom(true)
        webView.settings.javaScriptEnabled = true
        webView.settings.allowFileAccess = true
        webView.settings.allowContentAccess = true
        webView.settings.domStorageEnabled = true
        webView.settings.pluginState = WebSettings.PluginState.ON
        webView.webChromeClient = WebChromeClient()
        webView.loadUrl(path)

    }
}