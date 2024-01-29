package EmailSystem;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import com.mysql.cj.jdbc.DatabaseMetaData;
public class Email {
	static Scanner sc = new Scanner(System.in);
	public static Statement connect() 
	{
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
		}
		Connection con = null;
		try {
			con = DriverManager.getConnection ("jdbc:mysql://localhost:3306/Email", "root", "0909");
			DatabaseMetaData metaData = (DatabaseMetaData) con.getMetaData();
			ResultSet resultsetuser = metaData.getTables(null, null,"users", null);
			ResultSet resultsetadmin = metaData.getTables(null, null,"admin", null);
			Statement stmt = con.createStatement();
			if(resultsetuser.next())
			{
				return stmt;
				
			}
			else if(resultsetadmin.next())
			{
				String createusertable = "create table users(username varchar(20) not null primary key,password varchar(20) not null,DateOfCreation datetime)";
				stmt.executeUpdate(createusertable);
				String createadmintable = "create table admin(username varchar(20) not null primary key,password varchar(20) not null)";
				stmt.executeUpdate(createadmintable);
				String adminrecord = "insert into admin values('admin',123)";
				stmt.executeUpdate(adminrecord);
				return stmt;
			}
			
		} catch (SQLException e) 
		{
			System.out.println(e.getMessage());
		}
		return null;
			
		
		
	}
	public static void AddUser() 
	{
		Statement stmt = null;
		stmt = connect();
		System.out.print("Enter Username : ");
		String uname = sc.next();
		System.out.println("Enter Password");
		String pass = sc.next();
		String usernamevalidation="select *from users where binary username ='"+uname+"'";
		ResultSet valid = null;
		try {
			valid = stmt.executeQuery(usernamevalidation);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}	
		try {
			if(valid.next())
			{
				System.out.println("\nUsername exists Choose another username...");
			}
			else
			{
				String str="insert into users values('"+uname+"','"+pass+"',CURRENT_TIMESTAMP)";
				int rs=stmt.executeUpdate(str);
				if(rs==1)
				{	
					String ass = uname;
					String str1="create table "+ass+"(sendrecive varchar(10),username varchar(50),message varchar(100),time datetime)";
					stmt.executeUpdate(str1);
					System.out.println("Account Created Sucessfully..");
				}
				else
				{
					System.out.println("Failed");
				}
			}
		} catch (SQLException e) {
			System.out.println("Username not avaliable....");
		}	
	}
	public static void Sendmail(String uname) 
	{
		Statement stmt = null;
		stmt = connect();
		System.out.println("Enter Email address");
		String email = sc.next();
		sc.nextLine();
		String checkuserexist="select *from users where binary username ='"+email+"'";
		ResultSet validuser = null;
		try {
			validuser = stmt.executeQuery(checkuserexist);
			if(validuser.next())
			{
				System.out.println("Enter Message");
				String msg = sc.nextLine();
				String reciver="insert into "+email+" values('Recived','"+uname+"','"+msg+"',CURRENT_TIMESTAMP)";
				int send=stmt.executeUpdate(reciver);
				if(send==1)
				{
					String sender="insert into "+uname+" values('Send','"+email+"','"+msg+"',CURRENT_TIMESTAMP)";
					stmt.executeUpdate(sender);
				}
				else
				{
					String rollbackreciver="delete from "+email+" where binary username ='"+uname+"'";
					stmt.executeUpdate(rollbackreciver);
				}
			}
			else
			{
				System.out.println("Enter Valid Email Address...\n");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		
			
		}
			
	
	public static void userlogin() 
	{
		
		Statement stmt = connect();
		System.out.println("Enter Username");
		String uname = sc.next();
		System.out.println("Enter Password");
		String pass = sc.next();
		
		
		String passwordvalidation="select *from users where binary username ='"+uname+"' and password = '" +pass+ "'";
		ResultSet valid = null;
		try {
			valid = stmt.executeQuery(passwordvalidation);
		} catch (SQLException e) {
		
			System.out.println(e.getMessage());
		}	
		try {
			if(valid.next())
			{
				System.out.println("Login Sucessfull");
				boolean exit = false;
				do {
					System.out.println("1: Send Email");
					System.out.println("2: Inbox");
					System.out.println("3: Exit");
					int choice = sc.nextInt();
					switch(choice)
					{
					case 1 : Sendmail(uname);
							 break;
					case 2 : inbox(uname);
							break;
					case 3 : exit=true;
					}
				}while(exit==false);
			}
			else
			{
				System.out.println("Username Or Password Wrong");
			}
		} catch (SQLException e) {
			
			System.out.println(e.getMessage());
		}
	}
	public static void adminlogin() 
	{
		
		Statement stmt = connect();
		System.out.println("Enter Username");
		String uname = sc.next();
		System.out.println("Enter Password");
		String pass = sc.next();
		
		String adminvalidation="select *from admin where binary username ='"+uname+"' and password = '" +pass+ "'";
		ResultSet validation = null;
		try {
			validation = stmt.executeQuery(adminvalidation);
		} catch (SQLException e) {
			
			System.out.println(e.getMessage());
		}	
		try {
			if(validation.next())
			{
				System.out.println("Login Sucessfull");
				boolean exit = false;
				do {
					System.out.println("1: Show Users");
					System.out.println("2: Remove User");
					System.out.println("3: Exit");
					int choice = sc.nextInt();
					switch(choice)
					{
					case 1 : String showusers="select username from users";
							 ResultSet users = stmt.executeQuery(showusers);
							 int i = 1;
							 while (users.next())
								{
									System.out.println(i+" | "+users.getString("username"));
									i++;
								}
							 break;
					case 2 : System.out.println("Enter Username to delete");
							 String deleteuser = sc.next();
							 String userdelete="delete from users where username = '"+deleteuser+"'";
							 int confirm = stmt.executeUpdate(userdelete);
							 if(confirm==1)
								{
								 String deleteuserdata="drop table "+deleteuser+"";
								 stmt.executeUpdate(deleteuserdata);
								 System.out.println("Sucessfully Deleted");
								
								
								 
								}
								else
								{
									System.out.println("Username Not Found");
								}
							break;
					case 3 : exit=true;
					}
				}while(exit==false);
			}
			else
			{
				System.out.println("Username Or Password Wrong");
			}
		} catch (SQLException e) {
			
			System.out.println(e.getMessage());
		}
	}
	public static void inbox(String uname) 
	{
		boolean exit = false;
		do {
		Statement stmt = connect();
		System.out.println("1: All Mails");
		System.out.println("2: Search By Email Address");
		System.out.println("3: Exit");
		int choice = sc.nextInt();
		switch(choice)
		{
		case 1: String inbox="select *from "+uname+"";
			ResultSet valid = null;
			try {
				valid = stmt.executeQuery(inbox);
			} catch (SQLException e) {
				
				System.out.println(e.getMessage());
			}	
				int i = 1;
			try {
				while (valid.next())
				{
					System.out.print("| "+i+" | "+valid.getString("sendrecive"));
					System.out.print(" | "+valid.getString("username"));
					System.out.print(" | "+valid.getString("message"));
					System.out.print(" | "+valid.getString("time"));
					System.out.println();
					i++;
				}
			} catch (SQLException e) {
				
				System.out.println(e.getMessage());
			}
				break;
		case 2: System.out.println("Enter Email Address");
				String email = sc.next();
			String inboxwithname="select *from "+uname+" where binary username ='"+email+"'";
			ResultSet res = null;
			try {
				res = stmt.executeQuery(inboxwithname);
			} catch (SQLException e) {
			
				System.out.println(e.getMessage());
			}	
			try {
				while (res.next())
				{
					System.out.print(" | "+res.getString("sendrecive"));
					System.out.print(" | "+res.getString("username"));
					System.out.print(" | "+res.getString("message"));
					System.out.print(" | "+res.getString("time"));
					System.out.println();
				}
			} catch (SQLException e) {
		
				System.out.println(e.getMessage());
			}
		break;
		case 3 : exit = true;
		}
		}while(exit==false);
	}
	public static void index()
	{
		
		
		boolean exit = false;
		do {
			System.out.println("\n************ Welcone To Mail69 ************\n");
		System.out.println("1: Create Account");
		System.out.println("2: Login Existing account");
		System.out.println("3: Forget Password");
		System.out.println("4: Admin");
		System.out.println("5: Exit");
		int choice = sc.nextInt();
		switch(choice)
		{
		case 1 : AddUser();
				 break;
		case 2 : userlogin();
				break;
		case 3 : forgetpassword();
				break;
		case 4 : adminlogin();
				break;
		case 5 : exit=true;
		}
		}while(exit==false);
	}	
	public static void forgetpassword() 
	{
		Statement stmt = connect();
		System.out.print("Enter Username : ");
		String uname = sc.next();
		System.out.print("Enter Account Creation Date yyyy-mm-dd : ");
		String date = sc.next();	
		String adminvalidation="select password from users where username ='"+uname+"' and date(DateOfCreation) = '"+date+"'";
		ResultSet validation = null;
		try {
			validation = stmt.executeQuery(adminvalidation);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}	
		try {
			if(validation.next())
			{
				System.out.print(" Password : "+validation.getString("password"));
				System.out.println();
			}
			else
			{
				System.out.println("Wrong Details");
			}
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
	}
	
	public static void main(String[] args)
	{
		index();	
	}
}
