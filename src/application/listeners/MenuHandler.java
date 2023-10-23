package application.listeners;

import application.MediaDashboard;
import util.FileManager;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;

public class MenuHandler implements ActionListener {
    private final MediaDashboard dashboard;
    private final FileManager fileMan;

    public MenuHandler(MediaDashboard dashboard, FileManager manager) {
        this.dashboard = dashboard;
        this.fileMan = manager;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JMenuItem menuItem = (JMenuItem) e.getSource();

        switch (menuItem.getText()) {
            case "Open library" -> openFile();
            case "New library" -> newFile();
        }
    }

    private void openFile() {
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
                };
            }
        }


        boolean success = fileMan.setFile(fileChooser.getSelectedFile());
        if (!success) {
            JOptionPane.showMessageDialog(dashboard.getFrame(),
                    "Sorry, something went wrong creating the library.",
                    "Creation failed", JOptionPane.ERROR_MESSAGE);
        } else dashboard.populateTable();
    }
}
