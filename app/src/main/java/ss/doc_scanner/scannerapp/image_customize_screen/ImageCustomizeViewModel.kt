package ss.doc_scanner.scannerapp.image_customize_screen

import android.app.Application
import android.graphics.*
import android.os.Environment
import android.os.Handler
import android.os.Looper
import androidx.core.content.FileProvider
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.canhub.cropper.CropImageView
import java.io.File
import java.nio.file.Files.createFile
import java.text.SimpleDateFormat
import java.util.*
import android.net.Uri
import ss.doc_scanner.scannerapp.adapter.FilterViewAdapter.Companion.BLACK_WHITE
import ss.doc_scanner.scannerapp.adapter.FilterViewAdapter.Companion.COLORED
import ss.doc_scanner.scannerapp.adapter.FilterViewAdapter.Companion.GRAY_SCALE
import ss.doc_scanner.scannerapp.data.db.DBManager
import ss.doc_scanner.scannerapp.util.FileHelper


class ImageCustomizeViewModel(app : Application) : AndroidViewModel(app), ImageCustomizeViewListener {

    var mMatrix : Matrix? = null
    val context = getApplication<Application>().applicationContext
    val dbManager : DBManager = DBManager()
    var croppedImageUri : Uri? =  null
    var cachedBitmap : Bitmap? = null

    override fun onImageCustomizeViewModelInitialized(view: ImageCustomizeView) {
        view.loadView()

    }

    override fun onViewLoaded(view: ImageCustomizeView, width : Int, height : Int) {
        var imageLiveData = loadScaledImage(view, width, height)
        view.showImage(imageLiveData)
    }

    override fun onSeekBarProgressChanged(progress: Int, height: Int, width: Int, matrix: Matrix, view : ImageCustomizeView){
        var newImageMatrix = straigtenImage(progress, height, width, matrix)
        view.updateImageMatrix(newImageMatrix)

    }

    override fun onButtonNextClicked(view: ImageCustomizeView, bitmap: Bitmap) {
        var path = FileHelper.createFile("JPEG_modified_img.jpg", getApplication<Application>().applicationContext)
        FileHelper.writeBitmap(path, bitmap)
        var file = File(path)
        var uri = FileProvider.getUriForFile(context,  ss.doc_scanner.scannerapp.BuildConfig.APPLICATION_ID +".provider", file)
        view.launchCropActivity(uri)
    }

    /*override fun onCropImageResultReceived(result: CropImageView.CropResult, view : ImageCustomizeView) {
      if(result.isSuccessful){

         var colorMatrixColorFilter = createBrightnessAndContrast(1f, 1.5f)

          view.showCroppedImage(result.uriContent!!, colorMatrixColorFilter)
      } else{
          result.error
      }
    }*/
    override fun onCropImageResultReceived(result: CropImageView.CropResult, view : ImageCustomizeView) {
        if(result.isSuccessful){
            view.showCicrularDialog()
            croppedImageUri = result.uriContent!!
            //Added new line 31/10/2022

            var blackWhiteBitmapLiveData = callBlackAndWhiteFilter(croppedImageUri!!)
            view.showCroppedImage(blackWhiteBitmapLiveData)
        } else{
            result.error
        }
    }

    private fun callBlackAndWhiteFilter(uriContent: Uri) : LiveData<Bitmap>{
        var blackWhiteImageLiveData = MutableLiveData<Bitmap>();
        Thread(Runnable {
            var inputStream = context.contentResolver.openInputStream(uriContent!!)
            var croppedImageBitmap = BitmapFactory.decodeStream(inputStream)
            //var croppedImageBitmap = FileHelper.loadBitMap(imagePath)
            croppedImageBitmap = brightenImageIfDark(croppedImageBitmap!!)
            croppedImageBitmap = createGrayScaleImage(croppedImageBitmap!!)
            //croppedImageBitmap = applyBrightnessAndContrastFilter(croppedImageBitmap!!, 0.9f, 1.4f)
            var outputBitmap = applyBlackAndWhiteFilter(croppedImageBitmap!!)
            Handler(Looper.getMainLooper()).post(Runnable {
                blackWhiteImageLiveData.value = outputBitmap
                cachedBitmap = outputBitmap
            })
        }).start()
        return blackWhiteImageLiveData

    }

