package main;

import java.util.ArrayList;
import java.util.HashMap;

public class AccountSettings {
	//docx, dot, rtf, doc, txt
	private boolean docsChecked = false;
	//xls, xlsx, ods, csv, tsv, xlt, tab
	private boolean presentationChecked = true;
	//pptx, ppt, pps
	private boolean spreadsheetChecked = true;
	private ArrayList<String> restrictedTypes = new ArrayList<String>();
	
	public AccountSettings() {
		restrictedTypes.add(".mp4");
		restrictedTypes.add(".mp3");
		restrictedTypes.add(".jpg");
	}
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
	public ArrayList<String> getRestrictedTypes() {
		return restrictedTypes;
	}
	public void setRestrictedTypes(ArrayList<String> restrictedTypes) {
		this.restrictedTypes = restrictedTypes;
	}
}
