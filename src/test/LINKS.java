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
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;


@Path("/link")
public class LINKS
{
	@GET
	@Produces(MediaType.TEXT_HTML)
	public String GET_HTML(@QueryParam("R") String f_link)
	{
		String _To = GET_LINK(f_link);
		System.out.println("*** " + _To);
		String resource;
		if(!_To.equals("NONE"))
		{
			 resource = "<!DOCTYPE html>\n" + 
						"<html>\n" + 
						"   <head>\n" + 
						"      <title>HTML Meta Tag</title>\n" + 
						"      <meta http-equiv = \"refresh\" content = \"0; url = "+_To+"\" />\n" + 
						"   </head>\n" + 
						"   <body>\n" + 
						"      <p>REDIRECTING</p>\n" + 
						"   </body>\n" + 
						"</html>";
		}
		else 
		{
			 resource = EXPIRE_LINK(f_link)?"LINK IS REMOVED":"NOT VALID LINK";
		}
				
		return resource;
	}

	private String GET_LINK(String F_LINK)
	{
		
		 Connection conn = null;
		 Statement stmt = null;
		 String _val = "NONE";
		 try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(CONNECTION.DB_URL,CONNECTION.USER,CONNECTION.PASS);

		      //STEP 4: Execute a query
		      stmt = conn.createStatement();
		      String sql;
		      sql = "SELECT TO_LINK FROM LINKS WHERE FROM_LINK='"+F_LINK+"'";
		      ResultSet rs = stmt.executeQuery(sql);
		      if(rs.next()==true)
		    	  _val = rs.getString("TO_LINK");
		      
		     
		      //STEP 6: Clean-up environment
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
		   System.out.println("Goodbye!");
		   return _val;
	}
	
	private boolean EXPIRE_LINK(String E_LINK)
	{
		
		 Connection conn = null;
		   Statement stmt = null;
		 try{
		      //STEP 2: Register JDBC driver
		      Class.forName("com.mysql.jdbc.Driver");

		      //STEP 3: Open a connection
		      System.out.println("Connecting to database...");
		      conn = DriverManager.getConnection(CONNECTION.DB_URL,CONNECTION.USER,CONNECTION.PASS);

		      String sql;
		      sql = "DELETE from LINKS where EXPIRE_LINK = '"+E_LINK+"'";
		      PreparedStatement preparedStmt = conn.prepareStatement(sql);
		      preparedStmt.execute();
		      preparedStmt.close();
		      conn.close();
		      return true;
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
		  return false;
	}
}
