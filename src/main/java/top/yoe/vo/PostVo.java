package top.yoe.vo;

import top.yoe.pojo.Post;

/**
 * Created by hzq on 2019/12/8 at 13:38
 **/

public class PostVo {
    private Post post;
    private String username;
    private String realname;
    private String avatarUrl;

    @Override
    public String toString() {
        return "PostVo{" +
                "post=" + post +
                ", username='" + username + '\'' +
                ", realname='" + realname + '\'' +
                ", avatarUrl='" + avatarUrl + '\'' +
                '}';
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }
}
