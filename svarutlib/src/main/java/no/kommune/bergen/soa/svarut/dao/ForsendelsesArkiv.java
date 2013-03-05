package no.kommune.bergen.soa.svarut.dao;

import java.io.InputStream;
import java.security.AccessControlException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import no.kommune.bergen.soa.common.calendar.BusinessCalendar;
import no.kommune.bergen.soa.common.exception.UserException;
import no.kommune.bergen.soa.svarut.AltinnFacade;
import no.kommune.bergen.soa.svarut.domain.Fodselsnr;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;
import no.kommune.bergen.soa.svarut.domain.JuridiskEnhet;
import no.kommune.bergen.soa.svarut.domain.PrintReceipt;
import no.kommune.bergen.soa.svarut.domain.Printed;
import no.kommune.bergen.soa.svarut.dto.ShipmentPolicy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;

public class ForsendelsesArkiv {

	private static final Logger logger = LoggerFactory.getLogger(ForsendelsesArkiv.class);

	FileStore fileStore;
	JdbcTemplate jdbcTemplate;
	AltinnFacade altinnFacade;
	private int failedToPrintAlertWindowStartDay = 2, failedToPrintAlertWindowEndDay = 10;

	public ForsendelsesArkiv(FileStore fileStore, JdbcTemplate jdbcTemplate, AltinnFacade altinnFacade) {
		this.fileStore = fileStore;
		this.jdbcTemplate = jdbcTemplate;
		this.altinnFacade = altinnFacade;
	}

	private void insert(Forsendelse f, String filename) {
		String message = f.getMeldingsTekst();
		if (message.length() > 4000) {
			message = message.substring( 0, 3999 );
			logger.warn( "Message text is longer than 4000 characters. Truncated!" );
		}
		Integer orgnr = (f.getOrgnr() == null) ? null : new Integer(f.getOrgnr());
		logger.debug("Inserting id = {} fodselsnr = {} orgnr = {}", new Object[]{filename, f.getFnr(), orgnr});
		String sql = "INSERT  INTO FORSENDELSESARKIV (SENDT, ID, FODSELSNR, NAVN, ADRESSE1, ADRESSE2, ADRESSE3, POSTNR, POSTSTED, LAND, "
				+ "AVSENDER_NAVN, AVSENDER_ADRESSE1, AVSENDER_ADRESSE2, AVSENDER_ADRESSE3, AVSENDER_POSTNR, AVSENDER_POSTSTED, TITTEL, MELDING, APPID, PRINT_ID, ORGNR, FORSENDELSES_MATE, EPOST, REPLY_TO, PRINT_FARGE, ANSVARSSTED, KONTERINGKODE) "
				+ "VALUES (SYSDATE, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?,?)";

		Object[] args = new Object[]{filename, f.getFnr(), f.getNavn(), f.getAdresse1(), f.getAdresse2(), f.getAdresse3(), f.getPostnr(), f.getPoststed(), f.getLand(), f.getAvsenderNavn(), f.getAvsenderAdresse1(), f.getAvsenderAdresse2(),
				f.getAvsenderAdresse3(), f.getAvsenderPostnr(), f.getAvsenderPoststed(), f.getTittel(), message, f.getAppid(), f.getPrintId(), orgnr, f.getShipmentPolicy(), f.getEmail(), f.getReplyTo(), f.isPrintFarge() ? "1" : "0", f.getAnsvarsSted(), f.getKonteringkode()};
		int[] argTypes = {Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
				Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.CHAR, Types.VARCHAR, Types.VARCHAR};
		try {
			jdbcTemplate.update(sql, args, argTypes);
		} catch (Exception e) {
			throw new RuntimeException(String.format("%s. SQL: %s; Args: %s", e.getMessage(), sql, toString(args)), e);
		}
	}

	private String toString(Object[] args) {
		StringBuilder sb = new StringBuilder();
		for (Object arg : args) {
			sb.append(arg).append(", ");
		}
		return sb.toString();
	}

	String getIdentifier() {
		String sql = "select forsendelses_seq.nextval from dual";
		try {
			int value = jdbcTemplate.queryForInt(sql);
			return String.format("%012d", value);
		} catch (Exception e) {
			logger.warn("Problems executing sql. Exception: {}, Sql: {}", e.getMessage(), sql);
		}
		UUID uuid = UUID.randomUUID();
		return uuid.toString();
	}

