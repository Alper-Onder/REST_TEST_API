package test;


import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.SimpleTimeZone;

import javax.json.Json;
import javax.json.JsonArray;
import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;



@Path("/ALLLS")
public class ALLL {
	 
	
    @POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public String GET_ALL_LINKS(@FormParam("S") String _ses)
	{
    	return GET_ALL_LINKS_FUNC(_ses);
	}
    
    
    
    @GET
    @Path("/G")
	@Produces(MediaType.TEXT_HTML)
	public String GET_ALL_LINKS_2(@QueryParam("S") String _ses)
	{
    	return GET_ALL_LINKS_FUNC(_ses);
	}
    
    

    public String GET_ALL_LINKS_FUNC(String _ses)
    {

    	try
    	{
    		
    	SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
  		formatter.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
  		String _d =formatter.format(new Date(System.currentTimeMillis()));
    	 System.out.println("ALLLS: " +_d+  " " + _ses);
    	}catch(Exception e) {}
    	
    	 JSONArray ja =  GET_ALL_LINKS_FDB( _ses);
    	 JSONObject jo_of_ja = new JSONObject();
    	 jo_of_ja.put("ALL_LINKS",ja);
		return jo_of_ja.toString();
    }

	private JSONArray GET_ALL_LINKS_FDB(String _ses)
	{
		 Connection conn = null;
		 Statement stmt = null;
		 
		 //rrayList <JSONObject> jlist = new ArrayList<JSONObject>();
		 JSONArray _ja = new JSONArray();
		 
		 try{
		      Class.forName("com.mysql.jdbc.Driver");
		      conn = DriverManager.getConnection(CONNECTION.DB_URL,CONNECTION.USER,CONNECTION.PASS);
		      stmt = conn.createStatement();     
		      // note that i'm leaving "date_created" out of this insert statement
		      String sql;
		      int _user_id = -2;
		      sql = "SELECT USER_ID FROM SESSIONS WHERE SESSION_KEY='"+_ses+"'";
		      ResultSet rs = stmt.executeQuery(sql);
		      if(rs.next()==true)
		      {
		    	  _user_id = rs.getInt("USER_ID");
			   }
		      
		      sql = "SELECT ID,FROM_LINK, TO_LINK,CR_DATE,IS_ACTIVE, EXPIRE_LINK,EXPIRE_DATE,LINK_LABEL FROM LINKS WHERE OWNER_ID="+_user_id+"";
		      rs = stmt.executeQuery(sql);
		      while(rs.next())
		      {
		    	  JSONObject _jo = new JSONObject();
		    	  _jo.put("LID",rs.getInt("ID")); 
		    	  _jo.put("SHORT_LINK",rs.getString("FROM_LINK"));
		    	  _jo.put("LINK",rs.getString("TO_LINK"));
		    	  _jo.put("EXPIRE_LINK",rs.getString("EXPIRE_LINK"));
		    	  _jo.put("CREATION_DATE",rs.getString("CR_DATE"));
		    	  _jo.put("IS_ACTIVE",rs.getInt("IS_ACTIVE"));
		    	  
		    	  _jo.put("EXPIRE_DATE",rs.getString("EXPIRE_DATE"));
		    	  _jo.put("LABEL",rs.getString("LINK_LABEL"));
		    	  _ja.add(_jo);
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
		 return  _ja;
		 }
}
