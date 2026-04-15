package acc;

import javafx.beans.property.*;

public class UserAccount {
    private final IntegerProperty id;
    private final StringProperty username;
    private final StringProperty type;
    private final StringProperty status;

    public UserAccount(int id, String username, String type, String status) {
        this.id       = new SimpleIntegerProperty(id);
        this.username = new SimpleStringProperty(username);
        this.type     = new SimpleStringProperty(type);
        this.status   = new SimpleStringProperty(status == null ? "—" : status);
    }

    public IntegerProperty idProperty()       { return id; }
    public StringProperty usernameProperty()  { return username; }
    public StringProperty typeProperty()      { return type; }
    public StringProperty statusProperty()    { return status; }

    public int    getId()       { return id.get(); }
    public String getUsername() { return username.get(); }
    public String getType()     { return type.get(); }
    public String getStatus()   { return status.get(); }
}