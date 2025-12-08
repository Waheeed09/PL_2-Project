package services;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    private static final String DATA_DIR = "data/";
    
    // Initialize data directory
    static {
        try {
            Files.createDirectories(Paths.get(DATA_DIR));
        } catch (IOException e) {
            System.err.println("Error creating data directory: " + e.getMessage());
        }
    }
    
    /**
     * Read all lines from a file
     */
    public static List<String> readFile(String filename) {
        List<String> lines = new ArrayList<>();
        String filepath = DATA_DIR + filename;
        
        try {
            File file = new File(filepath);
            if (!file.exists()) {
                file.createNewFile();
                return lines;
            }
            
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
            reader.close();
        } catch (IOException e) {
            System.err.println("Error reading file " + filename + ": " + e.getMessage());
        }
        
        return lines;
    }
    
    /**
     * Write lines to a file (overwrite)
     */
    public static boolean writeFile(String filename, List<String> lines) {
        String filepath = DATA_DIR + filename;
        
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filepath, false));
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
            writer.close();
            return true;
        } catch (IOException e) {
            System.err.println("Error writing to file " + filename + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Append a line to a file
     */
    public static boolean appendToFile(String filename, String line) {
        String filepath = DATA_DIR + filename;
        
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(filepath, true));
            writer.write(line);
            writer.newLine();
            writer.close();
            return true;
        } catch (IOException e) {
            System.err.println("Error appending to file " + filename + ": " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Delete a specific line from file
     */
    public static boolean deleteLine(String filename, String lineToDelete) {
        List<String> lines = readFile(filename);
        lines.removeIf(line -> line.equals(lineToDelete));
        return writeFile(filename, lines);
    }
    
    /**
     * Update a specific line in file
     */
    public static boolean updateLine(String filename, String oldLine, String newLine) {
        List<String> lines = readFile(filename);
        for (int i = 0; i < lines.size(); i++) {
            if (lines.get(i).equals(oldLine)) {
                lines.set(i, newLine);
                return writeFile(filename, lines);
            }
        }
        return false;
    }
    
    /**
     * Search for a line that starts with a specific prefix
     */
    public static String findLineByPrefix(String filename, String prefix) {
        List<String> lines = readFile(filename);
        for (String line : lines) {
            if (line.startsWith(prefix)) {
                return line;
            }
        }
        return null;
    }
    
    /**
     * Get all lines that contain a specific substring
     */
    public static List<String> findLinesBySubstring(String filename, String substring) {
        List<String> lines = readFile(filename);
        List<String> matches = new ArrayList<>();
        for (String line : lines) {
            if (line.contains(substring)) {
                matches.add(line);
            }
        }
        return matches;
    }
    
    /**
     * Check if file exists
     */
    public static boolean fileExists(String filename) {
        return new File(DATA_DIR + filename).exists();
    }
    
    /**
     * Generate unique ID based on file content
     */
    public static String generateId(String filename, String prefix) {
        List<String> lines = readFile(filename);
        int maxId = 0;
        
        for (String line : lines) {
            String[] parts = line.split("\\|");
            if (parts.length > 0 && parts[0].startsWith(prefix)) {
                try {
                    int id = Integer.parseInt(parts[0].substring(prefix.length()));
                    if (id > maxId) {
                        maxId = id;
                    }
                } catch (NumberFormatException e) {
                    // Skip invalid IDs
                }
            }
        }
        
        return prefix + String.format("%03d", maxId + 1);
    }
}