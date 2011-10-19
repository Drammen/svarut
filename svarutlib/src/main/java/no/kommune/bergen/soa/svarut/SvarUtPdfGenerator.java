package no.kommune.bergen.soa.svarut;

import java.io.File;
import java.io.FileOutputStream;

import no.kommune.bergen.soa.common.pdf.PdfGenerator;
import no.kommune.bergen.soa.common.pdf.PdfGeneratorImpl;

import com.lowagie.text.Document;
import com.lowagie.text.PageSize;
import com.lowagie.text.pdf.PdfWriter;

public class SvarUtPdfGenerator extends PdfGeneratorImpl implements PdfGenerator {

	public SvarUtPdfGenerator( String tempdir ) {
		super( tempdir );
	}

	@Override
	protected Document createDocument( File file ) throws Exception {
		Document document = new Document( PageSize.A4, 48.0f, 36.0f, 16.0f, 36.0f );
		PdfWriter.getInstance( document, new FileOutputStream( file ) );
		return document;
	}
}
