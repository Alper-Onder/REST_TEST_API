package test;

import java.io.File;


import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.SimpleTimeZone;

import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONObject;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.core.Application;


@Path("/C")
public class CLINK
{
	
	 private void SHOW_TIME_ACCESSED()
	    {
	    	try
	    	{
	    		
	    	SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  		formatter.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
	  		String _d =formatter.format(new Date(System.currentTimeMillis()));
	    	System.out.println("CLINK: " +_d);
	    	}catch(Exception e) {}
	    }
		
	 
	 
     private static final char[] C = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz123456789".toCharArray();
	
    // http://localhost:8080/REST_TEST_API/rest/C?R=http://www.google.com&S=ab0f09c79a9bd10442b3af26d0bdaaed47ecaee66d58fc4ce8d9c91ab3eab9297ba2e0d766fd0654f15820b55a251d3ee56fda3091b46e578fcc6ebbb96fb1a
 	@POST
 	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
 	@Produces(MediaType.TEXT_HTML)
 	public String GENERATE_HTML_FUNC
 	(
 			@FormParam("R") String _link, 
 			@DefaultValue("") @FormParam("S") String _session,
 			@DefaultValue("") @FormParam("L") String _label,
 			@DefaultValue("") @FormParam("ED") String _expire_date
 	)
 	{
 	    	SHOW_TIME_ACCESSED();
 	    	
 			String _from, _expire;
 			
 			do
 			{
 				_from = GENERATE_RANDOM();
 			}while(CHECK_GEN_URLS(_from)!=1);
 			
 			
 			do
 			{
 				_expire = GENERATE_RANDOM();
 			}while(CHECK_GEN_URLS(_expire)!=1);
 			
 			return CREATE_LINK(_link,_from,_expire,_session,_label,_expire_date).toString();
 	}

    @GET
    @Path("/G")
	@Produces(MediaType.TEXT_HTML)
	public String GENERATE_HTML(
			@QueryParam("R") String _link,
			@DefaultValue("") @QueryParam("S") String _session, 
			@DefaultValue("") @QueryParam("L") String _label,
			@DefaultValue("") @QueryParam("ED") String _expire_date 
			)
	{
    	SHOW_TIME_ACCESSED();
		String _from, _expire;
		
		do
		{
			_from = GENERATE_RANDOM();
		}while(CHECK_GEN_URLS(_from)!=1);
		
		
		do
		{
			_expire = GENERATE_RANDOM();
		}while(CHECK_GEN_URLS(_expire)!=1);
		
		return CREATE_LINK(_link,_from,_expire,_session,_label,_expire_date).toString();
	}
    
