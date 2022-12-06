package ss.doc_scanner.scannerapp.util.dialog

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import ss.doc_scanner.scannerapp.R


class CircularBarDialog   : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        var builder : AlertDialog.Builder = AlertDialog.Builder(requireActivity())
       var inflater =  requireActivity().layoutInflater
        var view = inflater.inflate(R.layout.layout_circle_dialog, null)

        builder.setView(view)

        var dialog = builder.create()
        dialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)
        return dialog
    }
}