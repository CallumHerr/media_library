package application;

import util.FileManager;
import util.MediaItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class PlaylistEditor extends UserInterface{
    private final MediaDashboard dashboard;
    private final DefaultTableModel playlistModel;
    private final DefaultTableModel mediaModel;

    public PlaylistEditor(MediaDashboard dashboard) {
        //Sets basic class properties
        super(dashboard.getFileMan());
        this.dashboard = dashboard;

        //Field and label for editing playlist name
        JTextField nameField = new JTextField(32);
        JLabel nameLabel = new JLabel("Playlist name");
        nameLabel.setLabelFor(nameField);

        //Create a panel for label and field for better formatting
        JPanel namePanel = new JPanel();
        namePanel.add(nameLabel);
        namePanel.add(nameField);

        //Save the table models as class properties so that rows can be easily added later on
        String[] columnNames = {"Name"};
        this.playlistModel = new DefaultTableModel(columnNames, 0);
        this.mediaModel = new DefaultTableModel(columnNames, 0);

        //Creates the tables with the appropriate models and puts them in scroll panes so that the column
        //names are visible, sets a preferred size to stop table going off frame.
        Dimension prefSize = new Dimension(250, 400);
        JScrollPane playlistPane = new JScrollPane();
        playlistPane.setPreferredSize(prefSize);
        playlistPane.setViewportView(new JTable(this.playlistModel));
        JScrollPane mediaPane = new JScrollPane();
        mediaPane.setPreferredSize(prefSize);
        mediaPane.setViewportView(new JTable(this.mediaModel));

        //Add the scroll panes to a panel for formatting
        JPanel tablePanel = new JPanel();
        tablePanel.add(playlistPane);
        tablePanel.add(mediaPane);

        //Add the buttons for saving or deleting the playlists to a panel for formatting
        JButton delBtn = new JButton("Delete Playlist");
        JButton saveBtn = new JButton("Save Playlist");
        JPanel savePanel = new JPanel();
        savePanel.add(delBtn);
        savePanel.add(saveBtn);

        //Add the 3 panels to the main panel and align them appropriately using a border layout
        this.getContentPane().add(namePanel, BorderLayout.NORTH);
        this.getContentPane().add(tablePanel, BorderLayout.CENTER);
        this.getContentPane().add(savePanel, BorderLayout.SOUTH);

        //Set some frame properties to avoid repeating setting them

        this.buildGUI("Playlist editor");
        this.getFrame().setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        this.getFrame().setVisible(false);
    }

    public void open(String playlistName) {
        List<MediaItem> playlist = this.getFileMan().getPlaylist(playlistName);
        if (playlist == null) {
            JOptionPane.showMessageDialog(null,
                    "Sorry, that playlist doesn't exist.",
                    "Playlist Error", JOptionPane.ERROR_MESSAGE);
            
            return;
        }

        this.playlistModel.setRowCount(0);
        this.mediaModel.setRowCount(0);

        List<MediaItem> media = this.getFileMan().getMedia();
        media.removeIf(s -> playlist.contains(s));

        for (MediaItem item : playlist) {
            this.playlistModel.addRow(new String[]{ item.getName() });
        }
        for (MediaItem item : media) {
            this.mediaModel.addRow(new String[]{ item.getName() });
        }
    }
}
