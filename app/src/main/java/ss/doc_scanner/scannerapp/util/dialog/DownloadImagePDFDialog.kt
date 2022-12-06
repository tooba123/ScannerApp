package ss.doc_scanner.scannerapp.util.dialog

import android.app.Dialog
import android.graphics.LightingColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageView
import androidx.core.widget.ContentLoadingProgressBar
import androidx.fragment.app.DialogFragment
import ss.doc_scanner.scannerapp.R


class DownloadImagePDFDialog : DialogFragment() , Listener{

    lateinit var tvHeading : TextView
    lateinit var tvOutputFilePath : TextView
    lateinit var ContentLoadingProgressBar : ContentLoadingProgressBar
    lateinit var ivSuccessSymbol : AppCompatImageView
    lateinit var ivConvertSymbol : AppCompatImageView
    lateinit var btnClose :  AppCompatButton
    lateinit var pdfDialogListener: PDFDialogListener


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var builder  : AlertDialog.Builder =  AlertDialog.Builder(requireActivity())
        var inflator : LayoutInflater = requireActivity().layoutInflater

        var view = inflator.inflate(R.layout.layout_download_image_pdf_dialog, null)
        tvHeading = view.findViewById(R.id.tv_heading)
        tvOutputFilePath = view.findViewById(R.id.tv_path)
        ContentLoadingProgressBar = view.findViewById(R.id.cpb)
        ivSuccessSymbol = view.findViewById(R.id.iv_success)
        ivConvertSymbol = view.findViewById(R.id.iv_convert)
        btnClose = view.findViewById(R.id.btn_close)



        ContentLoadingProgressBar.indeterminateDrawable.colorFilter = LightingColorFilter(0x0364E9, 0x6200EE)
        ContentLoadingProgressBar.invalidate()

        btnClose.setOnClickListener{
            dismiss()
            pdfDialogListener.onButtonCloseClicked()
        }

        builder.setView(view)
        var dialog = builder.create()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.holo_blue_dark)
        return dialog
    }

    override fun onImageSavedToPDF(message: String, path: String) {
        ContentLoadingProgressBar.visibility = View.GONE
        tvOutputFilePath.visibility = View.VISIBLE
        tvOutputFilePath.text = path
        tvHeading.text = message
        ivConvertSymbol.visibility = View.INVISIBLE
        ivSuccessSymbol.visibility = View.VISIBLE
        btnClose.visibility = View.VISIBLE
    }

}
public interface Listener{
    public fun onImageSavedToPDF(message : String, path : String);
}