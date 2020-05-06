package test;

import java.util.List;
import java.util.Map;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;

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


@Path("/GALA")
public class GALA {
	 
	@POST
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_HTML)
	public String GET_ALL_ANALYTICS
	(
			@FormParam("S") String _ses
	)
	
	{
    	JSONObject jo = new JSONObject();
    	jo =  GET_LINK_ANALYTICS_FROM_DB(_ses);
    	
		return jo.toString();
	}
	
	@GET
	@Path("/G")
	@Produces(MediaType.TEXT_HTML)
	public String GET_ALL_ANALYTICSS
	(
			@QueryParam("S") String _ses
	)
	{
    	JSONObject jo = new JSONObject();
    	jo =  GET_LINK_ANALYTICS_FROM_DB(_ses);
    	
		return jo.toString();
	}
    
	private JSONObject GET_LINK_ANALYTICS_FROM_DB(String _ses)
	{
		 Connection conn = null;
		 Statement stmt = null;
		 
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
		    	  int _total_click_count_of_all_links = 0;
		    	  int _total_click_count_of_the_link = 0;
		    	  
		    	  
		    	  Map<Integer, GALA_MAP> _gala_map = new HashMap<Integer,GALA_MAP>();
		 
		    	   	  
		    	  sql = "SELECT ID FROM LINKS WHERE OWNER_ID ='"+_user_id+"'";
			      rs = stmt.executeQuery(sql);
		    	  while(rs.next()==true)
		    	  {
		    		 _gala_map.put(rs.getInt("ID"),new GALA_MAP());
		    	  }
		    	  
		    	  int _key_id  = 0;
		    	  for(int i = 0; i<_gala_map.size(); i++)
		    	  {
		    		  _key_id =  (int)_gala_map.keySet().toArray()[i];
		    		  System.out.println("SET K: " + _key_id);
		    		  sql = "SELECT DATE FROM LINK_ANALYTICS WHERE LINK_ID='"+_key_id+"'";
		    		  rs = stmt.executeQuery(sql);
		    		  
		    		  while(rs.next()==true)
			    	  {
			    		 _gala_map.get(_key_id).LIST.add(rs.getString("DATE"));
			    	  }
		    	  }
		    	  
		    	  JSONObject _temp_jo = new JSONObject();
		    	  JSONObject _temp_jo_link = new JSONObject();
		    	  JSONArray _temp_ja = new JSONArray();
		    	  
			      String _last_access = "";
		    	  for(int i = 0; i<_gala_map.size(); i++)
		    	  {
		    		  _key_id =  (int)_gala_map.keySet().toArray()[i];
		    		  System.out.println("GET K: " + _key_id);

		    		  _total_click_count_of_the_link = 0;
		    		  _temp_ja = new JSONArray();
		    		  _last_access = "";
		    		  for(int k = 0; k<_gala_map.get(_key_id).LIST.size(); k++)
		    		  { 
		    			  _total_click_count_of_all_links++;
		    			  _total_click_count_of_the_link++;
		    			  System.out.println("GET D: " + _gala_map.get(_key_id).LIST.get(k));
		    			  _temp_jo = new JSONObject();
				    	  _last_access = _gala_map.get(_key_id).LIST.get(k);
		    			  _temp_jo.put("CLICK_DATE",_last_access);
		    			  _temp_ja.add(_temp_jo);
		    		  }
		    		  
		    		  _temp_jo_link = new JSONObject();
		    		  _temp_jo_link.put("TOTAL_CLICK_COUNT", _total_click_count_of_the_link);
		    		  _temp_jo_link.put("ID", _key_id);
		    		  _temp_jo_link.put("CLICK_DATES", _temp_ja);
		    		  _temp_jo_link.put("LAST_ACCESS",_last_access);
		    		  
		    		  _ja.add(_temp_jo_link);
		    	  }
		    	  
		    	 _jo_result.put("RESULT",1);
		    	 _jo_result.put("TOTAL_CLICK_COUNT",_total_click_count_of_all_links);
		    	 _jo_result.put("LINKS",_ja);	    	
		    	  
		    	  
		    	 
		    	  /*
		    	   *  int _total_click_count_of_all_links = 0;
		    	  int _total_click_count_of_the_link = 0;
		    	  
		    	   ****JO*****
		    	    TOTAL_CLICK_COUNT:
		    	  	LINKS: ***JA****
		    	  			ID:
		    	  			TOTAL_CLICK_COUNT
		    	  			CLICK_DATES: **** JA *****
		    	  				CLICK_DATE
		    	  				CLICK_DATA
		    	  				....
		    	   */
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
		 
		 if(_jo_result.isEmpty())
			 _jo_result.put("RESULT",0);
		 return  _jo_result;
		 }
}

class GALA_MAP
{
	public LinkedList<String> LIST;
	
	public GALA_MAP()
	{
		LIST = new LinkedList<String>();
	}
	
	
}
/*
 * TOTAL_CLICK_COUNT:
		LINKS:
			ID:
			TOTAL_CLICK_COUNT
			CLICK_DATES:
				CLICK_DATE
				CLICK_DATA
				....
 */


