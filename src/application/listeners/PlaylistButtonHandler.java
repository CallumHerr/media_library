package application.listeners;

import application.PlaylistEditor;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class PlaylistButtonHandler extends Handler {
    /**
     * Class Constructor that passes the PlaylistEditor into the Handler constructor
     * @param ui the PlaylistEditor instance using this handler
     */
    public PlaylistButtonHandler(PlaylistEditor ui) {
        super(ui);
    }

    /**
     * Function ran when a button is clicked
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        JButton button = (JButton) e.getSource(); //Get the clicked button

        //Depending on what button is clicked run a different function
        switch (button.getText()) {
            case "Delete playlist" -> delete();
            case "Save playlist" -> save();
        }
    }

    /**
     * Function ran when the "delete playlist" button is clicked
     * will ask the user if they are sure they want to delete the current playlist
     */
    private void delete() {
        //Deletion confirmation prompt
        int choice = JOptionPane.showOptionDialog(this.getUI().getFrame(),
                "Are you sure you want to delete this playlist?",
                "Delete playlist", JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, null, null);
        if (choice != JOptionPane.YES_OPTION) return;

        //Remove the currently open playlist from the file manager and close the editor
        PlaylistEditor editor = (PlaylistEditor) this.getUI();
        editor.getFileMan().removePlaylist(editor.getPlaylistKey());
        editor.close();
    }

    /**
     * Function ran when the save playlist button is clicked
     * will update anything that has changed in the playlist editor from when it was first opened
     */
    private void save() {
        //Check for invalid playlist names
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

        //If the current playlist name is different to the playlistKey being stored then the
        //playlist should be removed so that it can be added again with the correct key
        if (!editor.getPlaylistKey().equals(playlistName)) {
            editor.getFileMan().removePlaylist(editor.getPlaylistKey());
            editor.setPlaylistKey(playlistName);
        }

        //Add new playlist to the file manager with the new list
        editor.getFileMan().addPlaylist(playlistName, editor.getPlaylistMedia());
        editor.close(); //Minimise the editor
    }
}
