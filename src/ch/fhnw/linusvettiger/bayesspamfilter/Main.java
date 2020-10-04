package ch.fhnw.linusvettiger.bayesspamfilter;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

import static ch.fhnw.linusvettiger.bayesspamfilter.BayesFilter.countOccurrences;
import static ch.fhnw.linusvettiger.bayesspamfilter.FileReader.readParseDirectory;
import static ch.fhnw.linusvettiger.bayesspamfilter.ListMerger.mergeListOfLists;

public class Main {
    private final static Path hamFolder = Paths.get("assets", "ham", "ham-anlern");
    private final static Path spamFolder = Paths.get("assets", "spam", "spam-anlern");

    private final static double alpha = 0f;

    public static void main(String[] args) throws IOException {
        List<List<String>> listOfListsOfHamWords = readParseDirectory(hamFolder);
        List<List<String>> listOfListsOfSpamWords = readParseDirectory(spamFolder);
        List<String> listOfAllWords = mergeListOfLists(List.of(mergeListOfLists(listOfListsOfHamWords), mergeListOfLists(listOfListsOfSpamWords)));

        HashMap<String, Double> hamOccurrences = countOccurrences(listOfAllWords, listOfListsOfHamWords, alpha);
        HashMap<String, Double> spamOccurrences = countOccurrences(listOfAllWords, listOfListsOfSpamWords, alpha);
    }
}
