package org.example.lab.data.contacts;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Contacts {
    private String phoneNumber;
    private String email;

    @Override
    public String toString() {
        return "Контакти тел. " + phoneNumber +
                "\ne-mail: " + email;
    }
}
