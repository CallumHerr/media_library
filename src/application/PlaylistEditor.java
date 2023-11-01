package application;

import application.listeners.MouseListener;
import application.listeners.PlaylistHandler;
import util.MediaItem;
import util.MediaTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class PlaylistEditor extends UserInterface{
    private final MediaDashboard dashboard; //The main media library dashboard
    private final JTextField nameField; //The field for editing the playlist name
    private final DefaultTableModel playlistModel; //Table model for the playlist table
    private final DefaultTableModel mediaModel; //Table model for the media table
    private String playlistKey; //The current key for the hashmap containing playlists
    private List<MediaItem> otherMedia; //Media not part of the current playlist
    private List<MediaItem> playlistMedia; //Media that is a part of the current playlist

    /**
     * Constructor for the PlaylistEditor class
     * @param dashboard the media library dashboard that created this instance
     */
    public PlaylistEditor(MediaDashboard dashboard) {
        //Sets basic class properties
        super(dashboard.getFileMan());
        this.dashboard = dashboard;

        //Field and label for editing playlist name
        this.nameField = new JTextField(16);
        JLabel nameLabel = new JLabel("Playlist name");
        nameLabel.setLabelFor(nameField);

        //Create a panel for label and field for better formatting
        JPanel namePanel = new JPanel();
        namePanel.add(nameLabel);
        namePanel.add(this.nameField);

        //Save the table models as class properties so that rows can be easily added later on
        String[] columnNames = {"Playlist"};
        this.playlistModel = new DefaultTableModel(columnNames, 0);
        columnNames[0] = "Other Media";
        this.mediaModel = new DefaultTableModel(columnNames, 0);

        //Create the preffered dimensions of the tables so two can fit beside each other
        Dimension prefSize = new Dimension(250, 400);
        MouseListener mouseListener = new MouseListener(this);

        //Create the playlist table and put it in a scrollpane so the column title is visible
        JScrollPane playlistPane = new JScrollPane();
        playlistPane.setPreferredSize(prefSize);
        MediaTable playlistTable = new MediaTable(this.playlistModel);
        playlistTable.addMouseListener(mouseListener);
        playlistPane.setViewportView(playlistTable);
        //Same for the media table
        JScrollPane mediaPane = new JScrollPane();
        mediaPane.setPreferredSize(prefSize);
        MediaTable mediaTable = new MediaTable(this.mediaModel);
        mediaTable.addMouseListener(mouseListener);
        mediaPane.setViewportView(mediaTable);

        //Create a button that will allow swapping media from one table to another
        JButton swapBtn = new JButton("Swap media");

        //Add the scroll panes and button to a panel for formatting
        JPanel tablePanel = new JPanel();
        tablePanel.add(playlistPane);
        tablePanel.add(mediaPane);

        //Add the buttons for saving or deleting the playlists to a panel for formatting
        JButton delBtn = new JButton("Delete playlist");
        JButton saveBtn = new JButton("Save playlist");
        JPanel savePanel = new JPanel();
        savePanel.add(delBtn);
        savePanel.add(saveBtn);

        //Create a button handler for this ui and give the buttons it as an action listener
        PlaylistHandler buttonHandler = new PlaylistHandler(this);
        delBtn.addActionListener(buttonHandler);
        saveBtn.addActionListener(buttonHandler);

        //Add the 3 panels to the main panel and align them appropriately using a border layout
        this.getContentPane().add(namePanel, BorderLayout.NORTH);
        this.getContentPane().add(tablePanel, BorderLayout.CENTER);
        this.getContentPane().add(savePanel, BorderLayout.SOUTH);

        //Sets the frames properties so the frame is ready to be used
        this.buildGUI("Playlist editor", false);
        this.getFrame().setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    /**
     * Opens the GUI while also resetting any properties that may need changing to accomodate for opening a new playlist
     * @param playlistName the playlist being opened name
     */
    public void open(String playlistName) {
        this.playlistKey = playlistName;

        List<MediaItem> playlist = this.getFileMan().getPlaylist(playlistName);
        if (playlist == null) this.playlistMedia = new ArrayList<>();
        else this.playlistMedia = new ArrayList<>(this.getFileMan().getPlaylist(playlistName));

        //Filter the complete media list to just media that isn't in the playlist
        this.otherMedia = new ArrayList<>(this.getFileMan().getMedia());
        this.otherMedia.removeIf(s -> playlistMedia.contains(s));

        this.nameField.setText(playlistName);

        this.populateTables();
        this.getFrame().setVisible(true); //Make the frame visible
    }

    public void populateTables() {
        this.playlistModel.setRowCount(0);
        this.mediaModel.setRowCount(0);

        for (MediaItem item : this.playlistMedia) {
            this.playlistModel.addRow(new String[]{ item.getName() });
        }
        for (MediaItem item : this.otherMedia) {
            this.mediaModel.addRow(new String[]{ item.getName() });
        }
    }

    /**
     * Function allowing the editor to be closed while updating the dashboard
     */
    public void close() {
        dashboard.genPlaylists();
        dashboard.populateTable();
        this.getFrame().setVisible(false);
    }

    /**
     * Gets the current playlist name as is stored in the filemanagers hashmap
     * @return the playlist name as a string
     */
    public String getPlaylistKey() {
        return this.playlistKey;
    }

    public void setPlaylistKey(String playlistKey) {
        this.playlistKey = playlistKey;
    }

    /**
     * Gets the current media that is being displayed as beloning to the playlist
     * @return
     */
    public List<MediaItem> getPlaylistMedia() {
        return this.playlistMedia;
    }

    public List<MediaItem> getOtherMedia() {
        return this.otherMedia;
    }

    /**
     * Get the name from the TextField
     * @return the text entered into the TextField property nameField
     */
    public String getName() {
        return this.nameField.getText();
    }
}
