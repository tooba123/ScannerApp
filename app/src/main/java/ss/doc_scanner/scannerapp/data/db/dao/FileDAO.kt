package ss.doc_scanner.scannerapp.data.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import ss.doc_scanner.scannerapp.data.db.model.File

@Dao
interface FileDAO {

    @Query("SELECT * FROM FILE")
    fun getAllFiles() : List<File>

    @Query("SELECT * FROM FILE ORDER BY date_created DESC")
    fun getAllFilesSortByDate() : List<File>

    @Insert
    fun insertFile(file : File)

    @Delete
    fun deleteFile(file : File) : Int

}