package top.yoe.vo;

import top.yoe.pojo.PostsComment;

/**
 * Created by hzq on 2019/12/15 at 17:58
 **/

public class PostCommentVo {
    private PostsComment comment;
    private String publisherRealName;
    private String publisherUsername;
    private String publisherAvatarUrl;
    private String receiverRealName;
    private String receiverUsername;

    @Override
    public String toString() {
        return "PostCommentVo{" +
                "comment=" + comment +
                ", publisherRealName='" + publisherRealName + '\'' +
                ", publisherUsername='" + publisherUsername + '\'' +
                ", publisherAvatarUrl='" + publisherAvatarUrl + '\'' +
                ", receiverRealName='" + receiverRealName + '\'' +
                ", receiverUsername='" + receiverUsername + '\'' +
                '}';
    }

    public String getPublisherAvatarUrl() {
        return publisherAvatarUrl;
    }

    public void setPublisherAvatarUrl(String publisherAvatarUrl) {
        this.publisherAvatarUrl = publisherAvatarUrl;
    }

    public PostsComment getComment() {
        return comment;
    }

    public void setComment(PostsComment comment) {
        this.comment = comment;
    }

    public String getPublisherRealName() {
        return publisherRealName;
    }

    public void setPublisherRealName(String publisherRealName) {
        this.publisherRealName = publisherRealName;
    }

    public String getPublisherUsername() {
        return publisherUsername;
    }

    public void setPublisherUsername(String publisherUsername) {
        this.publisherUsername = publisherUsername;
    }

    public String getReceiverRealName() {
        return receiverRealName;
    }

    public void setReceiverRealName(String receiverRealName) {
        this.receiverRealName = receiverRealName;
    }

    public String getReceiverUsername() {
        return receiverUsername;
    }

    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }
}
