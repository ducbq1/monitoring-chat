package com.example.demo.core.entity;

import com.example.demo.core.annotation.*;

@Metadata(menu = "Học sinh", title = "Học sinh")
@DataSource(name = "touchpoint")
@Table(name = "student")
public class Student extends BaseEntity {

    @PrimaryKey
    @Column(name = "name", title = "Tên")
    private String name;

    @Column(name = "age", title = "Tuổi")
    private Integer age;

    @Column(name = "gender", title = "Giới tính")
    private Boolean gender;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }
}
