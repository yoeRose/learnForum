package top.yoe.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;//资源类别唯一标识id

    @Column(name = "name")
    private String name;//资源类别名称

    @Column(name = "descn")
    private String descn;//资源描述

    @JsonIgnore
    @OneToMany(mappedBy = "category")//设置category和resources的映射关系：一对多
    private Set<Resources> resources = new HashSet<>();

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescn() {
        return descn;
    }

    public void setDescn(String descn) {
        this.descn = descn;
    }

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", descn='" + descn + '\'' +
                '}';
    }
}
