package top.yoe.web.controller;

import org.apache.commons.lang3.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.yoe.dao.PostRepository;
import top.yoe.dao.ResourcesRepository;
import top.yoe.dao.UserRepository;
import top.yoe.pojo.Post;
import top.yoe.pojo.Resources;
import top.yoe.pojo.User;
import top.yoe.util.*;
import top.yoe.vo.PostVo;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/admin")
public class AdminController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ResourcesRepository resourcesRepository;

    @Autowired
    private PostRepository postRepository;


    @Value("${itcast-fastdfs.upload_location}")
    private String upload_location;

    /**
     * 管理员登录
     * @param user
     * @param session
     * @return
     */
    @PostMapping("/login")
    public Map<String,Object> login(User user, HttpSession session){
        User aUser = userRepository.findByUsername(user.getUsername());
        if(aUser == null){
            return AjaxUtil.ajax_error(StatusUtil.USER_NOT_EXIST);
        }
        if(!aUser.getPassword().equals(MD5Util.MD5Encode(user.getPassword(),null))){
            return AjaxUtil.ajax_error(StatusUtil.LOGIN_FAIL);
        }
        if(aUser.getAdmin() == 0){
            return AjaxUtil.ajax_error(StatusUtil.PERMISSION_DENIED);
        }
        session.setAttribute("adminID",aUser.getId());
        return AjaxUtil.ajax_ok(StatusUtil.LOGIN_SUCCESS,aUser);
    }

    /**
     * 管理员登出
     * @param session
     * @return
     */
    @GetMapping("/logout")
    public Map<String,Object> logout(HttpSession session){
        session.invalidate();
        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS,null);
    }


    /**
     * 管理员单次添加用户
     * @param user
     * @return
     */
    @PostMapping("/addUser")
    public Map<String, Object> addUser(User user){
        user.setRegisterTime(new Date());
        User aUser = userRepository.findByUsername(user.getUsername());//查询学号
        if(aUser != null){
            return AjaxUtil.ajax_error(StatusUtil.USER_EXIST);
        }

        String avatarUrl = null;
        //自动生成头像
        try {

            avatarUrl = AvatarUtil.generateImg();
//            AvatarUtil.generateImg(user.getRealname(), "D:\\learnforum\\avatar", uuid);
//            avatarUrl = "http://localhost:8081/learnforum/userAvatar/"+uuid+".png";
            user.setAvatarUrl(avatarUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        user.setPassword(MD5Util.MD5Encode(user.getPassword(),null));
        User save = userRepository.save(user);
        System.out.println(save);
        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS,null);
    }

    /**
     * 通过名字或者学号查找用户，需要做分页功能,默认查询全部用户
     * @param name
     * @return
     */
    @GetMapping("/findAllUser")
    public Map<String,Object> findAllUser(@RequestParam(name = "currentPage",defaultValue = "1",required = false)Integer currentPage,
                                                           @RequestParam(name = "pageSize")Integer pageSize,
                                                           @RequestParam(name = "name",required = false) String name){
        Map<String,Object> page_msg = new HashMap<>();
        Sort sort = new Sort(Sort.Direction.ASC, "registerTime");
        Pageable pageable = new PageRequest(currentPage - 1, pageSize, sort);
        long totalCount = 0L;
        List<User> all = null;
        if(name != null){
            all = userRepository.findByUsernameOrRealname(name, name,pageable).getContent();
            totalCount = userRepository.countUserByUsernameOrRealname(name,name);
        }else {
            all = userRepository.findAll(pageable).getContent();
            totalCount = userRepository.count();
        }
        if(all == null){
            return AjaxUtil.ajax_error(StatusUtil.USER_NOT_EXIST);
        }
        for (User user : all) {
            user.setPassword(null);
        }
        page_msg.put("totalCount",totalCount);
        page_msg.put("data",all);

        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS,page_msg);
    }

    /**
     * 通过ID查找用户
     * @param userID
     * @return
     */
    @GetMapping("/findUserById")
    public Map<String,Object> findUserById(@RequestParam(name = "userID")Integer userID){
        User user = userRepository.findOne(userID);
        if(user == null){
            return AjaxUtil.ajax_error(StatusUtil.USER_NOT_EXIST);
        }
        user.setPassword(null);
        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS,user);
    }


    /**
     * 管理员修改用户信息
     * @param user
     * @return
     */
    @Transactional
    @PostMapping("/modifyUser")
    public Map<String,Object> modifyUserBy(User user){
        User one = userRepository.findOne(user.getId());
        if(one == null){
            return AjaxUtil.ajax_error(StatusUtil.USER_NOT_EXIST);
        }
        userRepository.modifyUserRealNameAndSelfDescn(user.getRealname(), user.getSelfDescn(), user.getId());
        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS,null);
    }

    /**
     * 管理员通过ID删除用户
     * @param userID
     * @return
     */
    @GetMapping("/deleteUser")
    public Map<String,Object> deleteUserById(@RequestParam(name = "userID")Integer userID){
        User user = userRepository.findOne(userID);
        if(user == null){
            return AjaxUtil.ajax_error(StatusUtil.USER_NOT_EXIST);
        }
        userRepository.delete(userID);
        return AjaxUtil.ajax_ok(StatusUtil.DELETE_USER_SUCCESS,null);
    }

    /**
     * 管理员对用户进行禁言
     * @param userID
     * @return
     */
    @GetMapping("/cancelOrBanUser")
    @Transactional
    public Map<String,Object> banUserById(@RequestParam(name = "userID")Integer userID){
        User user = userRepository.findOne(userID);
        if(user == null){
            return AjaxUtil.ajax_error(StatusUtil.USER_NOT_EXIST);
        }
        //禁言
        if(user.getBanned() == 0){
            userRepository.banUserById(userID);
            return AjaxUtil.ajax_ok(StatusUtil.BANN_SUCCESS,null);
        }
        //取消禁言
        userRepository.cancelBanUserById(userID);
        return AjaxUtil.ajax_ok(StatusUtil.CANCEL_BAN,null);
    }


    /**
     * 资源评级
     * @param id
     * @param level
     * @return
     */
    @GetMapping("/resourcesRate")
    public Map<String,Object> resourcesRating(@RequestParam(name = "resourcesid")Integer id,@RequestParam(name = "level")Integer level){
        Resources resource = resourcesRepository.findOne(id);
        if(resource == null){
            return AjaxUtil.ajax_error(StatusUtil.RESOURCES_NOT_EXIST);
        }
        resource.setLevel(level);
        resourcesRepository.save(resource);
        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS,null);
    }




    /**
     * 分页查询所有帖子
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/findAllPost")
    public Map<String, Object> findAllPost(@RequestParam Integer page, @RequestParam Integer pageSize) {
        Sort sort = new Sort(Sort.Direction.DESC, "createAt");
        Pageable pageable = new PageRequest(page-1, pageSize, sort);
        Page<Post> postPage = postRepository.findAll(pageable);

        Map<String, Object> map = new HashMap<>();
        map.put("posts", postPage.getContent());
        map.put("pageCount", postPage.getTotalPages());
        map.put("totalCount", postPage.getTotalElements());
        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS, map);
    }

    /**
     * 通过id删除帖子
     *
     * @param postId
     * @return
     */
    @GetMapping("/deletePostById")
    public Map<String, Object> deleteById(Integer postId) {
        Post post = postRepository.findOne(postId);
        if (post == null) {
            return AjaxUtil.ajax_error(StatusUtil.POST_NOT_EXIST);
        }

        postRepository.delete(postId);
        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS, null);
    }

    /**
     * 根据ID查询管理员
     * @param id
     * @return
     */
    @GetMapping("/findAdminById")
    public Map<String,Object> findAdminById(@RequestParam(name = "id")Integer id){
        User admin = userRepository.findOne(id);
        if(admin == null || admin.getAdmin() == 0){
            return AjaxUtil.ajax_error(StatusUtil.ADMIN_NOT_EXIST);
        }
        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS,admin);
    }


    /**
     * 查询所有管理员或者通过名字查询管理员
     * @param currentPage
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/findAllAdmin")
    public Map<String,Object> findAllAdmin(@RequestParam(name = "currentPage",defaultValue = "1",required = false)Integer currentPage,
                                           @RequestParam(name = "pageSize")Integer pageSize,
                                           @RequestParam(name = "name",required = false) String name) {
        Map<String, Object> page_msg = new HashMap<>();
        Sort sort = new Sort(Sort.Direction.DESC, "registerTime");
        Pageable pageable = new PageRequest(currentPage - 1, pageSize, sort);
        Long totalCount = 0L;
        List<User> admins = null;
        //不带名字查询
        if(name == null){
            admins = userRepository.findByAdmin(1, pageable).getContent();
            totalCount = userRepository.countUserByAdmin(1);
        }else {
            //带名字查询
            admins = userRepository.findByRealnameAndAdmin(name, 1, pageable).getContent();
            totalCount = userRepository.countUserByRealnameAndAdmin(name,1);
        }
        if(admins == null){
            return AjaxUtil.ajax_error(StatusUtil.ADMIN_NOT_EXIST);
        }
        for (User admin : admins) {
            admin.setPassword(null);
        }
        page_msg.put("totalCount",totalCount);
        page_msg.put("data",admins);
        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS, page_msg);
    }

    /**
     * 添加管理员
     * @param user
     * @return
     */
    @PostMapping("/addAdmin")
    public Map<String,Object> addAdmin(User user){
        if(user == null){
            return AjaxUtil.ajax_error(StatusUtil.FAIL);
        }
        User auser = userRepository.findByUsername(user.getUsername());
        if(auser != null){
            return AjaxUtil.ajax_error(StatusUtil.USER_EXIST);
        }

        String avatarUrl = null;
        //自动生成头像
        try {
            avatarUrl = AvatarUtil.generateImg();
//            AvatarUtil.generateImg(user.getRealname(), "D:\\learnforum\\avatar", uuid);
//            avatarUrl = "http://localhost:8081/learnforum/userAvatar/"+uuid+".png";
            user.setAvatarUrl(avatarUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        user.setPassword(MD5Util.MD5Encode(user.getPassword(),null));
        user.setAdmin(1);
        user.setRegisterTime(new Date());
        userRepository.save(user);
        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS,null);
    }

    /**
     * 设置是否为优质帖子
     *
     * @param postId
     * @return
     */
    @GetMapping("/setHighQuality")
    public Map<String, Object> setHighQuality(@RequestParam Integer postId) {
        Post post = postRepository.findOne(postId);
        if (post == null) {
            return AjaxUtil.ajax_error(StatusUtil.POST_NOT_EXIST);
        }

        if (post.getHighQuality() == 1) {
            post.setHighQuality(0);
        } else {
            post.setHighQuality(1);
        }
        postRepository.save(post);
        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS, null);
    }

    /**
     * 通过关键字查询帖子
     *
     * @param keyword
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/findPostsByKeyword")
    public Map<String, Object> findByKeyword(@RequestParam String keyword,
                                             @RequestParam Integer page,
                                             @RequestParam Integer pageSize) {
        List<Post> posts = postRepository.findByKeyword("%" + keyword + "%", (page - 1) * pageSize, pageSize);
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
        map.put("pageCount", posts.size()/pageSize + posts.size()%pageSize>0?1:0);
        map.put("totalCount", posts.size());
        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS, map);
    }

}
