package ss.doc_scanner.scannerapp.main

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel

import com.google.android.gms.ads.admanager.AdManagerAdRequest
import ss.doc_scanner.scannerapp.util.Constants.Companion.OPTION_CAMERA
import ss.doc_scanner.scannerapp.util.Constants.Companion.OPTION_GALLERY
import ss.doc_scanner.scannerapp.util.FileHelper
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class MainViewModel(var app: Application) : AndroidViewModel(app), MainViewListener {

    var currentImagePath = ""


    private fun takePictureFromCamera(view : MainView){
        view.checkCameraPermissions()
    }

    private fun takePictureFromGallery(view : MainView){
        view.checkGalleryPermission()
    }

    private fun createFile() : File{
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

    override fun onMainViewListenerLoaded(view: MainView) {
        view.loadViews()
        view.loadListeners()
        view.loadAndShowInterstitialAd()
        var adManagerAdRequest = AdManagerAdRequest.Builder().build()
        view.loadBannerAdd(adManagerAdRequest)
    }



    override fun onButtonPlusClicked(view : MainView) {
        view.displayPhotoOptionChooserDialog()
    }


    override fun onPhotoChooserOptionSelected(option: Int, view : MainView) {
        when(option){
            OPTION_GALLERY ->  {
                takePictureFromGallery(view)
            }
            OPTION_CAMERA -> {
                takePictureFromCamera(view)
            }
        }
    }

    override fun onCameraPermissionGranted(view : MainView){
        view.takePictureFromCamera(createFile())
    }

    override fun onGalleryPermissionGranted(view: MainView) {
        view.takePictureFromGallery()
    }

    override fun onCameraImageCreated(view : MainView){
        view.launchImageCustomizeActivity(currentImagePath)
    }

    override fun onImageUriReceived(uri: Uri, view : MainView) {
        var path = FileHelper.getFilePath(uri, app.applicationContext)
       view.launchImageCustomizeActivity(path)

    }

}

public interface MainViewListener{
    public fun onMainViewListenerLoaded(view: MainView)

    public fun onButtonPlusClicked(view : MainView)

    public fun onPhotoChooserOptionSelected(option : Int, view : MainView)

    public fun onCameraPermissionGranted(view : MainView)

    public fun onGalleryPermissionGranted(view : MainView)

    public fun onCameraImageCreated(view : MainView)

    public fun onImageUriReceived(uri : Uri, view : MainView);


}