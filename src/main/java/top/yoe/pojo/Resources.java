package top.yoe.pojo;

import javax.persistence.*;
import java.util.Date;
@Entity
@Table(name = "rescources")
public class Resources {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;//资源唯一标识id

    @Column(name = "name")
    private String name;//资源名称

    @Column(name = "file_name")
    private String file_name;//文件名称

    @Column(name = "descn")
    private String descn;//资源描述

    @Column(name = "image")
    private String image;//资源图片

    @Column(name = "path")
    private String path;//资源存储路径

    @Column(name = "level")
    private Integer level = 1;//资源等级

    @Column(name = "size")
    private long size;//资源大小

    @Column(name = "content_type")
    private String contentType;//资源的内容类型

    @Column(name = "create_at")
    private Date uploadTime;//资源上传时间

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "uploader_id")
    private User uploader;//资源上传者

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "category_id")
    private Category category;//资源所属分类


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

    public String getFile_name() {
        return file_name;
    }

    public void setFile_name(String file_name) {
        this.file_name = file_name;
    }

    public String getDescn() {
        return descn;
    }

    public void setDescn(String descn) {
        this.descn = descn;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Integer getLevel() {
        return level;
    }

    public void setLevel(Integer level) {
        this.level = level;
    }

    public User getUploader() {
        return uploader;
    }

    public void setUploader(User uploader) {
        this.uploader = uploader;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Date getUploadTime() {
        return uploadTime;
    }

    public void setUploadTime(Date uploadTime) {
        this.uploadTime = uploadTime;
    }

    @Override
    public String toString() {
        return "Resources{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", file_name='" + file_name + '\'' +
                ", descn='" + descn + '\'' +
                ", image='" + image + '\'' +
                ", path='" + path + '\'' +
                ", level=" + level +
                ", size=" + size +
                ", contentType='" + contentType + '\'' +
                ", uploadTime=" + uploadTime +
                '}';
    }
}
