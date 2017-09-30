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
		String result = null;
		Connection connection=getConnection();
		boolean notexist = false;
		try
		{
			result=super.search(text);
		}
		catch(Exception e)
		{
			notexist = true;
		}
		if(!notexist)
		{
			return result;
		}
		else 
		{
			try 
			{
				PreparedStatement stmt=connection.prepareStatement(
				"SELECT response FROM chatbotdb WHERE keyword like concat(?)");
				stmt.setString(1, text);
				ResultSet rs=stmt.executeQuery();
				rs.next();
				result=rs.getString(1);
				rs.close();
				stmt.close();
				connection.close();
			}
			catch (Exception e) 
			{
				log.info("Exception while reading file: {}", e.toString());
			} 
		}
		if (result != null)
			return result;
		throw new Exception("NOT FOUND");
    }

	
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
