package application;

import util.FileManager;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public abstract class UserInterface {
    private final JFrame frame;
    private final FileManager fileMan;

    public UserInterface(FileManager fileMan) {
        this.fileMan = fileMan;
        this.frame = new JFrame();

        JPanel panel = new JPanel();
        this.frame.setContentPane(panel);
    }

    public void buildGUI(String title, boolean visible) {
        this.frame.setTitle(title);
        this.frame.setSize(600, 600);
        this.frame.setVisible(visible);
    }

    public JFrame getFrame() {
        return this.frame;
    }

    public Container getContentPane() {
        return this.frame.getContentPane();
    }

    public FileManager getFileMan() {
        return this.fileMan;
    }
}
