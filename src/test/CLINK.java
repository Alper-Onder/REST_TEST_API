package test;

import java.io.File;
import java.sql.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.GET;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.core.Application;


@Path("/C")
public class CLINK
{
     private static final char[] C = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz123456789".toCharArray();
	
    // http://localhost:8080/REST_TEST_API/rest/C?R=http://www.google.com&S=ab0f09c79a9bd10442b3af26d0bdaaed47ecaee66d58fc4ce8d9c91ab3eab9297ba2e0d766fd0654f15820b55a251d3ee56fda3091b46e578fcc6ebbb96fb1a
    
    @GET
	@Produces(MediaType.TEXT_HTML)
	public String GENERATE_HTML(@QueryParam("R") String _link, @DefaultValue("") @QueryParam("S") String _session )
	{
		String _from, _expire;
		
		do
		{
			_from = GENERATE_RANDOM();
		}while(!CHECK_GEN_URLS(_from));
		
		
		do
		{
			_expire = GENERATE_RANDOM();
		}while(!CHECK_GEN_URLS(_from));
		
		System.out.println("FROM: " + _from);
		System.out.println("LINK: " + _link);
		System.out.println("EXPIRE: " + _expire);
		System.out.println("SESSION: " + _session);
		
		return CREATE_LINK(_link,_from,_expire,_session).toString();
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
	
	private boolean CHECK_GEN_URLS(String _URL)
	{
		
		 Connection conn = null;
		 Statement stmt = null;
		 boolean _val = false;
		 try{
		      Class.forName("com.mysql.jdbc.Driver");
		      System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(CONNECTION.DB_URL,CONNECTION.USER,CONNECTION.PASS);
		      stmt = conn.createStatement();
		      String sql;
		      sql = "SELECT ID FROM LINKS WHERE (FROM_LINK='"+_URL+"') OR (EXPIRE_LINK='"+_URL+"')";
		      ResultSet rs = stmt.executeQuery(sql);

		      if(rs.next()==false)
		    	  _val = true;
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
