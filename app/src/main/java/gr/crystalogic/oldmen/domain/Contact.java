package gr.crystalogic.oldmen.domain;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.io.Serializable;

public class Contact implements Comparable<Contact>, Serializable {

    private static final long serialVersionUID = 4439301448809751895L;

    private String id;
    private String displayName;
    private String number;
    private boolean photoChecked;
    private Bitmap photo;

    public Contact() {
    }

    public Contact(String name, String surname, String number) {
        this.displayName = name + " " + surname;
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

    public boolean isPhotoChecked() {
        return photoChecked;
    }

    public void setPhotoChecked(boolean photoChecked) {
        this.photoChecked = photoChecked;
    }

    public Bitmap getPhoto() {
        return photo;
    }

    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }
}
