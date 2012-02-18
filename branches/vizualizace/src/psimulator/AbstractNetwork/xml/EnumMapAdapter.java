package psimulator.AbstractNetwork.xml;


import java.util.EnumMap;
import java.util.Map;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import psimulator.AbstractNetwork.HwTypeEnum;

/**
 *
 * @author Martin Lukáš <lukasma1@fit.cvut.cz>
 */
public class EnumMapAdapter extends XmlAdapter< MapEntry[], EnumMap<HwTypeEnum, Integer>> {

    @Override
    public EnumMap<HwTypeEnum, Integer> unmarshal(MapEntry vt[]) throws Exception {

        EnumMap<HwTypeEnum, Integer> ret = new EnumMap<>(HwTypeEnum.class);


        for (int i = 0; i < vt.length; i++) {
            String key = (String) vt[i].getKey();
            Integer value = (Integer) vt[i].getValue();

            ret.put(HwTypeEnum.valueOf(key), value);
        }

        return ret;


    }

    @Override
    public MapEntry[] marshal(EnumMap<HwTypeEnum, Integer> bt) throws Exception {


        MapEntry[] ret = new MapEntry[bt.size()];
        int i = 0;
        for (Map.Entry<HwTypeEnum, Integer> entry : bt.entrySet()) {
            HwTypeEnum hwTypeEnum = entry.getKey();
            Integer value = entry.getValue();

            MapEntry en = new MapEntry();
            en.setKey(hwTypeEnum.toString());
            en.setValue(value);

            ret[i++] = en;
        }

        return ret;
    }
}
