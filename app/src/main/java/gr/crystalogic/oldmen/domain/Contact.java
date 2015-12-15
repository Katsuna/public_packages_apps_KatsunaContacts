package gr.crystalogic.oldmen.domain;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class Contact implements Comparable<Contact>, Serializable {

    private static final long serialVersionUID = 4439301448809751895L;

    private String id;
    private String displayName;
    private String number;

    public Contact() {
    }

    public Contact(String name, String surname, String number) {
        this.displayName = surname + " " + name;
        this.number = number;
    }

    public Contact(Contact contact) {
        id = contact.getId();
        displayName = contact.getDisplayName();
        number = contact.getNumber();
    }

    @Override
    public String toString() {
        return "Contact: " + id + " " + displayName + " " + number;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    @Override
    public int compareTo(@NonNull Contact another) {
        return displayName.compareTo(another.displayName);
    }
}
