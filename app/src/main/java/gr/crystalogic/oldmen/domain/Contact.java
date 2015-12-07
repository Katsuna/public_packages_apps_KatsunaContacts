package gr.crystalogic.oldmen.domain;

import java.util.List;

public class Contact {

    private String id;
    private Name name;
    private Phone phone;
    private List<Phone> phones;

    public Contact() {
    }

    public Contact(String name, String surname, String number) {
        this.name = new Name(name, surname);
        this.phone = new Phone(number);
    }

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

    public List<Phone> getPhones() {
        return phones;
    }

    public void setPhones(List<Phone> phones) {
        this.phones = phones;
    }

    public Phone getPhone() {
        return phone;
    }

    public void setPhone(Phone phone) {
        this.phone = phone;
    }
}
