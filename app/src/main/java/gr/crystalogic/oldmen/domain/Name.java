package gr.crystalogic.oldmen.domain;

import java.io.Serializable;

public class Name implements Serializable {

    private static final long serialVersionUID = -2664278639090496331L;

    private String id;
    private String name;
    private String surname;

    public Name() {}

    public Name(String name, String surname) {
        this.name = name;
        this.surname = surname;
    }

    public Name(Name name) {
        this.name = name.getName();
        this.surname = name.getSurname();
    }

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

    public String getFullName() {
        return surname + " " + name;
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
