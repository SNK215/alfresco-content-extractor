package org.example.model;

public class Credentials {
    private String user;
    private String password;
    private String serviceUrl;

    public Credentials() {
    }

    public Credentials(String user, String password, String serviceUrl) {
        this.user = user;
        this.password = password;
        this.serviceUrl = serviceUrl;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getServiceUrl() {
        return serviceUrl;
    }

    public void setServiceUrl(String serviceUrl) {
        this.serviceUrl = serviceUrl;
    }
}
