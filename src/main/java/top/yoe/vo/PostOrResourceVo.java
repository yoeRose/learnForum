package top.yoe.vo;

import top.yoe.pojo.Post;
import top.yoe.pojo.Resources;

/**
 * Created by hzq on 2019/12/16 at 16:45
 **/

public class PostOrResourceVo implements Comparable<PostOrResourceVo> {
    public static Integer POST = 0;
    public static Integer RESOURCE = 1;

    private Integer type;//类型，0为帖子，1为资源
    private Post post;
    private Resources resource;

    @Override
    public String toString() {
        return "PostOrResourceVo{" +
                "type=" + type +
                ", post=" + post +
                ", resource=" + resource +
                '}';
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public Resources getResource() {
        return resource;
    }

    public void setResource(Resources resource) {
        this.resource = resource;
    }

    @Override
    public int compareTo(PostOrResourceVo that) {
        int result = 0;
        if (this.getType() == POST) {
            if (that.getType() == POST) {
                result = this.getPost().getCreateAt().compareTo(that.getPost().getCreateAt());
            } else if (that.getType() == RESOURCE) {
                result = this.getPost().getCreateAt().compareTo(that.getResource().getUploadTime());
            }
        } else if (this.getType() == RESOURCE) {
            if (that.getType() == POST) {
                result = this.getResource().getUploadTime().compareTo(that.getPost().getCreateAt());
            } else if (that.getType() == RESOURCE) {
                result = this.getResource().getUploadTime().compareTo(that.getResource().getUploadTime());
            }
        }
        return -result;
    }
}
