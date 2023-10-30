package application.listeners;

import application.MediaDashboard;
import application.PlaylistEditor;
import util.FileManager;
import util.MediaItem;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.util.List;

public class MenuHandler implements ActionListener {
    private final MediaDashboard dashboard;
    private final PlaylistEditor playlistEditor;

    public MenuHandler(MediaDashboard dashboard) {
        this.dashboard = dashboard;
        this.playlistEditor = new PlaylistEditor(dashboard);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JMenuItem menuItem = (JMenuItem) e.getSource();

        switch (menuItem.getText()) {
            case "Open library" -> openFile();
            case "New library" -> newFile();
            default -> openPlaylist(menuItem.getText());
        }
    }

    private void openFile() {
        FileManager fileMan = dashboard.getFileMan();
        if (fileMan.hasFile() && fileMan.changesMade()) {
            int choice = JOptionPane.showOptionDialog(dashboard.getFrame(),
                    "Would you like to save any changes to the current file?",
                    "Save changes", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, null, null);
            if (choice == JOptionPane.YES_OPTION) {
                boolean  success = fileMan.save();
                if (!success) {
                    JOptionPane.showMessageDialog(dashboard.getFrame(),
                            "Sorry, something went wrong saving changes.",
                            "Save failed", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else if (choice == JOptionPane.CLOSED_OPTION) return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Media Library File", "csv"));

        int choice = fileChooser.showOpenDialog(dashboard.getFrame());
        if (choice == JFileChooser.CANCEL_OPTION) return;

        File file = fileChooser.getSelectedFile();
        if (!file.getName().endsWith("csv")) {
            JOptionPane.showMessageDialog(dashboard.getFrame(),
                    "Sorry you did not select a valid media library file.",
                    "Invalid file", JOptionPane.ERROR_MESSAGE);
            return;
        }

        boolean success = fileMan.setFile(file);
        if (!success) {
            JOptionPane.showMessageDialog(dashboard.getFrame(),
                    "Sorry, you didn't pick a valid media library file.\nOr something went wrong reading the file.",
                    "File error", JOptionPane.ERROR_MESSAGE);
        } else dashboard.populateTable();
    }

    private void newFile() {
        FileManager fileMan = dashboard.getFileMan();
        if (fileMan.hasFile() && fileMan.changesMade()) {
            int choice = JOptionPane.showOptionDialog(dashboard.getFrame(),
                    "Would you like to save any changes to the current file?",
                    "Save changes", JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE, null, null, null);
            if (choice == JOptionPane.YES_OPTION) {
                boolean  success = fileMan.save();
                if (!success) {
                    JOptionPane.showMessageDialog(dashboard.getFrame(),
                            "Sorry, something went wrong saving changes.",
                            "Save failed", JOptionPane.ERROR_MESSAGE);
                    return;
                }
            } else if (choice == JOptionPane.CLOSED_OPTION) return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File("media library.csv"));

        boolean fileSelected = false;
        while (!fileSelected) {
            int choice = fileChooser.showSaveDialog(dashboard.getFrame());
            if (choice != JFileChooser.APPROVE_OPTION) return;

            if (!fileChooser.getSelectedFile().getName().endsWith("csv")) {
                JOptionPane.showMessageDialog(dashboard.getFrame(),
                        "Invalid file type please create a file of type .csv",
                        "Invalid file", JOptionPane.ERROR_MESSAGE);
                continue;
            }

            if (fileChooser.getSelectedFile().exists()) {
                int replaceChoice = JOptionPane.showOptionDialog(dashboard.getFrame(),
                        "This file already exists, are you sure you want to replace it?",
                        "File already exists", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (replaceChoice == JOptionPane.YES_OPTION) {
                    fileSelected = true;
                    try {
                        FileWriter writer = new FileWriter(fileChooser.getSelectedFile());
                        writer.write("[MediaLibraryOrganiserFile]\n");
                        writer.close();
                    } catch (Exception e) {
                        JOptionPane.showMessageDialog(dashboard.getFrame(),
                                "Sorry, something went wrong when overwriting this file.",
                                "Save failed", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
            }
        }


        boolean success = fileMan.setFile(fileChooser.getSelectedFile());
        if (!success) {
            JOptionPane.showMessageDialog(dashboard.getFrame(),
                    "Sorry, something went wrong creating the library.",
                    "Creation failed", JOptionPane.ERROR_MESSAGE);
        } else dashboard.populateTable();
    }

    public void openPlaylist(String playlistName) {
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
