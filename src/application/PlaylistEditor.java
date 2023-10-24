package application;

import util.FileManager;

import javax.swing.*;

public class PlaylistEditor {
    private final JFrame frame;
    private final FileManager fileMan;

    public PlaylistEditor(FileManager fileManager) {
        this.frame = new JFrame();
        this.fileMan = fileManager;

        JPanel mainPanel = new JPanel();
        this.frame.setContentPane(mainPanel);

        JTextField nameField = new JTextField();
        JLabel nameLabel = new JLabel("Playlist name");
        nameLabel.setLabelFor(nameField);

        mainPanel.add(nameLabel);
    }
}
