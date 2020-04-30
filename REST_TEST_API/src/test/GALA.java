package test;

import java.util.List;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;

import javax.json.Json;
import javax.json.JsonArray;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;



@Path("/GALA")
public class GALA {
	 
	/*
	 	http://localhost:8080/REST_TEST_API/rest/GALA?S=SessionKey

		Real Example:
		http://localhost:8080/REST_TEST_API/rest/GALA?S=ab0f09c79a9bd10442b3af26d0bdaaed47ecaee66d58fc4ce8d9c91ab3eab9297ba2e0d766fd0654f15820b55a251d3ee56fda3091b46e578fcc6ebbb96fb1a
		
		RESULTS FOR ALL LINK ANALYTICS
		Result of the register operation is a JSON String with an array named LINKS and a property  named RESULT.
		
		The array LINKS has 3 key  ID, TOTAL_CLICK_COUNT and an array CLICK_DATES.
		
		The array CLICK_DATES has 1 key CLICK_DATE
		CLICK_DATE gives the date for UTC in “yyyy-MM-dd HH:mm:ss”
		CLICK_DATES containsall dates to create a graphic. 
		
		MEANING OF RESULT CODES:
		0 : Connection error.
		1: Successful.
		
		MEANING OF OTHER KEY CODES:
		TOTAL_CLICK_COUNT: total click count of the link.
		ID: id of the link can be comparable with ALL LINKS results.
		ORIGINAL_LINK: the original link of the shortened link.
		
		
		
		RESULT
		TOTAL_CLICK_COUNT:
		LINKS:
			ID:
			TOTAL_CLICK_COUNT
			CLICK_DATES:
				CLICK_DATE
				CLICK_DATA
				....
	 */
	 
    @GET
	@Produces(MediaType.TEXT_HTML)
	public String GET_ALL_ANALYTICS(@QueryParam("S") String _ses)
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
		    	  
		    	  LinkedList<Integer> _link_ids = new LinkedList<Integer>();
		    	  
		    	  sql = "SELECT ID FROM LINKS WHERE OWNER_ID ='"+_user_id+"'";
			      rs = stmt.executeQuery(sql);
		    	  while(rs.next()==true)
		    	  {
		    		  _link_ids.add(rs.getInt("ID"));
		    	  }
			      /*
		    	  
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
				      
				      while(rs.next())
				      {
				    	  _total_count++;
				    	  _jo = new JSONObject();
				    	  _jo.put("CLICK_DATE", rs.getString("DATE"));
				    	  _ja.add(_jo);
				    	 
				      }
				      _jo_result.put("TOTAL_CLICK_COUNT",_total_count);
				      _jo_result.put("CLICK_DATES",_ja);
				      _jo_result.put("RESULT",1);
				  }
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
		 return  _jo_result;
		 }
}
