package no.kommune.bergen.soa.common.pdf;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.lowagie.text.pdf.PdfWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ImageToPdfConverter {

	public static byte[] convert(byte[] data) throws IOException, DocumentException {
		Document document = new Document();
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		PdfWriter.getInstance(document, byteArrayOutputStream);
		document.open();
		Image image = Image.getInstance(data);
		float maxWidth = document.getPageSize().getWidth() - 75;
		float maxHeight = document.getPageSize().getHeight();

		if (image.getWidth() > maxWidth || image.getHeight() > maxHeight)
			image.scaleToFit(maxWidth, maxHeight);

		document.add(image);
		document.close();
		return byteArrayOutputStream.toByteArray();
	}
}
