package no.kommune.bergen.soa.svarut.context;

/** Holds configuration context crucial for building links to facilitate recipient download of his or her documents */
public class DownloadContext {
	private String servletPathTemplate = "", helpLink = "", readerDownloadLink = "", pdfLinkText = "", helpLinkText = "", readerDownloadLinkText = "";

	@Override
	public String toString() {
		return String.format( "{\n servletPathTemplate=%s\n helpLink=%s\n readerDownloadLink=%s\n pdfLinkText=%s\n helpLinkText=%s\n readerDownloadLinkText=%s\n \n}", servletPathTemplate, helpLink, readerDownloadLink, pdfLinkText, helpLinkText,
				readerDownloadLinkText );
	}

	public void verify() {}

	public String getServletPathTemplate() {
		return servletPathTemplate;
	}

	public void setServletPathTemplate( String servletPathTemplate ) {
		this.servletPathTemplate = servletPathTemplate;
	}

	public String getHelpLink() {
		return helpLink;
	}

	public void setHelpLink( String helpLink ) {
		this.helpLink = helpLink;
	}

	public String getReaderDownloadLink() {
		return readerDownloadLink;
	}

	public void setReaderDownloadLink( String readerDownloadLink ) {
		this.readerDownloadLink = readerDownloadLink;
	}

	public String getPdfLinkText() {
		return pdfLinkText;
	}

	public void setPdfLinkText( String pdfLinkText ) {
		this.pdfLinkText = pdfLinkText;
	}

	public String getHelpLinkText() {
		return helpLinkText;
	}

	public void setHelpLinkText( String helpLinkText ) {
		this.helpLinkText = helpLinkText;
	}

	public String getReaderDownloadLinkText() {
		return readerDownloadLinkText;
	}

	public void setReaderDownloadLinkText( String readerDownloadLinkText ) {
		this.readerDownloadLinkText = readerDownloadLinkText;
	}
}
