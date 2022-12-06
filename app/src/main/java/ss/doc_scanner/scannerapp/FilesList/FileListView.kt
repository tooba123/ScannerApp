package ss.doc_scanner.scannerapp.FilesList

import android.net.Uri
import androidx.lifecycle.LiveData
import ss.doc_scanner.scannerapp.data.db.model.File


interface FileListView {

    public fun loadFilesInView(filesLiveData: LiveData<List<File>>, shouldHighlightLastItem : Boolean);

    public fun launchPDFViewerActivity(path : String)

    public fun showFileInAnotherApp(uri : Uri)

    public fun showCircularLoaderDialog()
}