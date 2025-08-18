package com.example.demo.core.entity;

import com.example.demo.core.annotation.*;

import java.math.BigDecimal;

@Metadata(menu = "Người dùng", title = "Người dùng")
@DataSource(name = "authentication")
@Table(name = "Users")
public class Users extends BaseEntity {

    @PrimaryKey
    @Column(name = "id", title = "Mã")
    private BigDecimal id;

    @Column(name = "username", title = "Tên")
    private String username;

    @Column(name = "password", title = "Mật khẩu")
    private String password;

    public BigDecimal getId() {
        return id;
    }

    public void setId(BigDecimal id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
