package top.yoe.pojo;


import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;


@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)

    @Column(name = "id")
    private Integer id;

    @Column(name = "username")
    private String username;

    @JsonIgnore
    @Column(name = "password")
    private String password;

    @Column(name = "realname")
    private String realname;

    @Column(name = "self_descn")
    private String selfDescn;

    @Column(name = "is_banned")
    private Integer banned = 0;

    @Column(name = "is_admin")
    private Integer admin = 0;

    @Column(name = "create_at")
    private Date registerTime;

    @JsonIgnore
    @OneToMany(mappedBy = "uploader")
    private Set<Resources> rescources = new HashSet<>();

    @Column(name = "avatar_url")
    private String avatarUrl;//用户头像

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Set<Resources> getRescources() {
        return rescources;
    }

    public void setRescources(Set<Resources> rescources) {
        this.rescources = rescources;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
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

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getSelfDescn() {
        return selfDescn;
    }

    public void setSelfDescn(String selfDescn) {
        this.selfDescn = selfDescn;
    }

    public Date getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(Date registerTime) {
        this.registerTime = registerTime;
    }

    public Integer getBanned() {
        return banned;
    }

    public void setBanned(Integer banned) {
        this.banned = banned;
    }

    public Integer getAdmin() {
        return admin;
    }

    public void setAdmin(Integer admin) {
        this.admin = admin;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", realname='" + realname + '\'' +
                ", selfDescn='" + selfDescn + '\'' +
                ", banned=" + banned +
                ", admin=" + admin +
                ", registerTime=" + registerTime +
                ", avatarUrl='" + avatarUrl + '\'' +
                '}';
    }
}
