package ss.doc_scanner.scannerapp.util

import java.text.DecimalFormat

class UnitConverter {

    companion object{
        public fun convertToKBorMBorGB(bytes : Long) : String{

            var kb =  (bytes/1024).toFloat()
            var mb = (kb/1024).toFloat()
            var gb = (mb/1024).toFloat()

            var df = DecimalFormat("0.0")

            if(df.format(gb).toFloat() > 0){
                return df.format(gb) + " GB"
            } else if(df.format(mb).toFloat() > 0){
                return df.format(mb) + " MB"
            } else {
                return df.format(kb) + " KB"
            }
        }
    }
}