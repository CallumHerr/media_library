package application;

import util.FileManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public abstract class UserInterface {
    private final JFrame frame; //The frame the UI is built on
    private final FileManager fileMan; //The FileManager controlling files for the current application

    /**
     * Creates a new frame with a content pane as well as setting the given FileManager
     * @param fileMan The FileManager currently managing the media library files
     */
    public UserInterface(FileManager fileMan) {
        //Set the class properties
        this.fileMan = fileMan;
        this.frame = new JFrame();

        //Create a panel to set as the main content pane for the frame
        JPanel panel = new JPanel();
        this.frame.setContentPane(panel);
    }

    /**
     * Sets properties of the JFrame such as title and frame size
     * @param title The desired title of the window
     * @param visible If the frame should be visible to the user after being built
     */
    public void buildGUI(String title, boolean visible) {
        this.frame.setTitle(title);
        this.frame.setSize(600, 600);
        this.frame.setVisible(visible);
    }

    /**
     * gets the frame that the UI is built on
     * @return the frame property for the UserInterface
     */
    public JFrame getFrame() {
        return this.frame;
    }

    /**
     * Gets the content pane for the UI so more elements can be added
     * @return the main content pane
     */
    public Container getContentPane() {
        return this.frame.getContentPane();
    }

    /**
     * Gets the file manager so changes can be made to the files
     * @return Current FileManager instance
     */
    public FileManager getFileMan() {
        return this.fileMan;
    }
}
