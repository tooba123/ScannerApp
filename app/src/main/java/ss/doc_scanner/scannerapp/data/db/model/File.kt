package ss.doc_scanner.scannerapp.data.db.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
class File constructor( name : String?,  size : Long,  dateCreated : Long,  dateCreatedString : String?,  path : String?){

    @PrimaryKey(autoGenerate = true)
    var id = 0
    @ColumnInfo(name = "name")
    var name = name
    @ColumnInfo(name = "size")
    var size = size
    @ColumnInfo(name = "date_created")
    var dateCreated = dateCreated
    var dateCreatedString = dateCreatedString
    @ColumnInfo(name = "path")
    var path = path

}