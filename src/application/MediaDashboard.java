package application;

import application.listeners.DashboardHandler;
import application.listeners.MenuHandler;
import util.FileManager;
import util.MediaItem;
import application.listeners.WindowListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class MediaDashboard extends UserInterface {
    private final JTable table;
    private int playlistsLoaded;
    private String playlist; //The current playlist being displayed, "none" if no playlist selected

    public MediaDashboard() {
        //Initialising the class properties
        super(new FileManager());
        this.playlist = "none";
        this.playlistsLoaded = 0;

        //Create action listeners for interactable elements
        DashboardHandler btnHandler = new DashboardHandler(this);
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

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(this.table = new JTable());
        this.getContentPane().add(scrollPane);
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
        this.genPlaylists();

        //Set the frames properties and make it visible
        this.buildGUI("Media Library Organiser", true);
        this.getFrame().setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
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
        List<MediaItem> media;
        if (playlist.equals("none")) media = this.getFileMan().getMedia();
        else media = this.getFileMan().getPlaylist(playlist);

        Object[] columnNames = { "Name", "Size (MB)", "Media type", "Resolution", "Recording Length" };
        DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
        for (MediaItem item : media) {
            tableModel.addRow(item.getEntry());
        }

        this.table.setModel(tableModel);
    }

    public void genPlaylists() {
        String[] playlistNames = this.getFileMan().getPlaylistNames();
        JMenu menu = this.getFrame().getJMenuBar().getMenu(1);
        JMenuItem menuItem = (JMenuItem) menu.getMenuComponent(0);
        MenuHandler menuHandler = (MenuHandler) menuItem.getActionListeners()[0];

        for (int i = 0; i < this.playlistsLoaded; i++) {
            menu.remove(4);
        }
        this.playlistsLoaded = 0;

        for (String name : playlistNames) {
            JMenuItem item = new JMenuItem(name);
            item.addActionListener(menuHandler);
            menu.add(item);
            this.playlistsLoaded++;
        }
    }

    /**
     * Gets the name of the currently selected playlist being viewed
     * @return playlist name being viewed, "none" if no playlist is open
     */
    public String getCurrentPlaylist() {
        return this.playlist;
    }

    public int getRowIndex() {
        return this.table.getSelectedRow();
    }
}
