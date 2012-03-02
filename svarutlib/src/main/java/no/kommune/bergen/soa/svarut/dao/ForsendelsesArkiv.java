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
import no.kommune.bergen.soa.common.calendar.PresentDayBusinessCalendarForNorway;
import no.kommune.bergen.soa.common.exception.UserException;
import no.kommune.bergen.soa.svarut.domain.Fodselsnr;
import no.kommune.bergen.soa.svarut.domain.Forsendelse;
import no.kommune.bergen.soa.svarut.domain.JuridiskEnhet;
import no.kommune.bergen.soa.svarut.domain.PrintReceipt;
import no.kommune.bergen.soa.svarut.domain.Printed;
import no.kommune.bergen.soa.svarut.dto.ShipmentPolicy;

import org.apache.log4j.Logger;
import org.springframework.jdbc.core.JdbcTemplate;

public class ForsendelsesArkiv {
	final Logger logger = Logger.getLogger(ForsendelsesArkiv.class);
	FileStore fileStore;
	JdbcTemplate jdbcTemplate;
	private int failedToPrintAlertWindowStartDay = 4, failedToPrintAlertWindowEndDay = 14;

	public ForsendelsesArkiv(FileStore fileStore, JdbcTemplate jdbcTemplate) {
		this.fileStore = fileStore;
		this.jdbcTemplate = jdbcTemplate;
	}

