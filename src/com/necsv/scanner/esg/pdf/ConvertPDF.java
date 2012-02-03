package com.necsv.scanner.esg.pdf;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.PdfWriter; 
import com.necsv.scanner.esg.util.GlobalVariable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;

public class ConvertPDF {

	public static boolean createPDFs(String path) throws MalformedURLException, IOException {

		Document document = new Document(PageSize.A4);
		try {
			FileOutputStream os = new FileOutputStream(GlobalVariable.TARGET_PATH + "ImageDemo.pdf"); 
			PdfWriter.getInstance(document,	os);
			document.open();
			
			File f = new File(path);
			File[] files = f.listFiles();
			document.setPageCount(files.length);

			for (int i = 0; i < files.length; i++) {

				File file = files[i];			
				if (file.isFile() && file.getName().endsWith(".jpg")) {
					Image image = Image.getInstance(file.getAbsolutePath());
					image.scaleAbsolute(document.getPageSize().getWidth(), document.getPageSize().getHeight());
					document.add(image);
				}
			}
		} catch (DocumentException e) {
			e.printStackTrace();
			return false;
		}
		document.close();
		return true;
	}
}