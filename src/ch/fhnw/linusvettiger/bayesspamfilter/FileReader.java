package ch.fhnw.linusvettiger.bayesspamfilter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
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
    private static String readFile(Path path) throws IOException {
        byte[] rawFile = Files.readAllBytes(path);
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
                .filter(word -> word.length() > 0) // Filter out empty words
                .distinct()
                .forEach(allWords::add);
        return allWords;
    }

    /**
     * Helper function to read a file
     * @param path String representation of absolute or relative path from project root.
     * @return Words in the file, duplicates and empty strings removed
     * @throws IOException Error reading from the file system
     */
    public static List<String> readParseFile(String path) throws IOException {
        String rawOutput = readFile(Paths.get(path));
        return parseFile(rawOutput);
    }

    /**
     * Helper function to read a file
     * @param path Absolute or relative path from project root as a {@see Path}
     * @return Words in the file, duplicates and empty strings removed
     * @throws IOException Error reading from the file system
     */
    public static List<String> readParseFile(Path path) throws IOException {
        String rawOutput = readFile(path);
        return parseFile(rawOutput);
    }

    /**
     * Runs {@see FileReader.readParseFile} for each file in the current folder
     * @param folder Absolute or relative path from project root as a {@see Path}
     * @return List of results from {@see FileReader.readParseFile}
     * @throws IOException Error reading from the file system
     */
    public static List<List<String>> readParseDirectory(Path folder) throws IOException {
        List<List<String>> listOfWords = new ArrayList<>();
        Files.newDirectoryStream(folder).forEach(file -> {
            try {
                listOfWords.add(FileReader.readParseFile(file));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        return listOfWords;
    }
}
