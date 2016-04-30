package gr.crystalogic.contacts.domain;

import java.io.Serializable;

public class Name implements Serializable {

    private static final long serialVersionUID = -2664278639090496331L;

    private String id;
    private String name;
    private String surname;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    @Override
    public String toString() {
        return " Name: " + name + " " + surname;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
