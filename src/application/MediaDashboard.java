package application;

import application.listeners.ButtonHandler;
import application.listeners.MenuHandler;
import util.FileManager;
import util.MediaItem;
import application.listeners.WindowListener;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class MediaDashboard {
    private final JFrame frame; //The frame for the panel to attach to
    private final JTable table; //Table that displays all the media being managed
    private final FileManager fileMan; //File Manager that manages currently selected Library file
    private String playlist; //The current playlist being displayed, "none" if no playlist selected

    public MediaDashboard() {
        //Initialising the class properties
        this.fileMan = new FileManager();
        this.frame = new JFrame();
        this.frame.setContentPane(new JPanel());
        this.playlist = "none";

        //Create action listeners for interactable elements
        ButtonHandler btnHandler = new ButtonHandler(this, fileMan);
        MenuHandler menuHandler = new MenuHandler(this, fileMan);

        //Declaring and initialising the different menu elements
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem fileOpen = new JMenuItem("Open library");
        JMenuItem fileNew = new JMenuItem("New library");
        JMenu playlistMenu = new JMenu("Playlists");
        JMenuItem playlistNew = new JMenuItem("New playlist");
        JMenuItem playlistEdit = new JMenuItem("Edit playlist");

        //Attaching the menu elements to the frame.
        fileMenu.add(fileOpen);
        fileMenu.add(fileNew);
        menuBar.add(fileMenu);
        playlistMenu.add(playlistNew);
        playlistMenu.add(playlistEdit);
        menuBar.add(playlistMenu);
        this.frame.setJMenuBar(menuBar);

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
        this.frame.getContentPane().add(toolBar);
        toolBar.setFloatable(false); //Stops toolbar from being dragged

        //Creates the table and adds it to the content pane
        JScrollPane scrollPane = new JScrollPane(); //Scroll pane used so table column headings will be visible
        scrollPane.setViewportView(this.table = new JTable());
        this.frame.getContentPane().add(scrollPane);
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

        //Set the frames properties and make it visible
        this.frame.setTitle("Media library organiser");
        this.frame.setSize(600, 600);
        this.frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.frame.setVisible(true);
        this.frame.addWindowListener(new WindowListener(this.fileMan));
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
        if (playlist.equals("none")) {
            List<MediaItem> media = this.fileMan.getMedia();

            Object[] columnNames = { "Name", "Size (MB)", "Media type", "Resolution", "Recording Length" };
            DefaultTableModel tableModel = new DefaultTableModel(columnNames, 0);
            for (MediaItem item : media) {
                tableModel.addRow(item.getEntry());
            }

            table.setModel(tableModel);
        }
    }

    /**
     * Gets the ui frame
     * @return JFrame that the GUI is built on
     */
    public JFrame getFrame() {
        return this.frame;
    }

    /**
     * Gets the currently selected row by index
     * @return index of selected row, -1 if none selected
     */
    public int getRowIndex() {
        return this.table.getSelectedRow();
    }

    /**
     * Gets the name of the currently selected playlist being viewed
     * @return playlist name being viewed, "none" if no playlist is open
     */
    public String getCurrentPlaylist() {
        return this.playlist;
    }
}
