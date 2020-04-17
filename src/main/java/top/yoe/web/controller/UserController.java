package top.yoe.web.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import top.yoe.dao.PostRepository;
import top.yoe.dao.ResourcesRepository;
import top.yoe.dao.UserRepository;
import top.yoe.pojo.Post;
import top.yoe.pojo.Resources;
import top.yoe.pojo.User;
import top.yoe.util.AjaxUtil;
import top.yoe.util.AvatarUtil;
import top.yoe.util.MD5Util;
import top.yoe.util.StatusUtil;
import top.yoe.vo.PostOrResourceVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;


@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private ResourcesRepository resourcesRepository;

    /**
     * 用户注册
     * @param user
     * @return
     */
    //@RequestMapping(value = "/register",method = RequestMethod.POST)
    @PostMapping("/register")
    public Map<String, Object> register(User user){
        user.setRegisterTime(new Date());
        User aUser = userRepository.findByUsername(user.getUsername());//查询学号
        if(aUser != null){
            return AjaxUtil.ajax_error(StatusUtil.USER_EXIST);
        }
        user.setPassword(MD5Util.MD5Encode(user.getPassword(),null));
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
        User save = userRepository.save(user);
        System.out.println(save);
        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS,null);
    }

    /**
     * 用户登录
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
        session.setAttribute("userID",aUser.getId());
        return AjaxUtil.ajax_ok(StatusUtil.LOGIN_SUCCESS,aUser);
    }

    /**
     * 用户登出
     * @param session
     * @return
     */
    @GetMapping("/logout")
    public Map<String,Object> logout(HttpSession session){
        session.invalidate();
        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS,null);
    }

    /**
     * 通过名字或者学号查找用户，需要做分页功能
     * @param name
     * @return
     */
    @GetMapping("/findUser")
    public Map<String,Object> findUserByUsernameOrRealName(@RequestParam(name = "currentPage",defaultValue = "1",required = false)Integer currentPage,
                                                           @RequestParam(name = "pageSize")Integer pageSize,
                                                           @RequestParam(name = "name") String name){
        Map<String,Object> page_msg = new HashMap<>();
        Sort sort = new Sort(Sort.Direction.ASC, "registerTime");
        Pageable pageable = new PageRequest(currentPage - 1, pageSize, sort);
        List<User> all = userRepository.findByUsernameOrRealname(name, name,pageable).getContent();
        if(all == null){
            return AjaxUtil.ajax_error(StatusUtil.USER_NOT_EXIST);
        }
        for (User user : all) {
            user.setPassword(null);
        }
        long totalCount = userRepository.countUserByUsernameOrRealname(name,name);
        page_msg.put("totalCount",totalCount);
        page_msg.put("data",all);

        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS,page_msg);
    }

    /**
     * 用户修改密码
     * @param request
     * @param session
     * @return
     */
    @PostMapping("/modifyPassword")
    public Map<String,Object> modifyPassword(HttpServletRequest request,HttpSession session){
        String oldPwd = request.getParameter("oldPwd");//表单输入的旧密码
        String newPwd = request.getParameter("newPwd");//表单输入的新密码
        String confirmNewPwd = request.getParameter("confirmNewPwd");//输入的新密码确认
        Integer userID = (Integer) session.getAttribute("userID");
        User user = userRepository.findOne(userID);

        if(!user.getPassword().equals(MD5Util.MD5Encode(oldPwd,null))){
            return AjaxUtil.ajax_error(StatusUtil.OLDPWS_IDENTIFY_FAIL);
        }
        if(!newPwd.equals(confirmNewPwd)){
            return AjaxUtil.ajax_error(StatusUtil.NEWPWD_CONFIRM_FAIL);
        }
        String md5Pwd = MD5Util.MD5Encode(newPwd,null);
        user.setPassword(md5Pwd);
        userRepository.save(user);
        session.invalidate();

        return AjaxUtil.ajax_ok(StatusUtil.MODIFY_PWD_SUCCESS,null);
    }

    /**
     * 关注用户
     * @param followID 被关注用户的id
     * @param session
     * @return
     */
    @GetMapping("/follow")
    public Map<String,Object> follow(@RequestParam(name = "followID")Integer followID,HttpSession session){
        Integer userID = (Integer) session.getAttribute("userID");
        User follow = userRepository.findOne(followID);

        if(follow == null){
            return AjaxUtil.ajax_error(StatusUtil.USER_NOT_EXIST);
        }
        userRepository.follow(userID,followID);
        return AjaxUtil.ajax_ok(StatusUtil.FOLLOW_SUCCESS,null);
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
     * 修改用户信息
     * @param user
     * @return
     */
    @PostMapping("/modifyUser")
    @Transactional
    public Map<String,Object> modifyUser(User user){
        User auser = userRepository.findOne(user.getId());
        if(auser == null){
            return AjaxUtil.ajax_error(StatusUtil.USER_NOT_EXIST);
        }
        Integer updateCount = userRepository.modifyUser(user.getSelfDescn(), user.getId());
        if(updateCount == null){
            return AjaxUtil.ajax_error(StatusUtil.MODIFY_USERINFO_FAIL);
        }
        return AjaxUtil.ajax_ok(StatusUtil.MODIFY_USERINFO_SUCCESS,null);
    }

    /**
     * 查询用户的活动记录，包括发帖子、上传资源
     *
     * @param session
     * @return
     */
    @GetMapping("/activityHistory")
    public Map<String, Object> findActivityHistory(HttpSession session) {
        if (session.getAttribute("userID") == null) {
            return AjaxUtil.ajax_error(StatusUtil.SESSION_INVALID);
        }
        int userId = (int) session.getAttribute("userID");
        User user = userRepository.findOne(userId);
        if (user == null) {
            return AjaxUtil.ajax_error(StatusUtil.SESSION_INVALID);
        }

        List<Post> postList = postRepository.findByUserId(user.getId());
        List<Resources> resourcesList = resourcesRepository.findByUploader(user);
        List<PostOrResourceVo> postAndResourceList = new ArrayList<>();
        PostOrResourceVo postOrResourceVo = null;
        for (Post post : postList) {
            postOrResourceVo = new PostOrResourceVo();
            postOrResourceVo.setType(PostOrResourceVo.POST);
            postOrResourceVo.setPost(post);
            postAndResourceList.add(postOrResourceVo);
        }
        for (Resources resource : resourcesList) {
            postOrResourceVo = new PostOrResourceVo();
            postOrResourceVo.setType(PostOrResourceVo.RESOURCE);
            postOrResourceVo.setResource(resource);
            postAndResourceList.add(postOrResourceVo);
        }

        Collections.sort(postAndResourceList);

        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS, postAndResourceList);
    }
}
