package application.listeners;

import application.MediaDashboard;
import util.FileManager;
import util.MediaItem;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.List;

public class ButtonHandler implements ActionListener {
    private final MediaDashboard dashboard; //The dashboard attached to the listener

    /**
     * Save the dashboard and file manager for easy use on button presses
     * @param dashboard the MediaDashboard currently open
     */
    public ButtonHandler(MediaDashboard dashboard) {
        this.dashboard = dashboard;
    }

    /**
     * Function ran when a button on the MediaDashboard is clicked.
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        //if the file manager has no file saved then don't allow button presses
        if (!dashboard.getFileMan().hasFile()) {
            JOptionPane.showMessageDialog(dashboard.getFrame(),
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
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        //By default, will only show the selectable files
        fileChooser.setFileFilter(new FileNameExtensionFilter("Media Files",
                "jpg",
                "png",
                "jpeg",
                "mp3",
                "mp4"
        ));

        //If the file chooser is closed then do nothing
        int choice = fileChooser.showOpenDialog(dashboard.getFrame());
        if (choice == JFileChooser.CANCEL_OPTION) return;

        FileManager fileMan = dashboard.getFileMan();

        //If invalid file type then display error message and don't attempt to add media
        String dir = fileChooser.getSelectedFile().getAbsolutePath();
        if (fileMan.isValidFile(dir)) {
            JOptionPane.showMessageDialog(dashboard.getFrame(),
                    "Sorry, that file isn't supported.",
                    "Invalid file", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //Add the selected media to the file manager and update the media library table
        fileMan.addMedia(dir);
        this.dashboard.populateTable();
    }

    private void delMedia() {
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
        if (currentPlaylist.equals("none")) {
            mediaItem = fileMan.getMedia().get(rowIndex);
            fileMan.delMedia(rowIndex);
        }
        else {
            mediaItem = fileMan.getPlaylist(currentPlaylist).get(rowIndex);
            int mediaIndex = fileMan.getMedia().indexOf(mediaItem);
            fileMan.delMedia(mediaIndex);
        }

        for (String playlistName : mediaItem.getPlaylists()) {
            List<MediaItem> playlist = fileMan.getPlaylist(playlistName);
            playlist.remove(mediaItem);
        }

        dashboard.populateTable(currentPlaylist);
    }

    private void scanMedia() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        int choice = fileChooser.showOpenDialog(dashboard.getFrame());
        if (choice != JFileChooser.APPROVE_OPTION) return;

        FileManager fileMan = dashboard.getFileMan();
        File dir = fileChooser.getSelectedFile();
        int newFiles = 0;

        String[] dirList = dir.list();
        if (dirList != null) {
            for (String fileName : dir.list()) {
                if (!fileMan.isValidFile(fileName)) continue;

                newFiles++;
                fileMan.addMedia(dir.getAbsolutePath() + "/" + fileName);
            }
        }

        JOptionPane.showMessageDialog(dashboard.getFrame(),
                newFiles + " media files were found and added to the library.",
                "Scan complete", JOptionPane.INFORMATION_MESSAGE);
        dashboard.populateTable();
    }

    private void openInExplorer() {
        int rowIndex = dashboard.getRowIndex();
        if (rowIndex == -1) {
            JOptionPane.showMessageDialog(dashboard.getFrame(),
                    "Please select a file to open in file explorer",
                    "No file selected", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String currentPlaylist = dashboard.getCurrentPlaylist();
        FileManager fileMan = dashboard.getFileMan();
        String path;
        if (currentPlaylist.equals("none")) path = fileMan.getMedia().get(rowIndex).getPath();
        else path = fileMan.getPlaylist(currentPlaylist).get(rowIndex).getPath();
        File file = new File(path.substring(0, path.lastIndexOf("/")));

        try {
            Desktop desktop = Desktop.getDesktop();
            //If the platform supports BROWSE_FILE_DIR action then it will open folder and select file
            //Otherwise will just open the folder
            if (desktop.isSupported(Desktop.Action.BROWSE_FILE_DIR)) {
                desktop.browseFileDirectory(file);
            } else {
                desktop.open(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(dashboard.getFrame(),
                    "Sorry, unable to open file explorer location for this file.",
                    "File explorer unavailable", JOptionPane.ERROR_MESSAGE);
        }
    }
}
