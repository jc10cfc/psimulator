package psimulator.AbstractNetwork.xml;


import java.util.EnumMap;
import java.util.Map;
import javax.xml.bind.annotation.adapters.XmlAdapter;
import psimulator.AbstractNetwork.HwTypeEnum;

/**
 *
 * @author Martin Lukáš <lukasma1@fit.cvut.cz>
 */
public class EnumMapAdapter extends XmlAdapter< EnumMapEntry[], EnumMap<HwTypeEnum, Integer>> {

    @Override
    public EnumMap<HwTypeEnum, Integer> unmarshal(EnumMapEntry vt[]) throws Exception {

        EnumMap<HwTypeEnum, Integer> ret = new EnumMap<HwTypeEnum, Integer>(HwTypeEnum.class);


        for (int i = 0; i < vt.length; i++) {
            String key = vt[i].getKey();
            Integer value = vt[i].getValue();

            ret.put(HwTypeEnum.valueOf(key), value);
        }

        return ret;


    }

    @Override
    public EnumMapEntry[] marshal(EnumMap<HwTypeEnum, Integer> bt) throws Exception {


        EnumMapEntry[] ret = new EnumMapEntry[bt.size()];
        int i = 0;
        for (Map.Entry<HwTypeEnum, Integer> entry : bt.entrySet()) {
            HwTypeEnum hwTypeEnum = entry.getKey();
            Integer value = entry.getValue();

            EnumMapEntry en = new EnumMapEntry();
            en.setKey(hwTypeEnum.toString());
            en.setValue(value);

            ret[i++] = en;
        }

        return ret;
    }
}
