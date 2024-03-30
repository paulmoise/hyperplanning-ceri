package fr.ceri.ceriplanning.model;

public class CreateConnexion {
    String name;
    String password;

    public CreateConnexion(String name, String password) {
        this.name = name;
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        CreateConnexion other = (CreateConnexion) obj;
        return name.equals(other.name) && password.equals(other.password);
    }
}
