
package ui;

import java.awt.Color;
import java.awt.Component;

import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import main.RemoteFile;

/**
 * Displays a list of RemoteFiles and allows the user to select one or more items
 *
 */
public class FileList extends JList<RemoteFile>
{
	/**
	 * Serialization identifier.
	 * Increment any time the class signature changes.
	 */
	private static final long serialVersionUID = 1L;
	
	/**
	 * Create a FileList from a list of RemoteFiles
	 * @param model List of RemoteFiles
	 */
	public FileList(DefaultListModel<RemoteFile> model, DefaultListModel<RemoteFile> fullModel)
	{
		super(model); // ha ha ha

		this.setCellRenderer(new FileCellRenderer(fullModel));
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
	}

	/**
	 * Renderer for a single cell in out 
	 *
	 */
	private static class FileCellRenderer extends JLabel implements ListCellRenderer<RemoteFile>
	{
		/**
		 * Serialization identifier.
		 * Increment any time the class signature changes.
		 */
		private static final long serialVersionUID = 1L;
		
		/**
		 * The color displayed when the file cell is selected
		 */
		private static final Color selectedColor = new Color(230, 230, 250);
		
		/**
		 * The color displayed when the file cell is in focus
		 */
		private static final Color focusColor = new Color(202, 225, 255);
		
		/**
		 * An icon representing the service
		 */
		ImageIcon iconDropbox;
		ImageIcon iconGDrive;
		ImageIcon iconSplit;
		DefaultListModel<RemoteFile> fullModel;
		
		/**
		 * Create a FileCellRenderer
		 * @param fullModel 
		 */
		public FileCellRenderer(DefaultListModel<RemoteFile> fullModel)
		{
			super();
			
			this.iconDropbox = new ImageIcon(ClassLoader.getSystemResource("service_icons/dropbox.png"));
			this.iconGDrive = new ImageIcon(ClassLoader.getSystemResource("service_icons/gdrive.png"));
			this.iconSplit = new ImageIcon(ClassLoader.getSystemResource("service_icons/split.png"));
			this.fullModel = fullModel;
		}

		@Override
		public Component getListCellRendererComponent(
				JList<? extends RemoteFile> list, RemoteFile file,
				int index, boolean isSelected, boolean hasFocus) {
			String mainFile = file.getName();
			String mainFileExtension = "";
			int curr = mainFile.lastIndexOf(".");
		    if(curr <= 0){
		    	//do nothing
		    } else {
		    	mainFileExtension = mainFile.substring(curr, mainFile.length());
		    	mainFile = mainFile.substring(0, curr);
		    }
		    if(mainFileExtension.equals(".1")) {
		    	long sum = file.getSize();
		    	for(int i = 0; i < fullModel.getSize(); i++) {
		    		String fullFile = fullModel.get(i).getName();
		    		String fullExt = "";
		    		int curr2 = fullFile.lastIndexOf(".");
		    		if(curr2 > 0) {
		    			fullExt = fullFile.substring(curr2, fullFile.length());
		    			fullFile = fullFile.substring(0, curr2);
		    			if(!fullExt.equals(".1")) {
		    				if(mainFile.equals(fullFile)) {
		    					sum += fullModel.get(i).getSize();
		    				}
		    				else continue;
		    			}
		    			else continue;
		    		}
		    	}
		    	this.setText(mainFile + " Split File " + sum + " bytes");
		    	this.setIcon(this.iconSplit);
		    }
		    else {
		    	this.setText(file.getName() + " " + file.getRemoteDrive().getUsername() + "'s " + file.getRemoteDrive().getServiceNiceName() + " " + file.getSize() + " bytes");
		    	if(file.getRemoteDrive().getServiceNiceName().equals("Dropbox"))
		    		this.setIcon(this.iconDropbox);
		    	else this.setIcon(this.iconGDrive);
		    }
		    
			Color background = Color.WHITE;
			
			if (isSelected) {
				background = hasFocus ? focusColor : selectedColor;
			}
			
			this.setBackground(background);
			this.setOpaque(true);
			
			return this;
		}
	}
}
