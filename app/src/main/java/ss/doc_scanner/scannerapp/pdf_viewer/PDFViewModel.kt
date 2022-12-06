package ss.doc_scanner.scannerapp.pdf_viewer

import android.app.Application
import androidx.lifecycle.AndroidViewModel

class PDFViewModel(app : Application) : AndroidViewModel(app) , PDFViewListener{

    override fun onViewLoaded(view: PDFView) {
       showPDFFile(view)
    }

    private fun showPDFFile(view : PDFView){
        var path = view.getPDFFilePath()
        if(path != null){
            view.showPDFFile(path)
        }
    }
}

interface PDFViewListener{

    public fun onViewLoaded(view : PDFView)


}