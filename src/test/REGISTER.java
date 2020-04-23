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
			
			if(_username.length() < DEFAULTS.MIN_USERNAME_LENGTH)
				 return "ERROR:" + REGISTER_MESSAGES.TOO_SHORT_USERNAME;
			if(!email_check(_email))
				return "ERROR:" + REGISTER_MESSAGES.WRONG_EMAIL;
			if(_password.length() < DEFAULTS.MIN_PASSWORD_LENGTH)
				return "ERROR:" + REGISTER_MESSAGES.TOO_SHORT_PASSWORD;
			if(_password.length() > DEFAULTS.MAX_PASSWORD_LENGTH)
				return "ERROR:" + REGISTER_MESSAGES.TOO_LONG_PASSWORD;
			 if(!password_diff_check(_password,_password2)) 
				return "ERROR:" + REGISTER_MESSAGES.DIFFERENT_PASSWORDS;

			 
			int _username_and_email_check = CHECK_USERNAME_AND_EMAIL(_username,_email);
			
			if( _username_and_email_check == 0)
				return "ERROR:" + REGISTER_MESSAGES.CONNECTION_ERROR;
			if( _username_and_email_check == 1)
				return "ERROR:" + REGISTER_MESSAGES.USERNAME_EXIST;
			if( _username_and_email_check == 2)
				return "ERROR:" + REGISTER_MESSAGES.EMAIL_EXIST;
			
			if(_username_and_email_check == 3)
			{
				boolean _result =  REGISTER_USER(_username,_email,_password,_type);
				if(!_result)
					return "ERROR:" + REGISTER_MESSAGES.CONNECTION_ERROR;
				return "SUCCESS:"+REGISTER_MESSAGES.SUCCESSFUL;
			}
			return "ERROR:" + REGISTER_MESSAGES.CONNECTION_ERROR;
		
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


