package ss.doc_scanner.scannerapp.home

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import ss.doc_scanner.scannerapp.R
import ss.doc_scanner.scannerapp.image_customize_screen.ImageCustomizeActivity
import ss.doc_scanner.scannerapp.util.Constants.Companion.KEY_IMAGE_FILE_PATH
import ss.doc_scanner.scannerapp.util.callbacks.PhotoChooserListener
import ss.doc_scanner.scannerapp.util.dialog.PhotoChooserDialog

import java.io.File

class HomeFragment() : Fragment(), HomeView, PhotoChooserListener {

    var contentLayoutId : Int = R.layout.layout_home
    lateinit var homeViewModelListener : HomeViewModelListener

    lateinit var btnAdd : AppCompatButton

    lateinit var galleryPermissionLauncher : ActivityResultLauncher<Array<String>>
    lateinit var cameraPermissionLauncher: ActivityResultLauncher<Array<String>>
    lateinit var galleryActivityResultLauncher : ActivityResultLauncher<Intent>
    lateinit var cameraActivityLauncher : ActivityResultLauncher<Intent>


    companion object {
        fun newInstance(contentLayoutId : Int) : HomeFragment{
            var fragment = HomeFragment();
            fragment.contentLayoutId = contentLayoutId
            return fragment
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view : View = initView(inflater)
        initListener()
        return view
    }

    private fun initView(layoutInflater: LayoutInflater) : View {
        var view = layoutInflater.inflate(contentLayoutId, null, false)
        btnAdd = view.findViewById<AppCompatButton>(R.id.rv_add)
        return view
    }

    private fun initListener(){

        var homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        homeViewModelListener = homeViewModel

        btnAdd.setOnClickListener{
            homeViewModelListener.onBtnAddClicked(this@HomeFragment)
        }

        galleryPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions(), ActivityResultCallback {
            var permissionsGranted = true;

            for((key, permissionGranted) in it){
                if(!permissionGranted){
                    permissionsGranted = false
                    break
                }
            }
            if(permissionsGranted){
                homeViewModelListener.onGalleryPermissionsGranted(this@HomeFragment)
            }
        })

        galleryActivityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback {
            if(it.resultCode == RESULT_OK){
                var intent = it.data
                homeViewModelListener.onGalleryImageResultReceived(intent!!.data, this@HomeFragment)
            }
        })

        cameraPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions(), ActivityResultCallback {
            var permissionsGranted = true
            for((key, permissionGranted) in it){
                if(!permissionsGranted){
                    permissionsGranted = false
                    break
                }
            }
            if(permissionsGranted){
                homeViewModelListener.onCameraPermissionsGranted(this@HomeFragment)
            }
        })

        cameraActivityLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult(), ActivityResultCallback {

        })
    }

    override fun displayPhotoChooserDialog(){
       var photoChooserDialog = PhotoChooserDialog(this)
        photoChooserDialog.show(requireActivity().supportFragmentManager, "photo_chooser_dialog")
    }

    override fun checkGalleryPermissions(){
        var permissionGranted = true
        var permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        for(permission in permissions){
           if(ContextCompat.checkSelfPermission(requireContext(), permission) !=PackageManager.PERMISSION_GRANTED){
               permissionGranted = false
               break
           }
        }
        if(!permissionGranted){
            galleryPermissionLauncher.launch(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
        }

        else{
            homeViewModelListener.onGalleryPermissionsGranted(this@HomeFragment)
        }

    }

    override fun checkCameraPermissions() {
        var permissionGranted = true
        var permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        for(permission in permissions){
            if(ContextCompat.checkSelfPermission(requireContext(), permission) != PackageManager.PERMISSION_GRANTED){
                permissionGranted = false
                break
            }
        }

        if(!permissionGranted){
            cameraPermissionLauncher.launch(arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE))
        }

        else{
            homeViewModelListener.onCameraPermissionsGranted(this@HomeFragment)
        }
    }

    override fun takeImageFromGallery() {
        var intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        galleryActivityResultLauncher.launch(intent)
    }

    override fun takeImageFromCamera(file : File) {
        var intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        var uri = FileProvider.getUriForFile(requireContext(), ss.doc_scanner.scannerapp.BuildConfig.APPLICATION_ID +".provider", file)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION and Intent.FLAG_GRANT_READ_URI_PERMISSION)

        cameraActivityLauncher.launch(intent);
    }

    override fun launchImageCustomizeActivity(imgPath: String) {
        var intent = Intent(requireContext(), ImageCustomizeActivity::class.java)
        intent.putExtra(KEY_IMAGE_FILE_PATH, imgPath)
        startActivity(intent)
    }

    override fun onPhotoChooserOptionSelected(option: Int) {
        homeViewModelListener.onPhotoChooserOptionSelected(option, this@HomeFragment)
    }


}