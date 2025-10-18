package org.example.lab.handlers;

import org.example.lab.InfoBot;
import org.example.lab.data.student.StudentService;

public class StudentHandler implements UserInputHandler {

    private final StudentService studentService;

    public StudentHandler(StudentService studentService) {
        this.studentService = studentService;
    }

    @Override
    public void handle(Long chatId, String input, InfoBot bot) {
        String[] parts = input.split(",");
        if (parts.length == 2) {
            studentService.saveStudentData(chatId, parts[0].trim(), parts[1].trim());
            bot.sendMainMenu(chatId, "Дані збережено ✅\nВиберіть необхідну команду:");
            bot.clearUserState(chatId);
        } else {
            bot.sendMessage(chatId, "Невірний формат. Використовуйте:\nПрізвище І.П., Група");
        }
    }
}