package ss.doc_scanner.scannerapp.pdf_viewer

interface PDFView {

    public fun getPDFFilePath()  :String?;

    public fun showPDFFile(path : String)
}