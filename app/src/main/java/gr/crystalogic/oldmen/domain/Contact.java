package gr.crystalogic.oldmen.domain;

import android.graphics.Bitmap;
import android.support.annotation.NonNull;

import java.io.Serializable;

public class Contact implements Comparable<Contact>, Serializable {

    private static final long serialVersionUID = 4439301448809751895L;

    private String id;
    private String displayName;
    private Name name;
    private String primaryTelephone;
    private String secondaryTelephone;
    private String tertiaryTelephone;
    private String email;
    private String address;
    private boolean photoChecked;
    private Bitmap photo;
    private int timesContacted;
    private long lastTimeContacted;
    private boolean starred;

    public Contact() {
    }

    public Contact(String name, String surname, String primaryTelephone) {
        this.displayName = name + " " + surname;
        this.primaryTelephone = primaryTelephone;
    }

    public Contact(Contact contact) {
        id = contact.getId();
        displayName = contact.getDisplayName();
        primaryTelephone = contact.getPrimaryTelephone();
        timesContacted = contact.getTimesContacted();
        lastTimeContacted = contact.getLastTimeContacted();
        starred = contact.isStarred();
    }

    @Override
    public String toString() {
        return "Contact: " + id + " " + displayName + " " + primaryTelephone;
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

    public String getPrimaryTelephone() {
        return primaryTelephone;
    }

    public void setPrimaryTelephone(String primaryTelephone) {
        this.primaryTelephone = primaryTelephone;
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

    public int getTimesContacted() {
        return timesContacted;
    }

    public void setTimesContacted(int timesContacted) {
        this.timesContacted = timesContacted;
    }

    public long getLastTimeContacted() {
        return lastTimeContacted;
    }

    public void setLastTimeContacted(long lastTimeContacted) {
        this.lastTimeContacted = lastTimeContacted;
    }

    public boolean isStarred() {
        return starred;
    }

    public void setStarred(boolean starred) {
        this.starred = starred;
    }

    public String getSecondaryTelephone() {
        return secondaryTelephone;
    }

    public void setSecondaryTelephone(String secondaryTelephone) {
        this.secondaryTelephone = secondaryTelephone;
    }

    public String getTertiaryTelephone() {
        return tertiaryTelephone;
    }

    public void setTertiaryTelephone(String tertiaryTelephone) {
        this.tertiaryTelephone = tertiaryTelephone;
    }

    public Name getName() {
        return name;
    }

    public void setName(Name name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
