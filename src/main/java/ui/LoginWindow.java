package ui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import main.DBConnectionFactory;
import main.RemoteDrive;
import main.RemoteDriveStore;

import com.microsoft.sqlserver.jdbc.SQLServerPreparedStatement;


public class LoginWindow extends JFrame implements WindowListener{
	
	private Connection conn = null;
	private SQLServerPreparedStatement stmt = null;
	private ResultSet rs = null;
	private String userAccount = null;
	private int id = 0;
	
	public LoginWindow(final Connection conn) {
		
		super();
		
		final RemoteDriveStore driveStore = new RemoteDriveStore();
		this.conn = conn;
		
		this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		
		// Set defaults
		this.setTitle("ACS");
		this.setResizable(false);
		this.setSize(250, 150);
		this.setLayout(new BorderLayout(10, 10));
		
		// Center the window
		Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = scrSize.width / 2 - this.getWidth() / 2;
		int y = scrSize.height / 2 - this.getHeight() / 2;
		this.setLocation(x, y);
		
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel txtPanel = new JPanel();
		txtPanel.setPreferredSize(new Dimension(70, 80));
		this.add(txtPanel, BorderLayout.LINE_START);
		
		JLabel lblUsername = new JLabel("Username: ");
		lblUsername.setPreferredSize(new Dimension(70, 30));
        lblUsername.setHorizontalAlignment(4);
		txtPanel.add(lblUsername);
		
		JLabel lblPassword = new JLabel("Password: ");
		lblPassword.setPreferredSize(new Dimension(70, 30));
        lblPassword.setHorizontalAlignment(4);
		txtPanel.add(lblPassword);
		
		// TextFields Panel Container
        JPanel panelForTextFields = new JPanel();
        panelForTextFields.setPreferredSize(new Dimension(100, 70));
        this.add(panelForTextFields, BorderLayout.CENTER);
		
		// Username Textfield
        final JTextField usernameField = new JTextField(12);
        usernameField.setPreferredSize(new Dimension(100, 30));
        panelForTextFields.add(usernameField);

        // Login Textfield
        final JPasswordField loginField = new JPasswordField(12);
        loginField.setPreferredSize(new Dimension(100, 30));
        panelForTextFields.add(loginField);
        
        this.dispose();
        
        // Button for Logging in
        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
		
        	@Override
        	public void actionPerformed(ActionEvent e) {
        		String sql = "SELECT * from UserAccount where username = ? and password = ?";
        		try {
					stmt = (SQLServerPreparedStatement) conn.prepareStatement(sql);
					stmt.setString(1, usernameField.getText());
					stmt.setString(2, new String(loginField.getPassword()));
					rs = stmt.executeQuery();
					
					if(rs.next()) {
						JOptionPane.showMessageDialog(null, "Correct!");
						userAccount = rs.getString("username");
						id = rs.getInt("ID");
						final JFrame mainWindow = new MainWindow(driveStore, userAccount, id);
						LoginWindow.this.dispose();
		        		
		        		SwingUtilities.invokeLater(new Runnable() {
		        			public void run()
		        			{
		        				mainWindow.setVisible(true);
		        			}
		        		});
		        		
		        		driveStore.loadFromFile(id);
					}
					else {
						JOptionPane.showMessageDialog(null, "Error!");
					}
				} catch (SQLException e2) {
					JOptionPane.showMessageDialog(null, e2);
				}
        	}
        });
        this.add(loginButton, BorderLayout.PAGE_END);
	}

	public static void main(String[] args) {
		Connection conn = DBConnectionFactory.getInstance().getConnection();
		JFrame loginWindow = new LoginWindow(conn);
		loginWindow.setVisible(true);
	}

	@Override
	public void windowOpened(WindowEvent e) {}

	@Override
	public void windowClosing(WindowEvent e) {}

	@Override
	public void windowClosed(WindowEvent e) {}

	@Override
	public void windowIconified(WindowEvent e) {}

	@Override
	public void windowDeiconified(WindowEvent e) {}

	@Override
	public void windowActivated(WindowEvent e) {}

	@Override
	public void windowDeactivated(WindowEvent e) {}
}
