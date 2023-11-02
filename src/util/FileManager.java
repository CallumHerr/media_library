package util;

import java.io.File;
import java.io.FileWriter;
import java.util.*;
import java.util.prefs.Preferences;
import java.util.stream.Collectors;

public class FileManager {
    private File file; //File that the media library is currently saving to
    private final List<MediaItem> media; //All media currently being managed
    private final Map<String, List<MediaItem>> playlists; //All playlists being managed
    private final List<String> validFileTypes; //List of supported file types
    private boolean changesMade;

    /**
     * Gets the last opened file from the Java Preferences API and checks if it still exists
     * if so it will set the file as the one opened in the media library, otherwise will clear the
     * preference.
     */
    public FileManager() {
        //Initialise the class properties
        this.media = new ArrayList<>();
        this.playlists = new HashMap<>();
        this.validFileTypes = new ArrayList<>();
        this.changesMade = false;

        //Add all the currently supported file types to the list
        validFileTypes.add("wav");
        validFileTypes.add("mp4");
        validFileTypes.add("png");
        validFileTypes.add("jpg");
        validFileTypes.add("jpeg");

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
        //Clear the media list and playlists of the previously loaded media file
        media.clear();
        playlists.clear();
        try {
            Scanner reader = new Scanner(file); //Create a scanner for the file
            //If this heading isn't in the file then it's not a valid media library file
            if (!reader.nextLine().equals("[MediaLibraryOrganiserFile]")) return false;

            while (reader.hasNextLine()) {
                //Each line contains info for a new piece of media
                //stored as fileDir, playlist1, playlist2...
                String[] mediaInfo = reader.nextLine().split(",");
                String fDir = mediaInfo[0];

                //if it is not a supported file type then move onto the next file
                if (!this.isValidFile(fDir)) continue;

                //Create the media item and add it to the media list
                MediaItem media = new MediaItem(mediaInfo);
                this.media.add(media);
                //if entry is more than 1 element it is in playlists too
                if (mediaInfo.length > 1) {
                    for (int i = 1; i < mediaInfo.length; i++) {
                        //Check if the playlist already exists
                        List<MediaItem> playlist = this.playlists.get(mediaInfo[i]);
                        if (playlist != null) playlist.add(media); //It exists so just add the media item to the list
                        else {
                            //Playlist doesn't exist yet so make a new list with the media item and create the playlist
                            playlist = new ArrayList<>();
                            playlist.add(media);
                            this.playlists.put(mediaInfo[i], playlist);
                        }
                    }
                }
            }

            //Set the preference to the most recently opened file
            Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
            prefs.put("libraryDir", file.getAbsolutePath());

            //Set the currently managed file to the one just read from and return true to show success
            this.file = file;
            this.changesMade = false;
            return true;
        } catch (Exception e) {
            //Something went wrong so return false so error can be displayed
            return false;
        }

    }

    /**
     * Add new media item to the library
     * @param dir Directory of the new media file to manage
     */
    public void addMedia(String dir) {
        this.changesMade = true; //Mark that changes have been made for a later save prompt
        MediaItem newItem = new MediaItem(new String[]{dir});
        this.media.add(newItem);
    }

    /**
     * Remove a media item being managed by index in the list
     * @param index index of the media item to be removed
     */
    public void delMedia(int index) {
        this.changesMade = true; //Update changes made for the save prompt
        this.media.remove(index);
    }

    /**
     * Saves the media being managed to the currently set media library file
     * @return true if file was written to successfully, false otherwise
     */
    public boolean save() {
        //Using builder since string will continue to get more strings added on
        StringBuilder builder = new StringBuilder("[MediaLibraryOrganiserFile]\n");
        for (MediaItem item : this.media) {
            //Get all playlists containing the item
            List<String> playlists = this.getMediasPlaylists(item);
            //Add its name to the builder followed by all the playlists it is in
            builder.append(item.getPath());
            if (playlists.size() > 0) {
                builder.append(",").append(String.join(",", playlists));
            }
            builder.append("\n"); //End line with \n so the next items entry will be on a new line
        }

        try {
            //Attempt to write the current media library to the library file
            FileWriter writer = new FileWriter(this.file);
            writer.write(builder.toString());
            writer.close();
            return true; //If success return true other return false so error can be handled
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
        this.changesMade = true; //New playlist added so update changes made
        this.playlists.put(name, media);
    }

    /**
     * Delete a playlist and stop managing it, will not delete the media inside it.
     * @param name Playlist name to remove
     */
    public List<MediaItem> removePlaylist(String name) {
        this.changesMade = true; //Playlist removed so update changes made
         return this.playlists.remove(name);
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

    /**
     * Checks if a file is opened in the library manager
     * @return true if there is currently a file, false otherwise
     */
    public boolean hasFile() {
        return file != null;
    }

    /**
     * checks if the given dir is a supported file type
     * @param dir the file to check type of
     * @return true if the file type is supported, false otherwise
     */
    public boolean isValidFile(String dir) {
        //Get the file extension first
        String fileType = dir.substring(dir.length() - 3).toLowerCase();
        //check if it is a valid type
        return validFileTypes.contains(fileType);
    }

    /**
     * Checks if changes have been made since last opening the library
     * @return true if changes have been made, false otherwise
     */
    public boolean changesMade() {
        return this.changesMade;
    }

    /**
     * Gets a complete list of all the keys to the Playlists hashmap
     * @return All the playlist names currently stored
     */
    public String[] getPlaylistNames() {
        return this.playlists.keySet().toArray(new String[0]);
    }

    /**
     * Gets all the playlists that contain a given MediaItem
     * @param item the MediaItem to get the playlists for
     * @return A list of strings containing the names of all the playlists that contain the item
     */
    public List<String> getMediasPlaylists(MediaItem item) {
        return this.playlists.keySet()
                .stream() //Turn the key set into a stream so it can be filtered
                .filter(k -> this.playlists.get(k).contains(item)) //Filter the playlists to ones that cointain the item
                .collect(Collectors.toList()); //Convert the stream to a list
    }
}
