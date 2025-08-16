package com.example.demo.model;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "resources")
public class Resource {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String type;  // API, PAGE, MENU
    private String url;

    public Resource() {
    }

    public Resource(String name, String type, String url) {
        this.name = name;
        this.type = type;
        this.url = url;
    }

    @ManyToMany(mappedBy = "resources")
    private Set<Permission> permissions = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Set<Permission> getPermissions() {
        return permissions;
    }

    public void setPermissions(Set<Permission> permissions) {
        this.permissions = permissions;
    }
}
