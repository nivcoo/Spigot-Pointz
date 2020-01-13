package fr.nivcoo.pointz.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class DataBase {
	private String host;
	private String name;
	private String user;
	private String pass;
	private String url;
	private Connection conn;

	public DataBase(String h, String n, String u, String p) {
		this.host = h;
		this.name = n;
		this.user = u;
		this.pass = p;
		this.url = "jdbc:mysql://" + this.host + "/" + this.name;
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void connection() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}

		try {
			this.conn = DriverManager.getConnection(this.url, this.user, this.pass);

		} catch (SQLException e) {
		}
	}

	public boolean connected() {
		return this.conn != null;
	}

	private void connectIfNot() {
		if (!this.connected())
			this.connection();
	}

	public void disconnection() {
		if (this.connected())
			this.conn = null;
	}

	public Connection getConnection() throws SQLException {

		return DriverManager.getConnection(this.url, this.user, this.pass);

	}

	public ResultSet getResultSet(String request) {

		this.connectIfNot();

		try {
			Statement state = this.conn.createStatement();
			return state.executeQuery(request);
		} catch (SQLException e) {
			// e.printStackTrace();
		}
		return null;
	}

	public Connection getConn() {
		return conn;
	}

	public void setConn(Connection conn) {
		this.conn = conn;
	}

}