package application.listeners;

import application.MediaDashboard;
import application.PlaylistEditor;
import util.FileManager;
import util.MediaItem;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class MenuHandler extends Handler {
    private final PlaylistEditor playlistEditor; //Menu allowing editing of playlists

    /**
     * Handles all menu interactions for the dashboard
     * @param dashboard the media library organisers dashboard
     */
    public MenuHandler(MediaDashboard dashboard) {
        super(dashboard);
        this.playlistEditor = new PlaylistEditor(dashboard);
    }

    /**
     * Function ran when a menu button is clicked on the dashboard
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JMenuItem menuItem = (JMenuItem) e.getSource(); //JMenuItem clicked
        //Cast the UI to MediaDashboard, so we can use public functions to populate media table
        MediaDashboard dashboard = (MediaDashboard) this.getUI();

        //run function based on the text of what button was clicked
        switch (menuItem.getText()) {
            case "Open library" -> openFile();
            case "New library" -> newFile();
            case "New playlist" -> newPlaylist();
            case "Edit playlist" -> editPlaylist();
            case "Close playlist" -> dashboard.populateTable();
            default -> openPlaylist(menuItem.getText()); //If the text didn't match any others it must be a playlist
        }
    }

    /**
     * Function ran when the "Open library" button is clicked.
     * Will let the user pick a new Media Library File to open and manage
     */
    private void openFile() {
        //Get dashboard and filemanager as they are commonly used
        MediaDashboard dashboard = (MediaDashboard) this.getUI();
        FileManager fileMan = dashboard.getFileMan();
        //If there's already a file and changes have been made then ask if they would like to save their changes
        if (fileMan.hasFile() && fileMan.changesMade()) {
            int choice = JOptionPane.showOptionDialog(dashboard.getFrame(),
                    "Would you like to save any changes to the current file?",
                    "Save changes", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, null, null);

            if (choice == JOptionPane.YES_OPTION) {
                //If the use says yes then save the current media library file
                boolean  success = fileMan.save();
                if (!success) {
                    //If something went wrong when trying to save then display an error message
                    JOptionPane.showMessageDialog(dashboard.getFrame(),
                            "Sorry, something went wrong saving changes.",
                            "Save failed", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                //If the prompt was closed then do nothing, this will not trigger if user says no
            } else if (choice == JOptionPane.CLOSED_OPTION) return;
        }

        //Open a file chooser with a filter to only show csv files
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Media Library File", "csv"));

        //If the user closes the file chooser then do nothing
        int choice = fileChooser.showOpenDialog(dashboard.getFrame());
        if (choice == JFileChooser.CANCEL_OPTION) return;

        //Check if the user chose a csv file and if not display an error message
        File file = fileChooser.getSelectedFile();
        if (!file.getName().endsWith("csv")) {
            JOptionPane.showMessageDialog(dashboard.getFrame(),
                    "Sorry you did not select a valid media library file.",
                    "Invalid file", JOptionPane.ERROR_MESSAGE);
            return;
        }

        //If the file was not able to be read from, display an error message
        boolean success = fileMan.setFile(file);
        if (!success) {
            JOptionPane.showMessageDialog(dashboard.getFrame(),
                    "Sorry, you didn't pick a valid media library file.\nOr something went wrong reading the file.",
                    "File error", JOptionPane.ERROR_MESSAGE);
        } else {
            //If file is read successfully populate the media table and generate the playlist menu
            dashboard.populateTable();
            dashboard.genPlaylists();
        }
    }

    /**
     * Function ran when the "New library" menu item is clicked.
     * Prompts the user to choose a location to save a new media library file to and loads a blank library.
     */
    private void newFile() {
        //Get the dashboard and fileman due to frequent use
        MediaDashboard dashboard = (MediaDashboard) this.getUI();
        FileManager fileMan = dashboard.getFileMan();
        //If a file was already open and changes were made during the sesion ask the user if they want to save.
        if (fileMan.hasFile() && fileMan.changesMade()) {
            int choice = JOptionPane.showOptionDialog(dashboard.getFrame(),
                    "Would you like to save any changes to the current file?",
                    "Save changes", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, null, null);
            if (choice == JOptionPane.YES_OPTION) {
                //If the user wants to save attempt to update current file and display error if something went wrong.
                boolean  success = fileMan.save();
                if (!success) {
                    JOptionPane.showMessageDialog(dashboard.getFrame(),
                            "Sorry, something went wrong saving changes.",
                            "Save failed", JOptionPane.ERROR_MESSAGE);
                    //If something went wrong then stop the new library creation to avoid lost progress
                    return;
                }
                //If the panel is closed then do nothing, does not apply if they press no
            } else if (choice == JOptionPane.CLOSED_OPTION) return;
        }

        //Create a file chooser and give it a default file
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("media library.csv"));

        //Loop to make sure the user picks a file
        boolean fileSelected = false;
        while (!fileSelected) {

            //Prompt the user to choose a file location, if they close it then end the loop/function
            int choice = fileChooser.showSaveDialog(dashboard.getFrame());
            if (choice != JFileChooser.APPROVE_OPTION) return;

            //If invalid file type then loop end this cycle so the user has to pick a new file
            if (!fileChooser.getSelectedFile().getName().endsWith("csv")) {
                JOptionPane.showMessageDialog(dashboard.getFrame(),
                        "Invalid file type please create a file of type .csv",
                        "Invalid file", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            //If selected file exists ask the user if they want to replace the file specified
            if (fileChooser.getSelectedFile().exists()) {
                int replaceChoice = JOptionPane.showOptionDialog(dashboard.getFrame(),
                        "This file already exists, are you sure you want to replace it?",
                        "File already exists", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (replaceChoice == JOptionPane.YES_OPTION) {
                    try {
                        //Create a file writer to write heading to the csv file
                        FileWriter writer = new FileWriter(fileChooser.getSelectedFile());
                        //Heading allowing the file manager to quickly see that the given file is for the media organiser
                        writer.write("[MediaLibraryOrganiserFile]\n");
                        writer.close();
                        fileSelected = true; //Set file selected true so it won't loop again
                    } catch (Exception e) {
                        //If something went wrong display an error and end the loop and function with a return.
                        JOptionPane.showMessageDialog(dashboard.getFrame(),
                                "Sorry, something went wrong when overwriting this file.",
                                "Save failed", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
            fileSelected = true; //At this point a file has been selected so end the loop
        }


        //Attempt to set the file in file manager
        boolean success = fileMan.setFile(fileChooser.getSelectedFile());
        if (!success) {
            //If the file was unable to be set then display an error message
            JOptionPane.showMessageDialog(dashboard.getFrame(),
                    "Sorry, something went wrong creating the library.",
                    "Creation failed", JOptionPane.ERROR_MESSAGE);
        } else {
            //On success refresh the media table and playlist menu
            dashboard.populateTable();
            dashboard.genPlaylists();
        }
    }

    /**
     * 
     */
    private void newPlaylist() {
        JPanel panel = new JPanel();
        panel.add(new JLabel("Enter name for the playlist: "));
        JTextField textField = new JTextField(16);
        panel.add(textField);

        int choice = JOptionPane.showConfirmDialog(this.getUI().getFrame(),
                panel,
                "Playlist name",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (choice != JOptionPane.OK_OPTION) return;

        String name = textField.getText();
        if (name.length() < 1 || name.length() > 16) {
            JOptionPane.showMessageDialog(this.getUI().getFrame(),
                    "The name must be between 0-16 characters long",
                    "Invalid name", JOptionPane.ERROR_MESSAGE);
            return;
        }

        this.playlistEditor.open(name);
    }

    private void editPlaylist() {
        MediaDashboard dashboard = (MediaDashboard) this.getUI();
        if (dashboard.getFileMan().getPlaylistNames().length < 1) {
            JOptionPane.showMessageDialog(dashboard.getFrame(),
                    "You currently don't have any playlists.\n" +
                            "Use the \"New playlist\" button to create a new playlist",
                    "No playlists", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JPanel panel = new JPanel();
        panel.add(new JLabel("Select a playlist:"));
        DefaultComboBoxModel<String> boxModel = new DefaultComboBoxModel<>();
        for (String playlist : dashboard.getFileMan().getPlaylistNames()) {
            boxModel.addElement(playlist);
        }
        JComboBox<String> comboBox = new JComboBox<>(boxModel);
        panel.add(comboBox);

        int result = JOptionPane.showConfirmDialog(dashboard.getFrame(),
                panel, "Playlist editor",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (result != JOptionPane.OK_OPTION) return;

        this.playlistEditor.open(comboBox.getSelectedItem().toString());
    }

    private void openPlaylist(String playlistName) {
        MediaDashboard dashboard = (MediaDashboard) this.getUI();
        List<MediaItem> playlist = dashboard.getFileMan().getPlaylist(playlistName);
        if (playlist == null) {
            JOptionPane.showMessageDialog(dashboard.getFrame(),
                    "Sorry, that playlist doesn't exist.",
                    "Playlist error", JOptionPane.ERROR_MESSAGE);
            dashboard.genPlaylists();
            return;
        }
        dashboard.populateTable(playlistName);
    }
}
