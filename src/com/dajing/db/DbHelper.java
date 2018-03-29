package com.dajing.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.dajing.util.TimeUtil;

public class DbHelper {

	public static final String db_url_sqlite = "jdbc:sqlite:dajing_shualian";
	public static final String db_url_mysql = "jdbc:mysql://192.168.3.84:3306/shualian";
	public static final String db_username = "liter";
	public static final String db_password = "liter822";

	
	public static final String db_type_sqlite = "sqlite";
	public static final String db_type_mysql = "mysql";

	public static final int UNREAD = 0;
	public static final int READ = 1;

	private static Logger logger = Logger.getLogger(DbHelper.class);
	private static Connection conn;
	private String db_type;

	public DbHelper(String db_type) {
		getConn(db_type);
	}

	public DbHelper() {
		this.db_type = db_type_mysql;
	}

	/**
	 * 获取连接
	 * 
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Connection getConnSqlite() {
		return getConn(db_type_mysql);
	}

	/**
	 * 获取连接
	 * 
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Connection getConn(String type) {
		this.db_type = type;
		try {
			if (conn != null && !conn.isClosed()) {
				return conn;
			} else {
				if (db_type_sqlite.equals(db_type)) {
					Class.forName("org.sqlite.JDBC");
					conn = DriverManager.getConnection(db_url_sqlite, null, null);
				} else if (db_type_mysql.equals(db_type)) {
					Class.forName("com.mysql.jdbc.Driver");
					conn = DriverManager.getConnection(db_url_mysql, db_username, db_password);
				}
				return conn;
			}
		} catch (SQLException se) {
			se.printStackTrace();
		} catch (ClassNotFoundException ce) {
			ce.printStackTrace();
		}
		return conn;
	}

	/**
	 * 获取连接
	 * 
	 * @return
	 * @throws ClassNotFoundException
	 * @throws SQLException
	 */
	public Connection getConn() {

		return getConn(db_type);
	}

