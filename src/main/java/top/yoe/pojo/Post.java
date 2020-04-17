package top.yoe.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by hzq on 2019/12/8 at 10:58
 **/

@Entity
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "user_id")
    private Integer userId;
    @Column(name = "topic")
    private String topic;
    @Column(name = "content")
    private String content;
    @Column(name = "comment_count")
    private Integer commentCount = 0;
    @Column(name = "is_valid")
    private Integer isValid = 1;
    @Column(name = "high_quality")
    private Integer highQuality = 0;
    @Column(name = "create_at")
    private Timestamp createAt;
    @Column(name = "update_at")
    private Timestamp updateAt;

    //配置一对多关系
    @JsonIgnore//转json时忽略该属性
    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL)
    private Set<PostsComment> comments = new HashSet<>();

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", userId=" + userId +
                ", topic='" + topic + '\'' +
                ", content='" + content + '\'' +
                ", commentCount=" + commentCount +
                ", isValid=" + isValid +
                ", highQuality=" + highQuality +
                ", createAt=" + createAt +
                ", updateAt=" + updateAt +
                ", comments=" + comments +
                '}';
    }

    public Integer getHighQuality() {
        return highQuality;
    }

    public void setHighQuality(Integer highQuality) {
        this.highQuality = highQuality;
    }

    public Set<PostsComment> getComments() {
        return comments;
    }

    public void setComments(Set<PostsComment> comments) {
        this.comments = comments;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Integer getIsValid() {
        return isValid;
    }

    public void setIsValid(Integer isValid) {
        this.isValid = isValid;
    }

    public Timestamp getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Timestamp createAt) {
        this.createAt = createAt;
    }

    public Timestamp getUpdateAt() {
        return updateAt;
    }

    public void setUpdateAt(Timestamp updateAt) {
        this.updateAt = updateAt;
    }
}
