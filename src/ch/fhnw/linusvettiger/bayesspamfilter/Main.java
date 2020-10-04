package ch.fhnw.linusvettiger.bayesspamfilter;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.fhnw.linusvettiger.bayesspamfilter.BayesFilter.countOccurrences;
import static ch.fhnw.linusvettiger.bayesspamfilter.BayesFilter.getSpamProbability;
import static ch.fhnw.linusvettiger.bayesspamfilter.FileReader.readParseDirectory;
import static ch.fhnw.linusvettiger.bayesspamfilter.ListMerger.mergeListOfLists;

public class Main {
    private final static Path hamFolder = Paths.get("assets", "ham", "ham-anlern");
    private final static Path hamTestFolder = Paths.get("assets", "ham", "ham-test");
    private final static Path spamFolder = Paths.get("assets", "spam", "spam-anlern");
    private final static Path spamTestFolder = Paths.get("assets", "spam", "spam-test");

    // This alpha allows the formula to work because otherwise it would multiply with 0 whenever a word is only in
    // ham or spam exclusively and would make the formula not work because it would very often try to divide by 0
    private final static double alpha = 0.00000000000000000025f;

    // Threshold under under which, something is considered ham and equals or over is spam
    private final static double threshold = 1f;

    private static int[] runTest(Path folder, Map<String, Double> spamOccurrences, Map<String, Double> hamOccurrences) throws IOException {
        int runs = 0;
        int spam = 0;
        int ham = 0;

        // Ham Calibration
        List<List<String>> hamCalibration = readParseDirectory(folder);
        for (List<String> hamWords : hamCalibration) {
            BigDecimal spamProbability = getSpamProbability(hamWords, spamOccurrences, hamOccurrences);
            runs ++;
            if (spamProbability.doubleValue() < threshold) {
                ham ++;
            } else {
                spam ++;
            }
        }

        return new int[] {runs, spam, ham};
    }

    public static void main(String[] args) throws IOException {
        //  ===================
        // || Learning Phase ||
        // ===================
        List<List<String>> listOfListsOfHamWords = readParseDirectory(hamFolder);
        List<List<String>> listOfListsOfSpamWords = readParseDirectory(spamFolder);
        List<String> listOfAllWords = mergeListOfLists(List.of(mergeListOfLists(listOfListsOfHamWords), mergeListOfLists(listOfListsOfSpamWords)));

        HashMap<String, Double> hamOccurrences = countOccurrences(listOfAllWords, listOfListsOfHamWords, alpha);
        HashMap<String, Double> spamOccurrences = countOccurrences(listOfAllWords, listOfListsOfSpamWords, alpha);

        //  ===================
        // || Matching Phase ||
        // ===================
        int runs = 0;
        int matches = 0;
        // int falsePositives = 0; // Was used for debugging


        int[] hamResult = runTest(hamTestFolder, spamOccurrences, hamOccurrences);
        runs += hamResult[0];
        // falsePositives += hamResult[1];
        matches += hamResult[2];

        int[] spamResult = runTest(spamTestFolder, spamOccurrences, hamOccurrences);
        runs += spamResult[0];
        matches += spamResult[1];
        // falsePositives += spamResult[2];

        //  =============================
        // || Report and Cleanup Phase ||
        // =============================

        System.out.println("Report:");
        System.out.printf("Threshold: %f%n", threshold);
        System.out.printf("Alpha: %.20f%n", alpha);
        System.out.printf("Match Percentage: %f%%%n", (double)matches/runs*100);
    }
}
