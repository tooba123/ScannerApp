package ss.doc_scanner.scannerapp.adapter

import android.content.Context
import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import ss.doc_scanner.scannerapp.R

class FilterViewAdapter(var context : Context, var filterViewListener: FilterViewListener) : RecyclerView.Adapter<FilterViewAdapter.FilterButtonViewHolder>() {

    companion object{
        var BLACK_WHITE     = 1
        var GRAY_SCALE      = 2
        var COLORED         = 3
        var ORIGINAL        = 4
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FilterButtonViewHolder {
        var view = LayoutInflater.from(parent.context).inflate(R.layout.layout_filter_button, parent,false)
        return FilterButtonViewHolder(view)
    }

    override fun onBindViewHolder(holder: FilterButtonViewHolder, position: Int) {
        when(position){
            0 -> {
                //holder.tvFilterName.setTextColor(ContextCompat.getColor(context, R.color.black))
                holder.tvFilterName.text = context.resources.getString(R.string.black_and_white)
            }
            1 -> {
                //holder.tvFilterName.setTextColor(ContextCompat.getColor(context, R.color.gray))
                holder.tvFilterName.text = context.resources.getString(R.string.gray_scale)
            }
            2 -> {
                //holder.tvFilterName.setTextColor(ContextCompat.getColor(context, R.color.orange))
                holder.tvFilterName.text = context.resources.getString(R.string.colored)
            }
        }

        holder.itemIndex = position

        holder.viewContainer.setOnClickListener {
            filterViewListener.onFilterButtonClicked(holder.itemIndex + 1) {
                //holder.iconView.setImageBitmap(it)
            }
       }
    }

    override fun getItemCount(): Int {
        return 3
    }

    class FilterButtonViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var viewContainer = itemView.findViewById<ConstraintLayout>(R.id.cl_view_container)
        var iconView = itemView.findViewById<AppCompatImageView>(R.id.iv_icon)
        var tvFilterName = itemView.findViewById<TextView>(R.id.tv_filter_name)
        var itemIndex : Int = -1

    }
}


interface FilterViewListener{
    public fun onFilterButtonClicked(filterType : Int, onFilteredImageReceived : (bitmap : Bitmap) -> Unit)
}