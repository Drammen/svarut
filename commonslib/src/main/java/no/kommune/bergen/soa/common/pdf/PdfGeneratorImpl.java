package no.kommune.bergen.soa.common.pdf;

import java.awt.geom.AffineTransform;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import org.apache.log4j.Logger;


public class PdfGeneratorImpl implements PdfGenerator {
	private final String prefix = "delete-";
	private final File tempdir;

	private final Logger log = Logger.getLogger( PdfGeneratorImpl.class );

	public PdfGeneratorImpl( String tempdir ) {
		this.tempdir = new File( tempdir );
		if (!this.tempdir.exists()) {
			this.tempdir.mkdir();
		}
	}

	public InputStream createPdf( Map<String, String> model, String velocityMacro ) {
		try {
			return new FileInputStream( doCreatePdf( model, velocityMacro ) );
		} catch (Exception e) {
			throw new RuntimeException( "Pdf creation failed", e );
		}
	}

	public InputStream concat( File[] pdfs ) {
		return concat( pdfs, this.prefix );
	}

	public InputStream concat( File[] pdfs, String prefix ) {
		if (pdfs == null || pdfs.length == 0) return null;
		File file;
		try {
			InputStream[] inputStreams = new InputStream[pdfs.length];
			for (int i = 0; i < pdfs.length; i++) {
				inputStreams[i] = new FileInputStream( pdfs[i] );
			}
			file = createTempFile();
			concat( new FileOutputStream( file ), inputStreams );
			return new FileInputStream( file );
		} catch (IOException e) {
			throw new RuntimeException( "Problem creating temp file", e );
		}
	}

	public InputStream concat( File pdfA, File pdfB ) {
		return concat( pdfA, pdfB, this.prefix );
	}

	public InputStream concat( File pdfA, File pdfB, String prefix ) {
		return concat( new File[] { pdfA, pdfB }, prefix );
	}

	public void concat( OutputStream outputStream, InputStream[] pdfs ) {
		if (pdfs == null || pdfs.length == 0) return;
		PdfCopyFields copy = null;
		try {
			copy = new PdfCopyFields( outputStream );
			for (InputStream inputStream : pdfs) {
				PdfReader reader = null;
				try {
					reader = new PdfReader( inputStream );
					copy.addDocument( reader );
					if (reader.getNumberOfPages() % 2 == 1) {
						copy.addDocument( createBlankpage() );
					}
				} finally {
					try {
						if (reader != null) reader.close();
					} catch (Exception e) {}
				}
			}
		} catch (Exception e) {
			throw new RuntimeException( "Problem concatenating pdfs", e );
		} finally {
			try {
				if (copy != null) copy.close();
			} catch (Exception e) {}
		}
	}

	private PdfReader createBlankpage() throws DocumentException, IOException {
		return new PdfReader( createPdf( new HashMap<String, String>(), "" ) );
	}

	public boolean isValidPdf( File pdf ) {
		if (pdf == null) return false;
		try {
			concat( new File[] { pdf } );
		} catch (RuntimeException e) {
			return false;
		}
		return true;
	}

	public boolean isValidPdf( InputStream pdf ) {
		if (pdf == null) return false;
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		try {
			concat( byteArrayOutputStream, new InputStream[] { pdf } );
		} catch (RuntimeException e) {
			return false;
		}
		return true;
	}

	public File createTempFile() {
		try {
			return File.createTempFile( this.prefix, ".pdf", tempdir );
		} catch (IOException e) {
			throw new RuntimeException( "Unable to create temp file in " + tempdir, e );
		}
	}

	public void removeOldTempFiles( final long olderThanInMs ) {
		File[] files = tempdir.listFiles( new FileFilter() {
			public boolean accept( File file ) {
				String name = file.getName();
				if (file.isFile() && file.canWrite() && file.exists() && name.startsWith( prefix ) && name.endsWith( ".pdf" )) {
					long now = System.currentTimeMillis();
					long lastModified = file.lastModified();
					if (now - lastModified > olderThanInMs) {
						return true;
					}
				}
				return false;
			}
		} );
		for (File f : files) {
			f.delete();
		}
	}

	private File doCreatePdf( Map<String, String> model, String velocityMacro ) throws Exception {
		File file = createTempFile();
		Document document = createDocument( file );
		document.open();
		fillDocument( document, model, velocityMacro );
		document.close();
		return file;
	}

	protected Document createDocument( File file ) throws Exception {
		Document document = new Document();
		PdfWriter.getInstance( document, new FileOutputStream( file ) );
		return document;
	}

	private void fillDocument( Document document, Map<String, String> model, String velocityMacro ) throws DocumentException {
		Element[] elements = new PdfTemplateParser( model, velocityMacro ).getElements();
		for (Element element : elements) {
			document.add( element );
		}
	}

	public int formatA4( OutputStream outputStream, InputStream[] pdfs ) {
		PdfReader reader = null;
		Document document = null;
		try {
			document = new Document( PageSize.A4 );
			PdfWriter writer = PdfWriter.getInstance( document, outputStream );
			document.open();
			PdfContentByte pdfContentByte = writer.getDirectContent();
			for (InputStream pdf : pdfs) {
				try {
					reader = new PdfReader( pdf );
					int numberOfPages = reader.getNumberOfPages();
					for (int pageNum = 1; pageNum <= numberOfPages; pageNum++) {

						PdfImportedPage importedPage = writer.getImportedPage( reader, pageNum );

						Rectangle readerPageSize = reader.getPageSize( pageNum );
						Rectangle writerPageSize = writer.getPageSize();

						float rPageHeight = readerPageSize.getHeight();
						float rPageWidth = readerPageSize.getWidth();

						float wPageHeight = writerPageSize.getHeight();
						float wPageWidth = writerPageSize.getWidth();

						int pageRotation = reader.getPageRotation( pageNum );

						boolean rotate = (rPageWidth > rPageHeight) && (pageRotation == 0 || pageRotation == 180);
						//if changing rotation gives us better space rotate an extra 90 degrees.
						if(rotate) pageRotation += 90;
						double randrotate = (double)pageRotation * Math.PI/(double)180;

						AffineTransform transform = new AffineTransform();
						float margin = 0;
						float scale = 1.0f;
						if(pageRotation == 90 || pageRotation == 270 ){
							scale = Math.min((wPageHeight - 2 * margin) / rPageWidth, (wPageWidth- 2 * margin) / rPageHeight);
						} else {
							scale = Math.min(wPageHeight / rPageHeight, wPageWidth / rPageWidth);
						}
						transform.scale(scale,scale);
						transform.translate((wPageWidth/2) + margin, wPageHeight/2 + margin);
						//transform.rotate(-randrotate);
						transform.translate(-rPageWidth/2,-rPageHeight/2);

						pdfContentByte.addTemplate(importedPage, transform);

						document.newPage();
					}
					if (numberOfPages % 2 == 1) {
						writer.setPageEmpty( false );
						document.newPage();
					}
				} finally {
					if (reader != null) {
						reader.close();
					}
				}
			}
			return writer.getPageNumber() - 1;
		} catch (DocumentException e) {
			throw new RuntimeException( "Problems formating documents to A4", e );
		} catch (IOException e) {
			throw new RuntimeException( "Problems formating documents to A4", e );
		} finally {
			if (document != null) document.close();
		}
	}

}
