package ch.fhnw.linusvettiger.bayesspamfilter;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static ch.fhnw.linusvettiger.bayesspamfilter.FileReader.readParseDirectory;

public class Main {
    private final static Path hamFolder = Paths.get("assets", "ham", "ham-anlern");
    private final static Path spamFolder = Paths.get("assets", "spam", "spam-anlern");

    public static void main(String[] args) throws IOException {
        List<List<String>> listOfHamWords = readParseDirectory(hamFolder);
        List<List<String>> listOfSpamWords = readParseDirectory(spamFolder);

        System.out.println(listOfHamWords);
    }
}
