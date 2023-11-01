package application.listeners;

import application.MediaDashboard;
import util.FileManager;
import util.MediaItem;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

public class DashboardHandler extends Handler {

    /**
     * Save the dashboard and file manager for easy use on button presses
     * @param ui the MediaDashboard currently open
     */
    public DashboardHandler(MediaDashboard ui) {
        super(ui);
    }

    /**
     * Function ran when a button on the MediaDashboard is clicked.
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        //if the file managers file property hasn't been set then disable button presses.
        if (!this.getUI().getFileMan().hasFile()) {
            JOptionPane.showMessageDialog(this.getUI().getFrame(),
                    "You need to open a library file first from the file dropdown.",
                    "No library open", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JButton button = (JButton) e.getSource(); //Gets the clicked button

        //Run a function based on which button is clicked.
        switch (button.getText()) {
            case "Add media" -> addMedia();
            case "Remove media" -> delMedia();
            case "Scan folder" -> scanMedia();
            case "Open in explorer" -> openInExplorer();
        }
    }

    /**
     * The function that runs when the add media button is clicked.
     * Opens a new file chooser to allow the user to select a media file to be managed.
     */
    private void addMedia() {
        //Creates the file selection menu
        JFileChooser fileChooser = new JFileChooser();
        //By default, will only show the selectable files
        fileChooser.setFileFilter(new FileNameExtensionFilter("Media Files",
                "jpg",
                "png",
                "jpeg",
                "mp3",
                "mp4"
        ));

        //Get the GUI and cast to MediaDashboard class
        MediaDashboard dashboard = (MediaDashboard) this.getUI();
        //If the file chooser is closed then do nothing
        int choice = fileChooser.showOpenDialog(dashboard.getFrame());
        if (choice == JFileChooser.CANCEL_OPTION) return;


        //If invalid file type then display error message and don't attempt to add media
        String dir = fileChooser.getSelectedFile().getAbsolutePath();
        if (!dashboard.getFileMan().isValidFile(dir)) {
            JOptionPane.showMessageDialog(dashboard.getFrame(),
                    "Sorry, that file isn't supported.",
                    "Invalid file", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //Add the selected media to the file manager and update the media library table
        dashboard.getFileMan().addMedia(dir);
        dashboard.populateTable();
    }

    /**
     * The function that's ran when the remove media button is clicked.
     * Stops a file from being managed by the media library organiser.
     */
    private void delMedia() {
        //Get the GUI and cast it to the MediaDashboard class
        MediaDashboard dashboard = (MediaDashboard) this.getUI();
        //If no row is selected on the media table then display an error message
        int rowIndex = dashboard.getRowIndex();
        if (rowIndex == -1) {
            JOptionPane.showMessageDialog(dashboard.getFrame(),
                    "Please select a media item you wish to remove.",
                    "No media item selected", JOptionPane.ERROR_MESSAGE);
            return;
        }

        FileManager fileMan = dashboard.getFileMan();
        MediaItem mediaItem;
        String currentPlaylist = dashboard.getCurrentPlaylist();
        //If no playlist is selected then the rowIndex will match up with the media list so remove by index
        if (currentPlaylist.equals("none")) {
            mediaItem = fileMan.getMedia().get(rowIndex);
            fileMan.delMedia(rowIndex);
        } else {
            //If a playlist is selected get the MediaItem from the playlist
            mediaItem = fileMan.getPlaylist(currentPlaylist).get(rowIndex);
            //Use the MediaItem to get the index of the item in the media list then remove that item
            int mediaIndex = fileMan.getMedia().indexOf(mediaItem);
            fileMan.delMedia(mediaIndex);
        }

        //Cycle through all playlists that has the media item and remove the item from the playlist
        for (String playlistName : fileMan.getMediasPlaylists(mediaItem)) {
            List<MediaItem> playlist = fileMan.getPlaylist(playlistName);
            playlist.remove(mediaItem);
        }

        //Update the table with the updated media list
        dashboard.populateTable(currentPlaylist);
    }

    /**
     * The function ran when the scan media button is clicked.
     * Will let the user pick a folder that will then be searched and all valid file types in it will be
     * added to the media organiser.
     */
    private void scanMedia() {
        //Get the GUI and cast it to MediaDashboard class
        MediaDashboard dashboard = (MediaDashboard) this.getUI();
        //Create a file chooser that only allows directories to be chosen, not regular files
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        //If the file chooser was closed then nothing further needs to be done so return
        int choice = fileChooser.showOpenDialog(dashboard.getFrame());
        if (choice != JFileChooser.APPROVE_OPTION) return;

        //Get the selected directory and create an int to keep track of how many files have been added
        FileManager fileMan = dashboard.getFileMan();
        File dir = fileChooser.getSelectedFile();
        int newFiles = 0;

        //Get a list of the files in the given directory and check if null
        String[] dirList = dir.list();
        if (dirList != null) {
            //For each file check if it is a valid file type, if not the move to next item
            for (String fileName : dir.list()) {
                if (!fileMan.isValidFile(fileName)) continue;

                //File type is valid so increase file counter and add the file to the file manager
                newFiles++;
                fileMan.addMedia(dir.getAbsolutePath() + "\\" + fileName);
            }
        }

        //Show a message telling the user how many files have been added then populate the media table
        //with the full list
        JOptionPane.showMessageDialog(dashboard.getFrame(),
                newFiles + " media files were found and added to the library.",
                "Scan complete", JOptionPane.INFORMATION_MESSAGE);
        dashboard.populateTable();
    }

    /**
     * Function ran when the open in explorer button is clicked.
     * Will open the systems file explorer in the directory of the selected media item
     */
    private void openInExplorer() {
        //Check if a row is selected. If no row selected show error message and return.
        MediaDashboard dashboard = (MediaDashboard) this.getUI();
        int rowIndex = dashboard.getRowIndex();
        if (rowIndex == -1) {
            JOptionPane.showMessageDialog(dashboard.getFrame(),
                    "Please select a file to open in file explorer",
                    "No file selected", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //Check to see if playlist is loaded, if playlist not loaded then get path from regular media list
        //Otherwise get the path from the playlist current loaded
        String currentPlaylist = dashboard.getCurrentPlaylist();
        String path;
        if (currentPlaylist.equals("none")) path = dashboard.getFileMan().getMedia().get(rowIndex).getPath();
        else path = dashboard.getFileMan().getPlaylist(currentPlaylist).get(rowIndex).getPath();
        //Create a File instance with the given path
        File file = new File(path.substring(0, path.lastIndexOf("\\")));

        try {
            //Get the current desktop instance
            Desktop desktop = Desktop.getDesktop();
            //If the platform supports BROWSE_FILE_DIR action then it will open folder and select file
            //Otherwise will just open the folder
            if (desktop.isSupported(Desktop.Action.BROWSE_FILE_DIR)) {
                desktop.browseFileDirectory(file);
            } else {
                desktop.open(file);
            }
        } catch (Exception e) {
            //If something went wrong display an error message
            JOptionPane.showMessageDialog(dashboard.getFrame(),
                    "Sorry, unable to open file explorer location for this file.",
                    "File explorer unavailable", JOptionPane.ERROR_MESSAGE);
        }
    }
}
