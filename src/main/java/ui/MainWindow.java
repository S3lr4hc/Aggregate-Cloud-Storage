
package ui;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import com.microsoft.sqlserver.jdbc.SQLServerPreparedStatement;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils.Collections;

import main.AccountSettings;
import main.DBConnectionFactory;
import main.DriveStoreEventListener;
import main.FileManipulation;
import main.RemoteDrive;
import main.RemoteDriveFactory;
import main.RemoteDriveStore;
import main.RemoteEntry;
import main.RemoteFile;
import main.RemoteFolder;
import main.ShareFile;
import ui.FolderTree.FolderTreeNode;

/**
 * The topmost level interface for Fusein.
 * 
 * @author Ryan K
 * @date March 17, 2014
 */
public class MainWindow extends JFrame implements WindowListener, DriveStoreEventListener
{
	/**
	 * Serialization identifier.
	 * Increment any time the class signature changes.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Used to keep track of the current set of remote drives.
	 */
	private RemoteDriveStore remoteDrives;
	
	
	/**
	 * The RemoteDriveFactory object used in the UI to 
	 * create RemoteDrives
	 */
	private RemoteDriveFactory factory;
	
	/**
	 * The windows top menu bar
	 */
	private JMenuBar menuBar;
	
	/**
	 * A Toolbar for actions on files and folders
	 */
	private JToolBar toolBar;
	
	/**
	 * A Displayable folder hierarchy for RemoteFolders
	 */
	private FolderTree folderTree;
	
	/**
	 * Used to display space info of drive
	 */
	private JProgressBar statusBar;
	
	/**
	 * A vertical listing of files.
	 */
	private FileList fileList;
	
	/**
	 * A model of RemoteFiles
	 */
	private DefaultListModel<RemoteFile> fileListModel;
	
	
	private DefaultListModel<RemoteFile> completeFileListModel;

	/**
	 * Button used to upload a file to a RemoteDrive
	 */
	private JButton cmdUpload;
	
	/**
	 * String that shows the current user
	 */
	private String userAccount;
	
	/**
	 * String that shows the id of the current user
	 */
	private int userID;
	
	/**
	 * Double that shows the total space
	 */
	private static double totalSize;
	
	/**
	 * Double that shows the used space
	 */
	private static double usedSize;
	
	private boolean split;
	
	private boolean splitMethod;
	
	private String currfilePath;
	
	private FileManipulation fileManipulator;
	
