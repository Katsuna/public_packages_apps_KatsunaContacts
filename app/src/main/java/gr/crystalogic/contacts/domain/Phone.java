package gr.crystalogic.contacts.domain;

import java.io.Serializable;

import gr.crystalogic.contacts.utils.DataAction;

public class Phone implements Serializable {

    private static final long serialVersionUID = 8522263950995573452L;

    private String id;
    private String number;
    private String type;
    private boolean primary;
    private DataAction dataAction;

    public Phone() {
    }

    public Phone(String number) {
        this.number = number;
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

    public boolean isPrimary() {
        return primary;
    }

    public void setPrimary(boolean primary) {
        this.primary = primary;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public DataAction getDataAction() {
        return dataAction;
    }

    public void setDataAction(DataAction dataAction) {
        this.dataAction = dataAction;
    }
}
