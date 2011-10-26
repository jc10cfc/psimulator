package psimulator.userInterface.imageFactories;

import java.awt.Image;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;

/**
 *
 * @author Martin
 */
public class BufferedImageLoader {

    private HashMap<String, Image> imageBuffer;

    public BufferedImageLoader() {
        imageBuffer = new HashMap<String, Image>();
    }

    /**
     * Returns image from path. It uses buffer.
     * @param path
     * @return loaded image
     * @throws IOException When image could not be loaded from path.
     */
    public Image getImage(String path) throws IOException {
        // if hash map contains key, return value
        if (imageBuffer.containsKey(path)) {
            System.out.println("hit");
            return imageBuffer.get(path);
        } else { // else load image and return value
            Image image = ImageIO.read(getClass().getResource(path));
            System.out.println("miss");
            imageBuffer.put(path, image);
            return image;
        }
    }

}
