package ss.doc_scanner.scannerapp.FilesList

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ss.doc_scanner.scannerapp.data.db.DBManager
import ss.doc_scanner.scannerapp.data.db.model.File


class FileListViewModel(app : Application) : AndroidViewModel(app) , FileListViewListener{

    lateinit var files : ArrayList<File>
    var dbManager : DBManager = DBManager()
    var context =  getApplication<Application>().applicationContext



    override fun onViewLoaded(view: FileListView) {
        var fileLiveData = loadFiles()
        view.loadFilesInView(fileLiveData, false)
    }

    override fun onListItemBtnViewClicked(path: String, view: FileListView) {
        var uri = FileProvider.getUriForFile(context, ss.doc_scanner.scannerapp.BuildConfig.APPLICATION_ID + ".provider", java.io.File(path))
        view.showFileInAnotherApp(uri)
    }

    override fun onListItemBtnDeleteClicked(file: File, onFileDeleted: () -> Unit) {
        deleteFile(file, onFileDeleted)
    }

    override fun onRefreshData(view : FileListView, shouldHighlightLastItem: Boolean) {
        view.showCircularLoaderDialog()
        var fileLiveData = loadFiles()
        view.loadFilesInView(fileLiveData, shouldHighlightLastItem)
    }

    private fun deleteFile(file: File, onFileDeleted: () -> Unit){
        Thread(Runnable {
            var localFile = java.io.File(file.path)
            if(localFile.delete()){
                dbManager.deleteFile(context, file)
            }

            Handler(Looper.getMainLooper()).post(Runnable {
                onFileDeleted()
            })
        }).start()

    }

    private fun loadFiles() :LiveData<List<File>>{
        var filesLiveData = MutableLiveData<List<File>>()
        Thread(Runnable {
            var files = dbManager.getFilesSortByDate(context)

            Handler(Looper.getMainLooper())
                .post(Runnable {
                    filesLiveData.value = files
                })
        }).start()
        return filesLiveData
    }

    //Rough
    private fun initFiles(){
        files = ArrayList<File>()
        files.add(File("abc", 10, 1111, "11/09/2022", "path"))
        files.add(File("xyz", 7, 2222, "13/10/2022", "path"))
    }


}


interface FileListViewListener{
    public fun onViewLoaded(fileListView: FileListView)

    public fun onListItemBtnViewClicked(path : String, view : FileListView)

    public fun onListItemBtnDeleteClicked(file : File, onFileDeleted : () ->Unit)

    public fun onRefreshData(view : FileListView, shouldHighlightLastItem : Boolean)

}
