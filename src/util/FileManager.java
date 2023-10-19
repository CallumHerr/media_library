package util;

import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.prefs.Preferences;

public class FileManager {
    private File file; //File that the media library is currently saving to
    private final List<MediaItem> media; //All media currently being managed
    private final Map<String, List<MediaItem>> playlists; //All playlists being managed

    /**
     * Gets the last opened file from the Java Preferences API and checks if it still exists
     * if so it will set the file as the one opened in the media library, otherwise will clear the
     * preference.
     */
    public FileManager() {
        this.media = new ArrayList<>();
        this.playlists = new HashMap<>();

        //Checks the preferences saved for this class and gets the fileDir saved
        Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
        String fileDir = prefs.get("libraryDir", null);
        if (fileDir == null) return; //If no fileDir then nothing else needs to be done

        File file = new File(fileDir);
        //If file does not exist then the preferences should be cleared, nothing else to be done so return
        if (!file.exists()) {
            prefs.remove("libraryDir");
            return;
        }

        //Set the file property to last opened file, if this fails clear the preference to avoid further error
        boolean success = this.setFile(file);
        if (!success) prefs.remove("libraryDir");
    }

    /**
     * Reads from the media library file to set collect all media and playlists being managed
     * @param file the file containing the media library information
     * @return true if file was read successfully, false otherwise
     */
    public boolean setFile(File file) {
        this.file = file;

        try {
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                //Each line contains info for a new piece of media
                //stored as fileDir, playlist1, playlist2...
                String[] mediaInfo = reader.nextLine().split(",");
                String fDir = mediaInfo[0];

                //If it is not one of the following file types then it is not supported
                if (!fDir.endsWith(".jpg")
                        && !fDir.endsWith(".png")
                        && !fDir.endsWith(".mp4")
                        && !fDir.endsWith(".mp3")) continue;

                MediaItem media = new MediaItem(mediaInfo);
                this.media.add(media);
                //if entry is more than 1 element it is in playlists too
                if (mediaInfo.length > 1) {
                    for (int i = 1; i < mediaInfo.length; i++) {
                        this.playlists.get(mediaInfo[i]).add(media);
                    }
                }
            }

            //Set the preference to the most recently opened file
            Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
            prefs.put("libraryDir", file.getAbsolutePath());
            return true;
        } catch (Exception e) {
            return false;
        }

    }

    /**
     * Add new media item to the library
     * @param dir Directory of the new media file to manage
     */
    public void addMedia(String dir) {
        MediaItem newItem = new MediaItem(new String[]{dir});
        this.media.add(newItem);
    }

    /**
     * Remove a media item being managed by index in the list
     * @param index index of the media item to be removed
     */
    public void delMedia(int index) {
        this.media.remove(index);
    }

    /**
     * Saves the media being managed to the currently set media library file
     * @return true if file was written to successfully, false otherwise
     */
    public boolean save() {
        StringBuilder builder = new StringBuilder();
        for (MediaItem item : this.media) {
            builder.append(item.toString());
            builder.append("\n");
        }

        try {
            FileWriter writer = new FileWriter(this.file);
            writer.write(builder.toString());
            writer.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Add new playlist to be managed
     * @param name the name to save the playlist as
     * @param media List of MediaItem that the playlist contains
     */
    public void addPlaylist(String name, List<MediaItem> media) {
        this.playlists.put(name, media);
    }

    /**
     * Delete a playlist and stop managing it, will not delete the media inside it.
     * @param name Playlist name to remove
     */
    public void removePlaylist(String name) {
        this.playlists.remove(name);
    }

    /**
     * Get all the media that is a part of a certain playlist
     * @param name the playlist to get the media from
     * @return ArrayList of the media that belongs to the playlist
     */
    public List<MediaItem> getPlaylist(String name) {
        return this.playlists.get(name);
    }

    /**
     * Gets a complete list of all media currently being managed
     * @return List of all MediaItems being tracked
     */
    public List<MediaItem> getMedia() {
        return this.media;
    }

}
