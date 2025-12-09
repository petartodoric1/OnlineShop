package entities;

import java.io.Serializable;
import java.util.List;

public class Backup implements Serializable {

    private static final long serialVersionUID = 1L;

    private List<User> users;
    private List<Item> items;

    public Backup() {}

    public Backup(List<User> users, List<Item> items) {
        this.users = users;
        this.items = items;
    }

    public List<User> getUsers() {
        return users;
    }

    public List<Item> getItems() {
        return items;
    }
}