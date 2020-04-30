package test;


import java.io.File;


import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.SimpleTimeZone;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.GET;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.core.Application;


@Path("/CC")
public class CCLINK
{
	 private static final char[] C = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz123456789".toCharArray();
		
    @GET
	@Produces(MediaType.TEXT_HTML)
	public String GENERATE_HTML(@QueryParam("R") String _link, @QueryParam("L") String _custom, @QueryParam("S") String _session )
	{
    	try
    	{
    		
    	SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  		formatter.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
  		String _d =formatter.format(new Date(System.currentTimeMillis()));
    	System.out.println("CCLINK: " +_d + " " + _link + " " + _custom + " " + _session);
    	}catch(Exception e) {}
    	
		String _expire;
		
		
		 //URL: 0 = ce, 1=okay, 2= used link
		// LOG: 0 = ce, 1=okay, 2=not authorized
		
		JSONObject jo = new JSONObject();
		
		int _log_check = CHECK_LOGIN_TYPE(_session);
		
		if(_log_check == 0)
		{
			 jo.put("RESULT",0);
			 jo.put("SHORT_LINK","");
			 jo.put("LINK","");
			 jo.put("EXPIRE_LINK","");
		}
		else if(_log_check == 1)
		{
			int _custom_check = CHECK_GEN_URLS(_custom);
			if(_custom_check == 0) // CNE
			{
				 jo.put("RESULT",0);
				 jo.put("SHORT_LINK","");
				 jo.put("LINK","");
				 jo.put("EXPIRE_LINK","");
			}
			else if(_custom_check == 1) // OKAY
			{
				do
				{
					_expire = GENERATE_RANDOM();
				}while(CHECK_GEN_URLS(_expire) != 1);
				
				jo = CREATE_LINK(_link,_custom,_expire,_session);
				
			}
			else if(_custom_check == 2) // NOT VALID
			{
				 jo.put("RESULT",2);
				 jo.put("SHORT_LINK","");
				 jo.put("LINK","");
				 jo.put("EXPIRE_LINK","");
			}
		}else if(_log_check == 2)
		{
			 jo.put("RESULT",3); // 3 = NOT AUTHORIZED
			 jo.put("SHORT_LINK","");
			 jo.put("LINK","");
			 jo.put("EXPIRE_LINK","");
		}
		return jo.toString();
	}
    
    private int CHECK_LOGIN_TYPE(String _ses)
	{
		 Connection conn = null;
		 Statement stmt = null;
		 String _USER_NAME = "";
		 int _log = 0;
		 boolean _connect = false;
		 try{
		      Class.forName("com.mysql.jdbc.Driver");
		      conn = DriverManager.getConnection(CONNECTION.DB_URL,CONNECTION.USER,CONNECTION.PASS);
		      stmt = conn.createStatement();
		      String sql;
		    
		      sql = "SELECT USERS.ID, USERS.TYPE, SESSIONS.SESSION_KEY FROM SESSIONS LEFT JOIN USERS ON SESSIONS.USER_ID = USERS.ID WHERE SESSIONS.SESSION_KEY = '"+_ses+"'";

		     
		      
		      ResultSet rs = stmt.executeQuery(sql);
		    
		      
		      if(rs.next()==true)
		      {
		    	  if(rs.getInt("TYPE") == 2)
		    	  {
		    		  _log = 1;
		    	  }
		      }
		      else
		    	  _log = 2;
		      
		    
		      _connect =  true;
		  //    System.out.println("UN: " +  _USER_NAME);
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
		 return _log;
	}

	private String GENERATE_RANDOM()
	{
		String s="";
		int _r = 0;
		int	_c = (int)(Math.random() *6)+3; // 3-8
		for(int i =0; i<_c; i++)
		{
			_r = (int)(Math.random() * C.length);
			s+=C[_r];
		}
		return s;
	}
    

	private JSONObject CREATE_LINK(String _link, String _from, String _expire, String _session)
	{
		 Connection conn = null;
		 Statement stmt = null;
		 JSONObject jo = new JSONObject();
		 
		 int _jo_reflection = 0;
		 String _jo_from = "";
		 String _jo_link = "";
		 String _jo_expire = "";
		 
		 
		 try{
		      Class.forName("com.mysql.jdbc.Driver");
		      conn = DriverManager.getConnection(CONNECTION.DB_URL,CONNECTION.USER,CONNECTION.PASS);
		      stmt = conn.createStatement();     
		      // note that i'm leaving "date_created" out of this insert statement
		      String sql;
		      int _user_id = -1;
		      if(_session.length()>1)
		      {
		    	  sql = "SELECT USER_ID FROM SESSIONS WHERE SESSION_KEY='"+_session+"'";
		    	  
		    	   ResultSet rs = stmt.executeQuery(sql);
				    
				      if(rs.next()==true)
				      {
				    	  
				    	 _user_id = rs.getInt("USER_ID");
				      }
				      
				      rs.close();
		      }
		   		     
		      
		      stmt.executeUpdate("INSERT INTO LINKS (FROM_LINK,TO_LINK,EXPIRE_LINK,OWNER_ID) "
		          +"VALUES ('"+_from+"','"+_link+"','"+_expire+"',"+_user_id+")");
		      
		        _jo_reflection = 1;
		        _jo_from = _from;
				_jo_link = _link;
				_jo_expire = _expire;
		      
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
		 jo.put("RESULT",_jo_reflection);
		 jo.put("SHORT_LINK",_jo_from);
		 jo.put("LINK",_jo_link);
		 jo.put("EXPIRE_LINK", _jo_expire);
		 
		   return jo;
	}


	
	private int CHECK_GEN_URLS(String _URL)
	{
		
		 Connection conn = null;
		 Statement stmt = null;
		 int _val =0;
		 try{
		      Class.forName("com.mysql.jdbc.Driver");
		     // System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(CONNECTION.DB_URL,CONNECTION.USER,CONNECTION.PASS);
		      stmt = conn.createStatement();
		      String sql;
		      sql = "SELECT ID FROM LINKS WHERE (FROM_LINK='"+_URL+"') OR (EXPIRE_LINK='"+_URL+"')";
		      ResultSet rs = stmt.executeQuery(sql);

		      if(rs.next()==false)
		    	  _val = 1;
		      else
		    	  _val = 2;
		      rs.close();
		      stmt.close();
		      conn.close();
		   }catch(SQLException se){
		      //Handle errors for JDBC
		      se.printStackTrace();
		   }catch(Exception e){
		      //Handle errors for Class.forName
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
