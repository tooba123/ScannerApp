package ss.doc_scanner.scannerapp.main

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager.widget.ViewPager

import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.admanager.AdManagerAdRequest
import com.google.android.gms.ads.admanager.AdManagerAdView
import com.google.android.gms.ads.admanager.AdManagerInterstitialAd
import com.google.android.gms.ads.admanager.AdManagerInterstitialAdLoadCallback
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton
import ss.doc_scanner.scannerapp.R
import ss.doc_scanner.scannerapp.home.HomeFragment
import ss.doc_scanner.scannerapp.image_customize_screen.ImageCustomizeActivity
import ss.doc_scanner.scannerapp.util.Constants.Companion.KEY_IMAGE_FILE_PATH
import ss.doc_scanner.scannerapp.util.callbacks.PhotoChooserListener
import ss.doc_scanner.scannerapp.util.dialog.PhotoChooserDialog
import java.io.File

class MainActivity : AppCompatActivity(), MainView, PhotoChooserListener {

    lateinit var vp : ViewPager

    lateinit var mainViewListener : MainViewListener
    lateinit var btnPlus : ExtendedFloatingActionButton
    lateinit var adManagerAdView : AdManagerAdView

    lateinit var cameraActivityLauncher : ActivityResultLauncher<Intent>;
    lateinit var galleryActivityLauncher : ActivityResultLauncher<Intent>
    lateinit var cameraPermissionLauncher : ActivityResultLauncher<String>;
    lateinit var galleryPermissionLauncher : ActivityResultLauncher<Array<String>>

    lateinit var fileListFragmentListener: ss.doc_scanner.scannerapp.FilesList.FileListFragment.MainViewListener



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java);
        mainViewListener = mainViewModel
        mainViewListener.onMainViewListenerLoaded(this)
    }


    class MainFragmentPagerAdapter(fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

        var ItemsNum  : Int = 2
        override fun getCount(): Int {
            return ItemsNum
        }

        override fun getItem(position: Int): Fragment {

            return when(position){
                0 -> HomeFragment.newInstance(R.layout.layout_home)
                1 -> ss.doc_scanner.scannerapp.FilesList.FileListFragment.newInstance(R.layout.layout_converted_files)
                else -> HomeFragment.newInstance(R.layout.layout_home)
            }
        }

        override fun getPageTitle(position: Int): CharSequence? {

            return when(position){
                0 -> "Home"
                1 -> "Converted Files"
                else -> "Home"
            }
        }
    }

    public fun addFileListFragmentListener(fileListFragmentListener: ss.doc_scanner.scannerapp.FilesList.FileListFragment.MainViewListener){
        this.fileListFragmentListener = fileListFragmentListener
    }
    override fun loadViews() {
        vp = findViewById(R.id.vp);
        btnPlus = findViewById(R.id.btn_plus)
        adManagerAdView = findViewById(R.id.ad_manager_ad_view)
    }


    override fun loadListeners() {
        var mainPagerAdapter = MainFragmentPagerAdapter(supportFragmentManager);
        vp.adapter = mainPagerAdapter

        btnPlus.setOnClickListener {

            mainViewListener.onButtonPlusClicked(this@MainActivity)
        }

        cameraActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback {
            if(it.resultCode == Activity.RESULT_OK){

                mainViewListener.onCameraImageCreated(this@MainActivity)

            }
        })

        galleryActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback { result ->
            if(result.resultCode == RESULT_OK){
                var data = result.data
                if(data!=null){
                    mainViewListener.onImageUriReceived(data.data!!, this@MainActivity)
                }
            }
        })

        cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission(), ActivityResultCallback { granted: Boolean ->
            if(granted){
                mainViewListener.onCameraPermissionGranted(this@MainActivity)
            }
        })

        galleryPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions(), ActivityResultCallback{
            var allPermissionsGranted = true
            for ((key, permissionGranted) in it){
                if(!permissionGranted){
                    allPermissionsGranted = false
                    break
                }
            }
            if(allPermissionsGranted){
                mainViewListener.onGalleryPermissionGranted(this@MainActivity)
            }
        })
    }

    override fun displayPhotoOptionChooserDialog() {
        var dialog = PhotoChooserDialog(this)
        dialog.show(supportFragmentManager, "photoChooserDialog")
    }

    override fun takePictureFromGallery() {
        var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryActivityLauncher.launch(intent)
    }

    override fun takePictureFromCamera(file: File) {
        var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        var uri = FileProvider.getUriForFile(this, ss.doc_scanner.scannerapp.BuildConfig.APPLICATION_ID +".provider", file)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION and Intent.FLAG_GRANT_READ_URI_PERMISSION)

        cameraActivityLauncher.launch(intent);

    }

    override fun launchImageCustomizeActivity(imgPath: String) {
        var intent = Intent(this@MainActivity, ImageCustomizeActivity::class.java);
        intent.putExtra(KEY_IMAGE_FILE_PATH, imgPath)
        startActivity(intent)
    }

    override fun checkCameraPermissions() {
        if(ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED){
            mainViewListener.onCameraPermissionGranted(this@MainActivity)
        } else{
            cameraPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }
    }

    override fun checkGalleryPermission() {
        var permissionGranted = true
        var permissionSet = arrayOf( Manifest.permission.READ_EXTERNAL_STORAGE,  Manifest.permission.WRITE_EXTERNAL_STORAGE)
        for(permission in permissionSet){
            if(ContextCompat.checkSelfPermission(applicationContext, permission) != PackageManager.PERMISSION_GRANTED){
                permissionGranted = false
            }
        }

        if(!permissionGranted){
            galleryPermissionLauncher.launch(permissionSet)
        } else {
            mainViewListener.onGalleryPermissionGranted(this@MainActivity)
        }
    }

    override fun loadAndShowInterstitialAd() {
        var adRequest = AdManagerAdRequest.Builder().build()

        AdManagerInterstitialAd.load(this, getString(R.string.ad_unit_interstitial_id), adRequest, object : AdManagerInterstitialAdLoadCallback(){

            override fun onAdLoaded(interstitialAd: AdManagerInterstitialAd) {
                interstitialAd.show(this@MainActivity)
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
            }
        })
    }

    override fun loadBannerAdd(adRequest: AdRequest) {
        adManagerAdView.loadAd(adRequest)
    }


    override fun onPhotoChooserOptionSelected(option: Int) {
        mainViewListener.onPhotoChooserOptionSelected(option, this@MainActivity)
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        vp.currentItem = 2
        fileListFragmentListener.onRefreshData()
    }
}