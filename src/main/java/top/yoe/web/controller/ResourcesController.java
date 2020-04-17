package top.yoe.web.controller;

import org.apache.commons.lang3.ArrayUtils;
import org.csource.common.NameValuePair;
import org.csource.fastdfs.*;
import org.omg.CORBA.OBJ_ADAPTER;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import top.yoe.dao.CategoryRepository;
import top.yoe.dao.ResourcesRepository;
import top.yoe.dao.UserRepository;
import top.yoe.pojo.Category;
import top.yoe.pojo.Resources;
import top.yoe.pojo.User;
import top.yoe.util.*;

import javax.persistence.criteria.CriteriaBuilder;
import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

@RestController
@RequestMapping("/resources")
public class ResourcesController {
    private final long FILEMAXSIZE = 20*1024*1024;
    private final long COVERMAXSIZE  = 5*1024*1024;
    private String[] allowType = {".rar",".7z",".zip"};
    private String[] allowTypeExcel = {".xls",".xlsx"};
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ResourcesRepository resourcesRepository;

    @Value("${itcast-fastdfs.upload_location}")
    private String upload_location;

    /**
     * 1.客户端上传文件后，先保存在服务器端的一个临时目录
     * 2.再把临时目录的文件通过fastdfs服务传输到storage机器
     * @param file
     * @return
     */
    /**
     * 上传运行截图
     * @param file
     * @return
     */
    @PostMapping("/uploadCover")
    public Map<String,Object> uploadCover(@RequestParam(name = "cover") MultipartFile file){
        Map<String,Object> cover_msg = new HashMap<>();
        try {
            //获取上传文件的大小
            long size = file.getSize();

            if(size > COVERMAXSIZE){
                return AjaxUtil.ajax_error(StatusUtil.COVER_OVER_MAXSIZE);
            }

            //获取上传文件的文件类型
            String contentType = file.getContentType();

            //获取元文件名
            String originalFilename = file.getOriginalFilename();
            //获取扩展名
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));// .xxx

//            if(!ArrayUtils.contains(allowType,extension)){
//                return AjaxUtil.ajax_error(StatusUtil.FILE_TYPE_NOALLOW);
//            }

            //新文件名
            String newFileName = UUID.randomUUID().toString()+extension;

            //创建临时目录的新路径
            File newFile = new File(upload_location + newFileName);

            //上传的文件流写入临时目录
            file.transferTo(newFile);
            //获取临时目录的文件路径
            String local_filename = newFile.getAbsolutePath();


            /*做到上面这一步，就是正常的上传文件流程，而在分布式的系统中，文件上传到tracker服务器的临时目录后，
                需要通过tracker把文件传输到storage服务器，然后把临时目录的文件给删除
             */

            //读取配置文件
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            System.out.println("network_timeout=" + ClientGlobal.g_network_timeout + "ms");
            System.out.println("charset=" + ClientGlobal.g_charset);

            //创建tracker客户端
            TrackerClient tracker = new TrackerClient();
            //获取tracker服务端
            TrackerServer trackerServer = tracker.getConnection();

            StorageServer storageServer = null;
            //创建storage客户端
            StorageClient1 client = new StorageClient1(trackerServer, storageServer);
            //文件元信息
            NameValuePair[] metaList = new NameValuePair[1];

            metaList[0] = new NameValuePair("fileName",originalFilename);

            //从tracker服务器的临时目录上传到storage服务器
            String filePath = client.upload_file1(local_filename,null,metaList);
            System.out.println("upload success. file id is: " + filePath);
            //fileId：group1/M00/00/00/rBJ9t13rpHGAFWe_AABSMd999mE487.jpg

