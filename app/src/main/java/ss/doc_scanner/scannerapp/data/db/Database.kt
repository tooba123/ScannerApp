package ss.doc_scanner.scannerapp.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import ss.doc_scanner.scannerapp.data.db.dao.FileDAO
import ss.doc_scanner.scannerapp.data.db.model.File


@Database(entities = [File::class],  version = 1)
abstract class Database : RoomDatabase(){
    abstract fun FileDAO(): FileDAO


}