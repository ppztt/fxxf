package net.mingsoft.utils;

import java.util.*;

public class ApplicantsImportUtil {
    private ApplicantsImportUtil(){

    }

    public static List<ArrayList<String>> findSame(String[] array){
        Map<String, Integer> map = new HashMap<String, Integer>();
        //String[] array = new String[] { "ff", "BB", "cc", "dd", "AA", "BB", "cc", "dd", "AA", "BB", "AA" };

        for (String str : array) {
            Integer num = map.get(str);
            num = null == num ? 1 : num + 1;
            map.put(str, num);
        }

        Set set = map.entrySet();
        Iterator<Map.Entry> it = set.iterator();
        List<String> sList = new ArrayList<String>();
        while (it.hasNext()) {
            Map.Entry<String, Integer> entry = (Map.Entry<String, Integer>) it.next();
            String key = entry.getKey();
            Integer value = entry.getValue();
            //System.out.println("String :" + key + " num :" + value);
            if (value > 1) {
                sList.add(key);
            }
        }

        //System.out.println("============相同元素的下标======================");
        List<ArrayList<String>> indexArr = new ArrayList<>();
        for (String s : sList) {
            ArrayList<String> aIntegers = new ArrayList<>();
            for (int i = 0; i < array.length; i++) {
                if (array[i].equals(s)) {
                    aIntegers.add("第" + (3+i) + "行");
                }
            }
            if (aIntegers.size() > 0) {
                indexArr.add(aIntegers);
            }
        }
        //System.out.println(indexArr);
        return indexArr;
    }
}
