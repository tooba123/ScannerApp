package ss.doc_scanner.scannerapp.image_customize_screen

import android.graphics.Bitmap
import android.graphics.Matrix
import android.net.Uri
import androidx.lifecycle.LiveData

interface ImageCustomizeView {

    public fun loadView();

    public fun getImagePath() : String?

    public fun showImage(imageLiveData: LiveData<Bitmap>?)

    public fun updateImageMatrix(matrix : Matrix)

    public fun launchCropActivity(uri : Uri)

    public fun showCroppedImage(uri : Uri);

    public fun showCroppedImage(filteredBitmapliveData : LiveData<Bitmap>)

    public fun showSavedImagePDFMessage(outPutImagePathLiveData : LiveData<String>)

    public fun showImageConversionToPDFDialog()

    public fun showCicrularDialog()

    public fun setFilterButtonsAdapter()

    public fun resumeMainActivity()
}