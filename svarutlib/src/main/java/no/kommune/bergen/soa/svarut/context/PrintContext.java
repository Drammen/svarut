package no.kommune.bergen.soa.svarut.context;

import no.kommune.bergen.soa.svarut.PrintServiceProvider;
import no.kommune.bergen.soa.svarut.util.DispatchWindow;

/**
 * Holds configuration context regarding printouts and regular mail distributions.
 */
public class PrintContext {
	private PrintServiceProvider printServiceProvider;
	private String frontPageTemplate = "";
	private int failedToPrintAlertWindowStartDay = 4, failedToPrintAlertWindowEndDay = 14, printWindowAgeIndays = 30;
    private DispatchWindow dispatchWindow;

	@Override
	public String toString() {
		return String.format( "{\n  printServiceProvider=%s\n frontPageTemplate=%s\n failedToPrintAlertWindowStartDay=%s\n failedToPrintAlertWindowEndDay=%s\n printWindowAgeIndays=%s\n \n}", printServiceProvider, frontPageTemplate,
				failedToPrintAlertWindowStartDay, failedToPrintAlertWindowEndDay, printWindowAgeIndays );
	}

	public void verify() {
		if (printServiceProvider == null) throw new RuntimeException( "Undefined field: printServiceProvider" );
	}

	public PrintServiceProvider getPrintServiceProvider() {
		return printServiceProvider;
	}

	public void setPrintServiceProvider( PrintServiceProvider printServiceProvider ) {
		this.printServiceProvider = printServiceProvider;
	}

	public String getFrontPageTemplate() {
		return frontPageTemplate;
	}

	public void setFrontPageTemplate( String frontPageTemplate ) {
		this.frontPageTemplate = frontPageTemplate;
	}

	public int getFailedToPrintAlertWindowStartDay() {
		return failedToPrintAlertWindowStartDay;
	}

	/** When to start complaining about missing printout confirmation form PrintServiceProvider */
	public void setFailedToPrintAlertWindowStartDay( int failedToPrintAlertWindowStartDay ) {
		this.failedToPrintAlertWindowStartDay = failedToPrintAlertWindowStartDay;
	}

	public int getFailedToPrintAlertWindowEndDay() {
		return failedToPrintAlertWindowEndDay;
	}

	/** When to give up complaining about missing printout confirmation form PrintServiceProvider */
	public void setFailedToPrintAlertWindowEndDay( int failedToPrintAlertWindowEndDay ) {
		this.failedToPrintAlertWindowEndDay = failedToPrintAlertWindowEndDay;
	}

	public int getPrintWindowAgeIndays() {
		return printWindowAgeIndays;
	}

	/** When to give up trying to submit a recurring failing Forsendelse to PrintServiceProvider -- used as a circuit breaker. */
	public void setPrintWindowAgeIndays( int printWindowAgeIndays ) {
		this.printWindowAgeIndays = printWindowAgeIndays;
	}

    public DispatchWindow getDispatchWindow() {
        return dispatchWindow;
    }

    public void setDispatchWindow(DispatchWindow dispatchWindow) {
        this.dispatchWindow = dispatchWindow;
    }
}
