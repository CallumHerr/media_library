package application;

import application.listeners.MouseListener;
import application.listeners.PlaylistButtonHandler;
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

        //Create the preferred dimensions of the tables so two can fit beside each other
        Dimension prefSize = new Dimension(250, 400);
        MouseListener mouseListener = new MouseListener(this); //Checks for double clicks

        //Create the playlist table and put it in a scroll pane so the column title is visible
        JScrollPane playlistPane = new JScrollPane();
        playlistPane.setPreferredSize(prefSize);
        MediaTable playlistTable = new MediaTable(this.playlistModel);
        playlistTable.addMouseListener(mouseListener); //Makes sure to add the listener
        playlistPane.setViewportView(playlistTable);
        //Same for the media table
        JScrollPane mediaPane = new JScrollPane();
        mediaPane.setPreferredSize(prefSize);
        MediaTable mediaTable = new MediaTable(this.mediaModel);
        mediaTable.addMouseListener(mouseListener);
        mediaPane.setViewportView(mediaTable);

        //Add the scroll panes to a panel for formatting
        JPanel tablePanel = new JPanel();
        tablePanel.add(playlistPane);
        tablePanel.add(mediaPane);

        //Add the buttons for saving or deleting the playlists to a panel for formatting
        JButton delBtn = new JButton("Delete playlist");
        JButton saveBtn = new JButton("Save playlist");
        JPanel savePanel = new JPanel();
        savePanel.add(delBtn);
        savePanel.add(saveBtn);

        //Create a button handler for this GUI
        PlaylistButtonHandler buttonHandler = new PlaylistButtonHandler(this);
        //Add the button handler to each of the buttons
        delBtn.addActionListener(buttonHandler);
        saveBtn.addActionListener(buttonHandler);

        //Add the 3 panels to the main panel and align them appropriately using a border layout
        this.getContentPane().add(namePanel, BorderLayout.NORTH);
        this.getContentPane().add(tablePanel, BorderLayout.CENTER);
        this.getContentPane().add(savePanel, BorderLayout.SOUTH);

        //Sets the frames properties so the frame is ready to be used
        this.buildGUI("Playlist editor", false);
        //Just hide the JFrame so we don't need to make a new one each time the window is opened.
        this.getFrame().setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
    }

    /**
     * Opens the GUI while also resetting any properties that may need changing to accommodate for opening a new playlist
     * @param playlistName the playlist being opened name
     */
    public void open(String playlistName) {
        this.playlistKey = playlistName; //Set the current playlist key as the initial playlist name

        //Get the playlist specified, if it doesn't exist then make a new list for the media
        List<MediaItem> playlist = this.getFileMan().getPlaylist(playlistName);
        if (playlist == null) this.playlistMedia = new ArrayList<>();
        else this.playlistMedia = new ArrayList<>(this.getFileMan().getPlaylist(playlistName));

        //Filter the complete media list to just media that isn't in the playlist
        this.otherMedia = new ArrayList<>(this.getFileMan().getMedia());
        this.otherMedia.removeIf(s -> playlistMedia.contains(s));

        //Set the name field to start filled with the current playlists name
        this.nameField.setText(playlistName);

        //Fill the tables with the appropriate media items
        this.populateTables();
        this.getFrame().setVisible(true); //Make the frame visible
    }

    /**
     * Fill the tables with the correct items so the items can be moved
     */
    public void populateTables() {
        //Empty the current tables to avoid repeating data
        this.playlistModel.setRowCount(0);
        this.mediaModel.setRowCount(0);

        //For each playlist item add the items name to the playlist table
        for (MediaItem item : this.playlistMedia) {
            this.playlistModel.addRow(new String[]{ item.getName() });
        }
        //For each item not in the playlist add the items name to the other table
        for (MediaItem item : this.otherMedia) {
            this.mediaModel.addRow(new String[]{ item.getName() });
        }
    }

    /**
     * Function allowing the editor to be closed while updating the dashboard
     */
    public void close() {
        //Update the playlist names and add any new playlists
        dashboard.genPlaylists();
        dashboard.populateTable(); //Update the media dashboard to show all media again
        this.getFrame().setVisible(false); //Hide the editor frame
    }

    /**
     * Gets the current playlist name as is stored in the FileManagers hashmap
     * @return the playlist name as a string
     */
    public String getPlaylistKey() {
        return this.playlistKey;
    }

    /**
     * Sets the playlist key property of the class to an updated version of the playlist name
     * @param playlistKey the string used as the key to the playlist in the hashmap
     */
    public void setPlaylistKey(String playlistKey) {
        this.playlistKey = playlistKey;
    }

    /**
     * Gets the current media that is being displayed as belonging to the playlist
     * @return the media items from the playlist table
     */
    public List<MediaItem> getPlaylistMedia() {
        return this.playlistMedia;
    }

    /**
     * Gets the media items that are not a part of the current playlist
     * @return all the media items that are not currently in use by the playlist
     */
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
