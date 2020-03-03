package fr.nivcoo.pointz.utils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.zaxxer.hikari.HikariDataSource;

public class DataBase {
	private Connection conn;
	private HikariDataSource hikari;
	

	public DataBase(String h, String n, String u, String pass, String p) {
		
		hikari = new HikariDataSource();
        hikari.setDataSourceClassName("com.mysql.jdbc.jdbc2.optional.MysqlDataSource");
        hikari.addDataSourceProperty("serverName", h);
        hikari.addDataSourceProperty("port", p);
        hikari.addDataSourceProperty("databaseName", n);
        hikari.addDataSourceProperty("user", u);
        hikari.addDataSourceProperty("password", pass);
	}

	public void connection() {

		try {
			this.conn = hikari.getConnection();
		} catch (SQLException e) {
			e.printStackTrace();
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
		return hikari.getConnection();

		//return DriverManager.getConnection(this.url, this.user, this.pass);

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