	private AccountSettings acctSettings;
	/**
	 * Create a MainWindow to display a list of RemoteDrives
	 * @param driveStore The RemoteDrives to display
	 */
	public MainWindow(RemoteDriveStore driveStore, String userAccount, int id)
	{
		super();
		
		this.factory = new RemoteDriveFactory();
		this.remoteDrives = driveStore;
		this.userAccount = userAccount;
		this.currfilePath = System.getProperty("user.home");
		this.fileManipulator = new FileManipulation();
		this.userID = id;
		this.acctSettings = new AccountSettings(this.userID);
		
		// Set defaults
		this.setTitle("Fusein");
		this.setSize(800, 600); //TODO: Size to a scale of the current resolution.
		
		// Center the window
		Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
		int x = scrSize.width / 2 - this.getWidth() / 2;
		int y = scrSize.height / 2 - this.getHeight() / 2;
		this.split = false;
		this.splitMethod = false;
		this.setLocation(x, y);
		
		// We'll handle closing ourselves.
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		// Use a reasonable layout manager
		this.setLayout(new BorderLayout());
		
		// ==== Menu Bar ====
		this.menuBar = new JMenuBar();
		
		// == File ==
		JMenu menuFile = new JMenu("File");
		menuFile.setMnemonic(KeyEvent.VK_F);
		menuFile.getAccessibleContext().setAccessibleDescription("Operations on Files");
		
		JMenuItem cmdQuit = new JMenuItem("Quit", KeyEvent.VK_Q);
		cmdQuit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event)
			{
				MainWindow.this.dispatchEvent(new WindowEvent(MainWindow.this, WindowEvent.WINDOW_CLOSING));
			}
		});
		menuFile.add(cmdQuit);
		
		this.menuBar.add(menuFile);
		
		// == Options ==
		JMenu menuOptions = new JMenu("Options");
		menuOptions.setMnemonic(KeyEvent.VK_O);
		menuOptions.getAccessibleContext().setAccessibleDescription("Program Settings");
		
		JMenuItem cmdFileExclusion = new JMenuItem("Add File Exclusions");
		cmdFileExclusion.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				AddFileExclusionsView fileExclusionsView = new AddFileExclusionsView(remoteDrives, acctSettings, userID);
				fileExclusionsView.setVisible(true);
			}
		});
		menuOptions.add(cmdFileExclusion);
		
		JMenuItem cmdShareAlloc = new JMenuItem("Share Allocation");
		cmdShareAlloc.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				ShareAllocationView shareAllocView = new ShareAllocationView(remoteDrives);
				shareAllocView.setVisible(true);
			}
		});
		menuOptions.add(cmdShareAlloc);
		
		JMenuItem cmdSplit = new JMenuItem("Split Uploads");
		cmdSplit.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				int in = JOptionPane.showConfirmDialog(null, "Split All", "Please Select", JOptionPane.YES_NO_OPTION);
				if(in == JOptionPane.YES_OPTION)
					split = true;
				else split = false;
				System.out.println(split);
			}
		});
		menuOptions.add(cmdSplit);
		
		JMenuItem cmdSplitMethod = new JMenuItem("Split Method");
		cmdSplitMethod.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				Object[] options = {"Ratio",
	                    			"Best Fit"};
				int in = JOptionPane.showConfirmDialog(null, "Split Method?", "Please Select", JOptionPane.YES_NO_OPTION);
				if(in == JOptionPane.YES_OPTION)
					splitMethod = true;
				else splitMethod = false;
				System.out.println(splitMethod);
			}
		});
		menuOptions.add(cmdSplitMethod);
		
		JMenuItem cmdFileLocation = new JMenuItem("Set File Location");
		cmdFileLocation.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String in = JOptionPane.showInputDialog(null, "Current File Path: "+ currfilePath + "\nSet file path: ", currfilePath);
				if (in != null && in.length() > 0) {
					int res;
					if(new File(in).isDirectory()) {
						currfilePath = in;
						res = JOptionPane.showConfirmDialog(null,
							"File Path Change Successful!", "Message",
							JOptionPane.PLAIN_MESSAGE);
					} else {
						res = JOptionPane.showConfirmDialog(null,
							"File Path Change Failed!", "Message",
							JOptionPane.PLAIN_MESSAGE);
					}
				}
			}
		});
		menuOptions.add(cmdFileLocation);
		
		this.menuBar.add(menuOptions);
		//TODO
		// WOW LOOK AT ALL OF THESE SETTINGS
		// I CAN'T EVEN BELIEVE THE CONFIGURABILITY
		// OF THIS FINE APPLICATION
		
		this.setJMenuBar(this.menuBar);
		
		// ==== Tool Bar ====
		this.toolBar = new JToolBar();
		this.toolBar.setFloatable(false);
		
		final JButton cmdOpen = new JButton("Open");
		cmdOpen.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event)
			{
				RemoteFile fileToOpen = ((RemoteFile) fileList.getSelectedValue());
				OpenMethodWorker omw = new OpenMethodWorker(fileToOpen);
				omw.execute();
			}
		});
		cmdOpen.setEnabled(false);
		this.toolBar.add(cmdOpen);
		
		this.cmdUpload = new JButton("Upload");
		this.cmdUpload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event)
			{
				FolderTreeNode node = (FolderTreeNode)MainWindow.this.folderTree.getLastSelectedPathComponent();
				/*if (node == null) {
					return;
				}*/
				//RemoteFolder folder = node.getFolder();
				
				UploadFileDialog ufd = null;
				try {
					ufd = new UploadFileDialog(remoteDrives, acctSettings, split, splitMethod/*, folder*/);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				ufd.setVisible(true);
			}
		});
		this.cmdUpload.setEnabled(false);
		this.toolBar.add(this.cmdUpload);
		
		final JButton cmdDownload = new JButton("Download");
		cmdDownload.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event)
			{
				ArrayList<RemoteFile> fileOwner = new ArrayList<RemoteFile>();
				fileOwner.add(((RemoteFile)fileList.getSelectedValue()));
				String mainFile = fileList.getSelectedValue().getName();
				String mainFile2 = mainFile;
				int curr = mainFile2.lastIndexOf(".");
			    if(curr <= 0){
			    	//do nothing
			    } else {
			    	mainFile2 = mainFile2.substring(0, curr);
			    }
				for(int i = 0; i < completeFileListModel.getSize(); i++) {
					RemoteFile file = completeFileListModel.get(i);
					String comparedFile = file.getName();
					if(comparedFile.startsWith(mainFile2) && comparedFile.substring(mainFile2.length()).matches("^\\.\\d+$")) {
				    	if(!mainFile.equals(comparedFile)) {
				    		fileOwner.add(file);
				    	}
				    }
				}
				DownloadFileDialog dfd = null;
				try {
					dfd = new DownloadFileDialog(fileOwner, currfilePath);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				dfd.setVisible(true);
				String location = dfd.getFilePath();
				if(fileOwner.size() > 1) {
					try {
						fileManipulator.join(location + "\\" + mainFile2);
						fileManipulator.deleteAll(location + "\\" + mainFile2);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});
		cmdDownload.setEnabled(false);
		this.toolBar.add(cmdDownload);
		
		final JButton cmdDelete = new JButton("Delete");
		cmdDelete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event)
			{
				ArrayList<RemoteFile> fileOwner = new ArrayList<RemoteFile>();
				fileOwner.add(((RemoteFile) fileList.getSelectedValue()));
				String mainFile = fileList.getSelectedValue().getName();
				String mainFile2 = mainFile;
				int curr = mainFile2.lastIndexOf(".");
			    if(curr <= 0){
			    	//do nothing
			    } else {
			    	mainFile2 = mainFile2.substring(0, curr);
			    }
				for(int i = 0; i < completeFileListModel.getSize(); i++) {
					RemoteFile file = completeFileListModel.get(i);
					String comparedFile = file.getName();
					if(comparedFile.startsWith(mainFile2) && comparedFile.substring(mainFile2.length()).matches("^\\.\\d+$")) {
				    	if(!mainFile.equals(comparedFile)) {
				    		fileOwner.add(file);
				    	}
				    }
				}
				DeleteFileDialog dfd = new DeleteFileDialog(fileOwner, fileListModel, completeFileListModel);
				dfd.setVisible(true);
			}
		});
		cmdDelete.setEnabled(false);
		this.toolBar.add(cmdDelete);
		
		final JButton cmdShare = new JButton("Share");
		//fix acct mgmt
		cmdShare.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ShareFile sending = new ShareFile();
				ArrayList<RemoteFile> fileOwner = new ArrayList<RemoteFile>();
				fileOwner.add(((RemoteFile) fileList.getSelectedValue()));
				String mainFile = fileList.getSelectedValue().getName();
				String mainFile2 = mainFile;
				int curr = mainFile2.lastIndexOf(".");
			    if(curr <= 0){
			    	//do nothing
			    } else {
			    	mainFile2 = mainFile2.substring(0, curr);
			    }
				for(int i = 0; i < completeFileListModel.getSize(); i++) {
					RemoteFile file = completeFileListModel.get(i);
					String comparedFile = file.getName();
					if(comparedFile.startsWith(mainFile2) && comparedFile.substring(mainFile2.length()).matches("^\\.\\d+$")) {
				    	if(!mainFile.equals(comparedFile)) {
				    		fileOwner.add(file);
				    	}
				    }
				}
				DownloadFileDialog dfd = null;
					try {
						dfd = new DownloadFileDialog(fileOwner, currfilePath);
					} catch (IOException e2) {
						// TODO Auto-generated catch block
						e2.printStackTrace();
					}
					dfd.setVisible(true);
					String location = dfd.getFilePath();
					if(fileOwner.size() > 1) {
						try {
							fileManipulator.join(location + "\\" + mainFile2);
							fileManipulator.deleteAll(location + "\\" + mainFile2);
						} catch (IOException e3) {
							// TODO Auto-generated catch block
							e3.printStackTrace();
						}
					}
					if(fileOwner.size() > 1)
						try {
							sending.sendFile(currfilePath + "\\" + mainFile2);
						} catch (Exception e2) {
							// TODO Auto-generated catch block
							e2.printStackTrace();
						}
					else  {
						if(!mainFile.contains(".")) {
							File file = new File(currfilePath);
							File[] listofFiles = file.listFiles();
							for(File currFile:listofFiles) {
								if(currFile.getName().contains(mainFile)) {
									mainFile = currFile.getName();
								}
							}
						}
						try {
							sending.sendFile(currfilePath + "\\" + mainFile);
						} catch (Exception e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
					}
					try {
						if(fileOwner.size() > 1)
							fileManipulator.deleteFile(currfilePath + "\\" + mainFile2);
						else fileManipulator.deleteFile(currfilePath + "\\" + mainFile);
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
			}
		});
		cmdShare.setEnabled(false);
		this.toolBar.add(cmdShare);
		
		JButton cmdAddService = new JButton("Add Service");
		cmdAddService.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event)
			{
				AddServiceDialog asd = new AddServiceDialog(MainWindow.this, 
						remoteDrives, factory);
				asd.setVisible(true);
			}
		});
		this.toolBar.add(cmdAddService);
		
		JButton cmdShowServices = new JButton("View Services");
		cmdShowServices.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event)
			{
				ServiceListWindow serviceWindow = new ServiceListWindow(MainWindow.this.getDriveStore());
				serviceWindow.setVisible(true);
			}
		});
		this.toolBar.add(cmdShowServices);
		
		JButton cmdRefresh = new JButton("Refresh");
		cmdRefresh.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event)
			{
				getOverallDriveSize();
				folderTree.clearSelection();
                
                List<RemoteDrive> drives = getDriveStore().getAllDrives();
                
                MainWindow.this.fileListModel.clear();
                MainWindow.this.completeFileListModel.clear();
                
                for(RemoteDrive drive : drives) {
                	System.out.println("Reloading "+drive.getUsername()+ "'s " +drive.getServiceNiceName());
                	final RemoteFolder root = drive.getRootFolder();
                	(new SwingWorker<List<RemoteEntry>, Void>() {

						@Override
						protected List<RemoteEntry> doInBackground() throws Exception 
						{
							return root.getEntries();
						}
                		
						@Override
						protected void done() {
							List<RemoteEntry> entries;
							try {
								entries = this.get();
							} catch (InterruptedException | ExecutionException e) {
								e.printStackTrace();
								return;
							}
							
							Iterator<RemoteEntry> it = entries.iterator();
		    				while (it.hasNext()) {
		    					RemoteEntry entry = it.next();
		    					if (entry.isFile()) {
		    						RemoteEntry currEntry = entry;
		    						String scurrEntry = currEntry.getName();
		    						int curr = scurrEntry.lastIndexOf(".");
		    					    if(curr <= 0){
		    					    	//do nothing
		    					    } else {
		    					    	scurrEntry = scurrEntry.substring(curr, scurrEntry.length());
		    					    }
		    					    if(!scurrEntry.matches("^\\.\\d+$") || scurrEntry.equals(".1"))
		    					    	MainWindow.this.fileListModel.addElement(entry.asFile());
		    					    MainWindow.this.completeFileListModel.addElement(entry.asFile());
		    					}
		    				}
						}
                	}).execute();
                }
			}
		});
		this.toolBar.add(cmdRefresh);
		
		final JTextField searchField = new JTextField("Search");
		searchField.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(FocusEvent e) { }
			@Override
			public void focusGained(FocusEvent e) {
				searchField.selectAll();
			}
		});
		searchField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String searchValue = searchField.getText();
				searchList(searchValue);
			}
		});
		this.toolBar.add(searchField);
		
		this.add(this.toolBar, BorderLayout.PAGE_START);
		
		// ==== The Rest ====
		JPanel folderTreePanel = new JPanel();
		folderTreePanel.setLayout(new BorderLayout());
		
		JPanel fileViewBar = new JPanel();
		fileViewBar.setMinimumSize(new Dimension(150, 100));
		fileViewBar.setBorder(new EmptyBorder(5, 5, 5, 5));
		GridLayout fileViewLayout = new GridLayout(1, 2, 5, 5);
		fileViewBar.setLayout(fileViewLayout);
		
		folderTreePanel.add(fileViewBar, BorderLayout.PAGE_START);
		
		JLabel lbl = new JLabel(this.userAccount + " : FolderList");
		lbl.setHorizontalAlignment(SwingConstants.CENTER);
		fileViewBar.add(lbl);
		
		MainWindow.totalSize = 0;
		MainWindow.usedSize = 0;
		
		// Dummy root
		this.folderTree = new FolderTree();
		this.folderTree.setMinimumSize(new Dimension(150, 100));
		JScrollPane fileTreeView = new JScrollPane(this.folderTree);
		folderTreePanel.add(fileTreeView, BorderLayout.CENTER);
		
		this.folderTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent event)
			{
				final FolderTreeNode node = (FolderTreeNode)MainWindow.this.folderTree.getLastSelectedPathComponent();
				folderTree.repaint();
				if (node == null) {
					return;
				}
				final RemoteFolder folder = node.getFolder();
				
				(new SwingWorker<List<RemoteEntry>, Void>() {
					@Override
					protected List<RemoteEntry> doInBackground() throws Exception
					{
						return folder.getEntries();
					}
					
					@Override
					protected void done()
					{
						List<RemoteEntry> entries;
						try {
							entries = this.get();
						} catch (InterruptedException | ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							return;
						}
						
						MainWindow.this.fileListModel.clear();
						MainWindow.this.completeFileListModel.clear();
						DefaultTreeModel model = (DefaultTreeModel)MainWindow.this.folderTree.getModel();
						node.removeAllChildren();
						
						Iterator<RemoteEntry> it = entries.iterator();
						while (it.hasNext()) {
							RemoteEntry entry = it.next();
							if (entry.isFile()) {
								RemoteEntry currEntry = entry;
								String scurrEntry = currEntry.getName();
								int curr = scurrEntry.lastIndexOf(".");
							    if(curr <= 0){
							    	//do nothing
							    } else {
							    	scurrEntry = scurrEntry.substring(curr, scurrEntry.length());
							    	System.out.println(scurrEntry);
							    }
							    if(!scurrEntry.matches("^\\.\\d+$") || scurrEntry.equals(".1"))
							    	MainWindow.this.fileListModel.addElement(entry.asFile());
							    MainWindow.this.completeFileListModel.addElement(entry.asFile());
							} else {
								MainWindow.this.folderTree.addFolder(node, entry.asFolder());
							}
						}
						
						model.reload(node);
					}
				}).execute();
			}
		});
		
		this.completeFileListModel = new DefaultListModel<>();
		this.fileListModel = new DefaultListModel<>();
		this.fileList = new FileList(this.fileListModel, this.completeFileListModel);
		
		this.fileList.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent event)
			{
				if (MainWindow.this.fileList.getSelectedValue() == null) {
					cmdDelete.setEnabled(false);
					cmdDownload.setEnabled(false);
					cmdOpen.setEnabled(false);
					cmdShare.setEnabled(false);
				} else {
					String filename = fileList.getSelectedValue().getName();
					int curr = filename.lastIndexOf(".");
					if(curr > 0)
						filename = filename.substring(curr, filename.length());
					cmdDelete.setEnabled(true);
					cmdDownload.setEnabled(true);
					if(!filename.equals(".1"))
						cmdOpen.setEnabled(true);
					else cmdOpen.setEnabled(false);
					cmdShare.setEnabled(true);
				}
			}
		});
		
		JScrollPane fileListView = new JScrollPane(this.fileList);
		JSplitPane fileSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
		                               folderTreePanel, fileListView);
		
		fileSplit.setDividerSize(5);
		fileSplit.setContinuousLayout(true);
		
		this.add(fileSplit, BorderLayout.CENTER);
		
		// ==== Status Bar ====
		this.statusBar = new JProgressBar();//new JPanel();
		this.statusBar.setBorder(BorderFactory.createBevelBorder(BevelBorder.LOWERED));
		
		//TODO Implement a functional status bar
		//JLabel versionLabel = new JLabel("Fusein -- Project Deliverable 4");
		//FIX THIS TO SHOW CURRENT USER
		JButton refreshButton = new JButton("Refresh");
		JLabel versionLabel = new JLabel("Logged in as " + this.userAccount);
		this.statusBar.add(versionLabel);
		this.statusBar.setStringPainted(true);
		this.add(refreshButton, BorderLayout.PAGE_END);
		this.add(this.statusBar, BorderLayout.PAGE_END);
		
		// ==== CALLBACKS ====
		this.remoteDrives.addEventListener(this);
		this.addWindowListener(this);
		
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				getOverallDriveSize();
			}
		});
	}
	
	public void searchList(String searchValue) {
		for(int i = 0; i < fileListModel.size(); i++) {
			System.out.println(fileListModel.get(i).getName() + " " + i);
			if(!Pattern.compile(Pattern.quote(searchValue), Pattern.CASE_INSENSITIVE).matcher(fileListModel.get(i).getName()).find()/*fileListModel.get(i).getName().contains(searchValue)*/) {
				fileListModel.remove(i);
				i--;
			}
		}
	}
	
	public void getOverallDriveSize() {
		MainWindow.totalSize = 0;
		MainWindow.usedSize = 0;
		List<RemoteDrive> drives = getDriveStore().getAllDrives();
		
		for(RemoteDrive drive: drives) {
			MainWindow.totalSize += drive.getTotalSize();
			MainWindow.usedSize += drive.getUsedSize();
			System.out.println(drive.getTotalSize() + "/" + drive.getUsedSize());
		}
		double overAll = (usedSize/totalSize) * 100;
    	overAll = Math.ceil(overAll);
		MainWindow.this.statusBar.setValue((int)overAll);
	}
	
	/**
	 * Get the list of RemoteDrives
	 * @return The RemoteDrives displayed by the window
	 */
	public RemoteDriveStore getDriveStore()
	{
		return this.remoteDrives;
	}
	
	@Override
	public void driveRemoved(RemoteDrive drive)
	{
		int i = 0;
		while (i < this.fileListModel.size()) {
			RemoteFile file = this.fileListModel.elementAt(i);
			if (file == null) {
				break;
			}
			
			if (file.getRemoteDrive() == drive) {
				fileListModel.remove(i);
			} else {
				i++;
			}
		}
		
		/*DefaultTreeModel model = (DefaultTreeModel)this.folderTree.getModel();
		FolderTreeNode root = (FolderTreeNode)model.getRoot();
		Enumeration<FolderTreeNode> en = (Enumeration<FolderTreeNode>)root.children();
		while (en.hasMoreElements()) {
			FolderTreeNode node = en.nextElement();
			if (node.getFolder().getRemoteDrive() == drive) {
				model.removeNodeFromParent(node);
			}
		}*/
	}
	
	@Override
	public void driveAdded(final RemoteDrive drive)
	{
		cmdUpload.setEnabled(true);
		
		(new SwingWorker<List<RemoteEntry>, RemoteFolder>() {
			private FolderTreeNode root;
			
			@Override
			protected List<RemoteEntry> doInBackground() throws Exception
			{
				RemoteFolder root = drive.getRootFolder();
				
				this.publish(root);
				
				return root.getEntries();
			}
			
			@Override
			protected void process(List<RemoteFolder> chunks)
			{
				//this.root = MainWindow.this.folderTree.addFolder(null, chunks.get(0));
			}
			
			@Override
			protected void done()
			{
				List<RemoteEntry> entries;
				try {
					entries = this.get();
				} catch (InterruptedException | ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					return;
				}
				
				Iterator<RemoteEntry> it = entries.iterator();
				while (it.hasNext()) {
					RemoteEntry entry = it.next();
					if (entry.isFile()) {
						RemoteEntry currEntry = entry;
						String scurrEntry = currEntry.getName();
						int curr = scurrEntry.lastIndexOf(".");
					    if(curr <= 0){
					    	//do nothing
					    } else {
					    	scurrEntry = scurrEntry.substring(curr, scurrEntry.length());
					    }
					    if(!scurrEntry.matches("^\\.\\d+$") || scurrEntry.equals(".1"))
						    MainWindow.this.fileListModel.addElement(entry.asFile());
						MainWindow.this.completeFileListModel.addElement(entry.asFile());
					} else {
						MainWindow.this.folderTree.addFolder(null, entry.asFolder());
					}
				}
				
				//TODO this is kind of a hack
				DefaultTreeModel model = (DefaultTreeModel)MainWindow.this.folderTree.getModel();
				model.reload();
				//MainWindow.this.folderTree.expandPath(new TreePath(this.root.getPath()));
			}
		}).execute();
	}

	@Override
	public void windowActivated(WindowEvent event) {}

	@Override
	public void windowClosed(WindowEvent event) {}

	@Override
	public void windowClosing(WindowEvent event)
	{
		Connection conn = DBConnectionFactory.getInstance().getConnection();
		LoginWindow loginWindow = new LoginWindow(conn);
		
		this.remoteDrives.saveToFile(userID);
		this.dispose();
		loginWindow.setVisible(true);
	}

	@Override
	public void windowDeactivated(WindowEvent event) {}

	@Override
	public void windowDeiconified(WindowEvent event) {}

	@Override
	public void windowIconified(WindowEvent event) {}

	@Override
	public void windowOpened(WindowEvent event) {}
	
	private class OpenMethodWorker extends SwingWorker<Boolean, Void> {
		private RemoteFile fileToOpen;

		public OpenMethodWorker(RemoteFile fileToOpen) {
			this.fileToOpen = fileToOpen;
		}
		
		@Override
		protected Boolean doInBackground() throws Exception {
		    String fileUrl = this.fileToOpen.getLink();
		    
		    if (fileUrl == null) {
		    	return false;
		    }
		    
		    Desktop.getDesktop().browse(new URL(fileUrl).toURI());
		    
		    return true;
		}

		@Override
		protected void done() {
		}
	}
}
