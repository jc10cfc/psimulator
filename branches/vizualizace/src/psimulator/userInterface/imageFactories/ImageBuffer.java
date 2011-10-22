package psimulator.userInterface.imageFactories;

import java.awt.image.BufferedImage;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map.Entry;
import psimulator.userInterface.Editor.Enums.HwTypeEnum;

/**
 *
 * @author Martin
 */
public class ImageBuffer {
    /* Data structures for buffering */
    private EnumMap<HwTypeEnum,HashMap<Integer, BufferedImage>> hwComponentBuffer;
    private EnumMap<HwTypeEnum,HashMap<Integer, BufferedImage>> hwMarkedComponentBuffer;
    
    public ImageBuffer(){
        // create EnumMap with all HW components
        hwComponentBuffer = new EnumMap<HwTypeEnum,HashMap<Integer, BufferedImage>>(HwTypeEnum.class);
        hwMarkedComponentBuffer = new EnumMap<HwTypeEnum,HashMap<Integer, BufferedImage>>(HwTypeEnum.class);
        
        // for each component create HashMap with Integer (size) and BufferredImage
        for(HwTypeEnum c : HwTypeEnum.values()){
            hwComponentBuffer.put(c, new HashMap<Integer, BufferedImage>());
            hwMarkedComponentBuffer.put(c, new HashMap<Integer, BufferedImage>());
        }
    }
    
    /**
     * Clears alll BufferImages in buffer
     */
    public void clearBuffer(){
        // each HW components HashMap is cleared
        for(Entry<HwTypeEnum,HashMap<Integer, BufferedImage>> e : hwComponentBuffer.entrySet()){
            e.getValue().clear();
        }
        
        for(Entry<HwTypeEnum,HashMap<Integer, BufferedImage>> e : hwMarkedComponentBuffer.entrySet()){
            e.getValue().clear();
        }
    }
    
    /**
     * Puts BufferedImage into buffer
     * @param hwComponent
     * @param size
     * @param image 
     * @param marked 
     */
    public void putBufferedImage(HwTypeEnum hwComponent, Integer size, BufferedImage image, boolean marked){
        EnumMap<HwTypeEnum,HashMap<Integer, BufferedImage>> map;
        
        if(marked){
            map = hwMarkedComponentBuffer;
        }else{
            map = hwComponentBuffer;
        }
        
        map.get(hwComponent).put(size, image);
    }
    
    /**
     * Gets specified BufferedImage
     * @param hwComponent
     * @param size
     * @return BufferedImage if found, otherwise null
     * @param marked 
     */
    public BufferedImage getBufferedImage(HwTypeEnum hwComponent, Integer size, boolean marked){
        EnumMap<HwTypeEnum,HashMap<Integer, BufferedImage>> map;
        
        if(marked){
            map = hwMarkedComponentBuffer;
        }else{
            map = hwComponentBuffer;
        }
        
        // if is specified BufferedImage in buffer
        if(map.get(hwComponent).containsKey(size)){
            return (BufferedImage) map.get(hwComponent).get(size);
        }else{
            // if isnt
            return null;
        }
    }
    
}
