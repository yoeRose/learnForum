package top.yoe.pojo;

import javafx.geometry.Pos;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * Created by hzq on 2019/12/8 at 14:32
 **/

@Entity
@Table(name = "posts_comment")
public class PostsComment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;
    @Column(name = "content")
    private String content;
    @Column(name = "from_uid")
    private Integer fromUid;
    @Column(name = "to_uid")
    private Integer toUid;
    @Column(name = "to_comment_id")
    private Integer toCommentId;
    @Column(name = "create_at")
    private Timestamp createAt;

    //配置多对一关系
    @ManyToOne(targetEntity = Post.class)
    @JoinColumn(name = "post_id", referencedColumnName = "id")
    private Post post;

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Integer getFromUid() {
        return fromUid;
    }

    public void setFromUid(Integer fromUid) {
        this.fromUid = fromUid;
    }

    public Integer getToUid() {
        return toUid;
    }

    public void setToUid(Integer toUid) {
        this.toUid = toUid;
    }

    public Timestamp getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Timestamp createAt) {
        this.createAt = createAt;
    }

    @Override
    public String toString() {
        return "PostsComment{" +
                "id=" + id +
                ", content='" + content + '\'' +
                ", fromUid=" + fromUid +
                ", toUid=" + toUid +
                ", toCommentId=" + toCommentId +
                ", createAt=" + createAt +
                ", post=" + post +
                '}';
    }

    public Integer getToCommentId() {
        return toCommentId;
    }

    public void setToCommentId(Integer toCommentId) {
        this.toCommentId = toCommentId;
    }
}
