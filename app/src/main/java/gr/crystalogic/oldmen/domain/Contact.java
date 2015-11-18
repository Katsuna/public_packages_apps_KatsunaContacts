package gr.crystalogic.oldmen.domain;

import java.util.Collection;

public class Contact {

    private String id;
    private Name name;
    private Collection<Phone> phones;

    @Override
    public String toString() {
        return "Contact: " + id + " " + name + " " + phones;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public Collection<Phone> getPhones() {
        return phones;
    }

    public void setPhones(Collection<Phone> phones) {
        this.phones = phones;
    }
}
