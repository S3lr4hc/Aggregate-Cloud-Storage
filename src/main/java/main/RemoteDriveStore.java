
package main;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Properties;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import ui.LoginWindow;
import ui.MainWindow;

import com.mysql.jdbc.PreparedStatement;

/**
 * Maintains a set of remote drives.
 * 
 * @author Ryan K
 * @date March 05, 2014
 */
public class RemoteDriveStore
{
	private ArrayList<RemoteDrive> remoteDrives;
	private ArrayList<DriveStoreEventListener> listeners;
	private Connection conn = null;
	private PreparedStatement stmt = null;
	private ResultSet rs = null;
	
	/**
	 * Initialize an empty RemoteDriveStore.
	 */
	public RemoteDriveStore()
	{
		remoteDrives = new ArrayList<RemoteDrive>();
		listeners = new ArrayList<DriveStoreEventListener>();
		conn = DBConnectionFactory.getInstance().getConnection();
	}
	
	/**
	 * Add a drive to the set.
	 * 
	 * @param drive The drive to add to the set.
	 * @return The session ID of the added drive, or -1 if the drive was already in the set.
	 */
	public int addDrive(RemoteDrive drive)
	{
		if (this.remoteDrives.contains(drive)) {
			return -1;
		}
		
		this.remoteDrives.add(drive);
		
		Iterator<DriveStoreEventListener> it = this.listeners.iterator();
		while (it.hasNext()) {
			it.next().driveAdded(drive);
		}
		
		System.out.printf("Added provider \"%s\"\n", drive.getServiceNiceName());
		
		return this.remoteDrives.indexOf(drive);
	}
	
	/**
	 * Remove a drive from the set.
	 * 
	 * @param drive The drive to remove from the set
	 * @return True if the drive was in the set (and has now been removed), false otherwise.
	 * @throws DriveNotFoundException if the given RemoteDrive is not stored.
	 */
	public void removeDrive(RemoteDrive drive)
	{
		if (!this.remoteDrives.remove(drive)) {
			throw new DriveNotFoundException("RemoteDrive not stored!");
		}
		
		Iterator<DriveStoreEventListener> it = this.listeners.iterator();
		while (it.hasNext()) {
			it.next().driveRemoved(drive);
		}
		String sql = "DELETE FROM Drive WHERE Token=?";
		try {
			stmt = (PreparedStatement) conn.prepareStatement(sql);
			stmt.setString(1, drive.getAuthToken());
			stmt.executeUpdate();
			
		} catch (SQLException e2) {
			JOptionPane.showMessageDialog(null, e2);
		}
		System.out.printf("Removed provider \"%s\"\n", drive.getServiceNiceName());
	}
	
	/**
	 * Remove a drive from the set.
	 * 
	 * @param sessionID The session ID of the drive.
	 * @return The RemoteDrive that has been removed.
	 * @throws DriveNotFoundException if there is no stored drive with the given session ID.
	 */
	public RemoteDrive removeDriveById(int sessionID)
	{
		try {
			RemoteDrive removedDrive = this.remoteDrives.remove(sessionID);
			
			Iterator<DriveStoreEventListener> it = this.listeners.iterator();
			while (it.hasNext()) {
				it.next().driveRemoved(removedDrive);
			}
			
			System.out.printf("Removed provider \"%s\"\n", removedDrive.getServiceNiceName());
			
			return removedDrive;
		} catch (IndexOutOfBoundsException ex) {
			throw new DriveNotFoundException(String.format("RemoteDrive with session ID %d not stored!", sessionID), ex);
		}
	}
	
	/**
	 * Get a RemoteDrive by its session ID.
	 * 
	 * @param sessionID The session ID of the drive.
	 * @return The drive with the given session ID.
	 * @throws DriveNotFoundException if there is no stored drive with the given session ID.
	 */
	public RemoteDrive getDriveById(int sessionID)
	{
		try {
			return this.remoteDrives.get(sessionID);
		} catch (IndexOutOfBoundsException ex) {
			throw new DriveNotFoundException(String.format("RemoteDrive with session ID %d not stored!", sessionID), ex);
		}
	}
	
