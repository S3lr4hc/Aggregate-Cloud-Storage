
package ui;

import java.awt.BorderLayout;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import main.FileManipulation;
import main.RemoteFile;

/**
 * A file dialog for downloading files from a RemoteDrive
 * 
 * @date March 17, 2014
 */
public class DownloadFileDialog extends JPanel {
	
	/**
	 * Serialization Identifier.
	 * Increment this any time the class's signature changes.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The RemoteFile representing the file to be downloaded
	 */
	private ArrayList<RemoteFile> fileToDownload;
	
	private String filePath;
	
	private FileManipulation fileManipulator;

	/**
	 * Create a dialog to download a file
	 * @param fileOwner RemoteFile representing the file to be downloaded
	 * @throws IOException 
	 */
	public DownloadFileDialog(ArrayList<RemoteFile> fileToDownload, String filePath) throws IOException {
		super(new BorderLayout());
		this.fileToDownload = fileToDownload;
		this.filePath = filePath;
		this.fileManipulator = new FileManipulation();
		initDialog();
	}
	
	/**
	 * Initialize dialog and execute response
	 * @throws IOException 
	 * 
	 */
	public void initDialog() throws IOException {
		DownloadMethodWorker dwm = null;
		for(int i = 0; i < fileToDownload.size(); i++) {
			String fullPath = filePath + "\\" + fileToDownload.get(i).getName();
			dwm = new DownloadMethodWorker(fullPath, i);
			dwm.execute();
		}
		
		boolean succ;
		int res;
		try {
			succ = dwm.get();
			if (succ) {
				res = JOptionPane.showConfirmDialog(this,
						"Download Successful!", "Download file success",
						JOptionPane.PLAIN_MESSAGE);
			} else {
				res = JOptionPane.showConfirmDialog(this,
						"Download failed..." + "Try again.",
						"Download file Failure", JOptionPane.ERROR_MESSAGE);
			}

			if (res == JOptionPane.YES_OPTION) {
					return;
			}
		} catch (InterruptedException | ExecutionException e1) {
				e1.printStackTrace();
		}
	}
	
	public String getFilePath() {
		return this.filePath;
	}
	/**
	 * A class to perform the download method in a dedicated thread
	 *
	 */
	private class DownloadMethodWorker extends SwingWorker<Boolean, Void> {
		private String localPath;
		private int curr;

		public DownloadMethodWorker(String localPath, int curr) {
			this.localPath = localPath;
			this.curr = curr;
		}

		@Override
		protected Boolean doInBackground() throws Exception {
			return DownloadFileDialog.this.fileToDownload.get(curr).download(this.localPath);
		}

		@Override
		protected void done() {
		}
	}
}
