package no.kommune.bergen.soa.common.pdf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.itextpdf.text.*;

/**
 * A crude attempt to add syntax to a Velocity macro/template, that can spill over to Pdf generation. Presently a selection of
 * paragraph fonts are supported: "h1." - header, "f1." - small "f2." - smaller and "b1." - bold. Additionally, links (anchors) may
 * be created by inserting a reference to a macro variable link in the text. The model map then must contain key value pairs to link
 * and link-text. The value of link being the url and the value of link-text, it's textual replacement.
 */
public class PdfTemplateParser {
	private final Map<String, String> model;
	private final String velocityMacro;
	private final Font linkFont, h1Font, f1Font, f2Font, font, bFont;

	public PdfTemplateParser( Map<String, String> model, String velocityMacro ) {
		this.model = model;
		this.velocityMacro = velocityMacro;
		font = new Font( Font.FontFamily.TIMES_ROMAN, 10 );
		bFont = new Font( Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD );
		linkFont = new Font( Font.FontFamily.TIMES_ROMAN, 10f, Font.UNDERLINE, BaseColor.BLUE );
		h1Font = new Font( Font.FontFamily.TIMES_ROMAN, 10, Font.BOLD );
		f1Font = new Font( Font.FontFamily.TIMES_ROMAN, 8 );
		f2Font = new Font( Font.FontFamily.TIMES_ROMAN, 10 );
	}

	public Element[] getElements() {
		List<Element> result = new ArrayList<Element>();
		String[] paragraphMacros = velocityMacro.split( "\n\n" );
		for (String paragraphMacro : paragraphMacros) {
			Paragraph p = createParagraph( paragraphMacro );
			result.add( p );
		}
		return result.toArray( new Element[result.size()] );
	}

	private Paragraph createParagraph( String paragraphMacro ) {
		Paragraph p = null;
		if (paragraphMacro != null && paragraphMacro.startsWith( "h1." )) {
			p = new Paragraph( "", h1Font );
			paragraphMacro = paragraphMacro.substring( 3 );
			p.setMultipliedLeading( 1.2f );
		} else if (paragraphMacro != null && paragraphMacro.startsWith( "f1." )) {
			p = new Paragraph( "", f1Font );
			p.setMultipliedLeading( 1 );
			paragraphMacro = paragraphMacro.substring( 3 );
		} else if (paragraphMacro != null && paragraphMacro.startsWith( "f2." )) {
			p = new Paragraph( "", f2Font );
			p.setMultipliedLeading( 1 );
			paragraphMacro = paragraphMacro.substring( 3 );
		} else if (paragraphMacro != null && paragraphMacro.startsWith( "b1." )) {
			p = new Paragraph( "", bFont );
			p.setMultipliedLeading( 1.2f );
			paragraphMacro = paragraphMacro.substring( 3 );
		} else {
			p = new Paragraph( "", font );
			p.setMultipliedLeading( 1.2f );
		}
		Pattern variablePattern = Pattern.compile( "\\{(.*?)\\}" );
		Matcher matcher = variablePattern.matcher( paragraphMacro );
		int from = 0;
		while (matcher.find()) {
			int to = matcher.start();
			String s = paragraphMacro.substring( from, to );
			p.add( s );
			from = matcher.end();

			String key = matcher.group( 1 );
			String value = model.get( key );
			if (value == null) {
				p.add( "" );
			} else if (value.startsWith( "http" )) {
				Anchor a = createAnchor( key );
				p.add( a );
			} else {
				p.add( value );
			}
		}
		Pattern links = Pattern.compile("http(s)?://([\\w+?\\.\\w+])+([a-zA-Z0-9\\~\\!\\@\\#\\$\\%\\^\\&amp;\\*\\(\\)_\\-\\=\\+\\\\\\/\\?\\.\\:\\;\\'\\,]*)?");
		Matcher linkmatcher = links.matcher( paragraphMacro );
		while(linkmatcher.find()){
			int to = linkmatcher.start();
			String s = paragraphMacro.substring( from, to );
			p.add( s );
			from = linkmatcher.end();

			String key = paragraphMacro.substring(to,from);
			if(key.endsWith(".")){
				from -= 1;
				key = paragraphMacro.substring(to,from);
			}
			Anchor a = new Anchor( key, linkFont );
			a.setReference(key);
			p.add( a );
		}
		p.add( paragraphMacro.substring( from ) );
		return p;
	}

	private Anchor createAnchor( String key ) {
		String linkText = model.get( key + "-text" );
		Anchor result = new Anchor( linkText, linkFont );
		String link = model.get( key );
		result.setReference( link );
		return result;
	}
}
