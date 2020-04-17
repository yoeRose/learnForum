package top.yoe.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import top.yoe.dao.PostRepository;
import top.yoe.dao.PostsCommentRepository;
import top.yoe.dao.UserRepository;
import top.yoe.pojo.Post;
import top.yoe.pojo.PostsComment;
import top.yoe.pojo.User;
import top.yoe.util.AjaxUtil;
import top.yoe.util.StatusUtil;
import javax.servlet.http.HttpSession;
import java.sql.Timestamp;
import java.util.Map;

/**
 * Created by hzq on 2019/12/8 at 14:46
 **/

@RestController
@RequestMapping("/postsComment")
public class PostsCommentController {
    @Autowired
    private PostsCommentRepository postsCommentRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;

    /**
     * 发表评论
     *
     * @param content
     * @param postId
     * @param session
     * @return
     */
    @PostMapping("/publish")
    public Map<String, Object> publish(@RequestParam String content,
                                       @RequestParam Integer postId,
                                       @RequestParam(defaultValue = "-1") Integer toUid,
                                       @RequestParam(defaultValue = "-1") Integer toCommentId,
                                       HttpSession session) {
        Integer fromUid = (Integer) session.getAttribute("userID");
        Post post = postRepository.findOne(postId);
        if (fromUid == null) {
            return AjaxUtil.ajax_error(StatusUtil.SESSION_INVALID);//未登录
        }
        if (post == null) {
            return AjaxUtil.ajax_error(StatusUtil.POST_NOT_EXIST);
        }
        User user = userRepository.findOne(fromUid);
        if (user.getBanned() == 1) {
            return AjaxUtil.ajax_error(StatusUtil.IS_BANNED);
        }

        PostsComment comment = new PostsComment();
        comment.setContent(content);
        comment.setFromUid(fromUid);
        comment.setCreateAt(new Timestamp(System.currentTimeMillis()));
        comment.setPost(post);
        if (toUid==-1 && toCommentId==-1) {
            comment.setToUid(null);
            comment.setToCommentId(null);
        } else {
            comment.setToUid(toUid);
            comment.setToCommentId(toCommentId);
        }
        post.setCommentCount(post.getCommentCount()+1);
        post.setUpdateAt(new Timestamp(System.currentTimeMillis()));

        postsCommentRepository.save(comment);
        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS, null);
    }

    /**
     * 通过评论id删除评论
     *
     * @param commentId
     * @return
     */
    @GetMapping("/deleteById")
    public Map<String, Object> deleteById(@RequestParam Integer commentId) {
        PostsComment comment = postsCommentRepository.findOne(commentId);
        if (comment == null) {
            return AjaxUtil.ajax_error(StatusUtil.POST_COMMENT_NOT_EXIST);
        }

        Post post = comment.getPost();
        post.setCommentCount(post.getCommentCount() - 1);
        postRepository.save(post);
        postsCommentRepository.delete(commentId);
        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS, null);
    }
}
