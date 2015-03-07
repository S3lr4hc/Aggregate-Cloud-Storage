package main;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import com.mysql.jdbc.PreparedStatement;

public class AccountSettings {
	//docx, dot, rtf, doc, txt
	private boolean docsChecked = false;
	//xls, xlsx, ods, csv, tsv, xlt, tab
	private boolean presentationChecked = false;
	//pptx, ppt, pps
	private boolean spreadsheetChecked = false;
	
	private ArrayList<String> restrictedTypes;
	
	private Connection conn = null;
	
	private PreparedStatement stmt = null;
	
	private ResultSet rs = null;
	
	public AccountSettings(int userID) {
		conn = DBConnectionFactory.getInstance().getConnection();
		restrictedTypes = new ArrayList<String>();
		
		populateCustoms(userID);
		updateComplementary(userID);
	}
	private void populateCustoms(int userID) {
		String fileType = "";
		String sql = "SELECT * FROM CustomRestriction WHERE UserID = ?";
		restrictedTypes.clear();
		try {
			stmt = (PreparedStatement) conn.prepareStatement(sql);
			stmt.setInt(1, userID);
			rs = stmt.executeQuery();
			
			while(rs.next()) {
				fileType = rs.getString("FileType");
				
				restrictedTypes.add(fileType);
			}
			
		} catch (SQLException e2) {
			JOptionPane.showMessageDialog(null, e2);
		}
	}
	private void updateComplementary(int userID) {
		int temp;
		String sql = "SELECT * FROM ComplementaryRestriction WHERE UserID = ?";
		try {
			stmt = (PreparedStatement) conn.prepareStatement(sql);
			stmt.setInt(1, userID);
			rs = stmt.executeQuery();
			
			if(rs.next()) {
				temp = rs.getInt("DocsChecked");
				if(temp != 0)
				docsChecked = true;
				else docsChecked = false;
				temp = rs.getInt("PresentationChecked");
				if(temp != 0)
					presentationChecked = true;
				else presentationChecked = false;
				temp = rs.getInt("SpreadSheetChecked");
				if(temp != 0)
					spreadsheetChecked = true;
				else spreadsheetChecked = false;
			}
			
		} catch (SQLException e2) {
			JOptionPane.showMessageDialog(null, e2);
		}
	}
	public boolean isDocsChecked() {
		return docsChecked;
	}
	public void setDocsChecked(boolean docsChecked, int userID) {
		String sql = "UPDATE ComplementaryRestriction SET DocsChecked = ? WHERE UserID = ?";
		try {
			stmt = (PreparedStatement) conn.prepareStatement(sql);
			if(docsChecked == true)
			stmt.setInt(1, 1);
			else stmt.setInt(1, 0);
			stmt.setInt(2, userID);
			stmt.executeUpdate();
			
		} catch (SQLException e2) {
			JOptionPane.showMessageDialog(null, e2);
		}
		this.docsChecked = docsChecked;
	}
	public boolean isPresentationChecked() {
		return presentationChecked;
	}
	public void setPresentationChecked(boolean presentationChecked, int userID) {
		String sql = "UPDATE ComplementaryRestriction SET PresentationChecked = ? WHERE UserID = ?";
		try {
			stmt = (PreparedStatement) conn.prepareStatement(sql);
			if(presentationChecked == true)
				stmt.setInt(1, 1);
			else stmt.setInt(1, 0);
			stmt.setInt(2, userID);
			stmt.executeUpdate();
			
		} catch (SQLException e2) {
			JOptionPane.showMessageDialog(null, e2);
		}
		this.presentationChecked = presentationChecked;
	}
	public boolean isSpreadsheetChecked() {
		return spreadsheetChecked;
	}
	public void setSpreadsheetChecked(boolean spreadsheetChecked, int userID) {
		String sql = "UPDATE ComplementaryRestriction SET SpreadSheetChecked = ? WHERE UserID = ?";
		try {
			stmt = (PreparedStatement) conn.prepareStatement(sql);
			if(spreadsheetChecked == true)
				stmt.setInt(1, 1);
			else stmt.setInt(1, 0);
			stmt.setInt(2, userID);
			stmt.executeUpdate();
			
		} catch (SQLException e2) {
			JOptionPane.showMessageDialog(null, e2);
		}
		this.spreadsheetChecked = spreadsheetChecked;
	}
	public ArrayList<String> getRestrictedTypes() {
		return restrictedTypes;
	}
	public void setRestrictedTypes(ArrayList<String> restrictedTypes) {
		this.restrictedTypes = restrictedTypes;
	}
	public void removeCustomExtension(String fileType, int userID) {
		String sql = "DELETE FROM CustomRestriction WHERE UserID = ? AND FileType = ?";
		try {
			stmt = (PreparedStatement) conn.prepareStatement(sql);
			stmt.setInt(1, userID);
			stmt.setString(2, fileType);
			stmt.executeUpdate();
			
		} catch (SQLException e2) {
			JOptionPane.showMessageDialog(null, e2);
		}
		//populateCustoms(userID);
	}
}
