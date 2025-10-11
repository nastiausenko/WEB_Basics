package org.example.lab.student;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class StudentService {
    private final Map<Long, Student> studentData = new ConcurrentHashMap<>();

    public void saveStudentData(Long chatId, String name, String group) {
        studentData.put(chatId, new Student(name, group));
    }

    public Student getStudent(Long chatId) {
        return studentData.get(chatId);
    }

    public boolean hasStudentData(Long chatId) {
        return studentData.containsKey(chatId);
    }
}
