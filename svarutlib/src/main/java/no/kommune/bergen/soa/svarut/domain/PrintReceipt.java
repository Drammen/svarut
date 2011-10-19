package no.kommune.bergen.soa.svarut.domain;

/** Kvittering fra PrintProvider nor en forsendelse er sindt til print (og dermed også post) */
public class PrintReceipt {
	private String printId;
	private int pageCount;

	public String getPrintId() {
		return printId;
	}

	/** PrintServiceProvider sin unik referanse for en forsendelse som skal skrives ut på papir og sendes */
	public void setPrintId( String printId ) {
		this.printId = printId;
	}

	public int getPageCount() {
		return pageCount;
	}

	public void setPageCount( int pageCount ) {
		this.pageCount = pageCount;
	}

}
