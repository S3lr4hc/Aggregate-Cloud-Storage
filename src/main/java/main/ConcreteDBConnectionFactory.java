package main;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import javax.swing.JOptionPane;

/* Abstract Factory is used in this project to secure the creation
 * of UI components and database connections. When needed, a method
 * for creation of a specific component can be overloaded to handle
 * components with multiple constructors, such as JButton(String) and
 * JButton(ImageIcon).
 * */

public class ConcreteDBConnectionFactory extends DBConnectionFactory { //creates connection to the database

	public Connection getConnection() {
		
		 try {
	            Class.forName(getDriverName());
	            Connection conn = DriverManager.getConnection(getUrl(),getUsername() ,getPassword() );
	            
	            return conn;
	        } catch (SQLException ex) {
	            JOptionPane.showMessageDialog(null, ex);
	        } catch (ClassNotFoundException ex) {
	        	JOptionPane.showMessageDialog(null, ex);
	        }
		 
		return null;
	}

}
