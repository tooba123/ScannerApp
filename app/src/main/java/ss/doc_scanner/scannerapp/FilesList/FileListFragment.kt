package ss.doc_scanner.scannerapp.FilesList

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import ss.doc_scanner.scannerapp.R
import ss.doc_scanner.scannerapp.adapter.FileItemViewListener
import ss.doc_scanner.scannerapp.adapter.FilesListAdapter
import ss.doc_scanner.scannerapp.data.db.model.File
import ss.doc_scanner.scannerapp.main.MainActivity
import ss.doc_scanner.scannerapp.pdf_viewer.PDFViewActivity
import ss.doc_scanner.scannerapp.util.dialog.CircularBarDialog

class FileListFragment() : Fragment(), ss.doc_scanner.scannerapp.FilesList.FileListView,
    FileItemViewListener {

    private var  contentLayoutID : Int = R.layout.layout_converted_files
    lateinit var rvFileList : RecyclerView
    lateinit var fileListViewListener : ss.doc_scanner.scannerapp.FilesList.FileListViewListener
    lateinit var circularDialog: CircularBarDialog


    companion object {
        fun newInstance(contentLayoutID : Int) : ss.doc_scanner.scannerapp.FilesList.FileListFragment {
            var fileListFragment  = ss.doc_scanner.scannerapp.FilesList.FileListFragment()
            fileListFragment.contentLayoutID = contentLayoutID
            return fileListFragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        var fileListViewModel = ViewModelProvider(this).get(ss.doc_scanner.scannerapp.FilesList.FileListViewModel::class.java)
        fileListViewListener = fileListViewModel
        circularDialog = CircularBarDialog()

        var view  = initView(inflater, contentLayoutID)
        fileListViewListener.onViewLoaded(this)

        (activity as MainActivity).addFileListFragmentListener(object :
            ss.doc_scanner.scannerapp.FilesList.FileListFragment.MainViewListener {
            override fun onRefreshData() {
                fileListViewListener.onRefreshData(this@FileListFragment, true)
            }

        })
       return view
    }

    private fun initView(inflater: LayoutInflater, contentLayoutID : Int) : View{
        var view : View = inflater.inflate(contentLayoutID, null, false)
        rvFileList = view.findViewById<RecyclerView>(R.id.rv_file_list)
        rvFileList.layoutManager = LinearLayoutManager(requireActivity().applicationContext)
        return view
    }

    override fun loadFilesInView(fileLiveData: LiveData<List<File>>, shouldHighlightLastItem : Boolean) {
        fileLiveData.observe(this, Observer { files : List<File> ->
            if(circularDialog.isResumed){
                circularDialog.dismiss()
            }
            if(files == null){
            }
            else{
                if(rvFileList.adapter == null){
                    rvFileList.adapter = FilesListAdapter(files as ArrayList<File>, this@FileListFragment, shouldHighlightLastItem)
                }
                else{
                    var fileListAdapter = rvFileList.adapter as FilesListAdapter
                    var fileList = fileListAdapter.getFiles()
                    fileList.clear()
                    fileList.addAll(files)
                    fileListAdapter.shouldHighLightLastItem = shouldHighlightLastItem
                }
                var fileListAdapter = (rvFileList.adapter as FilesListAdapter)
                fileListAdapter.notifyDataSetChanged()
            }
        })


    }

    override fun launchPDFViewerActivity(path: String) {
        var intent = Intent(context, PDFViewActivity::class.java)
        intent.putExtra(PDFViewActivity.KEY_PDF_FILE_PATH, path)
        startActivity(intent)
    }

    override fun showFileInAnotherApp(uri: Uri) {
        var intent = Intent(Intent.ACTION_VIEW, uri)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(intent)
    }

    override fun showCircularLoaderDialog() {
        circularDialog.show(requireActivity()!!.supportFragmentManager, "circular_dialog")
    }

    override fun onButtonViewFileClicked(file: File) {
        fileListViewListener.onListItemBtnViewClicked(file.path!!, this@FileListFragment)

    }

    override fun onButtonDeleteFileClicked(file: File, onFileDeleted: () -> Unit) {
        fileListViewListener.onListItemBtnDeleteClicked(file, onFileDeleted)
    }

    override fun onBindImage(path: String, onImageLoaded: (Bitmap) -> Unit) {

    }

    public interface MainViewListener{
        public fun onRefreshData()
    }

}