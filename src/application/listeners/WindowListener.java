package application.listeners;

import util.FileManager;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class WindowListener extends WindowAdapter {
    private final FileManager fileMan; //The FileManager that is using this instance

    /**
     * Class Constructor for the WindowListener setting the fileMan property
     * @param fileMan the FileManager instance that initialised this class
     */
    public WindowListener(FileManager fileMan) {
        this.fileMan = fileMan;
    }

    /**
     * Function ran when a user attempts to close the frame
     * @param e the event to be processed
     */
    @Override
    public void windowClosing(WindowEvent e) {
        //If no changes were made then just close the application
        if (!fileMan.changesMade()) {
            System.exit(0);
            return;
        }

        //Ask if the user would like to save their changes
        int choice = JOptionPane.showOptionDialog(null,
                "Would you like to save your changes?",
                "Save changes",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, null, null);
        if (choice == JOptionPane.YES_OPTION) {
            //If yes then attempt to save but if something goes wrong then display an error and cancel closing the app
            boolean success = this.fileMan.save();
            if (!success) {
                JOptionPane.showMessageDialog(null,
                        "Sorry something went wrong",
                        "Save failed", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        //If the user closed the save prompt without saying yes/no then cancel the close
        if (choice == JOptionPane.CLOSED_OPTION) return;

        System.exit(0); //End the application
    }
}
