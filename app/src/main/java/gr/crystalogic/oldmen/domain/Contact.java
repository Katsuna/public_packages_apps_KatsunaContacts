package gr.crystalogic.oldmen.domain;

import android.support.annotation.NonNull;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Contact implements Comparable<Contact>, Serializable {

    private static final long serialVersionUID = 4439301448809751895L;

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

    public Contact(Contact contact) {
        id = contact.getId();

        if (contact.getName() != null) {
            name = new Name(contact.getName());
        }

        if (contact.getPhone() != null) {
            phone = new Phone(contact.getPhone());
        }

        if (contact.getPhones() != null) {
            phones = new ArrayList<>();
            for (Phone p : contact.getPhones()) {
                phones.add(new Phone(p));
            }
        }
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

    @Override
    public int compareTo(@NonNull Contact another) {

        Log.e("Contact-comparison", this.toString() + " - " + another.toString());

        int output = 0;

        if (name.getSurname() != null && another.getName().getSurname() != null) {
            output = name.getSurname().compareTo(another.getName().getSurname());
        } else if (name.getSurname() == null && another.getName().getSurname() != null) {
            output = 1;
        } else if (name.getSurname() != null && another.getName().getSurname() == null) {
            output = -1;
        } else {
            output = 0;
        }

        return output;
    }
}
