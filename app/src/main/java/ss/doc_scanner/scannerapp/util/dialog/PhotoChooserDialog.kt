package ss.doc_scanner.scannerapp.util.dialog

import android.app.Dialog
import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import ss.doc_scanner.scannerapp.R
import ss.doc_scanner.scannerapp.util.Constants.Companion.OPTION_CAMERA
import ss.doc_scanner.scannerapp.util.Constants.Companion.OPTION_GALLERY
import ss.doc_scanner.scannerapp.util.callbacks.PhotoChooserListener


class PhotoChooserDialog(var photoOptionListener : PhotoChooserListener) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
       var builder :  AlertDialog.Builder = AlertDialog.Builder(requireActivity())
       var inflator = requireActivity().layoutInflater

        var view = inflator.inflate(R.layout.layout_picture_chooser_dialog, null)
        var btnCamera = view.findViewById<ImageButton>(R.id.btn_camera)
        var btnGallery = view.findViewById<ImageButton>(R.id.btn_gallery)

        btnCamera.setOnClickListener {
            dismiss()
            photoOptionListener.onPhotoChooserOptionSelected(OPTION_CAMERA)
        }

        btnGallery.setOnClickListener {
            dismiss()
            photoOptionListener.onPhotoChooserOptionSelected(OPTION_GALLERY)
        }

        builder.setView(view)
        var dialog =  builder.create()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }


}

