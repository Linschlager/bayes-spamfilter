package ch.fhnw.linusvettiger.bayesspamfilter;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    // Defined folders to read from. TODO make configurable through arguments to the program
    private final static Path hamFolder = Paths.get("assets", "ham", "ham-anlern");
    private final static Path hamTestFolder = Paths.get("assets", "ham", "ham-test");
    private final static Path spamFolder = Paths.get("assets", "spam", "spam-anlern");
    private final static Path spamTestFolder = Paths.get("assets", "spam", "spam-test");

    // This alpha allows the formula to work because otherwise it would multiply with 0 whenever a word is only in
    // ham or spam exclusively and would make the formula not work because it would very often try to divide by 0
    private final static double alpha = 0.00000000000000000025f;

    // Threshold under under which, something is considered ham and equals or over is spam
    private final static double threshold = 1f;

    /**
     * Runs the trained occurrences against a folder of emails to match and count the results
     * @param folder Folder, the emails lie in
     * @param spamOccurrences // Trained spam occurrences
     * @param hamOccurrences // Trained ham occurrences
     * @return runs, number of found spam and ham emails
     * @throws IOException If there is a problem reading the filesystem
     */
    private static int[] runTest(Path folder, Map<String, Double> spamOccurrences, Map<String, Double> hamOccurrences) throws IOException {
        // Current use-case: Test for spam and ham and count the matches
        int runs = 0;
        int spam = 0;
        int ham = 0;

        // parse the complete folder that was passed to this function
        List<List<String>> parsedMails = FileReader.readParseDirectory(folder);
        for (List<String> parsedWords : parsedMails) {
            BigDecimal spamProbability = BayesFilter.getSpamProbability(parsedWords, spamOccurrences, hamOccurrences);
            runs ++;
            if (spamProbability.doubleValue() < threshold) {
                // TODO do something with HAM emails
                ham ++;
            } else {
                // TODO do something with SPAM emails
                spam ++;
            }
        }

        return new int[] {runs, spam, ham};
    }

    public static void main(String[] args) throws IOException {
        //  ===================
        // || Learning Phase ||
        // ===================

        // The full folder of ham and spam e-mails are going to be read in and create a list of words, without duplicates
        // Each element in the outer list represents one email and the inner list the words in that email.
        List<List<String>> listOfListsOfHamWords = FileReader.readParseDirectory(hamFolder);
        List<List<String>> listOfListsOfSpamWords = FileReader.readParseDirectory(spamFolder);

        // List of all words in order to create a map of all words and their occurrence
        List<String> listOfAllWords = ListMerger.mergeListOfLists(List.of(ListMerger.mergeListOfLists(listOfListsOfHamWords), ListMerger.mergeListOfLists(listOfListsOfSpamWords)));

        // Each word that was in one of the original emails is going to receive a probability value of occurring in either ham or spam emails
        // The key represents the word and the value is the probability in decimal of occurring in either ham or spam emails
        HashMap<String, Double> hamOccurrences = BayesFilter.countOccurrences(listOfAllWords, listOfListsOfHamWords, alpha);
        HashMap<String, Double> spamOccurrences = BayesFilter.countOccurrences(listOfAllWords, listOfListsOfSpamWords, alpha);

        //  ===================
        // || Matching Phase ||
        // ===================
        int hamRuns = 0; // number of ham emails read in to match
        int hamMatches = 0; // number of ham emails that were correctly tagged
        int[] hamResult = runTest(hamTestFolder, spamOccurrences, hamOccurrences);
        hamRuns += hamResult[0]; // number of runs
        hamMatches += hamResult[2]; // number of ham emails found

        int spamRuns = 0; // number of spam emails read in to match
        int spamMatches = 0; // number of spam emails that were correctly tagged
        int[] spamResult = runTest(spamTestFolder, spamOccurrences, hamOccurrences);
        spamRuns += spamResult[0]; // number of runs
        spamMatches += spamResult[1]; // number of spam emails found

        //  =============================
        // || Report and Cleanup Phase ||
        // =============================

        System.out.println("Report:");
        System.out.printf("Threshold: %f%n", threshold);
        System.out.printf("Alpha: %.20f%n", alpha);
        System.out.printf("Match Percentage for Ham-Emails: %f%%%n", ((double)hamMatches/hamRuns)*100);
        System.out.printf("Match Percentage for Spam-Emails: %f%%%n", ((double)spamMatches/spamRuns)*100);
    }
}
