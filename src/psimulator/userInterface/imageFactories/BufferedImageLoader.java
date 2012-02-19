package psimulator.userInterface.imageFactories;

import java.awt.Image;
import java.io.IOException;
import java.util.HashMap;
import javax.imageio.ImageIO;

/**
 *
 * @author Martin Švihlík <svihlma1 at fit.cvut.cz>
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
            //System.out.println("Hit");
            return imageBuffer.get(path);
        } else { // else load image and return value
            //System.out.println("Miss");
            Image image = ImageIO.read(getClass().getResource(path));
            imageBuffer.put(path, image);
            return image;
        }
    }

}
