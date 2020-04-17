package top.yoe.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.*;
import top.yoe.dao.PostRepository;
import top.yoe.dao.PostsCommentRepository;
import top.yoe.dao.UserRepository;
import top.yoe.pojo.Post;
import top.yoe.pojo.PostsComment;
import top.yoe.pojo.User;
import top.yoe.util.AjaxUtil;
import top.yoe.util.AvatarUtil;
import top.yoe.util.StatusUtil;
import top.yoe.vo.PostCommentVo;
import top.yoe.vo.PostVo;

import javax.persistence.criteria.*;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.*;

/**
 * Created by hzq on 2019/12/8 at 11:14
 **/

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private PostsCommentRepository postsCommentRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * 发布帖子
     *
     * @param post
     * @param session
     * @return
     */
    @PostMapping("/publish")
    public Map<String, Object> publish(Post post, HttpSession session) {
        Integer userId = (Integer) session.getAttribute("userID");
        if (userId == null) {
            return AjaxUtil.ajax_error(StatusUtil.SESSION_INVALID);
        }

        User user = userRepository.findOne(userId);
        if (user.getBanned() == 1) {
            return AjaxUtil.ajax_error(StatusUtil.IS_BANNED);
        }
        post.setCreateAt(new Timestamp(System.currentTimeMillis()));
        post.setUpdateAt(new Timestamp(System.currentTimeMillis()));
        post.setUserId(userId);

        postRepository.save(post);
        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS, null);
    }

    /**
     * 通过id删除帖子
     *
     * @param postId
     * @return
     */
    @GetMapping("/deleteById")
    public Map<String, Object> deleteById(Integer postId) {
        Post post = postRepository.findOne(postId);
        if (post == null) {
            return AjaxUtil.ajax_error(StatusUtil.POST_NOT_EXIST);
        }

        postRepository.delete(postId);
        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS, null);
    }

    /**
     * 分页查询所有帖子
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/findAll")
    public Map<String, Object> findAllByPaging(@RequestParam Integer page, @RequestParam Integer pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "createAt");
        Pageable pageable = new PageRequest(page-1, pageSize, sort);
        Page<Post> postPage = postRepository.findAll(pageable);
        List<Post> posts = postPage.getContent();
        List<PostVo> postVos = new ArrayList<>();
        PostVo postVo = null;
        User user = null;

        for (Post post : posts) {
            postVo = new PostVo();
            postVo.setPost(post);
            user = userRepository.findOne(post.getUserId());
            postVo.setUsername(user.getUsername());
            postVo.setRealname(user.getRealname());
            postVo.setAvatarUrl(user.getAvatarUrl());
            postVos.add(postVo);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("posts", postVos);
        map.put("pageCount", postPage.getTotalPages());
        map.put("totalCount", postPage.getTotalElements());
        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS, map);
    }

    /**
     * 分页查询优质帖子
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/findAllHighQuality")
    public Map<String, Object> findAllHighQuality(@RequestParam Integer page, @RequestParam Integer pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "createAt");
        Pageable pageable = new PageRequest(page-1, pageSize, sort);
        Specification<Post> specification = new Specification<Post>() {
            @Override
            public Predicate toPredicate(Root<Post> root, CriteriaQuery<?> criteriaQuery, CriteriaBuilder criteriaBuilder) {
                Path<Object> highQuality = root.get("highQuality");
                Predicate equal = criteriaBuilder.equal(highQuality, 1);
                return equal;
            }
        };
        Page<Post> postPage = postRepository.findAll(specification, pageable);
        List<Post> posts = postPage.getContent();
        List<PostVo> postVos = new ArrayList<>();
        PostVo postVo = null;
        User user = null;

        for (Post post : posts) {
            postVo = new PostVo();
            postVo.setPost(post);
            user = userRepository.findOne(post.getUserId());
            postVo.setUsername(user.getUsername());
            postVo.setRealname(user.getRealname());
            postVo.setAvatarUrl(user.getAvatarUrl());
            postVos.add(postVo);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("posts", postVos);
        map.put("pageCount", postPage.getTotalPages());
        map.put("totalCount", postPage.getTotalElements());
        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS, map);
    }

    /**
     * 通过id查询帖子
     *
     * @param postId
     * @return
     */
    @GetMapping("/findById")
    public Map<String, Object> findById(@RequestParam Integer postId) {
        Post post = postRepository.findOne(postId);
        if (post == null) {
            return AjaxUtil.ajax_error(StatusUtil.POST_NOT_EXIST);
        }
        User user = userRepository.findOne(post.getUserId());

        Map<String, Object> map = new HashMap<>();
        map.put("post", post);
        map.put("username", user.getUsername());
        map.put("realname", user.getRealname());
        map.put("avatarUrl", user.getAvatarUrl());
        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS, map);
    }

    /**
     * 分页查询帖子下的所有评论
     *
     * @param postId
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/findAllComments")
    public Map<String, Object> findAllCommentsByPaging(@RequestParam final Integer postId,
                                                       @RequestParam Integer page,
                                                       @RequestParam Integer pageSize) {
        final Post post = postRepository.findOne(postId);
        if (post == null) {
            return AjaxUtil.ajax_error(StatusUtil.POST_NOT_EXIST);
        }

        List<PostsComment> comments = postsCommentRepository.findByPaging(postId, (page - 1) * pageSize, pageSize);
        Integer count = postsCommentRepository.countByPostId(postId);
        int totalPage = count/pageSize;
        totalPage += (count%pageSize>0) ? 1 : 0;

        List<PostCommentVo> commentVos = new ArrayList<>();
        PostCommentVo commentVo = null;
        User publisher = null;
        User receiver = null;
        for (PostsComment comment : comments) {
            commentVo = new PostCommentVo();
            publisher = userRepository.findOne(comment.getFromUid());
            commentVo.setComment(comment);
            commentVo.setPublisherUsername(publisher.getUsername());
            commentVo.setPublisherRealName(publisher.getRealname());
            commentVo.setPublisherAvatarUrl(publisher.getAvatarUrl());
            if (comment.getToUid() != null) {
                receiver = userRepository.findOne(comment.getToUid());
                commentVo.setReceiverUsername(receiver.getUsername());
                commentVo.setReceiverRealName(receiver.getRealname());
            }
            commentVos.add(commentVo);
        }

        Map<String, Object> map = new HashMap<>();
        map.put("totalPage", totalPage);
        map.put("totalCount", count);
        map.put("commentVos", commentVos);

        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS, map);
    }

    /**
     * 通过关键字查询帖子
     *
     * @param keyword
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/findByKeyword")
    public Map<String, Object> findByKeyword(@RequestParam String keyword,
                                             @RequestParam Integer page,
                                             @RequestParam Integer pageSize) {
        List<Post> posts = postRepository.findByKeyword("%" + keyword + "%", (page - 1) * pageSize, pageSize);
        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS, posts);
    }

    /**
     * 初始化用户头像，只用执行一次
     *
     * @return
     */
    @GetMapping("/initAvatar")
    public Map<String, Object> initAvatar() {
        List<User> all = userRepository.findAll();
        String uuid;
        String avatarUrl;

        for (User user : all) {
            //自动生成头像
            try {
                avatarUrl = AvatarUtil.generateImg();
//            AvatarUtil.generateImg(user.getRealname(), "D:\\learnforum\\avatar", uuid);
//            avatarUrl = "http://localhost:8081/learnforum/userAvatar/"+uuid+".png";
                user.setAvatarUrl(avatarUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            userRepository.save(user);
        }

        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS, null);
    }
}
