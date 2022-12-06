package ss.doc_scanner.scannerapp.adapter

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.graphics.Bitmap
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatTextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import ss.doc_scanner.scannerapp.R
import ss.doc_scanner.scannerapp.data.db.model.File
import ss.doc_scanner.scannerapp.util.UnitConverter


class FilesListAdapter(var fileList : ArrayList<File>, var fileViewListener: FileItemViewListener, var shouldHighLightLastItem : Boolean) : RecyclerView.Adapter<FilesListAdapter.FileItemViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FileItemViewHolder {
        var itemView = LayoutInflater.from(parent.context).inflate(R.layout.layout_file_list_item, parent, false)
        return FileItemViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: FileItemViewHolder, position: Int) {
        holder.tvDate.text= fileList[position].dateCreatedString
        holder.tvSize.text = UnitConverter.convertToKBorMBorGB(fileList[position].size)
        holder.tvName.text = fileList[position].name

        holder.ivView.setOnClickListener{
            fileViewListener.onButtonViewFileClicked(fileList[position])
        }

        holder.ivDelete.setOnClickListener{
            fileViewListener.onButtonDeleteFileClicked(fileList[position]){
                fileList.removeAt(position)
                notifyDataSetChanged()
            }
        }

        fileViewListener.onBindImage(fileList[position].path!!){ bitmap : Bitmap ->
            holder.ivFile.setImageBitmap(bitmap)

        }

        if(position == 0 && shouldHighLightLastItem){

            ObjectAnimator.ofObject(holder.cvFileItemLayout, "backgroundColor", ArgbEvaluator(), Color.parseColor("#80FFF000"), Color.TRANSPARENT)
                .setDuration(7000)
                .start()

            shouldHighLightLastItem = false
        }



        /*Picasso.get()
            .load(R.drawable.ic_pdf)
            .resize(50,50)
            .into(holder.ivFile)*/
    }

    override fun getItemCount(): Int {
        return fileList.size
    }

    public fun getFiles() : ArrayList<File>{
        return fileList;
    }

    class FileItemViewHolder(view : View) : RecyclerView.ViewHolder(view){
        var ivFile : AppCompatImageView = view.findViewById(R.id.iv_file)
        var tvSize : AppCompatTextView = view.findViewById(R.id.tv_size)
        var tvDate : AppCompatTextView = view.findViewById(R.id.tv_date)
        var ivView : AppCompatImageView = view.findViewById(R.id.iv_view)
        var ivDelete : AppCompatImageView = view.findViewById(R.id.iv_delete)
        var tvName : AppCompatTextView = view.findViewById(R.id.tv_file_name)
        var cvFileItemLayout : ConstraintLayout = view.findViewById(R.id.cv_file_item_layout)


    }
}

interface FileItemViewListener{
    public fun onButtonViewFileClicked(file : File)
    public fun onButtonDeleteFileClicked(file : File, onFileDeleted: () -> Unit)
    public fun onBindImage(path: String, onImageLoaded: (Bitmap) -> Unit)
}

