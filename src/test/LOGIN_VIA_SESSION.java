package test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;
import java.util.UUID; 

@Path("/sl")
public class LOGIN_VIA_SESSION {


	@GET
	@Produces(MediaType.TEXT_HTML)
	public String LOGIN_FUNC
	(
		@QueryParam("S") String _ses
	)
	{
		
		 JSONObject jo = CHECK_SESSION_LOGIN(_ses);
		
		return jo.toString();
	}
	
	private JSONObject CHECK_SESSION_LOGIN(String _ses)
	{
		 Connection conn = null;
		 Statement stmt = null;
		 String _USER_NAME = "";
		 JSONObject jo = new JSONObject();
		 
		 try{
		      Class.forName("com.mysql.jdbc.Driver");
		      conn = DriverManager.getConnection(CONNECTION.DB_URL,CONNECTION.USER,CONNECTION.PASS);
		      stmt = conn.createStatement();
		      String sql;
		    
		     sql = "SELECT USERS.ID, USERS.USERNAME, SESSIONS.SESSION_KEY FROM SESSIONS LEFT JOIN USERS ON SESSIONS.USER_ID = USERS.ID WHERE SESSIONS.SESSION_KEY = '"+_ses+"'";
		      /*
		      sql = "SELECT USERS.ID, SESSIONS.SESSION_KEY"+
		      " FROM SESSIONS LEFT JOIN USERS"+
		      " ON SESSIONS.USER_ID = USERS.ID"+
		      " WHERE SESSIONS.SESSION_KEY = '"+_ses+"'";*/
		     
		      
		      ResultSet rs = stmt.executeQuery(sql);
		    
		      if(rs.next()==true)
		      {
		    	  
		    	 _USER_NAME = rs.getString("USERNAME");
		      }
		    
		      System.out.println("UN: " +  _USER_NAME);
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
		 jo.put("LOGIN_RESULT",_USER_NAME.equals("")?0:1);
		 jo.put("USERNAME",_USER_NAME);
		 return jo;
	}
}


