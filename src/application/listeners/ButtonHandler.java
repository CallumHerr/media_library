package application.listeners;

import application.MediaDashboard;
import util.FileManager;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ButtonHandler implements ActionListener {
    private final MediaDashboard dashboard;
    private final FileManager fileMan;

    public ButtonHandler(MediaDashboard dash, FileManager manager) {
        this.dashboard = dash;
        this.fileMan = manager;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();

        switch (button.getText()) {
            case "Add media" -> addMedia();
        }
    }

    private void addMedia() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
        fileChooser.setFileFilter(new FileNameExtensionFilter("Media Files",
                "jpg",
                "jpeg",
                "mp3",
                "mp4"
        ));

        fileChooser.showOpenDialog(dashboard);
    }
}