            System.out.println(newFile);
            //删除掉临时目录的文件
            newFile.delete();
            trackerServer.close();
            cover_msg.put("cover_path",filePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS,cover_msg);
    }

    /**
     * 上传资源文件
     * @param file
     * @return
     */
    @PostMapping("/uploadFile")
    public Map<String,Object> uploadFile(@RequestParam("filezip") MultipartFile file){
        Map<String,Object> file_msg = new HashMap<>();
        try {
            //获取上传文件的大小
            long size = file.getSize();

            if(size > FILEMAXSIZE){
                return AjaxUtil.ajax_error(StatusUtil.FILE_OVER_MAXSIZE);
            }

            //获取上传文件的文件类型
            String contentType = file.getContentType();

            //获取元文件名
            String originalFilename = file.getOriginalFilename();
            //获取扩展名
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));

            if(!ArrayUtils.contains(allowType,extension)){
                return AjaxUtil.ajax_error(StatusUtil.FILE_TYPE_NOALLOW);
            }

            //新文件名
            String newFileName = UUID.randomUUID().toString()+extension;

            //创建临时目录的新路径
            File newFile = new File(upload_location + newFileName);

            //上传的文件流写入临时目录
            file.transferTo(newFile);
            //获取临时目录的文件路径
            String local_filename = newFile.getAbsolutePath();


            /*做到上面这一步，就是正常的上传文件流程，而在分布式的系统中，文件上传到tracker服务器的临时目录后，
                需要通过tracker把文件传输到storage服务器，然后把临时目录的文件给删除
             */

            //读取配置文件
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            System.out.println("network_timeout=" + ClientGlobal.g_network_timeout + "ms");
            System.out.println("charset=" + ClientGlobal.g_charset);

            //创建tracker客户端
            TrackerClient tracker = new TrackerClient();
            //获取tracker服务端
            TrackerServer trackerServer = tracker.getConnection();

            StorageServer storageServer = null;
            //创建storage客户端
            StorageClient1 client = new StorageClient1(trackerServer, storageServer);
            //文件元信息
            NameValuePair[] metaList = new NameValuePair[1];

            metaList[0] = new NameValuePair("fileName",originalFilename);

            //从tracker服务器的临时目录上传到storage服务器
            String file_path = client.upload_file1(local_filename,null,metaList);
            System.out.println("upload success. file id is: " + file_path);

            //fileId：group1/M00/00/00/rBJ9t13rpHGAFWe_AABSMd999mE487.jpg
            System.out.println(newFile);


            //删除掉临时目录的文件
            //newFile.deleteOnExit();
            newFile.delete();
            trackerServer.close();
            file_msg.put("file_size",size);
            file_msg.put("content_type",contentType);
            file_msg.put("file_path",file_path);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }catch (IOException e){
            e.printStackTrace();
        }catch (Exception e){
            e.printStackTrace();
        }
        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS,file_msg);
    }

    /**
     * 封装resources数据,并上传
     * @param
     * @param
     * @return
     */
    @PostMapping("/uploadResources")
    public Map<String,Object> uploadResources(@RequestParam(name = "category_id")Integer categoryId,
                                              Resources resource,HttpSession session){

        Integer userID = (Integer) session.getAttribute("userID");
        if(userID == null){
            return AjaxUtil.ajax_error(StatusUtil.NO_LOGIN);
        }

        Category category = categoryRepository.findOne(categoryId);
        if(category == null){
            return AjaxUtil.ajax_error(StatusUtil.CATEGORY_NOT_EXIST);
        }

        User user = userRepository.findOne(userID);

        resource.setUploader(user);
        resource.setCategory(category);
        resource.setUploadTime(new Date());

        resourcesRepository.save(resource);

        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS,resource);
    }

    /**
     * 下载资源
     * @param request
     * @param response
     * @return
     * @throws IOException
     */
    @RequestMapping("/download")
    public Map<String,Object> downloadFile(HttpServletRequest request, HttpServletResponse response) throws IOException{

        response.setCharacterEncoding("utf-8");
        response.setContentType("application/octet-stream");

        Integer id = Integer.parseInt(request.getParameter("id"));

        Resources resource = resourcesRepository.findOne(id);//查询resources资源


        String file_name = resource.getFile_name();//获取文件的名字


        String mimeType = resource.getContentType();//获取文件的MIME类型

        ServletOutputStream outputStream = null;
        try {

            response.setHeader("content-type",mimeType);
            response.setHeader("content-disposition","attachment;filename="+ URLEncoder.encode(file_name,"UTF-8"));

            //链接dfs
            ClientGlobal.initByProperties("config/fastdfs-client.properties");
            System.out.println("network_timeout=" + ClientGlobal.g_network_timeout + "ms");
            System.out.println("charset=" + ClientGlobal.g_charset);

            //创建tracker客户端
            TrackerClient tracker = new TrackerClient();

            //连接tracker
            TrackerServer trackerServer = tracker.getConnection();

            StorageServer storageServer = null;
            //创建storage客户端
            StorageClient1 client = new StorageClient1(trackerServer, storageServer);

            //获取服务器的文件
            byte[] bytes = client.download_file1(resource.getPath());

            //写文件
            outputStream = response.getOutputStream();
            outputStream.write(bytes);

            //关闭tracker服务器
            trackerServer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            //关闭资源
            if(outputStream != null){
                outputStream.close();
            }
        }
        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS,null);
    }


    /**
     * 查询所有资源
     * 通过名字查询资源
     * @param currentPage
     * @param pageSize
     * @param name
     * @return
     */
    @RequestMapping("/findAllResources")
    public Map<String,Object> findPageResources(@RequestParam(name = "currentPage",defaultValue = "1",required = false)Integer currentPage,
                                                @RequestParam(name = "pageSize")Integer pageSize,
                                                @RequestParam(name = "name",required = false)String name){

        Sort sort = new Sort(Sort.Direction.DESC, "uploadTime");

        Pageable pageable = new PageRequest(currentPage-1,pageSize,sort);

        String like = "%"+name+"%";
        long totalCount = 0L;
        List<Resources> resources = null;
        if(name == null){
            resources = resourcesRepository.findAll(pageable).getContent();
            totalCount = resourcesRepository.count();
        }else{
            resources = resourcesRepository.findByNameLike(like,pageable).getContent();
            totalCount = resourcesRepository.countResourcesByNameLike(like);
        }

        Map<String,Object> page_msg = new HashMap<>();

        page_msg.put("totalCount",totalCount);
        page_msg.put("ResourcesList",resources);

        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS,page_msg);
    }

    /**
     *通过ID删除资源
     * @param id
     * @return
     */
    @GetMapping("/deleteById")
    public Map<String,Object> deleteResourcesById(@RequestParam(name = "resourcesid")Integer id){
        Resources one = resourcesRepository.findOne(id);
        if(one == null){
            return AjaxUtil.ajax_error(StatusUtil.RESOURCES_NOT_EXIST);
        }
        resourcesRepository.delete(id);
        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS,null);
    }

    /**
     * 根据分类查询资源
     * @param currentPage
     * @param pageSize
     * @param id
     * @return
     */
    @GetMapping("/findByCategory")
    public Map<String,Object> findByCategory(@RequestParam(name = "currentPage",defaultValue = "1",required = false)Integer currentPage,
                                             @RequestParam(name = "pageSize")Integer pageSize,
                                             @RequestParam(name = "categoryid")Integer id){
        Map<String,Object> page_msg = new HashMap<>();
        Sort sort = new Sort(Sort.Direction.DESC, "uploadTime");

        Pageable pageable = new PageRequest(currentPage-1,pageSize,sort);
        long totalCount = 0L;
        List<Resources> resources = null;
        Category category = categoryRepository.findOne(id);
        if(category == null) {
            return AjaxUtil.ajax_error(StatusUtil.CATEGORY_NOT_EXIST);
        }
        resources = resourcesRepository.findByCategory(category,pageable).getContent();
        totalCount = resourcesRepository.countResourcesByCategory(category);
        page_msg.put("totalCount",totalCount);
        page_msg.put("data",resources);
        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS,page_msg);
    }

    @GetMapping("findById")
    public Map<String,Object> findById(@RequestParam(name = "id")Integer id){
        Resources one = resourcesRepository.findOne(id);
        if(one == null){
            return AjaxUtil.ajax_error(StatusUtil.RESOURCES_NOT_EXIST);
        }
        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS,one);
    }


    /**
     * 下载学生信息模板
     * @param response
     * @return
     * @throws IOException
     */
    @GetMapping("/downloadExcel")
    public Map<String,Object> downloadExcel(HttpServletResponse response) throws IOException {
        File file = new File(upload_location+"students.xls");
        Path path = Paths.get(file.getAbsolutePath());
        String filename = file.getName();
        String mimeType = Files.probeContentType(path);

        FileInputStream fis = new FileInputStream(file);
        response.setHeader("content-type",mimeType);
        response.setHeader("content-disposition","attachment;filename="+filename);
        ServletOutputStream outputStream = response.getOutputStream();
        byte[] buff = new byte[1024*8];
        int len = 0;
        while((len = fis.read(buff)) != -1){
            outputStream.write(buff,0,len);
        }
        outputStream.flush();
        outputStream.close();
        fis.close();
        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS,null);
    }

    /**
     * 批量导入学生信息
     * @return
     */
    @PostMapping("/uploadExcel")
    public Map<String,Object> uploadExcel(@RequestParam(name = "excel") MultipartFile uploadFile) throws IOException {
        String avatarUrl = null;
        Map<Object,Object> msg = new HashMap<>();
        List<List<String>> lists = null;
        ExcelUtil excelUtil = new ExcelUtil();
        String originalFileName = uploadFile.getOriginalFilename();
        String extension = originalFileName.substring(originalFileName.lastIndexOf("."));

        if(!ArrayUtils.contains(allowTypeExcel,extension)){
            return AjaxUtil.ajax_error(StatusUtil.FILE_TYPE_NOALLOW);
        }

        File file = new File(upload_location+originalFileName);
        uploadFile.transferTo(file);
        String path = file.getAbsolutePath();
        //对上传的文件进行后缀判断
        if(extension.equals(".xls")){
            try {
                lists = excelUtil.readXls(path);
            } catch (Exception e) {
                e.printStackTrace();
                return AjaxUtil.ajax_error(StatusUtil.FAIL);
            }
        }else {
            try {
                lists = excelUtil.readXlsx(path);
            } catch (Exception e) {
                e.printStackTrace();
                return AjaxUtil.ajax_error(StatusUtil.FAIL);
            }
        }

        if(lists.size() == 0 || lists.size() > 3){
            return AjaxUtil.ajax_error(StatusUtil.FILE_TYPE_NOALLOW);
        }

        //第0行开始读数据
        for(int i = 0;i<lists.size();i++){
            int j = 1;
            List<String> model = lists.get(i);
            User one = userRepository.findByUsername(model.get(0));
            //判断excel中是否有已经存在的用户
            if(one != null){
                msg.put(j,"用户"+model.get(0)+"已存在");
                continue;
            }
            User user = new User();
            user.setUsername(model.get(0));
            user.setRealname(model.get(1));
            user.setPassword(MD5Util.MD5Encode(model.get(2),null));
            //自动生成头像
            try {
                avatarUrl = AvatarUtil.generateImg();
//            AvatarUtil.generateImg(user.getRealname(), "D:\\learnforum\\avatar", uuid);
//            avatarUrl = "http://localhost:8081/learnforum/userAvatar/"+uuid+".png";
                user.setAvatarUrl(avatarUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }
            user.setRegisterTime(new Date());
            userRepository.save(user);
        }

        return AjaxUtil.ajax_ok(StatusUtil.SUCCESS,msg);
    }
}
