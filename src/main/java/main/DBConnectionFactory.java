package main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Enumeration;
import java.util.ResourceBundle;

/* Abstract Factory is used in this project to secure the creation
 * of UI components and database connections. When needed, a method
 * for creation of a specific component can be overloaded to handle
 * components with multiple constructors, such as JButton(String) and
 * JButton(ImageIcon).
 * */

public abstract class DBConnectionFactory { //Abstract Factory for database connections
	
    private static String driverName="";
    private static String url="";
    private static String username="";
    private static String password="";
    
    public static DBConnectionFactory getInstance(){
    	
        ResourceBundle rb = ResourceBundle.getBundle("main.database");
        Enumeration<String> settings = rb.getKeys();
        driverName = rb.getString("driverName");
        url = rb.getString("url");
        username = rb.getString("username");
        password = rb.getString("password");
        
        return new ConcreteDBConnectionFactory();
    }
    
    public abstract Connection getConnection();

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}