    private fun applyBlackAndWhiteFilter(bitmap: Bitmap): Bitmap{
      var width =   bitmap.width
      var height = bitmap.height
      var outputBitmap = Bitmap.createBitmap(width, height, bitmap.config)

      for(x in 1 .. width - 1){
          for (y in 1..height - 1){
             var pixel =  bitmap.getPixel(x, y)
              var a = Color.alpha(pixel)
              var r = Color.red(pixel)
              var g = Color.green(pixel)
              var b = Color.blue(pixel)
              var gray = (0.2989 * r + 0.5870 * g + 0.1140 * b)

              var colorString = ""
              if(gray >= 128){
                  colorString = "#FFFFFFFF"
                  gray = 255.toDouble()
              }
              else{
                  colorString = "#FF000000"
                  gray = 0.toDouble()
              }
              outputBitmap.setPixel(x,y,Color.parseColor(colorString))

          }
      }
      return outputBitmap

    }

    private fun brightenImageIfDark(bitmap: Bitmap) : Bitmap{
        var histogram = IntArray(256)

        for(w in 1 .. bitmap.width - 1){
            for (h in 1 ..bitmap.height - 1){
                var pixel = bitmap.getPixel(w,h)
                var r = Color.red(pixel)
                var g = Color.green(pixel)
                var b = Color.blue(pixel)

                var brightness = (0.2126*r + 0.7152*g +  0.0722*b).toInt()
                histogram[brightness]++
            }
        }

        var totalPixelsCount = bitmap.width * bitmap.height

        var darkPixelsCount = 0
        for(i in 1 .. 35){
            darkPixelsCount = darkPixelsCount + histogram.get(i)
        }

        //picture is dark brighten it
        //if(darkPixelsCount > totalPixelsCount*0.002)
        if(darkPixelsCount > totalPixelsCount*0.002)
        {
            return applyBrightnessAndContrastFilter(bitmap, 25f, 1f)
        }
        return bitmap
    }

    private fun callBrightnessAndContrastFilter(uriContent : Uri) : LiveData<Bitmap>{
        var bitmapLiveData = MutableLiveData<Bitmap>()
        Thread(Runnable {
            var inputStream = context.contentResolver.openInputStream(uriContent!!)
            var bitmap = BitmapFactory.decodeStream(inputStream)
            //var bitmap = FileHelper.loadBitMap(imagePath)
            //applyBrightnessAndContrastFilter(bitmap!!, 0.9f, 1.4f)
            bitmap = applyBrightnessAndContrastFilter(bitmap!!, 1f, 1f)

            Handler(Looper.getMainLooper()).post(Runnable {
                bitmapLiveData.value = bitmap
                cachedBitmap = bitmap
            })
        }).start()
        return bitmapLiveData
    }

    private fun callGrayScaleFilter(uriContent : Uri) : LiveData<Bitmap>{
         var bitmapLiveData = MutableLiveData<Bitmap>()
        Thread(Runnable {
            var inputStream = context.contentResolver.openInputStream(uriContent!!)
            var croppedImageBitmap = BitmapFactory.decodeStream(inputStream)
            //var croppedImageBitmap = FileHelper.loadBitMap(imagePath)
            var black_and_white_bitmap  = createGrayScaleImage(croppedImageBitmap!!)


            Handler(Looper.getMainLooper()).post(Runnable {
                bitmapLiveData.value = black_and_white_bitmap
                cachedBitmap = black_and_white_bitmap
            })
        }).start()

        return bitmapLiveData
    }

    private fun createGrayScaleImage(bitmap: Bitmap)  :Bitmap{
        var grayScaleBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
        var canvas = Canvas(grayScaleBitmap)
        var paint = Paint()
        var colorMatrix = ColorMatrix();
        colorMatrix.setSaturation(0f)
        paint.setColorFilter(ColorMatrixColorFilter(colorMatrix))
        canvas.drawBitmap(bitmap, 0f, 0f, paint)
        return grayScaleBitmap
    }

