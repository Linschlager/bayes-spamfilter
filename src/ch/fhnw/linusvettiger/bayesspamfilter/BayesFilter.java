package ch.fhnw.linusvettiger.bayesspamfilter;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BayesFilter {
    /**
     * Counts the amount of times any given word has been used in the given emails and divides it by the number of emails in order to get a relative value
     * @param allWords List of all words
     * @param specificWords List of List of words that occurred in the given emails.
     * @param alpha default number to not allow a 0
     * @return Mapping of words with their relative occurrence in the given emails
     */
    public static HashMap<String, Double> countOccurrences(List<String> allWords, List<List<String>> specificWords, double alpha) {
        HashMap<String, Double> occurrences = new HashMap<>();
        // initialize the map with a default value of alpha
        allWords.forEach(word -> occurrences.put(word, alpha));
        // Count the number of times, each word has been used in the given emails
        specificWords.forEach(listOfWords -> {
            listOfWords.forEach(word -> {
                occurrences.put(word, occurrences.get(word) + 1);
            });
        });

        // Divide count by the amount of words in the specific list in order to get the probability of that word in decimal, relative to the number of emails
        occurrences.forEach((word, count) -> occurrences.put(word, count/specificWords.size()));
        return occurrences;
    }

    /**
     * Multiplies the probabilities of all given words in the given map
     * @param words List of words to look for
     * @param occurrences Mapping of words with their probability of occurring
     * @return probability of a given list of words to be in a spam or ham email
     */
    private static BigDecimal calculateProbability(List<String> words, Map<String, Double> occurrences) {
        BigDecimal probability = BigDecimal.ONE;
        for (String word : words) {
            if (occurrences.containsKey(word)) {
                probability = probability.multiply(BigDecimal.valueOf(occurrences.get(word)));
            }
        }
        return probability;
    }

    /**
     * Runs the Bayes formula on a list of given words, given the complete Mapping of words and probabilities of occurring in a spam or ham email
     * @param words List of words that occurred in a given email
     * @param spamOcc Mapping of words with their probability of occurring in a spam email
     * @param hamOcc Mapping of words with their probability of occurring in a ham email
     * @return Probability of being a spam email in decimal
     */
    public static BigDecimal getSpamProbability(List<String> words, Map<String, Double> spamOcc, Map<String, Double> hamOcc) {
        BigDecimal spam = calculateProbability(words, spamOcc);
        BigDecimal ham = calculateProbability(words, hamOcc);
        return spam.divide(spam.add(ham), MathContext.DECIMAL128);
    }
}
