package dao;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.SQLSyntaxErrorException;

public class UsersDAO {

	private Connection conn;

	public UsersDAO(Connection connection) {
		this.conn = connection;
	}

	public void setupUsersTable() throws SQLException {
		String query = "CREATE TABLE users " +
			"(username VARCHAR(255) PRIMARY KEY NOT NULL, " +
			" password VARBINARY(255) NOT NULL)";
		Statement stmt = conn.createStatement();
		stmt.executeUpdate(query);
	}

	public boolean checkCredentials(String username, String pass) {
		byte[] password;
		String query = "SELECT username, password FROM users WHERE username = ? AND password = ?";
		ResultSet res;
		boolean ret = false;
		try {
			PreparedStatement stmt = conn.prepareStatement(query);
			password = MessageDigest.getInstance("SHA-256").digest(pass.getBytes(StandardCharsets.UTF_8));
			System.err.println("Trying " + pass);
			stmt.setString(1, username);
			stmt.setBytes(2, password);
			res = stmt.executeQuery();
			ret = res.next();
        } catch (Exception ex) {
			ex.printStackTrace();
			return ret;
		}

		return ret;
	}

	public void initUsers() throws SQLException, NoSuchAlgorithmException {
		String username;
		byte[] password;
		String query = "INSERT INTO users(username, password) VALUES (?, ?)";
		PreparedStatement stmt = conn.prepareStatement(query);
		username = "daniel";
		String pass = "test";
		password = MessageDigest.getInstance("SHA-256").digest(pass.getBytes(StandardCharsets.UTF_8));
		stmt.setString(1, username);
		stmt.setBytes(2, password);
		stmt.executeUpdate();

		username = "adem";
		password = MessageDigest.getInstance("SHA-256").digest(pass.getBytes(StandardCharsets.UTF_8));
		stmt.setString(1, username);
		stmt.setBytes(2, password);
		stmt.executeUpdate();
	}
}