	private void insert(Forsendelse f, String filename) {
		String message = f.getMeldingsTekst();
		if (message.length() > 4000) {
			message = message.substring( 0, 3999 );
			logger.warn( "Message text is longer than 4000 characters. Truncated!" );
		}
		Integer orgnr = (f.getOrgnr() == null) ? null : new Integer(f.getOrgnr());
		if (logger.isDebugEnabled())
			logger.debug("Inserting id = " + filename + " fodselsnr = " + f.getFnr() + " orgnr = " + orgnr);
		String sql = "INSERT  INTO FORSENDELSESARKIV (SENDT, ID, FODSELSNR, NAVN, ADRESSE1, ADRESSE2, ADRESSE3, POSTNR, POSTSTED, LAND, "
				+ "AVSENDER_NAVN, AVSENDER_ADRESSE1, AVSENDER_ADRESSE2, AVSENDER_ADRESSE3, AVSENDER_POSTNR, AVSENDER_POSTSTED, TITTEL, MELDING, APPID, PRINT_ID, ORGNR, FORSENDELSES_MATE, EPOST, REPLY_TO, PRINT_FARGE, ANSVARSSTED) "
				+ "VALUES (SYSDATE, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?,?)";

		Object[] args = new Object[]{filename, f.getFnr(), f.getNavn(), f.getAdresse1(), f.getAdresse2(), f.getAdresse3(), f.getPostnr(), f.getPoststed(), f.getLand(), f.getAvsenderNavn(), f.getAvsenderAdresse1(), f.getAvsenderAdresse2(),
				f.getAvsenderAdresse3(), f.getAvsenderPostnr(), f.getAvsenderPoststed(), f.getTittel(), message, f.getAppid(), f.getPrintId(), orgnr, f.getShipmentPolicy(), f.getEmail(), f.getReplyTo(), f.isPrintFarge() ? "1" : "0", f.getAnsvarsSted()};
		int[] argTypes = {Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR,
				Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.VARCHAR, Types.CHAR, Types.VARCHAR};
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
			logger.warn(String.format("Problems executing sql. Exception: %s, Sql:%s", e.getMessage(), sql));
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

	public Map<String, Object> countRows() {
		String sql = "SELECT COUNT(ID) FROM FORSENDELSESARKIV";
		return jdbcTemplate.queryForMap(sql, null);
	}

	public Map<String, Object> retrieveAnyRow() {
		String sql = "SELECT * FROM FORSENDELSESARKIV";
		return jdbcTemplate.queryForMap( sql, null );
	}

	public Map<String, Object> retrieveRow(String id) {
		String sql = "SELECT * FROM FORSENDELSESARKIV WHERE ID=?";
		return jdbcTemplate.queryForMap(sql, new Object[]{id});
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
		String sql = "SELECT * FROM FORSENDELSESARKIV WHERE UTSKREVET IS NULL AND NORGEDOTNO IS NULL AND EPOST_SENDT IS NULL AND STOPPET IS NULL AND SENDT < (SYSDATE - 1)";
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
		if (logger.isDebugEnabled())
			logger.debug("Finding forsendelser for JuridiskEnhet " + juridiskEnhetValue + " found " + forsendelser.size());
		return forsendelser;
	}

	public Forsendelse retrieve(String id) {
		String sql = "SELECT * FROM FORSENDELSESARKIV WHERE ID=?";
		Map<?, ?> row = jdbcTemplate.queryForMap(sql, new Object[]{id});
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
		f.setPrintFarge(row.get("PRINT_FARGE").equals("1") ? true : false);
		f.setTidspunktPostlagt(toDate(row.get("TIDSPUNKTPOSTLAGT")));
		f.setAntallSider(toInt(row.get("ANTALLSIDER")));
		f.setAntallSiderPostlagt(toInt(row.get("ANTALLSIDERPOSTLAGT")));
		f.setNesteForsok(toDate(row.get("NESTE_FORSOK")));
		f.setAnsvarsSted((String) row.get("ANSVARSSTED"));
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
		return convertListOfIds(queryResponse);
	}

	@SuppressWarnings("unchecked")
	private List<String> convertListOfIds(List<?> queryResponse) {
		List<String> list = new ArrayList<String>();
		for (Object map : queryResponse) {
			try {
				String id = (String) ((Map) map).get("ID");
				list.add(id);
			} catch (RuntimeException e) {
				logger.error("Response missing ID ", e);
			}
		}
		return list;
	}

	public void setSentAltinn(String id, int receiptId) {
		if (logger.isDebugEnabled()) logger.debug("setSentAltinn() id=" + id);
		String sql = "UPDATE FORSENDELSESARKIV SET ALTINN_SENDT=SYSDATE, ALTINN_RECEIPT_ID=? WHERE ID=?";
		jdbcTemplate.update(sql, new Object[]{receiptId, id});
	}

	public void setSentNorgeDotNo(String id) {
		if (logger.isDebugEnabled()) logger.debug("setSentNorgeDotNo() id=" + id);
		String sql = "UPDATE FORSENDELSESARKIV SET NORGEDOTNO=SYSDATE WHERE ID=?";
		jdbcTemplate.update(sql, new Object[]{id});
	}

	public void setSentEmail(String id) {
		if (logger.isDebugEnabled()) logger.debug("setSentEmail() id=" + id);
		String sql = "UPDATE FORSENDELSESARKIV SET EPOST_SENDT=SYSDATE WHERE ID=?";
		jdbcTemplate.update(sql, new Object[]{id});
	}

	public void setPrinted(String id, PrintReceipt printReceipt) {
		String sql = "UPDATE FORSENDELSESARKIV SET UTSKREVET=SYSDATE,STOPPET=SYSDATE,PRINT_ID=?,ANTALLSIDER=? WHERE ID=?";
		if (logger.isDebugEnabled())
			logger.debug(String.format("Updating: %s, setting print_id = %s, antallSider= %s", id, printReceipt.getPrintId(), printReceipt.getPageCount()));
		jdbcTemplate.update(sql, new Object[]{printReceipt.getPrintId(), printReceipt.getPageCount(), id});
	}

	public void confirm(String id) { // Was read
		String sql = "UPDATE FORSENDELSESARKIV SET LEST=SYSDATE,STOPPET=SYSDATE WHERE LEST IS NULL AND ID=?";
		logger.info(String.format("Forsendelse ID=%s, was read", id));
		jdbcTemplate.update(sql, new Object[]{id}, new int[]{java.sql.Types.VARCHAR});
	}

	public void setUnread(String id) {
		String sql = "UPDATE FORSENDELSESARKIV SET LEST=NULL, STOPPET=NULL WHERE ID=?";
		jdbcTemplate.update(sql, new Object[]{id});
	}

	/**
	 * Sjekk om angitt juridik enhet har anledning til å se dokumentet tilhørende angitt forsendelses-id
	 */
	public void authorize(String id, JuridiskEnhet juridiskEnhet) {
		final String sqlFnr = "SELECT COUNT(*) FROM FORSENDELSESARKIV WHERE ID=? AND FODSELSNR=?";
		final String sqlOrg = "SELECT COUNT(*) FROM FORSENDELSESARKIV WHERE ID=? AND ORGNR=?";
		String sql = (juridiskEnhet instanceof Fodselsnr) ? sqlFnr : sqlOrg;
		String juridiskEnhetValue = juridiskEnhet.getValue();
		String msg = String.format("Unable to authorize JuridiskEnhet=%s for access to forsendelsesid=%s.", juridiskEnhetValue, id);
		Object[] args = new Object[]{id, juridiskEnhetValue};
		int[] types = new int[]{java.sql.Types.VARCHAR, java.sql.Types.CHAR};
		int count = 0;
		try {
			count = jdbcTemplate.queryForInt(sql, args, types);
		} catch (Exception e) {
			throw new RuntimeException(msg, e);
		}
		if (1 > count) throw new AccessControlException(msg);
	}

	public void remove(String id) {
		logger.info("Removing forsendelse: " + id);
		/* Take this back when fix verified in prod. -- Einar  { */
		//String sql = "DELETE FROM FORSENDELSESARKIV WHERE ID=?";
		//jdbcTemplate.update( sql, new Object[] { id } );
		//fileStore.remove( id );
		this.stop( id );
		/* Take this back when fix verified in prod. -- Einar } */
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
		List<String> list = convertListOfIds(jdbcTemplate.queryForList(sql, new Object[]{timestamp}));
		int count = 0;
		for (String id : list) {
			try {
				remove(id);
				count++;
			} catch (RuntimeException e) {
				logger.warn(String.format("Probems removing old entry. Id:%s Exception: %s", id, e.getMessage()));
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
		List<String> list = convertListOfIds(jdbcTemplate.queryForList(sql, new Object[]{timestamp}));
		int count = 0;
		for (String id : list) {
			try {
				remove(id);
				count++;
			} catch (RuntimeException e) {
				logger.warn(String.format("Probems removing unreachable entry. Id:%s Exception: %s", id, e.getMessage()));
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
		List<?> list = jdbcTemplate.queryForList(sql, new Object[]{timestamp});
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
		if (logger.isInfoEnabled()) logger.info("Stopping forsendelse " + id);
		String sql = "UPDATE FORSENDELSESARKIV SET STOPPET=SYSDATE WHERE ID=?";
		jdbcTemplate.update(sql, new Object[]{id});
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
		int rowsUpdatedCount = jdbcTemplate.update(sql, new Object[]{printed.getAntallSiderPostlagt(), printed.getTidspunktPostlagt(), forsendelsesId});
		if (rowsUpdatedCount != 1) {
			throw new RuntimeException("Nonexisting forsendelse: " + forsendelsesId);
		}
	}

	/**
	 * Finn forsendelser som mot formodning ikke er sendt i posten av PrintserviceProvider
	 */
	public List<String> retrieveFailedToPrint() {
		final String sql = "SELECT ID FROM FORSENDELSESARKIV WHERE UTSKREVET IS NOT NULL AND TIDSPUNKTPOSTLAGT IS NULL AND UTSKREVET <= ? AND UTSKREVET >= ?";
		final List<String> ids = new ArrayList<String>();
		long now = System.currentTimeMillis();
		BusinessCalendar businessCalendar = PresentDayBusinessCalendarForNorway.getInstance();
		Date failedToPrintAlertWindowStart = failedToPrintAlertWindowStart( now, this.failedToPrintAlertWindowStartDay, businessCalendar );
		Date failedToPrintAlertWindowEnd = failedToPrintAlertWindowStart( now, this.failedToPrintAlertWindowEndDay, businessCalendar );
		List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql, new Object[]{failedToPrintAlertWindowStart, failedToPrintAlertWindowEnd});
		if (rows != null) {
			for (Map<String, Object> row : rows) {
				if (row == null) continue;
				Object id = row.get("ID");
				if (id != null && id instanceof String) ids.add((String) id);
			}
		}
		return ids;
	}

	static Date failedToPrintAlertWindowStart( long now, int failedToPrintAlertWindowStartDay, BusinessCalendar businessCalendar ) {
		Date date = new Date( now - failedToPrintAlertWindowStartDay * 1000L * 60 * 60 * 24 );
		return businessCalendar.previousWorkday( date );
	}

	static Date failedToPrintAlertWindowEnd( long now, int failedToPrintAlertWindowEndDay, BusinessCalendar businessCalendar ) {
		return new Date( now - failedToPrintAlertWindowEndDay * 1000L * 60 * 60 * 24 );
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
	 *
	 * @param forsendelse
	 * @param nesteforsok
	 */
	public void markForsendelseFailed(Forsendelse forsendelse, Date nesteforsok) {
		String sql = "UPDATE FORSENDELSESARKIV SET NESTE_FORSOK=? WHERE ID=?";

		int rowsUpdatedCount = jdbcTemplate.update(sql, new Object[]{nesteforsok, forsendelse.getId()});
		if (rowsUpdatedCount != 1) {
			throw new RuntimeException("Nonexisting forsendelse: " + forsendelse.getId());
		}
		forsendelse.setNesteForsok(nesteforsok);
	}
}
