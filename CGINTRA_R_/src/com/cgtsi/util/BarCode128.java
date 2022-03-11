package com.cgtsi.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.Barcode128;
import com.itextpdf.text.pdf.PdfWriter;

public class BarCode128 {

 public static void main(String[] args) throws FileNotFoundException, DocumentException {

    Document document = new Document(new Rectangle(PageSize.A4));    
    PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("D:\\DKR_BarCode.pdf"));    


    document.open();
	    document.add(new Paragraph("Welcome DKR"));

		    Barcode128 code128 = new Barcode128();

		    code128.setGenerateChecksum(true);

		    code128.setCode("9136512523");    


	    document.add(code128.createImageWithBarcode(writer.getDirectContent(), null, null));

    document.close();

    System.out.println("Document Generated...!!!!!!");
  }

}