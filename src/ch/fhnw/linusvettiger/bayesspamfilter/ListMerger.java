package ch.fhnw.linusvettiger.bayesspamfilter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListMerger {
    public static List<String> mergeListOfLists(List<List<String>> list) {
        List<String> mergedList = new ArrayList<>();

        list.forEach(mergedList::addAll);

        return mergedList.stream().distinct().collect(Collectors.toList());
    }
}