	/**
	 * area地理位置
	 * 
	 * @param phone
	 * @param sid
	 * @param location
	 */
	public void insertArea(String province, String city, String district, String longitude, String latitude) {
		PreparedStatement prep = null;
		try {
			if (conn == null || conn.isClosed()) {
				conn = getConn();
			}
			prep = conn.prepareStatement(
					"insert into area(province,city,district,longitude" + ",latitude) " + "values (?, ?, ?, ?, ?);");

			prep.setString(1, province);
			prep.setString(2, city);
			prep.setString(3, district);
			prep.setString(4, longitude);
			prep.setString(5, latitude);

			prep.executeUpdate();

		} catch (SQLException e) {
			if (e.getMessage().contains("SQLITE_CONSTRAINT_UNIQUE")) {
				logger.debug("数据库中已有该" + province + "-" + city + "-" + district + "手机号码数据");
			} else {
				e.printStackTrace();
			}
		} finally {
			try {
				if (prep != null)
					prep.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void insertAd(int areaid, int adId, String adTitle, String imgUrl, String industry, String position,
			int userId, String userName, String userPhotoUrl, double videoTime, int visitCount) {
		PreparedStatement prep = null;
		try {
			if (conn == null || conn.isClosed()) {
				conn = getConn();
			}
			prep = conn.prepareStatement("insert into ad(adId,adTitle,imgUrl,industry,"
					+ "position,userId,userName,userPhotoUrl" + ",videoTime,visitCount,areaid,updateTime) "
					+ "values (?, ?, ?, ?, " + "?, ?, ?, ?, " + "?, ?, ?,?);");

			prep.setInt(1, adId);
			prep.setString(2, adTitle);
			prep.setString(3, imgUrl);
			prep.setString(4, industry);
			prep.setString(5, position);
			prep.setInt(6, userId);
			prep.setString(7, userName);
			prep.setString(8, userPhotoUrl);
			prep.setDouble(9, videoTime);
			prep.setInt(10, visitCount);
			prep.setInt(11, areaid);
			prep.setString(12, TimeUtil.getCurrentTime());

			prep.executeUpdate();

		} catch (SQLException e) {
			if (e.getMessage().contains("SQLITE_CONSTRAINT_UNIQUE")) {
				logger.debug("数据库中已有该" + adId + "广告");
			} else {
				e.printStackTrace();
			}
		} finally {
			try {
				if (prep != null)
					prep.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * ailezan获取手机号码记录
	 * 
	 * @param phone
	 * @param sid
	 * @param location
	 */
	public void insertAilezanPhone(String phone, String sid, String location) {
		PreparedStatement prep = null;
		try {
			if (conn == null || conn.isClosed()) {
				conn = getConn();
			}
			prep = conn.prepareStatement("insert into ailezan(phone,sid,addtime,addtimestamp" + ",status,location) "
					+ "values (?, ?, ?, ?, ?, ?);");

			prep.setString(1, phone);
			prep.setString(2, sid);
			prep.setString(3, TimeUtil.getCurrentTime());
			prep.setLong(4, System.currentTimeMillis());
			prep.setInt(5, 0);
			prep.setString(6, location);

			prep.executeUpdate();

		} catch (SQLException e) {
			if (e.getMessage().contains("SQLITE_CONSTRAINT_UNIQUE")) {
				logger.debug("数据库中已有该" + phone + "手机号码数据");
			} else {
				e.printStackTrace();
			}
		} finally {
			try {
				if (prep != null)
					prep.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 插入ailezan获取短信记录
	 * 
	 * @param phone
	 * @param sid
	 * @param location
	 */
	public void insertAilezanMessage(String phone, String sid, String messagecontent, String fromphone) {
		PreparedStatement prep = null;
		try {
			if (conn == null || conn.isClosed()) {
				conn = getConn();
			}
			prep = conn.prepareStatement("insert into message(phone,sid,messagecontent,fromphone,addtime,addtimestamp"
					+ ",status) " + " values (?, ?, ?, ?, ?,?,?);");

			prep.setString(1, phone);
			prep.setString(2, sid);
			prep.setString(3, messagecontent);
			prep.setString(4, fromphone);
			prep.setString(5, TimeUtil.getCurrentTime());
			prep.setLong(6, System.currentTimeMillis());
			prep.setInt(7, 0);

			prep.executeUpdate();

		} catch (SQLException e) {
			if (e.getMessage().contains("SQLITE_CONSTRAINT_UNIQUE")) {
				logger.debug("数据库中已有该" + phone + "手机号码数据");
			} else {
				e.printStackTrace();
			}
		} finally {
			try {
				if (prep != null)
					prep.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 插入ailezan获取短信记录
	 * 
	 * @param phone
	 * @param sid
	 * @param location
	 */
	public void insertUsername(String username, String password, String androidid, String imsi, String imei,
			String deviceId) {
		PreparedStatement prep = null;
		try {
			if (conn == null || conn.isClosed()) {
				conn = getConn();
			}
			prep = conn.prepareStatement("insert into username(username,password,androidid,imsi,imei,deviceId"
					+ ",addtime) " + " values (?, ?, ?, ?, ?,?,?);");

			prep.setString(1, username);
			prep.setString(2, password);
			prep.setString(3, androidid);
			prep.setString(4, imsi);
			prep.setString(5, imei);
			prep.setString(6, deviceId);
			prep.setString(7, TimeUtil.getCurrentTime());

			prep.executeUpdate();

		} catch (SQLException e) {
			if (e.getMessage().contains("SQLITE_CONSTRAINT_UNIQUE")) {
				logger.debug("数据库中已有该" + username + "手机号码数据");
			} else {
				e.printStackTrace();
			}
		} finally {
			try {
				if (prep != null)
					prep.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 插入ailezan获取短信记录
	 * 
	 * @param phone
	 * @param sid
	 * @param location
	 */
	public void insertAnswer(int adId, int questionId, String questionContent, int answerId, String answerContent) {
		PreparedStatement prep = null;
		try {
			if (conn == null || conn.isClosed()) {
				conn = getConn();
			}
			prep = conn.prepareStatement(
					"insert into answer(adId,questionId,questionContent,answerId,answerContent,updateTime) "
							+ " values (?, ?, ?, ?, ?, ?);");

			prep.setInt(1, adId);
			prep.setInt(2, questionId);
			prep.setString(3, questionContent);
			prep.setInt(4, answerId);
			prep.setString(5, answerContent);
			prep.setString(6, TimeUtil.getCurrentTime());

			prep.executeUpdate();

		} catch (SQLException e) {
			if (e.getMessage().contains("SQLITE_CONSTRAINT_UNIQUE")) {
				logger.debug("数据库中已有该" + questionContent + ":" + answerContent + "题目");
			} else {
				e.printStackTrace();
			}
		} finally {
			try {
				if (prep != null)
					prep.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 插入ailezan获取短信记录
	 * 
	 * @param phone
	 * @param sid
	 * @param location
	 */
	public void insertWork(int adId, String username) {
		PreparedStatement prep = null;
		try {
			if (conn == null || conn.isClosed()) {
				conn = getConn();
			}
			prep = conn.prepareStatement("insert into work(adId,username,updateTime) " + " values (?, ?, ?);");

			prep.setInt(1, adId);
			prep.setString(2, username);
			prep.setString(3, TimeUtil.getCurrentTime());

			prep.executeUpdate();

		} catch (SQLException e) {
			if (e.getMessage().contains("SQLITE_CONSTRAINT_UNIQUE")) {
				logger.debug("数据库中已有该" + adId + ":" + username + "答题");
			} else {
				e.printStackTrace();
			}
		} finally {
			try {
				if (prep != null)
					prep.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 插入ailezan获取短信记录
	 * 
	 * @param phone
	 * @param sid
	 * @param location
	 */
	public void insertAnswerLog(String username, int adId, int questionId_1, String questionContent_1,
			int userAnswerId_1, String userAnswerContent_1, int questionId_2, String questionContent_2,
			int userAnswerId_2, String userAnswerContent_2, int status, String message) {
		PreparedStatement prep = null;
		try {
			if (conn == null || conn.isClosed()) {
				conn = getConn();
			}
			prep = conn.prepareStatement(
					"insert into answerlog(username,adId,questionId_1,questionContent_1,userAnswerId_1,userAnswerContent_1,"
							+ "questionId_2,questionContent_2,userAnswerId_2,userAnswerContent_2,status,message,updateTime) "
							+ " values (?, ?, ?, ?, ?, ?, " + "?, ?, ?, ?, ?, " + "?, ?);");

			prep.setString(1, username);
			prep.setInt(2, adId);
			prep.setInt(3, questionId_1);
			prep.setString(4, questionContent_1);
			prep.setInt(5, userAnswerId_1);
			prep.setString(6, userAnswerContent_1);

			prep.setInt(7, questionId_2);
			prep.setString(8, questionContent_2);
			prep.setInt(9, userAnswerId_2);
			prep.setString(10, userAnswerContent_2);

			prep.setInt(11, status);
			prep.setString(12, message);
			prep.setString(13, TimeUtil.getCurrentTime());

			prep.executeUpdate();

		} catch (SQLException e) {
			if (e.getMessage().contains("SQLITE_CONSTRAINT_UNIQUE")) {
				logger.debug("数据库中已有该" + ":" + "题目");
			} else {
				e.printStackTrace();
			}
		} finally {
			try {
				if (prep != null)
					prep.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 更新爱乐赞获取号码状态 0,1,2,3
	 * 
	 * @param phone
	 * @param status
	 * @return
	 */
	public boolean updateAilezanPhoneStatus(String phone, int status) {
		boolean res = false;
		Statement st = null;
		try {
			if (conn == null || conn.isClosed()) {
				conn = getConn();
			}
			st = conn.createStatement();
			res = st.execute("update ailezan set status = " + status + " where phone ='" + phone + "';");

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return res;
	}

	/**
	 * 更新爱乐赞获取号码状态 0,1,2,3
	 * 
	 * @param phone
	 * @param status
	 * @return
	 */
	public boolean updateAilezanMessageStatus(String phone, int status) {
		boolean res = false;
		Statement st = null;
		try {
			if (conn == null || conn.isClosed()) {
				conn = getConn();
			}
			st = conn.createStatement();
			res = st.execute("update message set status = " + status + " where phone ='" + phone + "';");

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return res;
	}

	/**
	 * 更新爱乐赞获取号码状态 0,1,2,3
	 * 
	 * @param phone
	 * @param status
	 * @return
	 */
	public boolean updateAreaVedioNum(int areaid, int vedionum) {
		boolean res = false;
		Statement st = null;
		try {
			if (conn == null || conn.isClosed()) {
				conn = getConn();
			}
			st = conn.createStatement();
			res = st.execute("update area set vedionum = " + vedionum + " where areaid =" + areaid + ";");

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return res;
	}

	/**
	 * 更新用户登录信息
	 * 
	 * @param username
	 * @param sessionId
	 * @param castgc
	 * @return
	 */
	public boolean updateUserLoginInfo(String username, String sessionId, String castgc, int userId, int imUserId,
			String imPassword) {
		boolean res = false;
		Statement st = null;
		try {
			if (conn == null || conn.isClosed()) {
				conn = getConn();
			}
			st = conn.createStatement();
			res = st.execute("update username set sessionId = '" + sessionId //
					+ "', id=" + userId //
					+ ", imUserId=" + imUserId //
					+ ", imPassword='" + imPassword + //
					"', castgc= '" + castgc + "' where username ='" + username + "';");

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return res;
	}

	/**
	 * 更新用户登录信息
	 * 
	 * @param username
	 * @param sessionId
	 * @param castgc
	 * @return
	 */
	public boolean updateAreaVedionum(int areaid, int vedionum) {
		boolean res = false;
		Statement st = null;
		try {
			if (conn == null || conn.isClosed()) {
				conn = getConn();
			}
			st = conn.createStatement();
			res = st.execute("update area set vedionum = " + vedionum //
					+ " where areaid =" + areaid + ";");

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return res;
	}

	/**
	 * 查询单个用户名
	 * 
	 * @param username
	 * @return
	 */
	public JSONObject queryUsername(String username) {
		JSONObject jsonObject = null;
		ResultSet rs = null;
		Statement st = null;

		try {
			if (conn == null || conn.isClosed()) {
				conn = getConn();
			}
			st = conn.createStatement();
			rs = st.executeQuery("select * from username where username = '" + username + "';");
			while (rs.next()) {
				try {
					jsonObject = new JSONObject();
					jsonObject.put("userid", rs.getInt("userid"));
					jsonObject.put("username", rs.getString("username"));
					jsonObject.put("password", rs.getString("password"));
					jsonObject.put("sessionId", rs.getString("sessionId"));
					jsonObject.put("deviceId", rs.getString("deviceId"));
					jsonObject.put("appinfoid", rs.getString("appinfoid"));
					jsonObject.put("androidid", rs.getString("androidid"));
					jsonObject.put("imsi", rs.getString("imsi"));
					jsonObject.put("imei", rs.getString("imei"));
					jsonObject.put("contacts", rs.getString("contacts"));
					jsonObject.put("id", rs.getString("id"));
					jsonObject.put("myInvitationCode", rs.getString("myInvitationCode"));
					jsonObject.put("nickname", rs.getString("nickname"));
					jsonObject.put("slcoinAmount", rs.getInt("slcoinAmount"));
					jsonObject.put("shopId", rs.getString("shopId"));
					jsonObject.put("castgc", rs.getString("castgc"));
					jsonObject.put("addtime", rs.getString("addtime"));
					jsonObject.put("updatetime", rs.getString("updatetime"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonObject;
	}

	/**
	 * 查询单个用户名
	 * 
	 * @param username
	 * @return
	 */
	public JSONObject queryArea(int areaId) {
		JSONObject jsonObject = new JSONObject();
		ResultSet rs = null;
		Statement st = null;

		try {
			if (conn == null || conn.isClosed()) {
				conn = getConn();
			}
			st = conn.createStatement();
			rs = st.executeQuery("select * from area where areaid = '" + areaId + "';");
			while (rs.next()) {
				try {
					jsonObject.put("areaid", rs.getInt("areaid"));
					jsonObject.put("province", rs.getString("province"));
					jsonObject.put("city", rs.getString("city"));
					jsonObject.put("district", rs.getString("district"));
					jsonObject.put("longitude", rs.getDouble("longitude"));
					jsonObject.put("latitude", rs.getDouble("latitude"));
					jsonObject.put("vedionum", rs.getInt("vedionum"));

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonObject;
	}

	/**
	 * 查询所有
	 * 
	 * @param username
	 * @return
	 */
	public List<JSONObject> queryArea() {

		List<JSONObject> array = new ArrayList<JSONObject>();

		ResultSet rs = null;
		Statement st = null;

		try {
			if (conn == null || conn.isClosed()) {
				conn = getConn();
			}
			st = conn.createStatement();
			rs = st.executeQuery("select * from area ");
			while (rs.next()) {
				try {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("areaid", rs.getInt("areaid"));
					jsonObject.put("province", rs.getString("province"));
					jsonObject.put("city", rs.getString("city"));
					jsonObject.put("district", rs.getString("district"));
					jsonObject.put("longitude", rs.getDouble("longitude"));
					jsonObject.put("latitude", rs.getDouble("latitude"));
					jsonObject.put("vedionum", rs.getInt("vedionum"));
					array.add(jsonObject);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return array;
	}

	/**
	 * 查询所有
	 * 
	 * @param username
	 * @return
	 */
	public List<JSONObject> queryAreaOrderBy() {

		List<JSONObject> array = new ArrayList<JSONObject>();

		ResultSet rs = null;
		Statement st = null;

		try {
			if (conn == null || conn.isClosed()) {
				conn = getConn();
			}
			st = conn.createStatement();
			rs = st.executeQuery("select * from area order by  vedionum desc");
			while (rs.next()) {
				try {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("areaid", rs.getInt("areaid"));
					jsonObject.put("province", rs.getString("province"));
					jsonObject.put("city", rs.getString("city"));
					jsonObject.put("district", rs.getString("district"));
					jsonObject.put("longitude", rs.getDouble("longitude"));
					jsonObject.put("latitude", rs.getDouble("latitude"));
					jsonObject.put("vedionum", rs.getInt("vedionum"));
					array.add(jsonObject);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return array;
	}

	public List<JSONObject> queryAilezanPhoneByStatusAndTime(String status, long timestamp) {
		ResultSet rs = null;
		Statement st = null;

		List<JSONObject> jsonObjects = new ArrayList<JSONObject>();

		timestamp = System.currentTimeMillis() - timestamp;

		try {
			if (conn == null || conn.isClosed()) {
				conn = getConn();
			}
			st = conn.createStatement();
			rs = st.executeQuery(
					"select * from ailezan where status = " + status + " and addtimestamp>" + timestamp + ";");
			while (rs.next()) {
				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.put("phoneid", rs.getInt("phoneid"));
					jsonObject.put("phone", rs.getString("phone"));
					jsonObject.put("sid", rs.getString("sid"));
					jsonObject.put("addtime", rs.getString("addtime"));
					jsonObject.put("location", rs.getString("location"));
					jsonObject.put("addtimestamp", rs.getLong("addtimestamp"));
					jsonObject.put("status", rs.getInt("status"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
				jsonObjects.add(jsonObject);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonObjects;
	}

	/**
	 * 查询广告答案
	 * 
	 * @param adId
	 * @param questionContent
	 * @return
	 */
	public JSONObject queryAnswer(int adId, String questionContent) {
		
		JSONObject jsonObject = null;
		
		ResultSet rs = null;
		Statement st = null;
		
		try {
			if (conn == null || conn.isClosed()) {
				conn = getConn();
			}
			st = conn.createStatement();
			rs = st.executeQuery(
					"select * from answer where adId =" + adId + " and questionContent ='" + questionContent + "';");
			while (rs.next()) {
				try {
					jsonObject = new JSONObject();
					jsonObject.put("adId", rs.getInt("adId"));
					jsonObject.put("questionId", rs.getInt("questionId"));
					jsonObject.put("questionContent", rs.getString("questionContent"));
					jsonObject.put("answerId", rs.getInt("answerId"));
					jsonObject.put("answerContent", rs.getString("answerContent"));
					jsonObject.put("updateTime", rs.getString("updateTime"));
					
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonObject;
	}
	/**
	 * 查询答题记录
	 * 
	 * @param adId
	 * @param questionContent
	 * @return
	 */
	public List<JSONObject> queryAnswerLog(int adId) {

		List<JSONObject> jsonObjects = new ArrayList<JSONObject>();

		ResultSet rs = null;
		Statement st = null;

		try {
			if (conn == null || conn.isClosed()) {
				conn = getConn();
			}
			st = conn.createStatement();
			rs = st.executeQuery(
					"select * from answerlog where adId =" + adId + " and status=4 ;");
			while (rs.next()) {
				try {
					JSONObject jsonObject = new JSONObject();
					jsonObject.put("adId", rs.getInt("adId"));
					jsonObject.put("questionId_1", rs.getInt("questionId_1"));
					jsonObject.put("questionContent_1", rs.getString("questionContent_1"));
					jsonObject.put("userAnswerId_1", rs.getInt("userAnswerId_1"));
					jsonObject.put("userAnswerContent_1", rs.getString("userAnswerContent_1"));
					
					jsonObject.put("questionId_2", rs.getInt("questionId_2"));
					jsonObject.put("questionContent_2", rs.getString("questionContent_2"));
					jsonObject.put("userAnswerId_2", rs.getInt("userAnswerId_2"));
					jsonObject.put("userAnswerContent_2", rs.getString("userAnswerContent_2"));
					
					jsonObject.put("status", rs.getString("status"));
					
					jsonObjects.add(jsonObject);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return jsonObjects;
	}

	public boolean deletePhoneMessageByIndex(String index) {
		String sql = "delete from phonemessage where index = '" + index + "'";
		boolean res = false;
		try {
			res = conn.createStatement().execute(sql);
		} catch (Exception e) {
			logger.error(e.toString());
			res = true;
		}
		return res;
	}

	/**
	 * 获取系统当前时间
	 * 
	 * @return
	 */
	public static String getSystenTime() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");// 可以方便地修改日期格式
		String update_time = dateFormat.format(new Date(System.currentTimeMillis()));
		return update_time;
	}

	public int queryIndexByPort(String port) {

		int index = -1;
		ResultSet rs = null;
		Statement st = null;

		try {
			if (conn == null || conn.isClosed()) {
				conn = getConn();
			}
			st = conn.createStatement();
			rs = st.executeQuery("select * from port_info where port = '" + port + "'");
			while (rs.next()) {
				index = rs.getInt("port_index");
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return index;
	}

	public boolean checkAd(int adId) {

		ResultSet rs = null;
		boolean in = false;
		Statement st = null;
		try {
			if (conn == null || conn.isClosed()) {
				conn = getConn();
			}
			st = conn.createStatement();
			rs = st.executeQuery("SELECT COUNT(adId) totalCount FROM ad WHERE adId = '" + adId + "'");
			if (rs.next()) {
				int rowCount = rs.getInt("totalCount");
				if (rowCount > 0) {
					in = true;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return in;
	}

	public boolean checkUsername(String username) {

		ResultSet rs = null;
		boolean in = false;
		Statement st = null;
		try {
			if (conn == null || conn.isClosed()) {
				conn = getConn();
			}
			st = conn.createStatement();
			rs = st.executeQuery("SELECT COUNT(username) totalCount FROM username WHERE username = '" + username + "'");
			if (rs.next()) {
				int rowCount = rs.getInt("totalCount");
				if (rowCount > 0) {
					in = true;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return in;
	}

	public boolean checkAnswer(int adId, String questionContent, String answerContent) {

		ResultSet rs = null;
		boolean in = false;
		Statement st = null;
		try {
			if (conn == null || conn.isClosed()) {
				conn = getConn();
			}
			st = conn.createStatement();
			rs = st.executeQuery("SELECT COUNT(adId) totalCount FROM answer WHERE adId = " + adId
					+ " and questionContent='" + questionContent + "' and answerContent='" + answerContent + "';");
			if (rs.next()) {
				int rowCount = rs.getInt("totalCount");
				if (rowCount > 0) {
					in = true;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return in;
	}

	public boolean checkWork(int adId, String username) {

		ResultSet rs = null;
		boolean in = false;
		Statement st = null;
		try {
			if (conn == null || conn.isClosed()) {
				conn = getConn();
			}
			st = conn.createStatement();
			rs = st.executeQuery("SELECT COUNT(adId) totalCount FROM work WHERE adId = " + adId + " and username='"
					+ username + "';");
			if (rs.next()) {
				int rowCount = rs.getInt("totalCount");
				if (rowCount > 0) {
					in = true;
				}
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
				if (rs != null)
					rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return in;
	}

	public boolean comitSql(String sql) {
		boolean res = false;

		Statement st = null;
		try {

			if (conn == null || conn.isClosed()) {
				conn = getConn();
			}
			st = conn.createStatement();
			res = st.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			try {
				if (st != null)
					st.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		return res;

	}

}
