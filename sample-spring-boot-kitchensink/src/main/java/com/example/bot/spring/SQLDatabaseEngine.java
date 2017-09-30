package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.net.URISyntaxException;
import java.net.URI;

@Slf4j
public class SQLDatabaseEngine extends DatabaseEngine {
	@Override
	public String search(String text) throws Exception {
		String result=null;
		boolean exist=true;
		try{result=super.search(text);
				}catch(Exception e){exist=false;}
		if(exist){return result;}
		else {
			try {
				Connection connection=getConnection();
				PreparedStatement stmt=connection.prepareStatement(
				"SELECT response FROM chatbotdb WHERE keyword like concat('%', ?, '%')");
				stmt.setString(1, text);
				ResultSet rs=stmt.executeQuery();
				rs.next();
				result=rs.getString(1);
				rs.close();
				stmt.close();
				connection.close();}
			catch (Exception e) {
			log.info("Exception while reading file: {}", e.toString());
		} 
		}
		if (result != null)
			return result;
		throw new Exception("NOT FOUND");
    }
	private final String FILENAME = "/static/database.txt";

	
	
	private Connection getConnection() throws URISyntaxException, SQLException {
		Connection connection;
		URI dbUri = new URI(System.getenv("DATABASE_URL"));

		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() +  "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

		log.info("Username: {} Password: {}", username, password);
		log.info ("dbUrl: {}", dbUrl);
		
		connection = DriverManager.getConnection(dbUrl, username, password);

		return connection;
	}

}
