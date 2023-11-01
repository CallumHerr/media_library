package application.listeners;

import application.PlaylistEditor;
import util.MediaItem;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class PlaylistHandler extends Handler {
    public PlaylistHandler(PlaylistEditor ui) {
        super(ui);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource();

        switch (button.getText()) {
            case "Delete playlist" -> delete();
            case "Save playlist" -> save();
        }
    }

    private void delete() {
        int choice = JOptionPane.showOptionDialog(this.getUI().getFrame(),
                "Are you sure you want to delete this playlist?",
                "Delete playlist", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, null, null);
        if (choice != JOptionPane.YES_OPTION) return;

        PlaylistEditor editor = (PlaylistEditor) this.getUI();
        this.getUI().getFileMan().removePlaylist(editor.getPlaylistKey());
        editor.close();
    }

    private void save() {
        PlaylistEditor editor = (PlaylistEditor) this.getUI();
        String playlistName = editor.getName();
        if (playlistName.length() < 1) {
            JOptionPane.showMessageDialog(editor.getFrame(),
                    "Sorry, the playlist name can not be empty.",
                    "Invalid name", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (playlistName.length() > 16) {
            JOptionPane.showMessageDialog(editor.getFrame(),
                    "Sorry, the playlist name must be 16 or less characters long.",
                    "Invalid name", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (!editor.getPlaylistKey().equals(playlistName)) {
            editor.getFileMan().removePlaylist(editor.getPlaylistKey());
            editor.setPlaylistKey(playlistName);
        }

        editor.getFileMan().addPlaylist(playlistName, editor.getPlaylistMedia());
        editor.close();
    }
}
