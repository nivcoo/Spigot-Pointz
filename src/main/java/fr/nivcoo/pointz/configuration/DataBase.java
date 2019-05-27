package fr.nivcoo.pointz.configuration;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
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
			e.printStackTrace();
			System.out.println("[Points] Impossible de se connecter a la base de donn�e");
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

	public String getString(String request, int ci) {
		this.connectIfNot();
		try {
			Statement state = this.conn.createStatement();
			ResultSet result = state.executeQuery(request);
			try {
				while (result.next()) {
					return result.getString(ci);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return "NULL";
	}

	public ResultSet getResultSet(String request) {
		this.connectIfNot();
		try {
			Statement state = this.conn.createStatement();
			return state.executeQuery(request);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public int getInt(String request, int ci) {
		this.connectIfNot();
		try {
			Statement state = this.conn.createStatement();
			ResultSet result = state.executeQuery(request);
			try {
				while (result.next()) {
					return result.getInt(ci);
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public void sendRequest(String request) {
		this.connectIfNot();

		try {
			Statement state = this.conn.createStatement();

			state.executeUpdate(request);
			state.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Erreur ! ");
		}
	}

	public void sendPreparedRequest(String type, String tableName, String columName, int columValue, String whereName,
			String whereValue) {
		this.connectIfNot();

		try {
			PreparedStatement rs = this.conn.prepareStatement(
					type + " " + tableName + " SET " + columName + " = ? WHERE " + whereName + " = ?");
			rs.setString(2, whereValue);
			rs.setInt(1, columValue);
			rs.executeUpdate();
			rs.close();
		} catch (SQLException e) {
			e.printStackTrace();
			System.out.println("Erreur ! ");
		}
	}

}