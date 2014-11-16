package ui;

import java.awt.BorderLayout;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import main.RemoteDrive;
import main.RemoteDriveStore;
import main.RemoteFolder;
import main.FileManipulation;

/**
 * A file dialog for uploading files to a service
 * 
 * @date March 17, 2014
 */
public class UploadFileDialog extends JPanel {
	private static final long serialVersionUID = 1L;
	
	/**
	 * A file chooser used to select the upload files
	 */
	private JFileChooser chooseUploadFile;
	
	/**
	 * The current list of RemoteDrives
	 */
	private RemoteDriveStore remoteDrives;
	
	/**
	 * TODO: The folder to be uploaded to.
	 */
	private RemoteFolder folder;

	/**
	 * Create a dialog for uploading a file for a list of RemoteDrives
	 * @param remoteDrives The current list of RemoteDrives
	 * @throws IOException 
	 */
	public UploadFileDialog(RemoteDriveStore remoteDrives) throws IOException {
		super(new BorderLayout());
		
		this.remoteDrives = remoteDrives;
		//this.folder = folder;
		
		initUploadFile();
	}

	/**
	 * Initialize the dialog, display and execute the action
	 * @throws IOException 
	 */
	private void initUploadFile() throws IOException {
		JPanel filePanel = new JPanel();
		/*JPanel servicePanel = new JPanel();
		servicePanel.setLayout(new BoxLayout(servicePanel, BoxLayout.Y_AXIS));*/
		
		final FileManipulation fileManipulator = new FileManipulation();
		double overallSize = 0;
		double mainDriveSize = 0;
		ArrayList<RemoteDrive> serviceData = remoteDrives.getAllDrives();
		RemoteDrive largestGoogleDrive = null;
		RemoteDrive largestDropbox = null;
		double largestGoogleDriveSpace = 0;
		double largestDropboxSpace = 0;
		final String[] serviceNames = new String[serviceData.size()];
		for (int i = 0; i < serviceData.size(); i++) {
			RemoteDrive service = serviceData.get(i);
			serviceNames[i] = String.format("%s's %s", service.getUsername(), service.getServiceNiceName());
			double availableSpace = service.getTotalSize() - service.getUsedSize();
			if(mainDriveSize < availableSpace)
				mainDriveSize = availableSpace; 
			if(service.getServiceNiceName().equals("Google Drive") && availableSpace > largestGoogleDriveSpace) {
				largestGoogleDrive = serviceData.get(i);
				largestGoogleDriveSpace = availableSpace;
			}
			if(service.getServiceNiceName().equals("Dropbox") && availableSpace > largestDropboxSpace) {
				largestDropbox = serviceData.get(i);
				largestDropboxSpace = availableSpace;
			}
			overallSize += availableSpace;
		}
		double slicePercentage = mainDriveSize/overallSize;
		BigDecimal bd = new BigDecimal(slicePercentage);
		bd = bd.setScale(2, BigDecimal.ROUND_DOWN); // setScale is immutable
		slicePercentage = bd.doubleValue();
		JList<String> list = new JList<String>(serviceNames);
		// Default to first item.
		list.setSelectedIndex(0);

		/*JLabel title = new JLabel("Upload to:");
		title.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
		list.setAlignmentX(java.awt.Component.CENTER_ALIGNMENT);
		servicePanel.add(title);
		servicePanel.add(list);*/

		chooseUploadFile = new JFileChooser();
		chooseUploadFile.setFileSelectionMode(JFileChooser.FILES_ONLY);
		//chooseUploadFile.setAccessory(servicePanel);

		int choice = chooseUploadFile.showDialog(filePanel, "Upload File");

		// If the user selects a file to upload...
		if (choice == JFileChooser.APPROVE_OPTION) {
			final File file = chooseUploadFile.getSelectedFile();
			String serv = list.getSelectedValue();
			// Now we need to determine the service to upload to.
			fileManipulator.splitFile(file.getAbsolutePath(), (long) (file.length() * slicePercentage));
			int numberParts = fileManipulator.getNumberParts(file.getAbsolutePath());
			double divideSplits = Math.ceil((double)(numberParts)/2d);
			UploadMethodWorker umw = null;
			for (int part = 1; part <= numberParts; part++) {
				File filetoUL = new File(file.getAbsoluteFile() + "." + part);
				
				if(part <= divideSplits) {
					umw = new UploadMethodWorker(filetoUL, largestGoogleDrive.getRootFolder());
				} else {
					umw = new UploadMethodWorker(filetoUL, largestDropbox.getRootFolder());
				}
				umw.execute();
				filetoUL = null;
			}
			//UploadMethodWorker umw = new UploadMethodWorker(file, mDrive.getRootFolder());
			//umw.execute();
			boolean succ;
			try {
				succ = umw.get();
				int res;
				
				if (succ) {
					res = JOptionPane
							.showConfirmDialog(this, "Upload successful!",
									"Upload Success",
									JOptionPane.PLAIN_MESSAGE);
				} else {
					res = JOptionPane
							.showConfirmDialog(this, "Upload failed..."
									+ "Try again.", "Upload Failure",
									JOptionPane.ERROR_MESSAGE);
				}
				
				if (res == JOptionPane.YES_OPTION) {
					// TODO Update file listing.
				}
			} catch (InterruptedException | ExecutionException e1) {
				e1.printStackTrace();
			}
			fileManipulator.deleteAll(file.getAbsolutePath());
		}
	}

	/**
	 * A class to perform file upload in a dedicated thread
	 *
	 */
	private class UploadMethodWorker extends SwingWorker<Boolean, Void> {
		/**
		 * The local file to be uploaded
		 */
		File toUpload;
		
		/**
		 * The destination remote drive folder
		 */
		RemoteFolder folderDest;
		
		/**
		 * The status of the operation
		 */
		boolean success;

		/**
		 * Create a worker to upload file to mDrive
		 * @param file The local file to be uploaded
		 * @param mDrive The destination drive
		 */
		public UploadMethodWorker(File file, RemoteFolder folderDest) {
			this.toUpload = file;
			this.folderDest = folderDest;
		}

		@Override
		protected Boolean doInBackground() throws Exception {
			return (folderDest.uploadFile(toUpload.getAbsolutePath()) != null);
		}

		@Override
		protected void done() {
			try {
				success = get();
				System.out.println(success);
				toUpload = null;
			} catch (InterruptedException | ExecutionException e) {
				e.printStackTrace();
			}
		}
	}

}
