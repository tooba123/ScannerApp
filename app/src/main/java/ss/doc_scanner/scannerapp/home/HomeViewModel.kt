package ss.doc_scanner.scannerapp.home

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import ss.doc_scanner.scannerapp.util.Constants.Companion.OPTION_CAMERA
import ss.doc_scanner.scannerapp.util.Constants.Companion.OPTION_GALLERY
import ss.doc_scanner.scannerapp.util.FileHelper
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class HomeViewModel(var app : Application) : AndroidViewModel(app), HomeViewModelListener {

    var currentImagePath = ""
    override fun onHomeViewModelListenerLoaded(view: HomeView) {
        view.displayPhotoChooserDialog()
    }

    override fun onBtnAddClicked(view: HomeView) {
        view.displayPhotoChooserDialog()
    }

    override fun onPhotoChooserOptionSelected(option: Int, view: HomeView) {
        when(option){
            OPTION_GALLERY -> takeImageFromGallery(view)

            OPTION_CAMERA -> takeImageFromCamera(view)

        }
    }

    override fun onGalleryPermissionsGranted(view: HomeView) {
        view.takeImageFromGallery()
    }

    override fun onGalleryImageResultReceived(uri: Uri?, view: HomeView) {
        var path = FileHelper.getFilePath(uri!!, app.applicationContext)
        view.launchImageCustomizeActivity(path)
    }

    override fun onCameraPermissionsGranted(view: HomeView) {
        view.takeImageFromCamera(createFile())
    }

    private fun takeImageFromGallery(view: HomeView){
        view.checkGalleryPermissions()
    }

    private fun takeImageFromCamera(view : HomeView){
        view.checkCameraPermissions()
    }

    private fun createFile() : File {
        var timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imgFileName = "JPEG_" + "temp" +".jpg"
        //var path  = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/temp_dir" + "/"+ imgFileName +".jpg";
        var dirPath =  "/temp_dir/"

        var dir = File(getApplication<Application>().applicationContext.filesDir, dirPath)
        //var dir =getApplication<Application>().applicationContext.getExternalFilesDir(null)

        var file = File(dir, imgFileName)
        if(!file.parentFile.exists()){
            file.parentFile.mkdirs()
        }

        if(!file.exists()){
            file.createNewFile();
        }

        currentImagePath = file.absolutePath
        return file;
    }
}

interface HomeViewModelListener {
    public fun onHomeViewModelListenerLoaded(view : HomeView)

    public fun onBtnAddClicked(view : HomeView)

    public fun onPhotoChooserOptionSelected(option : Int, view : HomeView)

    public fun onGalleryPermissionsGranted(view : HomeView)

    public fun onGalleryImageResultReceived(uri : Uri?, view : HomeView)

    public fun onCameraPermissionsGranted(view : HomeView)
}