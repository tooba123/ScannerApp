package ss.doc_scanner.scannerapp.home

import java.io.File

interface HomeView {

    public fun displayPhotoChooserDialog()

    public fun checkGalleryPermissions()

    public fun checkCameraPermissions()

    public fun takeImageFromGallery()

    public fun takeImageFromCamera(file : File)

    public fun launchImageCustomizeActivity(imgPath : String)

}