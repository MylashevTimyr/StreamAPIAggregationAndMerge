package org.example;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class ParallelStreamCollectMapAdvancedExample {
    public static void main(String[] args) {
        List<Student> students = Arrays.asList(
                new Student("Student1", Map.of("Math", 90, "Physics", 85)),
                new Student("Student2", Map.of("Math", 95, "Physics", 88)),
                new Student("Student3", Map.of("Math", 88, "Chemistry", 92)),
                new Student("Student4", Map.of("Physics", 78, "Chemistry", 85))
        );


        Map<String, double[]> subjectAggregates = students.parallelStream()
                .flatMap(student -> student.getGrades().entrySet().stream())
                .collect(Collectors.toConcurrentMap(
                        Map.Entry::getKey,
                        entry -> new double[]{entry.getValue(), 1},
                        (a, b) -> {
                            a[0] += b[0];
                            a[1] += b[1];
                            return a;
                        },
                        ConcurrentHashMap::new
                ));

        Map<String, Double> subjectAverages = subjectAggregates.entrySet().stream()
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        entry -> entry.getValue()[0] / entry.getValue()[1]
                ));

        subjectAverages.forEach((subject, average) ->
                System.out.printf("Subject: %s, Average Grade: %.2f%n", subject, average));
    }
}
