package util;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MediaItem {
    private final String name; //Media files name
    private final String fileDir; //Media files absolute path
    private final String type; //Type of media: Image, Audio, Video
    private final float size; //Size of the media file in MB
    private final String resolution; //Resolution of the media if it is video/image, else empty string
    private final int length; //Length of the recording if it is audio/video, else 0
    private final List<String> playlists; //Playlists that this media is in

    /**
     * Sets all the properties of the media based on information from the file
     * @param entry String array with first element as file path and all other elements the name of playlists the medias in
     */
    public MediaItem(String[] entry) {
        this.fileDir = entry[0];
        this.length = 0;
        this.playlists = new ArrayList<>();
        if (entry.length > 1) {
            this.playlists.addAll(Arrays.asList(entry).subList(1, entry.length));
        }

        File file = new File(this.fileDir);
        this.name = file.getName();

        //Set file type based on file ending, if it's not mp4/mp3 it must be an image.
        String fileSuffix = name.substring(name.length()-3);
        switch (fileSuffix) {
            case "mp4" -> this.type = "Video";
            case "mp3" -> this.type = "Audio";
            default -> this.type = "Image";
        }

        //File.length() gives size in bytes, adjusting to store size in MB
        float rawSize = file.length() / 1000000f;
        //Rounding the value to get the size to 2 decimal places for readability
        this.size = Math.round(rawSize * 100f) / 100f;

        //Setting resolution if able to obtain that information otherwise blank string
        String res;
        if (this.type.equals("Image")) {
            try {
                BufferedImage img = ImageIO.read(file);
                res = img.getWidth() + "x" + img.getHeight();
            } catch (IOException e) {
                res = "";
            }
        } else res = "";

        this.resolution = res;
    }

    /**
     * Gets the media item in a form that can be inserted into a table
     * @return Array of Strings to be added as a row into a table
     */
    public String[] getEntry() {
        return new String[]{
                this.name,
                String.valueOf(this.size),
                this.type,
                this.resolution,
                String.valueOf(this.length)
        };
    }

    /**
     * Gets a full list of all playlists that the media item is a part of
     * @return List of playlist names
     */
    public List<String> getPlaylists() {
        return this.playlists;
    }

    /**
     * converts the MediaItem to a string to be saved into a .csv file
     * @return the .csv version of the media item for saving
     */
    public String toString() {
        return fileDir + "," +
                String.join(",", playlists);
    }

    /**
     * Gets the file path of the media item
     * @return the media items file location
     */
    public String getPath() {
        return this.fileDir;
    }
}
