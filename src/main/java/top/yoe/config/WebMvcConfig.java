package top.yoe.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;


/**
 * 配置头像路径
 *
 * Created by hzq on 2019/8/10 at 14:21
 **/

@Configuration
public class WebMvcConfig extends WebMvcConfigurerAdapter {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/userAvatar/**").addResourceLocations("file:/home/yoe/upload/avatar/");//配置头像访问虚拟路径
//        registry.addResourceHandler("/userAvatar/**").addResourceLocations("file:D:\\learnforum\\avatar\\");//配置头像访问虚拟路径
        super.addResourceHandlers(registry);
    }
}
