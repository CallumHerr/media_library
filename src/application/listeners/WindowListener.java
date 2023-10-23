package application.listeners;

import util.FileManager;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WindowListener extends WindowAdapter {
    private final FileManager fileMan;
    public WindowListener(FileManager fileMan) {
        this.fileMan = fileMan;
    }
    @Override
    public void windowClosing(WindowEvent e) {
        if (!fileMan.changesMade()) {
            System.exit(0);
            return;
        }

        int choice = JOptionPane.showOptionDialog(null,
                "Would you like to save your changes?",
                "Save changes",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, null, null);
        if (choice == JOptionPane.YES_OPTION) {
            boolean success = this.fileMan.save();
            if (!success) {
                JOptionPane.showMessageDialog(null,
                        "Sorry something went wrong",
                        "Save failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        if (choice == JOptionPane.CLOSED_OPTION) return;

        System.exit(0);
    }
}
