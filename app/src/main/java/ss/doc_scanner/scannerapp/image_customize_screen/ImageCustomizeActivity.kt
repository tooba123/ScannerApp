package ss.doc_scanner.scannerapp.image_customize_screen

import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.RelativeLayout
import android.widget.SeekBar
import android.widget.TextView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.view.doOnLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.canhub.cropper.*
import ss.doc_scanner.scannerapp.adapter.FilterViewListener
import ss.doc_scanner.scannerapp.util.dialog.CircularBarDialog
import ss.doc_scanner.scannerapp.util.dialog.DownloadImagePDFDialog
import ss.doc_scanner.scannerapp.util.dialog.PDFDialogListener
import ss.doc_scanner.scannerapp.R
import ss.doc_scanner.scannerapp.adapter.FilterViewAdapter
import ss.doc_scanner.scannerapp.main.MainActivity
import ss.doc_scanner.scannerapp.util.Constants.Companion.KEY_IMAGE_FILE_PATH


class ImageCustomizeActivity : AppCompatActivity(), ImageCustomizeView, FilterViewListener,
    PDFDialogListener {

    lateinit var imageView : AppCompatImageView
    lateinit var ImageCustomizeViewListener  : ImageCustomizeViewListener
    lateinit var sbImageRepositiong  : SeekBar
    lateinit var btnNext : AppCompatImageButton
    lateinit var cropImage : ActivityResultLauncher<CropImageContractOptions>
    lateinit var btnDone : AppCompatImageButton
    lateinit var downloadImagePDFDialog : DownloadImagePDFDialog
    lateinit var circularDialog: CircularBarDialog
    lateinit var rvFilterButtonView : RecyclerView
    lateinit var rvSeekBar : RelativeLayout
    lateinit var tvScrollDescription : TextView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_image_customize)
        ImageCustomizeViewListener = ViewModelProvider(this).get(ImageCustomizeViewModel::class.java)
        ImageCustomizeViewListener.onImageCustomizeViewModelInitialized(this@ImageCustomizeActivity)
    }

    private fun initView(){
        imageView = findViewById(R.id.iv_image)
        sbImageRepositiong = findViewById(R.id.sb_img_repositoning)
        btnNext = findViewById(R.id.btn_next)
        btnDone = findViewById(R.id.btn_done)
        circularDialog = CircularBarDialog()
        rvSeekBar = findViewById(R.id.rv_seek_bar)
        rvFilterButtonView = findViewById(R.id.rv_filter_button_view)
        tvScrollDescription = findViewById(R.id.tv_scroll_description)


        var layoutManager = LinearLayoutManager(applicationContext, LinearLayoutManager.HORIZONTAL, false)
        rvFilterButtonView.layoutManager = layoutManager
        setFilterButtonsAdapter()

        imageView.doOnLayout {
            ImageCustomizeViewListener.onViewLoaded(this@ImageCustomizeActivity, it.measuredWidth, it.measuredHeight)
        }
    }

    private fun initListeners(){
        sbImageRepositiong.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {
                ImageCustomizeViewListener.onSeekBarProgressChanged(p1, imageView.drawable.intrinsicHeight, imageView.drawable.intrinsicWidth, imageView.matrix, this@ImageCustomizeActivity)
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {
            }

        })

        cropImage = registerForActivityResult(CropImageContract()){ result ->
            ImageCustomizeViewListener.onCropImageResultReceived(result,this@ImageCustomizeActivity)
        }

        btnNext.setOnClickListener {
            ImageCustomizeViewListener.onButtonNextClicked(this@ImageCustomizeActivity, getBitmapFromView(imageView))
            //var bitmapDrawable = imageView.getDrawable() as BitmapDrawable
            //ImageCustomizeViewListener.onButtonNextClicked(this@ImageCustomizeActivity, bitmapDrawable.bitmap)
        }

        btnDone.setOnClickListener {
            var bitmapDrawable = imageView.getDrawable() as BitmapDrawable
            ImageCustomizeViewListener.onButtonDoneClicked(this@ImageCustomizeActivity, getBitmapFromView(imageView))
        }
    }

    override fun loadView() {
        initView()
        initListeners()
    }

    override fun getImagePath() : String?{
        if(intent.hasExtra(KEY_IMAGE_FILE_PATH)){
            return intent.getStringExtra(KEY_IMAGE_FILE_PATH).toString()
        }
        return null
    }

    override fun showImage(imageLiveData: LiveData<Bitmap>?) {
       imageLiveData!!.observe(this, Observer { bitmap : Bitmap ->
           if(circularDialog.isResumed)
                circularDialog.dismiss()
           imageView.setImageBitmap(bitmap)
       })
    }

    override fun updateImageMatrix(matrix: Matrix) {
        imageView.imageMatrix = matrix
        imageView.invalidate()

    }

    override fun launchCropActivity(imageUri : Uri) {
        cropImage.launch(options(uri = imageUri){
            setGuidelines(CropImageView.Guidelines.ON)
            setOutputCompressFormat(Bitmap.CompressFormat.PNG)
        })
    }



    override fun showCroppedImage(uri: Uri) {
        circularDialog.dismiss()
        imageView.setImageURI(uri)
        imageView.invalidate()
        sbImageRepositiong.setProgress(45)
        sbImageRepositiong.invalidate()
        btnNext.visibility = View.GONE
        btnDone.visibility = View.VISIBLE
        sbImageRepositiong.visibility = View.GONE
    }


    override fun showCroppedImage(filteredBitmapliveData : LiveData<Bitmap>) {
        filteredBitmapliveData.observe(this, Observer { blackWhiteColoredBitmap : Bitmap ->
            circularDialog.dismiss()
            imageView.setImageBitmap(blackWhiteColoredBitmap)
            imageView.invalidate()
            sbImageRepositiong.setProgress(45)
            sbImageRepositiong.invalidate()
            btnNext.visibility = View.GONE
            btnDone.visibility = View.VISIBLE
            sbImageRepositiong.visibility = View.GONE
            rvSeekBar.visibility = View.GONE
            rvFilterButtonView.visibility = View.VISIBLE
            tvScrollDescription.visibility = View.INVISIBLE
        })
    }

    override fun showSavedImagePDFMessage(outPutImagePathLiveData: LiveData<String>) {
        outPutImagePathLiveData.observe(this, Observer {
            if(downloadImagePDFDialog !=null){
                var path = it
                var message = "File is downloaded at following location : "
                downloadImagePDFDialog.onImageSavedToPDF(message, path)
            }
        })
    }

    override fun showImageConversionToPDFDialog() {
        downloadImagePDFDialog = DownloadImagePDFDialog();
        downloadImagePDFDialog.pdfDialogListener = this
        downloadImagePDFDialog.show(supportFragmentManager, "image_conversion_to_pdf")
    }

    override fun showCicrularDialog() {
        circularDialog.show(supportFragmentManager, "circular_dialog")
    }

    override fun setFilterButtonsAdapter() {
        var filterViewAdapter = FilterViewAdapter(applicationContext, this@ImageCustomizeActivity)
        rvFilterButtonView.adapter = filterViewAdapter
        filterViewAdapter.notifyDataSetChanged()
    }

    override fun resumeMainActivity() {
        var intent = Intent(this@ImageCustomizeActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
    }

    private fun getBitmapFromView(view : View) : Bitmap{
        var bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        var canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    override fun onFilterButtonClicked(filterType : Int, onFilteredImageReceived: (bitmap: Bitmap) -> Unit) {
        ImageCustomizeViewListener.onFilterButtonClicked(this@ImageCustomizeActivity, filterType, onFilteredImageReceived)
    }

    override fun onButtonCloseClicked() {
        ImageCustomizeViewListener.onPDFDialogBtnCloseClicked(this@ImageCustomizeActivity)
    }
}