	public ArrayList<RemoteDrive> getAllDrives() {
		return remoteDrives;
	}
	
	public void addEventListener(DriveStoreEventListener listener)
	{
		this.listeners.add(listener);
	}
	
	public void removeEventListener(DriveStoreEventListener listener)
	{
		this.listeners.remove(listener);
	}
	
	public Iterator<RemoteDrive> iterator()
	{
		//TODO wrap this so we're notified if the user calls Iterator.remove()
		return this.remoteDrives.iterator();
	}
	
	public void saveToFile(int userID)
	{
		
		//Properties properties = new Properties();
		
		Iterator<RemoteDrive> it = this.remoteDrives.iterator();
		//int i = 0;
		while (it.hasNext()) {
			RemoteDrive drive = it.next();
			//properties.setProperty("service" + i, String.format("%s:%s", drive.getServiceNiceName(), drive.getAuthToken()));
			//i++;
			String sql = "INSERT INTO Drive(UserID, Service, Token) VALUES (?,?,?) ON DUPLICATE KEY UPDATE Token = VALUES(Token)";
			try {
				stmt = (PreparedStatement) conn.prepareStatement(sql);
				stmt.setInt(1, userID);
				stmt.setString(2, drive.getServiceNiceName());
				stmt.setString(3, drive.getAuthToken());
				stmt.executeUpdate();
				
			} catch (SQLException e2) {
				JOptionPane.showMessageDialog(null, e2);
			}
		}
		
		/*try {
			FileWriter writer = new FileWriter(path);
			properties.store(writer, "Authentication Tokens");
			writer.close();
		} catch (IOException e) {
			System.err.println("Failed to write " + path);
		}*/
	}
	
	public void loadFromFile(int userID)
	{
		String service = "";
		String token = "";
		RemoteDriveFactory rdFactory = new RemoteDriveFactory();
		long startTime = System.nanoTime();
		/*FileReader reader;
		Properties properties = new Properties();
		
		try {
			reader = new FileReader(path);
			properties.load(reader);
			reader.close();
		} catch (IOException e) {
			System.err.println("Failed to read " + path);
			return;
		}
		
		RemoteDriveFactory rdFactory = new RemoteDriveFactory();
		
		Iterator<?> it = properties.values().iterator();
		while (it.hasNext()) {
			String line = (String)it.next();
			int sepIndex = line.indexOf(':');
			
			if (sepIndex == -1) {
				System.err.printf("Malformed provider definition \"%s\"!\n", line);
				continue;
			}
			
			String driveType = line.substring(0, sepIndex);
			String authToken = line.substring(sepIndex + 1);
			
			System.out.printf("Restoring %s provider from file...\n", driveType);
			
			RemoteDrive drive = rdFactory.createRemoteDrive(driveType);
			if (drive == null) {
				System.err.printf("Bad provider name \"%s\"!\n", driveType);
				continue;
			}
			
			drive.setAuthToken(authToken);
			this.addDrive(drive);
		}*/
		String sql = "SELECT * FROM Drive WHERE UserID = ?";
		try {
			stmt = (PreparedStatement) conn.prepareStatement(sql);
			stmt.setInt(1, userID);
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				service = rs.getString("Service");				
				token = rs.getString("Token");
				System.out.printf("Restoring %s provider from file...\n", service);
				RemoteDrive drive = rdFactory.createRemoteDrive(service);
				if (drive == null) {
					System.err.printf("Bad provider name \"%s\"!\n", service);
					continue;
				}
				
				drive.setAuthToken(token);
				this.addDrive(drive);
			}
			long estimatedTime = System.nanoTime() - startTime;
    		System.out.println("Elapsed Time: " + estimatedTime);
		} catch (SQLException e2) {
			JOptionPane.showMessageDialog(null, e2);
		}
	}
}