    @POST
    @Path("/CST")
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public String GENERATE_HTML_CUSTOM
	(		@FormParam("R") String _link,
			@FormParam("L") String _custom, 
			@FormParam("S") String _session, 
			@FormParam("LB") String _label,
			@FormParam("ED") String _expire_date
			)
	{
    	
    	SHOW_TIME_ACCESSED();
    	
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
			 jo.put("LABEL","");
			 jo.put("CREATION_DATE","");
			 jo.put("IS_ACTIVE","");
			 jo.put("EXPIRE_DATE","");
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
				 jo.put("LABEL","");
				 jo.put("CREATION_DATE","");
				 jo.put("IS_ACTIVE","");
				 jo.put("EXPIRE_DATE","");
			}
			else if(_custom_check == 1) // OKAY
			{
				do
				{
					_expire = GENERATE_RANDOM();
				}while(CHECK_GEN_URLS(_expire) != 1);
				
				jo = CREATE_LINK(_link,_custom,_expire,_session,_label,_expire_date);
				
			}
			else if(_custom_check == 2) // 2 NOT VALID
			{
				 jo.put("RESULT",2);
				 jo.put("SHORT_LINK","");
				 jo.put("LINK","");
				 jo.put("EXPIRE_LINK","");
				 jo.put("LABEL","");
				 jo.put("CREATION_DATE","");
				 jo.put("IS_ACTIVE","");
				 jo.put("EXPIRE_DATE","");
			}
		}else if(_log_check == 2)
		{
			 jo.put("RESULT",3); // 3 = NOT AUTHORIZED
			 jo.put("SHORT_LINK","");
			 jo.put("LINK","");
			 jo.put("EXPIRE_LINK","");
			 jo.put("LABEL","");
			 jo.put("CREATION_DATE","");
			 jo.put("IS_ACTIVE","");
			 jo.put("EXPIRE_DATE","");
		}
		return jo.toString();
	}
   
    @GET
    @Path("/CST/G")
	@Produces(MediaType.TEXT_HTML)
	public String GENERATE_HTML_CUSTOM_2(
			@QueryParam("R") String _link,
			@QueryParam("L") String _custom, 
			@QueryParam("S") String _session, 
			@QueryParam("LB") String _label,
			@QueryParam("ED") String _expire_date
			)
	{
    	
    	SHOW_TIME_ACCESSED();
    	
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
			 jo.put("LABEL","");
			 jo.put("CREATION_DATE","");
			 jo.put("IS_ACTIVE","");
			 jo.put("EXPIRE_DATE","");
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
				 jo.put("LABEL","");
				 jo.put("CREATION_DATE","");
				 jo.put("IS_ACTIVE","");
				 jo.put("EXPIRE_DATE","");
			}
			else if(_custom_check == 1) // OKAY
			{
				do
				{
					_expire = GENERATE_RANDOM();
				}while(CHECK_GEN_URLS(_expire) != 1);
				
				jo = CREATE_LINK(_link,_custom,_expire,_session,_label,_expire_date);
				
			}
			else if(_custom_check == 2) // NOT VALID
			{
				 jo.put("RESULT",2);
				 jo.put("SHORT_LINK","");
				 jo.put("LINK","");
				 jo.put("EXPIRE_LINK","");
				 jo.put("LABEL","");
				 jo.put("CREATION_DATE","");
				 jo.put("IS_ACTIVE","");
				 jo.put("EXPIRE_DATE","");
			}
		}else if(_log_check == 2)
		{
			 jo.put("RESULT",3); // 3 = NOT AUTHORIZED
			 jo.put("SHORT_LINK","");
			 jo.put("LINK","");
			 jo.put("EXPIRE_LINK","");
			 jo.put("LABEL","");
			 jo.put("CREATION_DATE","");
			 jo.put("IS_ACTIVE","");
			 jo.put("EXPIRE_DATE","");
		}
		return jo.toString();
	}
    
    
	private JSONObject CREATE_LINK(String _link, String _from, String _expire, String _session,String _label,String _expire_date)
	{
		 Connection conn = null;
		 Statement stmt = null;
		 JSONObject jo = new JSONObject();
		 
		 int _jo_reflection = 0;
		 String _jo_from = "";
		 String _jo_link = "";
		 String _jo_expire = "";
		 
		   SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	  		formatter.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
	  		String _d =formatter.format(new Date(System.currentTimeMillis()));

		 
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
		   		     
		    	
		      stmt.executeUpdate("INSERT INTO LINKS (FROM_LINK,TO_LINK,EXPIRE_LINK,OWNER_ID,LINK_LABEL,CR_DATE,IS_ACTIVE,EXPIRE_DATE) "
		          +"VALUES ('"+_from+"','"+_link+"','"+_expire+"',"+_user_id+",'"+_label+"','"+_d+"',1,'"+_expire_date+"')");
		      
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
		 jo.put("LABEL",_label);
		 jo.put("CREATION_DATE",_d);
		 jo.put("IS_ACTIVE",1);
		 jo.put("SHORT_LINK",_jo_from);
		 jo.put("LINK",_jo_link);
		 jo.put("EXPIRE_LINK", _jo_expire);
		 jo.put("EXPIRE_DATE",_expire_date);
		 
		   return jo;
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
		    
		      sql = "SELECT USERS.ID, USERS.SUB_TYPE, SESSIONS.SESSION_KEY FROM SESSIONS LEFT JOIN USERS ON SESSIONS.USER_ID = USERS.ID WHERE SESSIONS.SESSION_KEY = '"+_ses+"'";

		     
		      
		      ResultSet rs = stmt.executeQuery(sql);
		    
		      
		      if(rs.next()==true)
		      {
		    	  if(rs.getInt("SUB_TYPE") == 2 || rs.getInt("SUB_TYPE") == 1)
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
	
	private boolean CHECK_GEN_URLS_2(String _URL)
	{
		
		 Connection conn = null;
		 Statement stmt = null;
		 boolean _val = false;
		 try{
		      Class.forName("com.mysql.jdbc.Driver");
		    //  System.out.println("Connecting to database...");
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
