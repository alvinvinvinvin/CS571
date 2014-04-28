import java.sql.*;
import java.util.*;
import java.text.*;

public class DataStore {
	private static final String USER_TABLE_NAME = "user";
	private static final String SUBSCRIBE_TABLE_NAME = "subscribe";
	private static final String CHATLOG_TABLE_NAME = "chatlog";
	private static final String DIRECTORY_OF_DB = "C:\\Users\\han\\workspace\\hanTest3\\";
	Connection con;

	public DataStore(String db) {
		try {
			Class.forName("org.sqlite.JDBC");
			con = DriverManager.getConnection("jdbc:sqlite:" + db);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}

	/*
	 * User operations
	 * 
	 */

	public synchronized String getUsernames() {
		try {
			Statement s = con.createStatement();
			ResultSet r = s.executeQuery("select username from "
					+ USER_TABLE_NAME);
			String user = "";
			while (r.next()) {
				user =user + r.getString(1)+",";
			}
			return user;

		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	public synchronized boolean checkUserExisted(String username) {
		try {
			Statement s = con.createStatement();
			ResultSet r = s.executeQuery("select username from "
					+ USER_TABLE_NAME + " where username = '" + username + "'");
			if (r.next())
				return true;
			else
				return false;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return false;

	}

	public synchronized boolean loginCheck(final String username,
			final String password) {
		try {
			Statement s = con.createStatement();
			ResultSet r = s.executeQuery("select * from " + USER_TABLE_NAME
					+ " where username ='" + username + "' and password = '"
					+ password + "'");
			if (r.next()) {
				return true;
			}
			return false;
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return false;
	}


	public synchronized boolean insertUser(String username, String password) {
		try {
			Statement s = con.createStatement();
			s.executeUpdate("insert into " + USER_TABLE_NAME + " values ('"
					+ username + "', '" + password + "')");
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

	public synchronized boolean deleteUser(String username) {
		try {
			Statement s = con.createStatement();
			s.executeUpdate("delete from " + USER_TABLE_NAME
					+ " where username = '" + username + "'");
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
	
	/*
	 * Subscribe operations
	 * 
	 * 
	 */

	public synchronized boolean createSubscribe(final String reader,
			final String author) {

		try {
			Statement s = con.createStatement();
			s.executeUpdate("insert into " + SUBSCRIBE_TABLE_NAME
					+ " values ('" + reader + "','" + author + "')");
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
	
	public synchronized String getSubscribe(final String reader){
		try{
			Statement s = con.createStatement();
			ResultSet r = s.executeQuery("select author from "+SUBSCRIBE_TABLE_NAME+" where reader='"+reader+"'");
			String authors="";
			while(r.next())
			{
				authors = authors+r.getString(1)+",";
			}
			return authors;
		}catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
	
	public synchronized boolean existedSubscribe(final String reader, final String author){
		try{
			Statement s = con.createStatement();
			ResultSet r = s.executeQuery("select * from "+SUBSCRIBE_TABLE_NAME+" where reader='"+reader+"' and author='"+author+"'");
			if(r.next())
				return true;
			else return false;
		}catch (SQLException e) {
			return false;
		}
	}

	public synchronized boolean unSubscribe(final String author,
			final String reader) {
		try {
			Statement s = con.createStatement();
			s.executeUpdate("delete from " + SUBSCRIBE_TABLE_NAME
					+ " where author = '" + author + "' and reader='" + reader
					+ "'");
			return true;
		} catch (SQLException e) {
			return false;
		}
	}

//	public synchronized boolean updateSubscribe(final String author,
//			final String reader, final String date) {
//		try {
//			Statement s = con.createStatement();
//			s.executeUpdate("update " + SUBSCRIBE_TABLE_NAME
//					+ " set date = '" + date + "' where author = '"
//					+ author + "' and reader = '" + reader + "'");
//			return true;
//		} catch (SQLException e) {
//			return false;
//		}
//	}

	
	/*
	 * 
	 * chatlog operations
	 * 
	 */
	public synchronized boolean createChatlog(final String author,
			final String content, final String date) {
		try {
			Statement s = con.createStatement();
			s.executeUpdate("insert into " + CHATLOG_TABLE_NAME + " values ('"+ author + "','" + content + "', '" + date + "')");
			return true;
		} catch (SQLException e) {
			return false;
		}
	}
	
	public synchronized String getChatlog(final String reader,  final String date){
		try{
			String strAuthors = getSubscribe(reader);
			String[] authors = strAuthors.split(",");
			
			String chatlogs = "";
			for(int i=0; i<authors.length; i++)
			{
				String author = authors[i];
				
				Statement sToChat = con.createStatement();
				ResultSet r = sToChat.executeQuery("select * from "+ CHATLOG_TABLE_NAME + " where author='"+author+"' and date >='"+date+"'");
				String chatlogFromOneAuthor = "";
				while (r.next()) {
					chatlogFromOneAuthor =chatlogFromOneAuthor + r.getString("author")+"|"+ r.getString("content") + "|" + r.getString("date") + ",";
				}
				chatlogs = chatlogs + chatlogFromOneAuthor;
				author = null;
			}
			return chatlogs;
		}catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return null;
	}
}
