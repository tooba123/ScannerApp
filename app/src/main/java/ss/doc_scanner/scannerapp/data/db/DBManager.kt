package ss.doc_scanner.scannerapp.data.db

import android.content.Context
import androidx.room.Room
import ss.doc_scanner.scannerapp.data.db.model.File

class DBManager {

    companion object{

        private  var  db  : Database? = null
        private fun getDB(appContext : Context) : Database?{
            if(db == null){
                 db = Room.databaseBuilder(appContext, Database::class.java, "scanner-app-db").build()
            }
            return db
        }
    }

    public fun insertFile(appContext : Context, file : File){
        getDB(appContext)!!.FileDAO().insertFile(file)
    }

    public fun getFilesSortByDate(appContext: Context) : List<File>{
        return getDB(appContext)!!.FileDAO().getAllFilesSortByDate()
    }

    public fun deleteFile(appContext: Context, file : File) : Int{
        return getDB(appContext)!!.FileDAO().deleteFile(file)
    }




}