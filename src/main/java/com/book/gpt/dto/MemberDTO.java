package com.book.gpt.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;


@Getter
@Setter
public class MemberDTO {
    private String id;
    private String password;
    private String name;
    private String email;
    private String tel;
    private int cash;
    private int auth;
    private Collection<? extends GrantedAuthority> authorities; // 권한 정보
    private String role;


    // 조영준
    // @setter가 안먹어서 일단 만듦.
    public void setName(String name) {
        this.name = name;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setTel(String tel) {
        this.tel = tel;
    }
    public void setCash(int cash) {
        this.cash = cash;
    }
}
