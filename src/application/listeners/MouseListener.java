package application.listeners;

import application.PlaylistEditor;
import util.MediaItem;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MouseListener extends MouseAdapter {
    private final PlaylistEditor editor; //The PlaylistEditor that uses the MouseListener

    /**
     * Creates a mouse listener to check for double-clicking on the table
     * @param editor the playlist editor using this instance
     */
    public MouseListener(PlaylistEditor editor) {
        this.editor = editor;
    }

    /**
     * Function ran when the mouse is clicked
     * @param e the event to be processed
     */
    @Override
    public void mousePressed(MouseEvent e) {
        //If it wast a double click or there is no row selected the event can be ignored
        JTable selectedTable = (JTable) e.getSource();
        if (e.getClickCount() != 2 || selectedTable.getSelectedRow() == -1) return;

        int rowIndex = selectedTable.getSelectedRow();
        MediaItem selectedMedia;
        //If selected item is in the playlist table then get the media from that list and swap to the other table
        if (selectedTable.getColumnName(0).equals("Playlist")) {
            selectedMedia = editor.getPlaylistMedia().get(rowIndex);
            editor.getPlaylistMedia().remove(rowIndex);
            editor.getOtherMedia().add(selectedMedia);
            //If selected item is in the other media table then do the same but get value from other list
        } else {
            selectedMedia = editor.getOtherMedia().get(rowIndex);
            editor.getOtherMedia().remove(rowIndex);
            editor.getPlaylistMedia().add(selectedMedia);
        }

        editor.populateTables(); //Update the tables with the items new positions
    }
}
