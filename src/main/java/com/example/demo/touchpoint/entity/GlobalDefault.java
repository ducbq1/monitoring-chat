package com.example.demo.touchpoint.entity;

import com.example.demo.touchpoint.annotation.Column;
import com.example.demo.touchpoint.annotation.Table;

@Table(name = "global_defaults")
public class GlobalDefault {

    @Column(name = "name")
    private String name;

    @Column(name = "value")
    private String value;

    public GlobalDefault() {}

    public GlobalDefault(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}
