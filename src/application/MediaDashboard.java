package application;

import util.FileManager;
import util.MediaItem;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class MediaDashboard extends JFrame {
    private final JPanel mainPanel;
    private final JMenuBar menuBar;
    private final JMenu fileMenu;
    private final JMenuItem fileOpen;
    private final JMenuItem fileNew;
    private final JMenu playlistMenu;
    private final JMenuItem playlistNew;
    private final JMenuItem playlistEdit;
    private final List<JMenuItem> playlists;
    private final JToolBar toolbar;
    private final JButton addBtn;
    private final JButton delBtn;
    private final JButton scanBtn;
    private final JButton openBtn;
    private final JTable table;
    private final FileManager fileMan;

    public MediaDashboard() {
        this.fileMan = new FileManager();
        this.mainPanel = new JPanel();
        this.setJMenuBar(this.menuBar = new JMenuBar());
        menuBar.add(this.fileMenu = new JMenu("File"));
        fileMenu.add(this.fileOpen = new JMenuItem("Open Library"));
        fileMenu.add(this.fileNew = new JMenuItem("New Library"));
        menuBar.add(this.playlistMenu = new JMenu("Playlists"));
        playlistMenu.add(this.playlistNew = new JMenuItem("New playlist"));
        playlistMenu.add(this.playlistEdit = new JMenuItem("Edit Playlist"));
        mainPanel.add(this.toolbar = new JToolBar());
        toolbar.add(this.addBtn = new JButton("Add media"));
        toolbar.add(this.delBtn = new JButton("Remove media"));
        toolbar.add(this.scanBtn = new JButton("Scan folder"));
        toolbar.add(this.openBtn = new JButton("Open in explorer"));
        this.playlists = new ArrayList<>();

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setViewportView(this.table = new JTable());
        mainPanel.add(scrollPane);
        toolbar.setFloatable(false);

        this.populateTable("none");

        this.setContentPane(mainPanel);
        this.setTitle("Media library organiser");
        this.setSize(600, 600);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
    }

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
}
