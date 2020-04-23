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

@Path("/REG")
// /REG?EMAIL=email&USERNAME=username&PASSWORD=password&REPASSWORD=repassword&TYPE=type
public class REGISTER {
		@GET
		@Produces(MediaType.TEXT_HTML)
		public String GET_HTML
		(
			@QueryParam("EMAIL") String _email, 
			@QueryParam("USERNAME") String _username,
			@QueryParam("PASSWORD") String _password, 
			@QueryParam("REPASSWORD") String _password2,
			@QueryParam("TYPE")  int _type
		)
		{
			JSONObject jo = new JSONObject(); 
			
			if(_username.length() < DEFAULTS.MIN_USERNAME_LENGTH)
			{

				jo.put("RESULT_CODE",0);
				jo.put("RESULT_MESSAGE","SHORT USERNAME");
				jo.put("ERROR_CODE",1);
				 return jo.toString();
			}
			if(!email_check(_email))
			{
				jo.put("RESULT_CODE",0);
				jo.put("RESULT_MESSAGE","NOT VALID E-MAIL");
				jo.put("ERROR_CODE",2);
				 return jo.toString();
			}
			if(_password.length() < DEFAULTS.MIN_PASSWORD_LENGTH)
			{
				jo.put("RESULT_CODE",0);
				jo.put("RESULT_MESSAGE","SHORT PASSWORD (min: 4 character)");
				jo.put("ERROR_CODE",3);
				 return jo.toString();
			}
			if(_password.length() > DEFAULTS.MAX_PASSWORD_LENGTH)
			{
				jo.put("RESULT_CODE",0);
				jo.put("RESULT_MESSAGE","LONG PASSWORD (max: 16 character)");
				jo.put("ERROR_CODE",4);
				 return jo.toString();
			}
		    if(!password_diff_check(_password,_password2)) 
			{
					jo.put("RESULT_CODE",0);
					jo.put("RESULT_MESSAGE","PASSWORDS ARE NOT SAME");
					jo.put("ERROR_CODE",5);
					 return jo.toString();
			}


			int _username_and_email_check = CHECK_USERNAME_AND_EMAIL(_username,_email);
			
			if( _username_and_email_check == 0)
			{
				jo.put("RESULT_CODE",-1);
				jo.put("RESULT_MESSAGE","CONNECTION ERROR");
				jo.put("ERROR_CODE",-1);
				 return jo.toString();
			}
			if( _username_and_email_check == 1)
			{
				jo.put("RESULT_CODE",0);
				jo.put("RESULT_MESSAGE","USERNAME EXISTS");
				jo.put("ERROR_CODE",6);
				 return jo.toString();
			}
			if( _username_and_email_check == 2)
			{
				jo.put("RESULT_CODE",0);
				jo.put("RESULT_MESSAGE","EMAIL EXISTS");
				jo.put("ERROR_CODE",7);
				 return jo.toString();
			}
			
			if(_username_and_email_check == 3)
			{
				boolean _result =  REGISTER_USER(_username,_email,_password,_type);
				if(!_result)
				{
					jo.put("RESULT_CODE",-1);
					jo.put("RESULT_MESSAGE","CONNECTION ERROR");
					jo.put("ERROR_CODE",-2);
					 return jo.toString();
				}
				
				jo.put("RESULT_CODE",1);
				jo.put("RESULT_MESSAGE","SUCCESS");
				jo.put("ERROR_CODE",0);
				 return jo.toString();
				
			}
			jo.put("RESULT_CODE",-1);
			jo.put("RESULT_MESSAGE","CONNECTION ERROR");
			jo.put("ERROR_CODE",-3);
			 return jo.toString();
		
		}
		
		private boolean password_diff_check(String _p1, String _p2)
		{
			return _p1.equals(_p2);
		}	
		
		private boolean email_check(String _s)
		{
			if(!_s.contains("@"))
				return false;
			
			if(!_s.contains("."))
				return false;
			
			return true;
		}
		

		private int CHECK_USERNAME_AND_EMAIL(String _user, String _email)
		{
			// 0 errror, 1 user, 2 email, 3 success
			 Connection conn = null;
			 Statement stmt = null;
			 int _val = 0;
			 
			 try{
			      Class.forName("com.mysql.jdbc.Driver");
			      System.out.println("Connecting to database...");
			      conn = DriverManager.getConnection(CONNECTION.DB_URL,CONNECTION.USER,CONNECTION.PASS);
			      stmt = conn.createStatement();
			      String sql;
			      sql = "SELECT EMAIL,USERNAME FROM USERS WHERE (USERNAME='"+_user+"') OR (EMAIL='"+_email+"')";
			      ResultSet rs = stmt.executeQuery(sql);

			      if(rs.next()==false)
			      {
			    	  _val = 3;
			      }
			      else
			      {
			    	  do
			    	  { 
			    		  if(rs.getString("USERNAME").equals(_user))
				    	  {
				    		  _val = 1;
				    		  break;
				    	  }
				    	  else if(rs.getString("EMAIL").equals(_email))
				    	  {
				    		  _val = 2;
				    		  break;
				    	  }
			    	  }while(rs.next()==true);
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
		

		private boolean REGISTER_USER(String _user, String _email, String _password, int _type)
		{
			
			 Connection conn = null;
			 Statement stmt = null;
			 boolean _val = false;
			 
			 try{
			      Class.forName("com.mysql.jdbc.Driver");
			      System.out.println("Connecting to database...");
			      conn = DriverManager.getConnection(CONNECTION.DB_URL,CONNECTION.USER,CONNECTION.PASS);
			      stmt = conn.createStatement();     
			      // note that i'm leaving "date_created" out of this insert statement
			      stmt.executeUpdate("INSERT INTO USERS (USERNAME, EMAIL, PASSWORD, SUB_TYPE) "
			          +"VALUES ('"+_user+"', '"+_email+"', '"+ENC.ENCRYPT(_password) +"',"+_type+")");
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
}


