package ch.fhnw.linusvettiger.bayesspamfilter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class FileReader {
    /**
     * Reads a file
     * @param path Path, the file can be found under, relative to the cwd
     * @return the content of the read file as a string containing linebreaks
     * @throws IOException if the file cannot be found or read
     */
    private static String readFile(String path) throws IOException {
        var parsedPath = Paths.get(path);
        byte[] rawFile = Files.readAllBytes(parsedPath);
        return new String(rawFile);
    }

    /**
     * Reads the whole file and generates a list of all words
     * Uses any whitespace as a delimiter
     * @param rawFile The whole file
     * @return a list of all words contained in the file without duplicates
     */
    private static List<String> parseFile(String rawFile) {
        ArrayList<String> allWords = new ArrayList<>();
        Arrays
                .stream(rawFile.split("\\s"))
                .distinct()
                .forEach(allWords::add);
        return allWords;
    }

    public static List<String> readParseFile(String path) throws IOException {
        String rawOutput = readFile(path);
        return parseFile(rawOutput);

    }
}
