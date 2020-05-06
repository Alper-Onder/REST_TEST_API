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



@Path("/GLA")
public class GLA {


	
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public String GLA_POST
	(
			@FormParam("LID") String _lid, 
			@FormParam("S") String _ses
	)
	{
		return  GET_ALL_ANALYTICS(_lid,_ses);
	}
	
	@GET
	@Path("/G")
	@Produces(MediaType.TEXT_HTML)
	public String GLA_GET
	(
			@QueryParam("LID") String _lid, 
			@QueryParam("S") String _ses
	)
	{
		return  GET_ALL_ANALYTICS(_lid,_ses);
	}
	
	
	public String GET_ALL_ANALYTICS
	(
			 String _lid, 
			 String _ses
	)
	{
    	
    	try
    	{
    		SimpleDateFormat formatter= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
      		formatter.setTimeZone(new SimpleTimeZone(SimpleTimeZone.UTC_TIME, "UTC"));
      		String _d =formatter.format(new Date(System.currentTimeMillis()));
        	System.out.println("GLA: " +_d + " " + _lid + " " + _ses);
    	}catch(Exception e) {}
    
  		
    	
    	
    	int _ilid = 0;
    	boolean _ilid_check = false;
    	try
    	{
    		_ilid = Integer.parseInt(_lid);
    		_ilid_check = true;
    	}
    	catch(Exception e) {}
    	JSONObject jo;
    	
    	if(_ilid_check)
    		jo =  GET_LINK_ANALYTICS_FROM_DB(_ilid,_ses);
    	else
    	{
    		jo=new JSONObject();
    		jo.put("RESULT",4); // NOT INTEGER LID NUMBER
    	}
    		
    	
		return jo.toString();
	}
    
	private JSONObject GET_LINK_ANALYTICS_FROM_DB(int _ilid,String _ses)
	{
		 Connection conn = null;
		 Statement stmt = null;
		 
		 //rrayList <JSONObject> jlist = new ArrayList<JSONObject>();
		 JSONArray _ja = new JSONArray();
		 JSONObject _jo_result = new JSONObject();
		 try{
		      Class.forName("com.mysql.jdbc.Driver");
		      conn = DriverManager.getConnection(CONNECTION.DB_URL,CONNECTION.USER,CONNECTION.PASS);
		      stmt = conn.createStatement();     
		      // note that i'm leaving "date_created" out of this insert statement
		      String sql;
		      int _user_id = -2;
		      sql = "SELECT USER_ID FROM SESSIONS WHERE SESSION_KEY='"+_ses+"'";
		      ResultSet rs = stmt.executeQuery(sql);
		      boolean _check_valid_session = false;
		      if(rs.next()==true)
		      {
		    	  _user_id = rs.getInt("USER_ID");
		    	  _check_valid_session = true;
			  }
		      else	  
		      {
		    	  _jo_result.put("RESULT",2); // INCORRECT SESSION KEY
		      }
		      
		      if(_check_valid_session)
		      {
		    	  boolean _owner_check = false;
			      sql = "SELECT OWNER_ID FROM LINKS WHERE ID ='"+_ilid+"'";
			      rs = stmt.executeQuery(sql);
			      
			      if(rs.next()==true)
			      {
			    	 if(_user_id == rs.getInt("OWNER_ID"))
			    	 {
			    		 _owner_check = true;
			    	 } 
			    	 else
			    	 {
			    		 _jo_result.put("RESULT",3); // INCORRECT USER FOR LINK
			    	 }
				  }

			      if(_owner_check)
				  {	  
			    	  int _total_count = 0;
				      JSONObject _jo = new JSONObject();
			    	  sql = "SELECT LINK_ID, DATE FROM LINK_ANALYTICS WHERE LINK_ID="+_ilid+"";
				      rs = stmt.executeQuery(sql);
				      String _last_access = "";
				      while(rs.next())
				      {
				    	  _total_count++;
				    	  _jo = new JSONObject();
				    	  _last_access = rs.getString("DATE");
				    	  _jo.put("CLICK_DATE",_last_access);
				    	  _ja.add(_jo);
				    	 
				      }
				      _jo_result.put("TOTAL_CLICK_COUNT",_total_count);
				      _jo_result.put("LAST_ACCESS", _last_access);
				      _jo_result.put("CLICK_DATES",_ja);
				      _jo_result.put("RESULT",1);
				  }
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
		 return  _jo_result;
		 }
}
