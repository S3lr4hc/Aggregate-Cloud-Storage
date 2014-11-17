package main;

import java.util.HashMap;

public class AccountSettings {
	private boolean docsChecked = true;
	private boolean presentationChecked = false;
	private boolean spreadsheetChecked = true;
	private String[] restrictedTypes = {".mp4", ".mp3", ".jpg"};
	
	public boolean isDocsChecked() {
		return docsChecked;
	}
	public void setDocsChecked(boolean docsChecked) {
		this.docsChecked = docsChecked;
	}
	public boolean isPresentationChecked() {
		return presentationChecked;
	}
	public void setPresentationChecked(boolean presentationChecked) {
		this.presentationChecked = presentationChecked;
	}
	public boolean isSpreadsheetChecked() {
		return spreadsheetChecked;
	}
	public void setSpreadsheetChecked(boolean spreadsheetChecked) {
		this.spreadsheetChecked = spreadsheetChecked;
	}
	public String[] getRestrictedTypes() {
		return restrictedTypes;
	}
	public void setRestrictedTypes(String[] restrictedTypes) {
		this.restrictedTypes = restrictedTypes;
	}
}
