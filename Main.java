package dpa;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/*
 * Тестовый проект
 * Группировка строк
 * Дмитриев Павел
 */

public class Main {
    public static void main(String[] args) {

        long startTime = System.nanoTime();

        String filePath = args[0];

        ArrayList<String> fileLines = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (isValidString(line)) {
                    fileLines.add(line);
                }

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        processFileLines(fileLines);

        long endTime = System.nanoTime();
        double executionTime = (endTime - startTime) / 1e9;

        System.out.println("Время выполнения: " + executionTime + " секунд");
    }

    public static void processFileLines(List<String> fileLines) {
        // Карта значений подстрок с индексом и номером строки
        Map<String, Integer> indexToStringIndex = new HashMap<>();
        // Disjoint Set Union
        DSU dsu = new DSU(fileLines.size());

        for (int i = 0; i < fileLines.size(); i++) {
            String line = fileLines.get(i);
            String[] parts = line.split(";");

            for (int j = 0; j < parts.length; j++) {
                String part = parts[j].trim();

                if (!part.equals("\"\"")) {
                    String key = (j) + ":" + part;
                    int stringIndex = indexToStringIndex.getOrDefault(key, -1);
                    if (stringIndex == -1) {
                        indexToStringIndex.put(key, i);
                    } else {
                        dsu.union(i, stringIndex);
                    }
                }
            }
        }

        Map<Integer, Set<String>> components = new HashMap<>();


        for (int i = 0; i < fileLines.size(); i++) {
            int root = dsu.find(i);
            components.computeIfAbsent(root, k -> new HashSet<>()).add(fileLines.get(i));
        }

        List<Set<String>> filteredComponents = new ArrayList<>();
        for (Set<String> component : components.values()) {
            if (component.size() > 1) {
                filteredComponents.add(component);
            }
        }

        int groupCount = filteredComponents.size();

        filteredComponents.sort((a, b) -> Integer.compare(b.size(), a.size()));

        System.out.println("Получилось " + groupCount + " групп с более чем одним элементом:\n");
        int count = 1;
        for (Set<String> component : filteredComponents) {
            System.out.println("Группа" + count);
            for (String line : component) {
                System.out.println(line);
            }
            System.out.println();
            count++;
        }
    }

    public static boolean isValidString(String input) {

        String firstPartPattern = "^\"(\\d*)\"?";
        String secondPartPattern = "(;\"(\\d*)\")*$";

        String fullPattern = firstPartPattern + secondPartPattern;

        Pattern pattern = Pattern.compile(fullPattern);
        Matcher matcher = pattern.matcher(input);

        return matcher.matches();
    }
}
