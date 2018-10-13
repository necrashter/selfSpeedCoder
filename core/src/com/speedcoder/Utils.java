package com.speedcoder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by ilker on 17.08.2018.
 */

public class Utils {
    public static String difference(String str1, String str2) {
        if (str1 == null) {
            return str2;
        }
        if (str2 == null) {
            return str1;
        }
        int at = indexOfDifference(str1, str2);
        if (at == -1) {
            return "";
        }
        return str2.substring(at);
    }

    public static int indexOfDifference(CharSequence cs1, CharSequence cs2) {
        if (cs1 == cs2) {
            return -1;
        }
        if (cs1 == null || cs2 == null) {
            return 0;
        }
        int i;
        for (i = 0; i < cs1.length() && i < cs2.length(); ++i) {
            if (cs1.charAt(i) != cs2.charAt(i)) {
                break;
            }
        }
        if (i < cs2.length() || i < cs1.length()) {
            return i;
        }
        return -1;
    }

    public static LinkedHashMap<Character,Integer> sortByValues(HashMap<Character,Integer> hashMap){
        List<Integer> values = new ArrayList<Integer>(hashMap.values());
        List<Character> keys = new ArrayList<Character>(hashMap.keySet());
        Collections.sort(values, Collections.reverseOrder());

        LinkedHashMap<Character,Integer> sorted = new LinkedHashMap<Character, Integer>();
        for(int i:values){
            Iterator<Character> characterIterator = keys.iterator();
            char c;
            while (characterIterator.hasNext()){
                c=characterIterator.next();
                if(i==hashMap.get(c)){
                    sorted.put(c,i);
                    characterIterator.remove();
                    break;
                }
            }
        }
        return sorted;
    }
}
