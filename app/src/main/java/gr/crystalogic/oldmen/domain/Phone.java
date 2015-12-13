package gr.crystalogic.oldmen.domain;

import java.io.Serializable;

public class Phone implements Serializable {

    private static final long serialVersionUID = 8522263950995573452L;

    private String number;
    private String type;

    public Phone() {
    }

    public Phone(String number) {
        this.number = number;
    }

    public Phone(Phone phone) {
        this.number = phone.getNumber();
        this.type = phone.getType();
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return " Phone: " + number + " " + type;
    }
}