	public String save(Forsendelse f, InputStream inputStream) {
		String identifier = getIdentifier();
		String filename = fileStore.save(inputStream, identifier);
		insert(f, filename);
		return filename;
	}

	public Map<String, Object> retrieveRow(String id) {
		String sql = "SELECT * FROM FORSENDELSESARKIV WHERE ID=?";
		return jdbcTemplate.queryForMap(sql, id);
	}

	public List<Forsendelse> retrieveRows( Date fromAndIncluding, Date toNotIncluding ) {
		List<Forsendelse> list = new ArrayList<Forsendelse>();
		String sql = "SELECT * from forsendelsesarkiv where sendt between ? and ?";
		Object[] args = new Object[] { fromAndIncluding, toNotIncluding };
		int[] types = new int[] { Types.DATE, Types.DATE };
		List<Map<String, Object>> rows = jdbcTemplate.queryForList( sql, args, types );
		for (Map<String, Object> row : rows) {
			list.add( createForsendelse( row ) );
		}
		return list;
	}

	public List<Forsendelse> retrieveRows(String[] ids) {
		if (ids.length > 10000) throw new UserException("Selection is too large, more than 10000 rows");
		List<String> list = Arrays.asList(ids);
		if (list.size() < 1000) return retrieveRowsMax1000(list);
		List<Forsendelse> result = new ArrayList<Forsendelse>();
		List<String> tempList = new ArrayList<String>();
		for (int i = 0; i < list.size(); i++) {
			tempList.add(list.get(i));
			if (i % 1000 == 0) {
				result.addAll(retrieveRowsMax1000(tempList));
				tempList = new ArrayList<String>();
			}
		}
		result.addAll(retrieveRowsMax1000(tempList));
		return result;
	}

