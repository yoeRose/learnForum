package top.yoe.util;

import java.util.HashMap;
import java.util.Map;

public class AjaxUtil {
    public static Map<String,Object> ajax_ok(StatusUtil StatusDesc,Object data){
        Map<String,Object> ok_msg = new HashMap<>();
        ok_msg.put("data",data);
        ok_msg.put("msg",StatusDesc.getStatus_msg());
        ok_msg.put("status_code",StatusDesc.getStatus_code());
        ok_msg.put("status","success");
        return ok_msg;
    }

    public static Map<String,Object> ajax_error(StatusUtil StatusDesc){
        Map<String,Object> err_msg = new HashMap<>();
        err_msg.put("msg",StatusDesc.getStatus_msg());
        err_msg.put("status_code",StatusDesc.getStatus_code());
        err_msg.put("status","fail");
        return err_msg;
    }
}
