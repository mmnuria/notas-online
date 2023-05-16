package model;

public class User {
    private String login;
    private String dni;
    private final String password = "123456";

    public User(String login, String dni) {
        this.login = login;
        this.dni = dni;
    }

    public String getDni() {
        return this.dni;
    }

    public String getPassword() {
        return this.password;
    }

    public String getLogin() {
        return this.login;
    }

}
