package ss.doc_scanner.scannerapp.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import com.itextpdf.text.Document
import com.itextpdf.text.Image
import com.itextpdf.text.PageSize
import com.itextpdf.text.pdf.PdfWriter
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

class FileHelper {

    companion object{
        public fun loadBitMap(path : String) : Bitmap?{
            if(path == null || path.equals("")){
                return null
            }
            return BitmapFactory.decodeFile(path)
        }

        public fun createFile(fileName : String, context :Context) : String {
            var timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            var imgFileName = fileName
            //var path  = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).absolutePath + "/temp_dir" + "/"+ imgFileName +".jpg";
            var dirPath =  "/temp_dir/"

            var dir = File(context.filesDir, dirPath)
            //var dir =getApplication<Application>().applicationContext.getExternalFilesDir(null)

            var file = File(dir, imgFileName)
            if(!file.parentFile.exists()){
                file.parentFile.mkdirs()
            }

            if(!file.exists()){
                file.createNewFile();
            }

            return file.absolutePath

        }

        public fun writeBitmap(fileName : String, bitmap : Bitmap) {
            var fileOutputstream = FileOutputStream(fileName)
            // PNG is a lossless format, the compression format 100 is ignored
            bitmap.compress(Bitmap.CompressFormat.PNG, 100,  fileOutputstream);
        }

        public fun writeAnImagetoPDF(filePath: String, readImageFilePath : String, dateCreated : Long) : File{
            var outputMediaFile = File(filePath)
            outputMediaFile.setLastModified(dateCreated)
            var inputMediaFile = File(readImageFilePath)

            var document = Document(PageSize.A4)
            PdfWriter.getInstance(document, FileOutputStream(outputMediaFile))

            document.open()

            var image = Image.getInstance(inputMediaFile.absolutePath)
            var widthScaler = ((document.pageSize.width  - 100)/ image.width) * 100
            var heightScaler = ((document.pageSize.height  - 100)/ image.height) * 100
            image.scalePercent(70f)


            //image.alignment = Image.ALIGN_TOP and Image.ALIGN_BOTTOM
            //image.alignment = Image.ALIGN_CENTER
            image.setAbsolutePosition((document.pageSize.width - image.scaledWidth)/2, (document.pageSize.height - image.scaledHeight)/2)

            document.add(image)
            document.close()
            return outputMediaFile
        }

        public fun getFilePath(uri : Uri, context  :Context)  :String{
            var path = ""
            var cursor = context.contentResolver.query(uri, null, null, null, null)
            if(cursor == null){
                path  = uri.path!!
            } else{
                cursor.moveToFirst()
                var index = cursor.getColumnIndex("_data")
                path = cursor.getString(index)
            }

            return path
        }
    }
}