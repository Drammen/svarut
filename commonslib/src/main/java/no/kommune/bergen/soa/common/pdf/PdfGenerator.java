package no.kommune.bergen.soa.common.pdf;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Map;

public interface PdfGenerator {
	/**
	 * Merges model data with a velocity macro to produce a PDF.
	 *
	 * @param model
	 *            Data to be inserted into the macro
	 * @param velocityMacro
	 *            A string containing velocity placeholders.
	 * @return A temporary file containing the PDF.
	 */
	InputStream createPdf( Map<String, String> model, String velocityMacro );

	InputStream concat( File pdfA, File pdfB );

	InputStream concat( File pdfA, File pdfB, String prefix );

	InputStream concat( File[] pdfs );

	void concat( OutputStream outputStream, InputStream[] pdfs );

	boolean isValidPdf( File pdf );

	boolean isValidPdf( InputStream pdf );

	/** Returns a count of pages put out */
	int formatA4( OutputStream outputStream, InputStream[] pdfs );

    void removeOldTempFiles( final long olderThanInMs );

	File createTempFile();

}