    override fun onButtonDoneClicked(view: ImageCustomizeView, bitmap : Bitmap) {
        view.showImageConversionToPDFDialog()
        croppedImageUri = null

        if(cachedBitmap != null){
            var outputFilePathLiveData = saveBitMapToPDF(cachedBitmap!!)
            view.showSavedImagePDFMessage(outputFilePathLiveData)
            cachedBitmap = null
        }
    }

    override fun onFilterButtonClicked(view : ImageCustomizeView, filterType : Int, onFilteredImageReceived :(bitmap  :Bitmap) -> Unit) {
        when(filterType){
            BLACK_WHITE ->
                if(croppedImageUri != null) {
                    view.showCicrularDialog()
                    var bitmapLiveData = callBlackAndWhiteFilter(croppedImageUri!!)
                    view.showImage(bitmapLiveData)
                }
            GRAY_SCALE ->

                if(croppedImageUri != null) {
                    view.showCicrularDialog()
                    var bitmapLiveData = callGrayScaleFilter(croppedImageUri!!)
                    view.showImage(bitmapLiveData)
                }
            COLORED ->
                if(croppedImageUri != null) {
                    view.showCicrularDialog()
                    var bitmapLiveData = callBrightnessAndContrastFilter(croppedImageUri!!)
                    view.showImage(bitmapLiveData)
                }
        }
    }

    override fun onPDFDialogBtnCloseClicked(view : ImageCustomizeView) {
        view.resumeMainActivity()
    }

    private fun saveBitMapToPDF(bitmap : Bitmap) : LiveData<String>{
        var outputFilePathLiveData = MutableLiveData<String>()
        Thread(Runnable {

            var path =  FileHelper.createFile("JPEG_modified_img.jpg", getApplication<Application>().applicationContext)
            FileHelper.writeBitmap(path, bitmap)

            var currentDate = Date()
            var timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(currentDate)
            var writeImagePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).absolutePath + "/" + timeStamp + ".pdf"

            var outputMediaFile = writeImageToPDF(path, writeImagePath, currentDate.time)

            //insert the file in Database

            var dateFormat = SimpleDateFormat("yyyy-MM-dd")

            var file = ss.doc_scanner.scannerapp.data.db.model.File(name = outputMediaFile.name,
                size =  outputMediaFile.length(), dateCreated = outputMediaFile.lastModified(),
                dateCreatedString = dateFormat.format(currentDate), outputMediaFile.absolutePath)

            dbManager.insertFile(context, file)

            var mainThreadHandler = Handler(Looper.getMainLooper())
            mainThreadHandler.post(Runnable {
                outputFilePathLiveData.value = outputMediaFile.absolutePath
            })
        }).start()

