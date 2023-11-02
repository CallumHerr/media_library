package util;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class MediaItem {
    private final String name; //Media files name
    private final String fileDir; //Media files absolute path
    private final String type; //Type of media: Image, Audio, Video
    private final float size; //Size of the media file in MB
    private final String resolution; //Resolution of the media if it is video/image, else empty string
    private final double length; //Length of the recording if it is audio/video, else 0

    /**
     * Sets all the properties of the media based on information from the file
     * @param entry String array with first element as file path and all other elements the name of playlists the medias in
     */
    public MediaItem(String[] entry) {
        //Set the file directory as the first element in the entry array
        this.fileDir = entry[0];

        //Create a new file so information can be retrieved from it
        File file = new File(this.fileDir);
        this.name = file.getName();

        //Set file type based on file ending, if it's not mp4/wav it must be an image.
        String fileSuffix = name.substring(name.length()-3);
        switch (fileSuffix) {
            case "mp4" -> this.type = "Video";
            case "wav" -> this.type = "Audio";
            default -> this.type = "Image";
        }

        //File.length() gives size in bytes, adjusting to store size in MB
        float rawSize = file.length() / 1000000f;
        //Rounding the value to get the size to 2 decimal places for readability
        this.size = Math.round(rawSize * 100f) / 100f;

        //If file is an image try to get resolution, if this fails then set resolution as "Unknown"
        String res;
        if (this.type.equals("Image")) {
            try {
                BufferedImage img = ImageIO.read(file);
                res = img.getWidth() + "x" + img.getHeight();
            } catch (IOException e) {
                res = "Unknown";
            }
        } else res = "N/A"; //If it's not an image then unable to get resolution so N/A

        this.resolution = res; //Set the resolution property to what was just calculated

        double length;
        if (this.type.equals("Audio")) {
            try {
                AudioInputStream stream = AudioSystem.getAudioInputStream(file); //Get the audio as a stream
                AudioFormat format = stream.getFormat(); //Get the format of the audio file
                long frames = stream.getFrameLength(); //Get the number of frames in the audio stream
                length = (frames+0.0) / format.getFrameRate(); //Length is the number of frames / frame rate
            } catch (Exception e) {
                length = 0d; //Something went wrong so set length as 0
            }
        } else length = 0; //Not audio so 0 length
        this.length = length; //Set the length property
    }

    /**
     * Gets the media item in a form that can be inserted into a table
     * @return Array of Strings to be added as a row into a table
     */
    public String[] getEntry() {
        //If length is empty replace it with N/A
        String length;
        if (this.length == 0) length = "N/A";
        else length = this.length + "s"; //Add s to the length to show its in seconds

        //Returns the data of the item in an array for tables
        return new String[]{
                this.name,
                String.valueOf(this.size),
                this.type,
                this.resolution,
                length
        };
    }

    /**
     * Gets the file path of the media item
     * @return the media items file location
     */
    public String getPath() {
        return this.fileDir;
    }

    /**
     * Gets the name of the media item
     * @return name property of this MediaItem
     */
    public String getName() {
        return name;
    }
}
