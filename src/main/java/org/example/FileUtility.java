package org.example;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FileUtility {
    public static void main(String[] args) {
        //flags to decide whether to implement functionality or not
        boolean appendFlag = false;
        boolean shortStatFlag = false;
        boolean fullStatFlag = false;

        String outputFilePrefix = "";
        String outputPath = ".";
        List<String> inputFiles = new ArrayList<>();

        //lists to store lines from output files by data type
        List<Long> integers = new ArrayList<>();
        List<Double> floats = new ArrayList<>();
        List<String> strings = new ArrayList<>();

        //here we read each argument from command line passed using "java jar"
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-a":
                    appendFlag = true;
                    break;
                case "-s":
                    shortStatFlag = true;
                    break;
                case "-f":
                    fullStatFlag = true;
                    break;
                case "-p":
                    if (i + 1 < args.length) {
                        outputFilePrefix = args[++i]; // here we set prefix for output file names
                    } else {
                        System.err.println("Value after -p not found.");
                        return;
                    }
                    break;
                case "-o":
                    if (i + 1< args.length) {
                        outputPath = args[++i]; // set output path for output files
                    } else {
                        System.err.println("Value after -o not found.");
                        return;
                    }
                    break;
                default: // here we check whether it ends with ".txt" or its a typo
                    if (args[i].endsWith(".txt")) {
                        inputFiles.add(args[i]);
                    } else if (args[i].startsWith("-")) {
                        System.err.println("Unknown flag: " + args[i]);
                        return;
                    } else {
                        System.err.println("Invalid input file (must end with .txt): " + args[i]);
                        return;
                    }
                    break;
            }
        }

        //if no input files are found, then return an error
        if (inputFiles.isEmpty()) {
            System.err.println("Input files are not provided.");
            return;
        }

        //call a method to read each file line by line and classify each file by data type
        readInputFiles(inputFiles, integers,  floats, strings);

        //System.out.println("Input files: " + inputFiles);

        // create directory in case outputPath is set
        try {
            Files.createDirectories(Paths.get(outputPath));
            System.out.println(Paths.get(outputPath));
        } catch (IOException e) {
            System.err.println("Could not create output directory: " + e.getMessage());
            return;
        }

        //write the classified lines to the corresponding output files
        writeOutputFiles(outputPath, outputFilePrefix, appendFlag, integers, floats, strings);

        //print out statistics depending on the flags set
        printStatistics(shortStatFlag, fullStatFlag, integers, floats, strings);
    }

    private static void readInputFiles(List<String> inputFiles, List<Long> integers, List<Double> floats, List<String> strings) {
        for (String file : inputFiles) {
            if (!Files.exists(Paths.get(file))) {
                System.err.println("File not found: " + file);
                continue; //skip the file if doesnt exist
            }

            try (BufferedReader reader = Files.newBufferedReader(Paths.get(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    line = line.trim(); // remove spaces
                    if (line.isEmpty()) continue; // skipping empty lines

                    //try parsing as integer or float number
                    try {
                        long integerValue = Long.parseLong(line);
                        integers.add(integerValue);
                        continue;
                    } catch (NumberFormatException ignored) {}
                    try {
                        double floatValue = Double.parseDouble(line);
                        floats.add(floatValue);
                        continue;
                    } catch (NumberFormatException ignored) {}

                    strings.add(line); // by default if isnt a number, then a string
                }
            } catch (IOException e) {
                System.err.println("Error reading file " + file + ": " + e.getMessage());
            }
        }
    }

    private static void writeOutputFiles(String outputPath, String prefix, boolean appendMode, List<Long> integers, List<Double> floats, List<String> strings) {
        // create a new list to store ints as strings for file writing
        List<String> integerLines = new ArrayList<>();
        for (Long number : integers) {
            String line = String.valueOf(number);
            integerLines.add(line);
        }

        //same for the float numbers
        List<String> floatLines = new ArrayList<>();
        for (Double number : floats) {
            String line = String.valueOf(number);
            floatLines.add(line);
        }

        //create the output files for the data types only if such data types exist in the input files
        if (!integers.isEmpty()) {
            String intFile = Paths.get(outputPath, prefix + "integers.txt").toString();

            writeLinesToFile(intFile, integerLines, appendMode);
        }

        if (!floats.isEmpty()) {
            String floatFile = Paths.get(outputPath, prefix + "floats.txt").toString();
            writeLinesToFile(floatFile, floatLines, appendMode);
        }

        if (!strings.isEmpty()) {
            String stringFile = Paths.get(outputPath, prefix + "strings.txt").toString();

            writeLinesToFile(stringFile, strings, appendMode);
        }
    }

    private static void printStatistics(boolean shortStats, boolean fullStats, List<Long> integers, List<Double> floats, List<String> strings) {
        //short stats should include the number of elements of each data type
        if (shortStats) {
            System.out.println("Short Statistics");
            System.out.println("Integers count: " + integers.size());
            System.out.println("Floats count:   " + floats.size());
            System.out.println("Strings count:  " + strings.size());
        }

        if (fullStats) {
            System.out.println("Full Statistics");

            if (!integers.isEmpty()) {
                long min = Collections.min(integers);
                long max = Collections.max(integers);
                long sum = 0;
                for (long n : integers) sum += n;
                double avg = (double) sum / integers.size();

                System.out.println("Integers:");
                System.out.println("  Min: " + min);
                System.out.println("  Max: " + max);
                System.out.println("  Sum: " + sum);
                System.out.println("  Avg: " + avg);
            }

            if (!floats.isEmpty()) {
                double min = Collections.min(floats);
                double max = Collections.max(floats);
                double sum = 0;
                for (double n : floats) sum += n;
                double avg = sum /floats.size();

                System.out.println("Floats:");
                System.out.println("  Min: " + min);
                System.out.println("  Max: " + max);
                System.out.println("  Sum: " + sum);
                System.out.println("  Avg: " + avg);
            }

            if (!strings.isEmpty()) {
                int minLen = strings.stream().mapToInt(String::length).min().orElse(0);
                int maxLen = strings.stream().mapToInt(String::length).max().orElse(0);
                System.out.println("Strings:");
                System.out.println("  Shortest length: " + minLen);
                System.out.println("  Longest length:  " + maxLen);
            }
        }
    }

    //method to write a line to a file using BufferWriter
    private static void writeLinesToFile(String filePath, List<String> lines, boolean append) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, append))) {
            //read each line and write it to a file
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error writing to " + filePath + ": " + e.getMessage());
        }
    }
}

//java -jar target/java-test-task-1.0-SNAPSHOT.jar -s -f -p sample- in1.txt in2.txt
//java -jar target/java-test-task-1.0-SNAPSHOT.jar -s -f -a -p sample- -o output in1.txt in2.txt