package org.example.lab.data.student;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Student {
    private String name;
    private String group;

    @Override
    public String toString() {
        return "Ст. " + name + "\nГрупа: " + group;
    }
}
