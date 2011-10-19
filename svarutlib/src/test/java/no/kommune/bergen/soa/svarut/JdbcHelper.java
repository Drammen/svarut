package no.kommune.bergen.soa.svarut;

import java.io.*;
import java.net.URL;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class JdbcHelper {
	JdbcTemplate jdbcTemplate;

	public void createTable( String tableName ) {

        InputStream stream= JdbcHelper.class.getClassLoader().getResourceAsStream(tableName + ".sql");
		BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(stream));
        String ddl = "";
        String line = "";
		try {
            while ( null != (line = bufferedReader.readLine()) )
			    ddl += line;
		} catch (Exception e) {
			throw new RuntimeException( "Problems reading ddl: " + tableName, e );
		} finally {
			if (bufferedReader != null) try {
				bufferedReader.close();
			} catch (IOException e) {
				;
			}
		}
		JdbcTemplate jdbcTemplate = getJdbcTemplate();
		jdbcTemplate.execute( "DROP TABLE " + tableName + " if exists" );
		jdbcTemplate.execute( ddl );
	}

	public JdbcTemplate getJdbcTemplate() {
		if (this.jdbcTemplate == null) {
			this.jdbcTemplate = createJdbcTemplate();
		}
		return this.jdbcTemplate;
	}

	public JdbcTemplate createJdbcTemplate() {
		DriverManagerDataSource dataSource = createDataSource();
		JdbcTemplate jdbcTemplate = new JdbcTemplate( dataSource );
		return jdbcTemplate;
	}

	public DriverManagerDataSource createDataSource() {
		return ServiceContext.createTestDataSource();
	}

}
