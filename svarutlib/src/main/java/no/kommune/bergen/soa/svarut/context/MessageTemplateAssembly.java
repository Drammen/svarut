package no.kommune.bergen.soa.svarut.context;

/** Holds Velocity templates for message production */
public class MessageTemplateAssembly {
	private String subjectTemplate = "", bodyTemplate = "", bodyTemplateNoAttachment = "", replyTo = "", toTemplate = "", pdfTemplate = "";

	@Override
	public String toString() {
		return String.format( "{\n  subjectTemplate=%s\n bodyTemplate=%s\n bodyTemplateNoAttachment=%s\n replyTo=%s\n toTemplate=%s\n pdfTemplate=%s\n \n}", subjectTemplate, bodyTemplate, bodyTemplateNoAttachment, replyTo, toTemplate, pdfTemplate );
	}

	public String getSubjectTemplate() {
		return subjectTemplate;
	}

	/** Message subject */
	public void setSubjectTemplate( String subjectTemplate ) {
		this.subjectTemplate = subjectTemplate;
	}

	public String getBodyTemplate() {
		return bodyTemplate;
	}

	/** Message body when document attachment will be provided as downloadable link */
	public void setBodyTemplate( String bodyTemplate ) {
		this.bodyTemplate = bodyTemplate;
	}

	public String getBodyTemplateNoAttachment() {
		return bodyTemplateNoAttachment;
	}

	/** Message body when no document attachment will be provided */
	public void setBodyTemplateNoAttachment( String bodyTemplateNoAttachment ) {
		this.bodyTemplateNoAttachment = bodyTemplateNoAttachment;
	}

	public String getReplyTo() {
		return replyTo;
	}

	public void setReplyTo( String replyTo ) {
		this.replyTo = replyTo;
	}

	/** Used to create correct recipient address for Norge.no, primarily */
	public String getToTemplate() {
		return toTemplate;
	}

	public void setToTemplate( String toTemplate ) {
		this.toTemplate = toTemplate;
	}

	public String getPdfTemplate() {
		return pdfTemplate;
	}

	/** The template to be used when producing documents to attach, print or provide as downloadable links */
	public void setPdfTemplate( String pdfTemplate ) {
		this.pdfTemplate = pdfTemplate;
	}
}
