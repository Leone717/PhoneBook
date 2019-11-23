package phonebook;                                                              //itt kell az iText 5.5.5-öt hozzáadni a libraries-hez

import com.itextpdf.text.Chunk;                                                 //importok
import com.itextpdf.text.Document;                                              
import com.itextpdf.text.Element;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import java.io.FileOutputStream;
import javafx.collections.ObservableList;

public class PdfGeneration {

    public void pdfGeneration(String fileName, ObservableList<Person> data) {   //pdf generálás a doku nevének és tartalmának megadásával
        Document document = new Document();                                     //új pdf doku létrehozása, IMPORTNÁL FILGYEJÜNK HOGY itext-BŐL IMPORTÁLJUNK

        try {
            //Céges logó
            PdfWriter.getInstance(document, new FileOutputStream(fileName + ".pdf"));//pdf író fgv meghívása, paraméternek a fájnév és .pdf kiterjesztés
            document.open();                                                        //dokumentum megnyitása, hogy bele lehessen írni
            Image image1 = Image.getInstance(getClass().getResource("/logo.jpg"));  //image bekérése, itt az image nem javafx-es, hanem itextes lesz importon látszik(külön osztály rá)
                                                                                    //készítünk egy image objektumot, ami getInstance segítségével megkeresi hogy hol van az a fájl
            image1.scaleToFit(400, 172);                                            //image méretezése
            image1.setAbsolutePosition(170f, 650f);                                 //image pozíció megadása
            document.add(image1);                                                   //kép hozzáadása a dokuhoz
            
            //Sortörések
            document.add(new Paragraph("\n\n\n\n\n\n\n\n\n\n\n\n\n"));

            //Táblázat
            float[] columnWidths = {2, 4, 4, 6};                                    //oszlopszélességek
            PdfPTable table = new PdfPTable(columnWidths);                          //PDFtáblakészítés, praméterenként az oszlopszélességek
            table.setWidthPercentage(100);                                          //oszlopszélesség százalékosan
            PdfPCell cell = new PdfPCell(new Phrase("KontaktLista"));               //PDFcella, Kontaktlista fejléc létrehozása
            cell.setBackgroundColor(GrayColor.GRAYWHITE);                           //cella háttérszíne, "enyhe fehérszürke"
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);                      //cellán belül középre igazítás
            cell.setColspan(4);                                                     //4 oszlopunk van, így megadjunk hogy 4 oszlop széles legyen 
            table.addCell(cell);                                                    //cell hozzáadása a PDF-tablehoz
            
            table.getDefaultCell().setBackgroundColor(new GrayColor(0.75f));        //új cellaszín
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);    //igazítás
            table.addCell("Sorszám");                                               //cellanevek, oszlopok meghatározása
            table.addCell("Vezetéknév");
            table.addCell("Keresztnév");
            table.addCell("E-mail cím");
            table.setHeaderRows(1);                                                 //táblázat fejléc beállítássa a korábban bevitt paraméterekkel
            
            table.getDefaultCell().setBackgroundColor(GrayColor.GRAYWHITE);         //köv. cellák testerszabása
            table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
            
            for (int i = 1; i <= data.size(); i++) {                                //for ciklus a data-ra(személyek), 1-el kezdjük mert:
                Person actualPerson = data.get(i - 1);                              //i-ből levonunk 1-et hogy a 0-ás embert hívjuk be az ArrayListből
                
                table.addCell(""+i);                                                //de mivel az első i=1, sorszámnak pont jó lesz, 1-el kezdődik majd 
                table.addCell(actualPerson.getLastName());                          //személy adatainak bekérése 
                table.addCell(actualPerson.getFirstName());
                table.addCell(actualPerson.getEmail());
            }
            
            document.add(table);                                                    //elkészült tábla hozzáadása a pdfdokuhoz
           
 
            //Aláírás
            Chunk signature = new Chunk("\n\n Generálva a Telefonkönyv alkalmazás segítségével.");//Chunk aláírás készítése
            Paragraph base = new Paragraph(signature);                              //paragrafhoz adása az aláírásnak
            document.add(base);                                                     //a dokumentumhoz adása az aláírsának

        } catch (Exception e) {                                                     //EXCEPTION ha pdf generálásnál hiba lenne
            e.printStackTrace();
        }
        document.close();                                                           //MINDIG LEZÁRJUK a megnyitott pdf dokut
    }


}
