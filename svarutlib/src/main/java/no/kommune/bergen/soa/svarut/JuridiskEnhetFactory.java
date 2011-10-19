package no.kommune.bergen.soa.svarut;

import no.kommune.bergen.soa.svarut.domain.Fodselsnr;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;
import no.kommune.bergen.soa.svarut.domain.IngenJuridiskEnhet;
import no.kommune.bergen.soa.svarut.domain.JuridiskEnhet;
import no.kommune.bergen.soa.svarut.domain.Orgnr;
import no.kommune.bergen.soa.svarut.dto.UserContext;
import no.kommune.bergen.soa.util.Strings;

public class JuridiskEnhetFactory {
	private JuridiskEnhetFactory() {}

	public static JuridiskEnhet create( String value ) {
		if (value == null) return new IngenJuridiskEnhet();
		if (value.length() == 11) return new Fodselsnr( value );
		if (value.length() == 9) return new Orgnr( value );
		return new IngenJuridiskEnhet();
	}

	/** Antar at UserContext.userId og/eller UserContext.onBehalfOf inneholder fødselsnr */
	public static JuridiskEnhet create( UserContext userContext ) {
		if (Strings.isEmpty( userContext.getOnBehalfOfId() )) return create( userContext.getUserid() );
		return create( userContext.getOnBehalfOfId() );
	}

	public static JuridiskEnhet create( Forsendelse forsendelse ) {
		if (!Strings.isEmpty( forsendelse.getOrgnr() )) return create( forsendelse.getOrgnr() );
		return create( forsendelse.getFnr() );
	}
}