        return outputFilePathLiveData
    }

    private fun writeImageToPDF(readImageFilePath : String, writeImagePath : String, dateCreated : Long) : File{
        var outputMediaFile = FileHelper.writeAnImagetoPDF(writeImagePath, readImageFilePath, dateCreated)
        return outputMediaFile
    }

    /*private fun createBrightnessAndContrast(brightness : Float, contrast : Float) : ColorMatrixColorFilter{
        var  colorMatrix = ColorMatrix()

        var colorMatrixVals = arrayOf<Float>(contrast, 0f,0f,0f, brightness,
                                        0f, contrast, 0f,0f, brightness,
                                        0f, 0f, contrast, 0f, brightness,
                                        0f, 0f, 0f, 1f, 0f).toFloatArray()

        colorMatrix!!.set(colorMatrixVals)

        var colorMatrixColorFilter = ColorMatrixColorFilter(colorMatrix)
        return colorMatrixColorFilter
    }*/

    private fun applyBrightnessAndContrastFilter(bitmap : Bitmap, brightness : Float, contrast : Float) : Bitmap{
        var outputBitmap = Bitmap.createBitmap(bitmap,0, 0,  bitmap.width - 1, bitmap.height - 1)

        var  colorMatrix = ColorMatrix()

        var colorMatrixVals = arrayOf<Float>(
            contrast, 0f,0f,0f, brightness,
            0f, contrast, 0f,0f, brightness,
            0f, 0f, contrast, 0f, brightness,
            0f, 0f, 0f, contrast, brightness).toFloatArray()


        colorMatrix!!.set(colorMatrixVals)

        var colorMatrixColorFilter = ColorMatrixColorFilter(colorMatrix)

        var paint = Paint()
        paint.setColorFilter(colorMatrixColorFilter)
        var canvas = Canvas(outputBitmap)
        canvas.drawBitmap(outputBitmap, 0f,0f, paint)

        return outputBitmap
    }

    private fun straigtenImage(progress : Int, h : Int, w : Int, matrix : Matrix) : Matrix{
        var angle : Double = progress - 45.toDouble()
        var width : Double = w.toDouble()
        var height : Double = h.toDouble()
        if(width > height){
            width = h.toDouble()
            height = w.toDouble()
        }

        var a = Math.atan(height/width)

        var len1 = (width/2) / Math.cos(a - Math.abs(Math.toRadians(angle)))
        var len2 = Math.sqrt(Math.pow(width/2, 2.toDouble()) + Math.pow(height/2, 2.toDouble()))

        var scale = (len2/len1).toFloat()

        if(mMatrix == null){
            mMatrix = Matrix(matrix)
        }

        var newMatrix = Matrix(mMatrix)

        var newX = ((width/2) * (1-scale)).toFloat()
        var newY = ((height/2) * (1-scale)).toFloat()

        newMatrix.postScale(scale, scale)
        newMatrix.postTranslate(newX, newY)
        newMatrix.postRotate(angle.toFloat(), (width/2).toFloat(), (height/2).toFloat())

        return newMatrix

    }


    private fun loadScaledImage(view : ImageCustomizeView, width : Int, height : Int) : LiveData<Bitmap>? {
        var path = view.getImagePath()
        if(path == null){
                //dismiss progress bar
        }else{
            //rough line below
            /*path = "/storage/emulated/0/Download/20220924_110345.pdf"
            var file = File(path)

            if(file.exists()){
                print("file present")
            }*/
            var imageLiveData = LoadScaledBitmapImage(path, width, height)
            return imageLiveData
        }
        return null;
    }

    private fun LoadScaledBitmapImage(path : String, width : Int, h : Int) : LiveData<Bitmap>{
        var imageLiveData  = MutableLiveData<Bitmap>()
        Thread(Runnable {

            var imageBitmap = FileHelper.loadBitMap(path)

            var height = (imageBitmap!!.height / (imageBitmap.width * 1.0f) * width)
            imageBitmap = Bitmap.createScaledBitmap(imageBitmap!!, width, height.toInt(), false)
            if(imageBitmap != null){
                Handler(Looper.getMainLooper()).post(Runnable {
                    imageLiveData.value = imageBitmap
                })
            }
        }).start()

        return imageLiveData
    }


}

interface ImageCustomizeViewListener{

    public fun onImageCustomizeViewModelInitialized(view : ImageCustomizeView)

    public fun onViewLoaded(view: ImageCustomizeView, width : Int, height : Int)

    public fun onSeekBarProgressChanged(progress : Int, height : Int, width : Int, matrix : Matrix, view : ImageCustomizeView)

    public fun onButtonNextClicked(view : ImageCustomizeView, bitmap : Bitmap);

    public fun onCropImageResultReceived(result : CropImageView.CropResult, view : ImageCustomizeView)

    public fun onButtonDoneClicked(view : ImageCustomizeView, bitmap: Bitmap)

    public fun onFilterButtonClicked(view : ImageCustomizeView, filterType : Int, onFilteredImageReceived : (bitmap : Bitmap) -> Unit)

    public fun onPDFDialogBtnCloseClicked(view : ImageCustomizeView)

}