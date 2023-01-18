package application.entities;

public class User {
    private String id;
    private String name;
    private Integer pin;

    public User(String id, String name, Integer pin) {
        this.id = id;
        this.name = name;
        this.pin = pin;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean verifyPin(Integer pin) {
        return this.pin.intValue() == pin.intValue();
    }

}
