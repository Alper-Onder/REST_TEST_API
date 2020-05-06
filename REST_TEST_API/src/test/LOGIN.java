package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;

import java.util.Date;
import java.util.SimpleTimeZone;
import java.util.UUID; 

//http://localhost:8080/REST_TEST_API/rest/login?ID=prodian&PS=prodian1
@Path("/login")
public class LOGIN {

	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public String LOGIN_FUNC
	(
			@FormParam("ID") String _id, 
			@FormParam("PS") String _password
	)
	{	
		return LOGIN_FNC(_id,_password);
	}
	
	@GET
	@Path("/G")
	@Produces(MediaType.TEXT_HTML)
	public String LOGIN_FUNC_2
	(
			@QueryParam("ID") String _id, 
			@QueryParam("PS") String _password
	)
	{	
		return LOGIN_FNC(_id,_password);
	}
	
	private String LOGIN_FNC(String _id, String _password)
	{
		try
    	{
			SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  		formatter.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
	  		String _d =formatter.format(new Date(System.currentTimeMillis()));
	    	System.out.println("login: " +_d + " " + _id + " " + _password );

    	}catch(Exception e) {}
		
		
		JSONObject jo = new JSONObject(); 
		int _login_result = DB_LOGIN(_id,_password);
		
		jo.put("LOGIN_RESULT", _login_result == -1 ? -1 : _login_result == 0?0:1);
		
		if(_login_result>0)
		{
			String _r = OPEN_SESSION(_login_result);
			jo.put("SESSION",_r);
		}
		else	
		{
			jo.put("SESSION","");
		}
		
		return jo.toString();
	}
	
	private String OPEN_SESSION(int _ID)
	{
		String _ses_id;
		do
		{
			_ses_id = UNIQUE_SESSION_ID_CREATOR();
		}while(!CHECK_SESSION(_ses_id));
	
		boolean _b =  REGISTER_SESSION(_ID,_ses_id);
		
		if(_b)
			return _ses_id;
		return "";
		
	}
	
	private String UNIQUE_SESSION_ID_CREATOR()
	{
		String _ID = UUID.randomUUID().toString(); 
		//System.out.println("UUID1: " + _ID);
		_ID.replaceAll("-","");
		//System.out.println("UUID2: " + _ID);
		return ENC.ENCRYPT(_ID.substring(0,15))+""+ENC.ENCRYPT(_ID.substring(16));
	}

	private boolean CHECK_SESSION(String _ses)
	{
		 Connection conn = null;
		 Statement stmt = null;
		 boolean _val = false;
		 
		 try{
		      Class.forName("com.mysql.jdbc.Driver");
		      conn = DriverManager.getConnection(CONNECTION.DB_URL,CONNECTION.USER,CONNECTION.PASS);
		      stmt = conn.createStatement();
		      String sql;
		      sql = "SELECT SESSION_KEY FROM SESSIONS WHERE SESSION_KEY='"+_ses+"'";
		      ResultSet rs = stmt.executeQuery(sql);

		      if(rs.next()==false)
		      {
		    	  _val = true;
		      }
		    
		      rs.close();
		      stmt.close();
		      conn.close();
		   }catch(SQLException se){
		      //Handle MESSAGES for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle MESSAGES for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            stmt.close();
		      }catch(SQLException se2){
		      }// nothing we can do
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
		   return _val;
	}
	

	private boolean REGISTER_SESSION(int _ID, String _KEY)
	{
		 Connection conn = null;
		 Statement stmt = null;
		 boolean _val = false;
		 
		 try{
		      Class.forName("com.mysql.jdbc.Driver");
		      conn = DriverManager.getConnection(CONNECTION.DB_URL,CONNECTION.USER,CONNECTION.PASS);
		      stmt = conn.createStatement();     
		      // note that i'm leaving "date_created" out of this insert statement
		      stmt.executeUpdate("INSERT INTO SESSIONS (USER_ID, SESSION_KEY) "
		          +"VALUES ("+_ID+", '"+_KEY+"')");
		      _val = true;
		      stmt.close();
		      conn.close();
		   }catch(SQLException se){
		      //Handle MESSAGES for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle MESSAGES for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            stmt.close();
		      }catch(SQLException se2){
		      }// nothing we can do
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
		   return _val;
	}

	private int DB_LOGIN(String _id, String _password)
	{
		 Connection conn = null;
		 Statement stmt = null;
		 int _val = -1;
		 
		 try{
		      Class.forName("com.mysql.jdbc.Driver");
		      //System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(CONNECTION.DB_URL,CONNECTION.USER,CONNECTION.PASS);
		      stmt = conn.createStatement();
		      String sql;
		      sql = "SELECT * FROM USERS WHERE (((USERNAME='"+_id+"') OR (EMAIL='"+_id+"')) AND (PASSWORD='"+ENC.ENCRYPT(_password)+"'))";
		      ResultSet rs = stmt.executeQuery(sql);

		      if(rs.next())
		      {
		    	  _val = rs.getInt("ID");
		      }
		      else
		      {
		    	  _val = 0;
		      }
		      rs.close();
		      stmt.close();
		      conn.close();
		   }catch(SQLException se){
		      //Handle MESSAGES for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle MESSAGES for Class.forName
		      e.printStackTrace();
		   }finally{
		      //finally block used to close resources
		      try{
		         if(stmt!=null)
		            stmt.close();
		      }catch(SQLException se2){
		      }// nothing we can do
		      try{
		         if(conn!=null)
		            conn.close();
		      }catch(SQLException se){
		         se.printStackTrace();
		      }//end finally try
		   }//end try
		   return _val;
	}
	
}