	private List<Forsendelse> retrieveRowsMax1000(List<String> ids) {
		List<Forsendelse> list = new ArrayList<Forsendelse>();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < ids.size(); i++) {
			String id = ids.get(i);
			if (i > 0) sb.append(",");
			sb.append(String.format("'%s'", id));
		}
		String sql = String.format("SELECT * FROM FORSENDELSESARKIV WHERE ID IN(%s)", sb.toString());
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		for (Map<String, Object> row : rows) {
			list.add(createForsendelse(row));
		}
		return list;
	}

	/**
	 * Henter dokumentet tilhørende angitt forsendelses-id
	 */
	public InputStream retrieveContent(String id) {
		return fileStore.fetch(id);
	}

	/**
	 * Let etter forsendeser som mot formodning ikke er blitt behandlet
	 */
	public List<Forsendelse> retrieveIgnored() {
		String sql = "SELECT * FROM FORSENDELSESARKIV WHERE UTSKREVET IS NULL AND NORGEDOTNO IS NULL AND ALTINN_SENDT IS NULL AND EPOST_SENDT IS NULL AND STOPPET IS NULL AND SENDT < (SYSDATE - 1)";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
		List<Forsendelse> forsendelser = new ArrayList<Forsendelse>();
		for (Map<String, Object> row : rows) {
			forsendelser.add(createForsendelse(row));
		}
		return forsendelser;
	}

	public List<Forsendelse> retrieveList(JuridiskEnhet juridiskEnhet) {
		final String sqlFnr = "SELECT * FROM FORSENDELSESARKIV WHERE FODSELSNR = ? ORDER BY SENDT DESC";
		final String sqlOrg = "SELECT * FROM FORSENDELSESARKIV WHERE ORGNR = ? ORDER BY SENDT DESC";
		String sql = (juridiskEnhet instanceof Fodselsnr) ? sqlFnr : sqlOrg;
		String juridiskEnhetValue = juridiskEnhet.getValue();
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[]{juridiskEnhetValue}, new int[]{Types.CHAR});
		List<Forsendelse> forsendelser = new ArrayList<Forsendelse>();
		for (Map<String, Object> row : rows) {
			forsendelser.add(createForsendelse(row));
		}
		logger.debug("Finding forsendelser for JuridiskEnhet {} found {}", juridiskEnhetValue, forsendelser.size());
		return forsendelser;
	}

	public Forsendelse retrieve(String id) {
		String sql = "SELECT * FROM FORSENDELSESARKIV WHERE ID=?";
		Map<?, ?> row = jdbcTemplate.queryForMap(sql, id);
		return createForsendelse(row);
	}

	Forsendelse createForsendelse(Map<?, ?> row) {
		Forsendelse f = new Forsendelse();
		f.setId((String) row.get("ID"));
		f.setTittel((String) row.get("TITTEL"));
		f.setMeldingsTekst((String) row.get("MELDING"));
		f.setFnr((String) row.get("FODSELSNR"));
		Number orgnr = (Number) row.get("ORGNR");
		f.setOrgnr((orgnr == null) ? null : orgnr.toString());
		f.setNavn((String) row.get("NAVN"));
		f.setAdresse1((String) row.get("ADRESSE1"));
		f.setAdresse2((String) row.get("ADRESSE2"));
		f.setAdresse3((String) row.get("ADRESSE3"));
		f.setPostnr((String) row.get("POSTNR"));
		f.setPoststed((String) row.get("POSTSTED"));
		f.setLand((String) row.get("LAND"));
		f.setAvsenderNavn((String) row.get("AVSENDER_NAVN"));
		f.setAvsenderAdresse1((String) row.get("AVSENDER_ADRESSE1"));
		f.setAvsenderAdresse2((String) row.get("AVSENDER_ADRESSE2"));
		f.setAvsenderAdresse3((String) row.get("AVSENDER_ADRESSE3"));
		f.setAvsenderPostnr((String) row.get("AVSENDER_POSTNR"));
		f.setAvsenderPoststed((String) row.get("AVSENDER_POSTSTED"));
		f.setAppid((String) row.get("APPID"));
		f.setFile(fileStore.getFile(f.getId()));
		f.setPrintId((String) row.get("PRINT_ID"));
		f.setSendt(toDate(row.get("SENDT")));
		f.setLest(toDate(row.get("LEST")));
		f.setNorgedotno(toDate(row.get("NORGEDOTNO")));
		f.setAltinn(toDate(row.get("ALTINN_SENDT")));
		f.setUtskrevet(toDate(row.get("UTSKREVET")));
		f.setShipmentPolicy((String) row.get("FORSENDELSES_MATE"));
		f.setEmail((String) row.get("EPOST"));
		f.setReplyTo((String) row.get("REPLY_TO"));
		f.setPrintFarge(row.get("PRINT_FARGE").equals("1"));
		f.setTidspunktPostlagt(toDate(row.get("TIDSPUNKTPOSTLAGT")));
		f.setAntallSider(toInt(row.get("ANTALLSIDER")));
		f.setAntallSiderPostlagt(toInt(row.get("ANTALLSIDERPOSTLAGT")));
		f.setNesteForsok(toDate(row.get("NESTE_FORSOK")));
		f.setAnsvarsSted((String) row.get("ANSVARSSTED"));
		f.setKonteringkode((String) row.get("KONTERINGKODE"));
		return f;
	}

	private Date toDate(Object object) {
		if (object == null) return null;
		if (object instanceof Timestamp) {
			return new Date(((Timestamp) object).getTime());
		} else if (object instanceof Date) {
			return (Date) object;
		}
		throw new IllegalArgumentException("Object must either be (a subclass of) java.util.Date or java.sql.Timestamp");
	}

	private int toInt(Object object) {
		if (object == null) return 0;
		if (object instanceof Number) {
			return ((Number) object).intValue();
		} else {
			return 0;
		}
	}

	/**
	 * Finner forsendelser som ligger klar til utsendelse for et sett shipmentpolicies.
	 * Returnerer bare uleste forsendelser (lest -> STOPPET=dato).
	 * Midlertidig vil listen returnere leste forsendelser for organisasjoner. De skal sendes til print uansett.
	 * . Returnerer en liste av forsendelses-ider
	 */
	public List<String> readUnsent(ShipmentPolicy[] shipmentPolicies) {
		String sql = "SELECT ID FROM FORSENDELSESARKIV "
				+ "WHERE UTSKREVET IS NULL "
				+ "AND NORGEDOTNO IS NULL "
				+ "AND ALTINN_SENDT IS NULL "
				+ "AND EPOST_SENDT IS NULL "
				+ "AND STOPPET IS NULL "
				+ "AND ( NESTE_FORSOK IS NULL OR NESTE_FORSOK < SYSDATE )";
		if (shipmentPolicies != null && shipmentPolicies.length > 0) {
			sql = sql + " AND FORSENDELSES_MATE IN ( ";
			for (ShipmentPolicy sp : shipmentPolicies) {
				sql = sql + "'" + sp.value() + "',";
			}
			sql = sql.substring(0, sql.length() - 1) + ")";
		}
		List<?> queryResponse = jdbcTemplate.queryForList(sql);
		List<String> allUnsent = convertListOfIds(queryResponse);
		return allUnsent;
	}

	private List<String> convertListOfIds(List<?> queryResponse) {
		List<String> list = new ArrayList<String>();
		for (Object map : queryResponse) {
			try {
				String id = (String) ((Map<?, ?>) map).get("ID");
				list.add(id);
			} catch (RuntimeException e) {
				logger.error("Response missing ID ", e);
			}
		}
		return list;
	}

	public void setSentAltinn(String id, int receiptId) {
		logger.debug("setSentAltinn() id={}", id);
		String sql = "UPDATE FORSENDELSESARKIV SET ALTINN_SENDT=SYSDATE, ALTINN_RECEIPT_ID=? WHERE ID=?";
		jdbcTemplate.update(sql, receiptId, id);
	}

	public void setSentNorgeDotNo(String id) {
		logger.debug("setSentNorgeDotNo() id={}", id);
		String sql = "UPDATE FORSENDELSESARKIV SET NORGEDOTNO=SYSDATE WHERE ID=?";
		jdbcTemplate.update(sql, id);
	}

	public void setSentEmail(String id) {
		logger.debug("setSentEmail() id={}", id);
		String sql = "UPDATE FORSENDELSESARKIV SET EPOST_SENDT=SYSDATE WHERE ID=?";
		jdbcTemplate.update(sql, id);
	}

	public void setPrinted(String id, PrintReceipt printReceipt) {
		String sql = "UPDATE FORSENDELSESARKIV SET UTSKREVET=SYSDATE,STOPPET=SYSDATE,PRINT_ID=?,ANTALLSIDER=? WHERE ID=?";
		logger.debug("Updating: {}, setting print_id = {}, antallSider = {}", new Object[]{id, printReceipt.getPrintId(), printReceipt.getPageCount()});
		jdbcTemplate.update(sql, printReceipt.getPrintId(), printReceipt.getPageCount(), id);
	}

	//	Confirm forsendelse was read
	public void confirm(String id) {
		String sql = "UPDATE FORSENDELSESARKIV SET LEST=SYSDATE,STOPPET=SYSDATE WHERE LEST IS NULL AND ID=?";
		logger.info("Forsendelse ID={}, was read", id);
		jdbcTemplate.update(sql, new Object[]{id}, new int[]{java.sql.Types.VARCHAR});
	}

	public void setUnread(String id) {
		String sql = "UPDATE FORSENDELSESARKIV SET LEST=NULL, STOPPET=NULL WHERE ID=?";
		jdbcTemplate.update(sql, id);
	}

	/**
	 * Sjekk om angitt juridisk enhet har anledning til å se dokumentet tilhørende angitt forsendelses-id
	 * Gjøres kun ved å sjekke at forsendelse med gitt id stemmer overens med enten gitt fødselsnr eller orgnr
	 */
	public void authorize(String id, JuridiskEnhet juridiskEnhet) {

		final String sqlFnr = "SELECT COUNT(*) FROM FORSENDELSESARKIV WHERE ID=? AND FODSELSNR=?";
		final String sqlOrg = "SELECT COUNT(*) FROM FORSENDELSESARKIV WHERE ID=? AND ORGNR=?";
		String sql = (juridiskEnhet instanceof Fodselsnr) ? sqlFnr : sqlOrg;
		String juridiskEnhetValue = juridiskEnhet.getValue();
		String msg = String.format("Unable to authorize JuridiskEnhet=%s for access to forsendelsesid=%s.", juridiskEnhetValue, id);
		Object[] args = new Object[]{id, juridiskEnhetValue};
		int[] types = new int[]{java.sql.Types.VARCHAR, java.sql.Types.CHAR};
		int count;
		try {
			count = jdbcTemplate.queryForInt(sql, args, types);
		} catch (Exception e) {
			throw new RuntimeException(msg, e);
		}
		if (1 > count) throw new AccessControlException(msg);
	}

	/**
	 * Sjekk om angitt fødselsnr har lov til å se dokumentet tilhørende angitt forsendelses-id.
	 * Sjekker mot Altinn-service og ser at bruker med angitt fødselsnr har tilgang til registrert orgnr på forsendelsen
	 */
	public void authorize(String id, String fodselsNr) {
		final String sql = "SELECT ORGNR, FODSELSNR FROM FORSENDELSESARKIV WHERE ID=?";
		String errMsg = String.format("Unable to authorize fødselsnr=%s for access to forsendelsesid=%s.", fodselsNr, id);
		Object[] args = new Object[]{id};
		int[] types = new int[]{java.sql.Types.VARCHAR};
		Map<?, ?> forsendelseMap;
		try {
			forsendelseMap = jdbcTemplate.queryForMap(sql, args, types);
		} catch (Exception e) {
			throw new RuntimeException("JDBC query failed. " + errMsg, e);
		}
		if(forsendelseMap.isEmpty()) {
			throw new RuntimeException("ID was not found. No record found in FORSENDELSESARKIV with ID:" + id);
		}

		// Fetch Map values
		Object forsendelseFodselsNr = forsendelseMap.get("FODSELSNR");
		Object forsendelseOrgNr = forsendelseMap.get("ORGNR");

		// Authorize by orgnr
		if(forsendelseOrgNr != null) {
			String orgNr = forsendelseOrgNr.toString();
			String orgErrMsg = String.format("Unable to authorize fødselsnr=%s to access forsendelsesid=%s with organisasjonsnr=%s", fodselsNr, id, orgNr);
			if(!orgNr.isEmpty()) {
				// Check by Altinn service
				boolean authorized = altinnFacade.authorizeUserAgainstOrgNr(fodselsNr, orgNr);
				if(!authorized)
					throw new AccessControlException(String.format("Not authorized to access forsendelsesid=%s by Altinn service. Orgnr=%s not found with given fodselsNr=%s", id, orgNr, fodselsNr));
			} else {
				throw new AccessControlException(orgErrMsg);
			}
		} else if(forsendelseFodselsNr != null) { // Authorize by fodselsnr
			if(!forsendelseFodselsNr.toString().equals(fodselsNr))
				throw new AccessControlException(errMsg);
		} else { // Unauthorized
			throw new AccessControlException(errMsg);
		}


	}

	public void remove(String id) {
		logger.info("Removing forsendelse: {}", id);
		String sql = "DELETE FROM FORSENDELSESARKIV WHERE ID=?";
		jdbcTemplate.update( sql, id);
		fileStore.remove( id );
	}

	Timestamp calculateTimeLimit(long days) {
		long now = System.currentTimeMillis();
		long limit = now - (days * 24 * 60 * 60 * 1000);
		return new Timestamp(limit);
	}

	/**
	 * Return count
	 */
	public int removeOlderThan(long days) {
		Timestamp timestamp = calculateTimeLimit(days);
		String sql = "SELECT ID FROM FORSENDELSESARKIV WHERE STOPPET IS NOT NULL AND STOPPET<?";
		List<String> list = convertListOfIds(jdbcTemplate.queryForList(sql, timestamp));
		int count = 0;
		for (String id : list) {
			try {
				remove(id);
				count++;
			} catch (RuntimeException e) {
				logger.warn(String.format("Problems removing old entry. Id:%s Exception: %s", id, e.getMessage()));
			}
		}
		return count;
	}

	/**
	 * Return count
	 */
	public int removeUnreachable(long days) {
		Timestamp timestamp = calculateTimeLimit(days);
		String sql = "SELECT ID FROM FORSENDELSESARKIV WHERE FODSELSNR IS NULL AND ORGNR IS NULL AND STOPPET<?";
		List<String> list = convertListOfIds(jdbcTemplate.queryForList(sql, timestamp));
		int count = 0;
		for (String id : list) {
			try {
				remove(id);
				count++;
			} catch (RuntimeException e) {
				logger.warn("Problems removing unreachable entry. Id: {} Exception: {}", id, e.getMessage());
			}
		}
		return count;
	}

	/**
	 * Return liste med forsendelses-ider for et sett shipment-policyer
	 */
	public List<String> retrieveYoungerThan(long days, ShipmentPolicy[] shipmentPolicies) {
		long now = System.currentTimeMillis();
		long millis = days * 24 * 60 * 60 * 1000;
		long limit = now - millis;
		Timestamp timestamp = new Timestamp(limit);
		String sql = "SELECT ID FROM FORSENDELSESARKIV WHERE LEST IS NULL AND STOPPET IS NULL AND SENDT>?";
		if (shipmentPolicies != null && shipmentPolicies.length > 0) {
			sql = sql + " AND FORSENDELSES_MATE IN ( ";
			for (ShipmentPolicy sp : shipmentPolicies) {
				sql = sql + "'" + sp.value() + "',";
			}
			sql = sql.substring(0, sql.length() - 1) + ")";
		}
		List<?> list = jdbcTemplate.queryForList(sql, timestamp);
		return convertListOfIds(list);
	}

	/**
	 * TODO PIA-1573 ta dette bort når man bestemmer seg for å ikke printe uansett.
	 * MIDLERTIDIG: Vi printer alle forsendelser sendt til organisasjon i Altinn uansett inntil videre.
	 * Return liste med forsendelses-ider sendt til Altinn, lest men ikke printet, for et sett shipment-policyer
	 */
	public List<String> retrieveSentToAltinnButNotPrinted(ShipmentPolicy[] shipmentPolicies) {
		String sql = "SELECT ID FROM FORSENDELSESARKIV WHERE "
				+ "ALTINN_SENDT IS NOT NULL AND "
				+ "ORGNR IS NOT NULL AND "
				+ "UTSKREVET IS NULL";

		if (shipmentPolicies != null && shipmentPolicies.length > 0) {
			sql = sql + " AND FORSENDELSES_MATE IN ( ";
			for (ShipmentPolicy sp : shipmentPolicies) {
				sql = sql + "'" + sp.value() + "',";
			}
			sql = sql.substring(0, sql.length() - 1) + ")";
		}
		List<?> list = jdbcTemplate.queryForList(sql);
		return convertListOfIds(list);
	}

	public Map<String, Integer> statistics() {
		int submitted = jdbcTemplate.queryForInt("SELECT COUNT(*) FROM FORSENDELSESARKIV");
		int read = jdbcTemplate.queryForInt("SELECT COUNT(*) FROM FORSENDELSESARKIV WHERE LEST IS NOT NULL");
		int printed = jdbcTemplate.queryForInt("SELECT COUNT(*) FROM FORSENDELSESARKIV WHERE UTSKREVET IS NOT NULL");
		int norgeDotNo = jdbcTemplate.queryForInt("SELECT COUNT(*) FROM FORSENDELSESARKIV WHERE NORGEDOTNO IS NOT NULL");
		int altinn = jdbcTemplate.queryForInt("SELECT COUNT(*) FROM FORSENDELSESARKIV WHERE ALTINN_SENDT IS NOT NULL");
		int stoppet = jdbcTemplate.queryForInt("SELECT COUNT(*) FROM FORSENDELSESARKIV WHERE STOPPET IS NOT NULL");
		int email = jdbcTemplate.queryForInt("SELECT COUNT(*) FROM FORSENDELSESARKIV WHERE EPOST_SENDT IS NOT NULL");
		Map<String, Integer> map = new HashMap<String, Integer>();
		map.put("SENDT", submitted);
		map.put("LEST", read);
		map.put("NORGEDOTNO", norgeDotNo);
		map.put("ALTINN", altinn);
		map.put("UTSKREVET", printed);
		map.put("STOPPET", stoppet);
		map.put("EPOST_SENDT", email);
		return map;
	}

	/**
	 * Forsendelsen tas ut av all videre behandling
	 */
	public void stop(String id) {
		logger.info("Stopping forsendelse {}", id);
		String sql = "UPDATE FORSENDELSESARKIV SET STOPPET=SYSDATE WHERE ID=?";
		jdbcTemplate.update(sql, id);
	}

	public void updateSentToPrint(String forsendelsesId, Date sentPrintDate) {
		if (forsendelsesId == null || forsendelsesId.length() == 0) {
			throw new RuntimeException("Nonexisting forsendelse: " + forsendelsesId);
		}
		String sql = "UPDATE FORSENDELSESARKIV SET UTSKREVET=? WHERE ID=?";
		int rowsUpdatedCount = jdbcTemplate.update(sql, sentPrintDate, forsendelsesId);
		if (rowsUpdatedCount != 1) {
			throw new RuntimeException("Nonexisting forsendelse: " + forsendelsesId);
		}
	}

	/**
	 * benyttes i.f.m tilbakerapportering av status fra PrintServiceProvider
	 */
	public void updatePrinted(Printed printed) {
		String forsendelsesId = printed.getForsendelsesId();
		if (forsendelsesId == null || forsendelsesId.length() == 0) {
			throw new RuntimeException("Nonexisting forsendelse: " + forsendelsesId);
		}
		String sql = "UPDATE FORSENDELSESARKIV SET ANTALLSIDERPOSTLAGT=?, TIDSPUNKTPOSTLAGT=? WHERE ID=?";
		int rowsUpdatedCount = jdbcTemplate.update(sql, printed.getAntallSiderPostlagt(), printed.getTidspunktPostlagt(), forsendelsesId);
		if (rowsUpdatedCount != 1) {
			throw new RuntimeException("Nonexisting forsendelse: " + forsendelsesId);
		}
	}

	/**
	 * Finn forsendelser som mot formodning ikke er sendt i posten av PrintserviceProvider
	 */
	public List<String> retrieveFailedToPrint() {
		Date failedToPrintAlertWindowStart = BusinessCalendar.getPreviousWorkday(failedToPrintAlertWindowStartDay);
		Date failedToPrintAlertWindowEnd = BusinessCalendar.getPreviousWorkday(failedToPrintAlertWindowEndDay);
		final String sql = "SELECT ID FROM FORSENDELSESARKIV WHERE UTSKREVET IS NOT NULL AND TIDSPUNKTPOSTLAGT IS NULL AND UTSKREVET <= ? AND UTSKREVET >= ?";
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, failedToPrintAlertWindowStart, failedToPrintAlertWindowEnd);
		return getIdList(rows);
	}

	/**
	 * Når skal vi begynne å varsle om mulig glipp fra PrintServiceProvider sin side
	 */
	public void setFailedToPrintAlertWindowStartDay(int failedToPrintAlertWindowStartDay) {
		this.failedToPrintAlertWindowStartDay = failedToPrintAlertWindowStartDay;
	}

	/**
	 * Når skal vi slutte å varsle om mulig glipp fra PrintServiceProvider sin side
	 */
	public void setFailedToPrintAlertWindowEndDay(int failedToPrintAlertWindowEndDay) {
		this.failedToPrintAlertWindowEndDay = failedToPrintAlertWindowEndDay;
	}

	/**
	 * Marker en forsendelse som  feilet i forsendelsesarkivet.
	 */
	public void markForsendelseFailed(Forsendelse forsendelse, Date nesteforsok) {
		String sql = "UPDATE FORSENDELSESARKIV SET NESTE_FORSOK=? WHERE ID=?";

		int rowsUpdatedCount = jdbcTemplate.update(sql, nesteforsok, forsendelse.getId());
		if (rowsUpdatedCount != 1) {
			throw new RuntimeException("Nonexisting forsendelse: " + forsendelse.getId());
		}
		forsendelse.setNesteForsok(nesteforsok);
	}

	private List<String> getIdList(List<Map<String, Object>> rows) {
		final List<String> ids = new ArrayList<String>();
		if (rows != null) {
			for (Map<String, Object> row : rows) {
				if (row == null) continue;
				Object id = row.get("ID");
				if (id != null && id instanceof String) ids.add((String) id);
			}
		}
		return ids;
	}
}
