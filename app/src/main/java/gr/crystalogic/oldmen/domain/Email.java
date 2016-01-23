package gr.crystalogic.oldmen.domain;

import java.io.Serializable;

import gr.crystalogic.oldmen.utils.DataAction;

public class Email implements Serializable{

    private static final long serialVersionUID = -8354850594411881375L;

    private String id;
    private String address;
    private DataAction dataAction;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public DataAction getDataAction() {
        return dataAction;
    }

    public void setDataAction(DataAction dataAction) {
        this.dataAction = dataAction;
    }
}
