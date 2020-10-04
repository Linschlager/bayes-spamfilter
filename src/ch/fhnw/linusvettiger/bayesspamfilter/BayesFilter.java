package ch.fhnw.linusvettiger.bayesspamfilter;

import java.util.HashMap;
import java.util.List;

public class BayesFilter {
    public static HashMap<String, Double> countOccurrences(List<String> allWords, List<List<String>> specificWords, double alpha) {
        HashMap<String, Double> occurrences = new HashMap<>();
        allWords.forEach(word -> occurrences.put(word, alpha));
        specificWords.forEach(listOfWords -> {
            listOfWords.forEach(word -> {
                occurrences.put(word, occurrences.get(word) + 1);
            });
        });
        return occurrences;
    }
}
