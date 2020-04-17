package top.yoe.util;

public enum StatusUtil {
    SUCCESS("操作成功",1000),
    FAIL("操作失败",2000),
    USER_EXIST("用户已存在",2001),
    USER_NOT_EXIST("用户不存在",2002),
    LOGIN_FAIL("账号或者密码错误",2003),
    LOGIN_SUCCESS("登录成功",1001),
    OLDPWS_IDENTIFY_FAIL("旧密码确认失败",2004),
    NEWPWD_CONFIRM_FAIL("新密码确认失败",2005),
    MODIFY_PWD_SUCCESS("修改密码成功",1002),
    FOLLOW_SUCCESS("关注成功",1003),
    MODIFY_USERINFO_FAIL("用户信息修改失败",2006),
    MODIFY_USERINFO_SUCCESS("用户信息修改成功",1004),
    DELETE_USER_SUCCESS("删除用户成功",1005),
    DELTE_USER_FAIL("删除用户失败",2007),
    BANN_SUCCESS("禁言成功",1007),
    CANCEL_BAN("取消禁言成功",1008),
    FILE_OVER_MAXSIZE("文件大小大于20M",2008),
    COVER_OVER_MAXSIZE("截图大小大于5M",2009),
    FILE_TYPE_NOALLOW("文件格式不符合",2010),
    CATEGORY_EXIST("分类已经存在",2011),
    CATEGORY_NOT_EXIST("分类不存在",2012),


    SESSION_INVALID("用户未登录或登录态已失效，请先登录", 4001),
    POST_NOT_EXIST("帖子不存在", 4002),
    POST_COMMENT_NOT_EXIST("帖子评论不存在", 4003),
    PERMISSION_DENIED("权限不足",2013),
    RESOURCES_NOT_EXIST("资源不存在",2014),

    ADMIN_NOT_EXIST("管理员不存在",2015),

    NO_LOGIN("尚未登录",2016),
    IS_BANNED("禁言中...", 2017);


    private String status_msg;
    private int status_code;


    StatusUtil(String status_msg, int status_code) {
        this.status_msg = status_msg;
        this.status_code = status_code;
    }

    public int getStatus_code() {
        return status_code;
    }

    public void setStatus_code(int status_code) {
        this.status_code = status_code;
    }

    public String getStatus_msg() {
        return status_msg;
    }

    public void setStatus_msg(String status_msg) {
        this.status_msg = status_msg;
    }
}
