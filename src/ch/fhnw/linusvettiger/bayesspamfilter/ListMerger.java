package ch.fhnw.linusvettiger.bayesspamfilter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ListMerger {
    /**
     * Merges a List of Lists of Strings by adding them to a single list and removing the duplicates
     * @param list List of lists of Strings
     * @return 1D list of all items in the nested list, without duplicates
     */
    public static List<String> mergeListOfLists(List<List<String>> list) {
        List<String> mergedList = new ArrayList<>();
        list.forEach(mergedList::addAll);
        return mergedList.stream().distinct().collect(Collectors.toList());
    }
}
