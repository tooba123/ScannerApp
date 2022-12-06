package ss.doc_scanner.scannerapp.main

import com.google.android.gms.ads.AdRequest
import java.io.File


interface MainView {

    public  fun loadViews();

    public  fun loadListeners();

    public fun displayPhotoOptionChooserDialog()

    public fun takePictureFromGallery()

    public fun  takePictureFromCamera(file : File)

    public fun launchImageCustomizeActivity(imgPath : String)

    public fun checkCameraPermissions()

    public fun checkGalleryPermission()

    public fun loadAndShowInterstitialAd();

    public fun loadBannerAdd(adRequest: AdRequest);


}