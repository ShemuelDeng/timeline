package com.shemuel.timeline.common;

import java.util.HashMap;
import java.util.Map;

/**
 * @author dengsx
 * @create 2025/11/22
 **/
public class WindowPosition {
    public static final Map<Integer, String> positionMap = new HashMap<>();

    static {
        positionMap.put(0, "center");
        positionMap.put(1, "lt");
        positionMap.put(2, "lb");
        positionMap.put(3, "rt");
        positionMap.put(4, "rb");
    }
}
