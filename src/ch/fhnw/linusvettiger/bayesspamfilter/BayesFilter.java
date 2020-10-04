package ch.fhnw.linusvettiger.bayesspamfilter;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BayesFilter {
    public static HashMap<String, Double> countOccurrences(List<String> allWords, List<List<String>> specificWords, double alpha) {
        HashMap<String, Double> occurrences = new HashMap<>();
        allWords.forEach(word -> occurrences.put(word, alpha));
        specificWords.forEach(listOfWords -> {
            listOfWords.forEach(word -> {
                occurrences.put(word, occurrences.get(word) + 1);
            });
        });

        // Divide count by the amount of words in the specific list in order to get the probability of that word
        occurrences.forEach((word, count) -> occurrences.put(word, count/specificWords.size()));
        return occurrences;
    }

    private static BigDecimal calculateProbability(List<String> words, Map<String, Double> occurrences) {
        BigDecimal probability = BigDecimal.ONE;
        for (String word : words) {
            if (occurrences.containsKey(word)) {
                probability = probability.multiply(BigDecimal.valueOf(occurrences.get(word)));
            }
        }
        return probability;
    }

    public static BigDecimal getSpamProbability(List<String> words, Map<String, Double> spamOcc, Map<String, Double> hamOcc) {
        BigDecimal spam = calculateProbability(words, spamOcc);
        BigDecimal ham = calculateProbability(words, hamOcc);
        return spam.divide(spam.add(ham), MathContext.DECIMAL128);
    }
}
