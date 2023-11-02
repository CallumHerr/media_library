package application;

import application.listeners.DashboardButtonHandler;
import application.listeners.MenuHandler;
import util.FileManager;
import util.MediaItem;
import application.listeners.WindowListener;
import util.MediaTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class MediaDashboard extends UserInterface {
    private final MediaTable table; //Table displaying all the currently managed media files
    private int playlistsLoaded; //Total number of playlists currently loaded
    private String playlist; //The current playlist being displayed, "none" if no playlist selected

    public MediaDashboard() {
        //Initialising the class properties
        super(new FileManager());
        this.playlist = "none";
        this.playlistsLoaded = 0;

        //Create action listeners for interactable elements
        DashboardButtonHandler btnHandler = new DashboardButtonHandler(this);
        MenuHandler menuHandler = new MenuHandler(this);

        //Declaring and initialising the different menu elements
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem fileOpen = new JMenuItem("Open library");
        JMenuItem fileNew = new JMenuItem("New library");
        JMenu playlistMenu = new JMenu("Playlists");
        JMenuItem playlistNew = new JMenuItem("New playlist");
        JMenuItem playlistEdit = new JMenuItem("Edit playlist");
        JMenuItem playlistClose = new JMenuItem("Close playlist");

        //Attaching the menu elements to the frame.
        fileMenu.add(fileOpen);
        fileMenu.add(fileNew);
        menuBar.add(fileMenu);
        playlistMenu.add(playlistNew);
        playlistMenu.add(playlistEdit);
        playlistMenu.add(playlistClose);
        playlistMenu.addSeparator();
        menuBar.add(playlistMenu);
        this.getFrame().setJMenuBar(menuBar);

        //Declare and initialise toolbar and buttons
        JToolBar toolBar = new JToolBar();
        JButton addBtn = new JButton("Add media");
        JButton delBtn = new JButton("Remove media");
        JButton scanBtn = new JButton("Scan folder");
        JButton openBtn = new JButton("Open in explorer");

        //Attach buttons to toolbar and toolbar to content pane
        toolBar.add(addBtn);
        toolBar.add(delBtn);
        toolBar.add(scanBtn);
        toolBar.add(openBtn);
        this.getContentPane().add(toolBar);
        toolBar.setFloatable(false); //Stops toolbar from being dragged

        //Creates a scroll pane so the table headings are visible
        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(this.table = new MediaTable());
        this.getContentPane().add(scrollPane); //Adds scroll pane to the GUI
        this.populateTable(); //Fill the table with any media currently saved

        //add the previously initialised action handlers to the buttons
        addBtn.addActionListener(btnHandler);
        delBtn.addActionListener(btnHandler);
        scanBtn.addActionListener(btnHandler);
        openBtn.addActionListener(btnHandler);

        //and action handlers to menu items
        fileNew.addActionListener(menuHandler);
        fileOpen.addActionListener(menuHandler);
        playlistNew.addActionListener(menuHandler);
        playlistEdit.addActionListener(menuHandler);
        playlistClose.addActionListener(menuHandler);
        this.genPlaylists(); //Creates the menu items for each playlist and adds action listeners.

        //Set the frames properties and make it visible
        this.buildGUI("Media Library Organiser", true);
        this.getFrame().setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        //Add window listener for managing the close window event
        this.getFrame().addWindowListener(new WindowListener(this.getFileMan()));
    }

    /**
     * If no playlist is specified then fills the table with all available media
     */
    public void populateTable() {
        populateTable("none");
    }

    /**
     * fill media table with all media belonging to specified playlist
     * @param playlist name of the playlist to populate the table with, if no playlist chosen then "none"
     */
    public void populateTable(String playlist) {
        this.playlist = playlist; //Set the current playlist property to the one selected
        List<MediaItem> media;
        //If there is no playlist then get all media, otherwise get the specified playlist media
        if (playlist.equals("none")) media = this.getFileMan().getMedia();
        else media = this.getFileMan().getPlaylist(playlist);

        //Create a table model with the appropriate column names
        Object[] columnNames = { "Name", "Size (MB)", "Media type", "Resolution", "Audio Length" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        //Cycle through each media item and use the getEntry function to get it as a table entry
        for (MediaItem item : media) {
            tableModel.addRow(item.getEntry());
        }

        //Set the created model to the table to display the media
        this.table.setModel(tableModel);
    }

    /**
     * Generate all the JMenuItems for the playlists and add the MenuListener for them
     */
    public void genPlaylists() {
        //Get all playlist names
        String[] playlistNames = this.getFileMan().getPlaylistNames();
        //Get the menu handler from the already set menu items, avoids setting extra class properties
        JMenu menu = this.getFrame().getJMenuBar().getMenu(1);
        JMenuItem menuItem = (JMenuItem) menu.getMenuComponent(0);
        MenuHandler menuHandler = (MenuHandler) menuItem.getActionListeners()[0];

        //Remove all the already loaded playlists
        for (int i = 0; i < this.playlistsLoaded; i++) {
            menu.remove(4); //All playlists will be in position 4 as once 4th is deleted 5th becomes 4th
        }
        this.playlistsLoaded = 0;
        //Set the number of loaded playlists to 0

        //For each playlist add a new MenuItem with the playlist name as the text
        for (String name : playlistNames) {
            JMenuItem item = new JMenuItem(name);
            //Add the menu handler to each item
            item.addActionListener(menuHandler);
            menu.add(item); //Add the items to the JMenu
            this.playlistsLoaded++; //Increment the number of playlists loaded each time
        }
    }

    /**
     * Gets the name of the currently selected playlist being viewed
     * @return playlist name being viewed, "none" if no playlist is open
     */
    public String getCurrentPlaylist() {
        return this.playlist;
    }

    /**
     * Gets the index of the row that is currently selected in the table
     * @return the row index of the currently selected row, -1 means no row selected
     */
    public int getRowIndex() {
        return this.table.getSelectedRow();
    }
}
