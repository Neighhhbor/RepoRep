package com.hxxdemo.oss;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.hxxdemo.config.Globals;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value="/fileUpload")
public class FileUploadController {
    private static final Logger log = LoggerFactory.getLogger(FileUploadController.class);
    /**
     * 文件上传
     * @param file
     */
//    @ApiOperation(value="文件上传到阿里云OSS", notes="文件上传到阿里云OSS")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "file", value = "file", required = true, dataType = "MultipartFile", paramType = "path"),
//    })
    @RequestMapping(value = "uploadImages",method = RequestMethod.POST)
    public Map<String,Object> uploadFile(MultipartFile file){
    	Map<String,Object> map = new HashMap<String,Object>();
    	String img[] = { "bmp", "jpg", "jpeg", "png", "gif","mp4"};
        try {
            if(null != file){
                String filename = file.getOriginalFilename();
                // 获取文件后缀名并转化为写，用于后续比较
                String fileType = filename.substring(filename.lastIndexOf(".") + 1, filename.length()).toLowerCase();
                boolean bool =false;
                // 创建图片类型数组
                for (int i = 0; i < img.length; i++){
                	if (img[i].equals(fileType)) {
                		bool = true;
                	}
                }
                if(!bool) {
                	map.put("errorcode", Globals.ERRORCODE3003);
                	map.put("errormessage", Globals.ERRORMESSAGE3003);
                	return map;
                }

                if(!"".equals(filename.trim())){
                    File newFile = new File(filename);
                    FileOutputStream os = new FileOutputStream(newFile);
                    os.write(file.getBytes());
                    os.close();
                    file.transferTo(newFile);
                    //上传到OSS
                    String url = AliyunOSSUtil.upload(newFile);
                    newFile.delete();
                    map.put("errorcode", Globals.ERRORCODE0);
                    map.put("errormessage", Globals.ERRORMESSAGE0);
                    map.put("info", url);
                    return map;
                }
            }
        }catch (Exception e){
            log.error(e.getMessage());
            map.put("errorcode", Globals.ERRORCODE3002);
            map.put("errormessage",Globals.ERRORMESSAGE3002);
            return map;
        }
        map.put("errorcode", Globals.ERRORCODE3001);
        map.put("errormessage",Globals.ERRORMESSAGE3001);
        return map;
    }
    @RequestMapping(value = "uploadImagesForWebsite",method = RequestMethod.POST)
    public Map<String,Object> uploadImagesForWebsite(MultipartFile files[]){
    	Map<String,Object> map = new HashMap<String,Object>();
    	String img[] = { "bmp", "jpg", "jpeg", "png", "gif","mp4"};
    	Map<String,Object> urlMap = new HashMap<>();
        try {
            if(null != files && files.length > 0){
            	for (int j = 0; j < files.length; j++) {
					MultipartFile file = files[j];
					String filename = file.getOriginalFilename();
	                // 获取文件后缀名并转化为写，用于后续比较
	                String fileType = filename.substring(filename.lastIndexOf(".") + 1, filename.length()).toLowerCase();
	                boolean bool =false;
	                // 创建图片类型数组
	                for (int i = 0; i < img.length; i++){
	                	if (img[i].equals(fileType)) {
	                		bool = true;
	                	}
	                }
	                if(!bool) {
	                	continue;
	                }

	                if(!"".equals(filename.trim())){
	                    File newFile = new File(filename);
	                    FileOutputStream os = new FileOutputStream(newFile);
	                    os.write(file.getBytes());
	                    os.close();
	                    file.transferTo(newFile);
	                    //上传到OSS
	                    String url = AliyunOSSUtil.upload(newFile,"website");
	                    newFile.delete();
	                    urlMap.put(filename, url);
	                   
	                }
				}
            }else {
                map.put("errorcode", Globals.ERRORCODE3001);
                map.put("errormessage",Globals.ERRORMESSAGE3001);
                return map;
            }
            
        }catch (Exception e){
            log.error(e.getMessage());
            map.put("errorcode", Globals.ERRORCODE3002);
            map.put("errormessage",Globals.ERRORMESSAGE3002);
            return map;
        }
        map.put("errorcode", Globals.ERRORCODE0);
        map.put("errormessage", Globals.ERRORMESSAGE0);
        map.put("info", urlMap);
        return map;
    }
    @RequestMapping(value = "uploadImagesForNews",method = RequestMethod.POST)
    public Map<String,Object> uploadImagesForNews(MultipartFile files[]){
    	Map<String,Object> map = new HashMap<String,Object>();
    	String img[] = { "bmp", "jpg", "jpeg", "png", "gif","mp4"};
    	Map<String,Object> urlMap = new HashMap<>();
    	try {
    		if(null != files && files.length > 0){
    			for (int j = 0; j < files.length; j++) {
    				MultipartFile file = files[j];
    				String filename = file.getOriginalFilename();
    				// 获取文件后缀名并转化为写，用于后续比较
    				String fileType = filename.substring(filename.lastIndexOf(".") + 1, filename.length()).toLowerCase();
    				boolean bool =false;
    				// 创建图片类型数组
    				for (int i = 0; i < img.length; i++){
    					if (img[i].equals(fileType)) {
    						bool = true;
    					}
    				}
    				if(!bool) {
    					continue;
    				}
    				
    				if(!"".equals(filename.trim())){
    					File newFile = new File(filename);
    					FileOutputStream os = new FileOutputStream(newFile);
    					os.write(file.getBytes());
    					os.close();
    					file.transferTo(newFile);
    					//上传到OSS
    					String url = AliyunOSSUtil.upload(newFile,"news");
    					newFile.delete();
    					urlMap.put(filename, url);
    					
    				}
    			}
    		}else {
    			map.put("errorcode", Globals.ERRORCODE3001);
    			map.put("errormessage",Globals.ERRORMESSAGE3001);
    			return map;
    		}
    		
    	}catch (Exception e){
    		log.error(e.getMessage());
    		map.put("errorcode", Globals.ERRORCODE3002);
    		map.put("errormessage",Globals.ERRORMESSAGE3002);
    		return map;
    	}
    	map.put("errorcode", Globals.ERRORCODE0);
    	map.put("errormessage", Globals.ERRORMESSAGE0);
    	map.put("info", urlMap);
    	return map;
    }
}