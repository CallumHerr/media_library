package application.listeners;

import application.PlaylistEditor;
import util.MediaItem;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseListener extends MouseAdapter {
    private final PlaylistEditor editor;

    public MouseListener(PlaylistEditor editor) {
        this.editor = editor;
    }
    @Override
    public void mousePressed(MouseEvent e) {
        JTable selectedTable = (JTable) e.getSource();
        if (e.getClickCount() != 2 || selectedTable.getSelectedRow() == -1) return;

        int rowIndex = selectedTable.getSelectedRow();
        MediaItem selectedMedia;
        if (selectedTable.getColumnName(0).equals("Playlist")) {
            selectedMedia = editor.getPlaylistMedia().get(rowIndex);
            editor.getPlaylistMedia().remove(rowIndex);
            editor.getOtherMedia().add(selectedMedia);
        } else {
            selectedMedia = editor.getOtherMedia().get(rowIndex);
            editor.getOtherMedia().remove(rowIndex);
            editor.getPlaylistMedia().add(selectedMedia);
        }

        editor.populateTables();
    }
}
