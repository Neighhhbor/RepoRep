package com.hxxdemo.oss;

import com.aliyun.oss.ClientException;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.CannedAccessControlList;
import com.aliyun.oss.model.CreateBucketRequest;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.hxxdemo.oss.util.EvaluateConfigLoader;

import org.slf4j.LoggerFactory;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

public class AliyunOSSUtil {

    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(AliyunOSSUtil.class);

    public static String upload(File file){
        //logger.info("=========>OSS文件上传开始："+file.getName());
        String endpoint=EvaluateConfigLoader.getEndpoint();
        String accessKeyId=EvaluateConfigLoader.getAccessKeyId();
        String accessKeySecret=EvaluateConfigLoader.getAccessKeySecret();
        String bucketName=EvaluateConfigLoader.getBucketName();
        String fileHost=EvaluateConfigLoader.getFileHost();
        String filePath=EvaluateConfigLoader.getFilepath();
        String filePathWebsite=EvaluateConfigLoader.getFilepath();
        String filePathNews=EvaluateConfigLoader.getFilepath();
//        String endpoint=ConstantProperties.SPRING_FILE_ENDPOINT;
//        String accessKeyId=ConstantProperties.SPRING_FILE_ACCESS_KEY_ID;
//        String accessKeySecret=ConstantProperties.SPRING_FILE_ACCESS_KEY_SECRET;
//        String bucketName=ConstantProperties.SPRING_FILE_BUCKET_NAME1;
//        String fileHost=ConstantProperties.SPRING_FILE_FILE_HOST;

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = format.format(new Date());

        if(null == file){
            return null;
        }

        OSSClient ossClient = new OSSClient(endpoint,accessKeyId,accessKeySecret);
        try {
            //容器不存在，就创建
            if(! ossClient.doesBucketExist(bucketName)){
                ossClient.createBucket(bucketName);
                CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
                createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
                ossClient.createBucket(createBucketRequest);
            }
            String filename = file.getName();
            // 获取文件后缀名并转化为写，用于后续比较
            String fileType = filename.substring(filename.lastIndexOf(".") + 1, filename.length()).toLowerCase();
            //创建文件路径
            String fileUrl = filePath+"/"+dateStr + "/" + UUID.randomUUID().toString().replace("-","")+"."+fileType;
            //上传文件
            PutObjectResult result = ossClient.putObject(new PutObjectRequest(bucketName, fileUrl, file));
            //设置权限 这里是公开读
            ossClient.setBucketAcl(bucketName,CannedAccessControlList.PublicRead);
            if(null != result){
                logger.info("==========>OSS文件上传成功,OSS地址："+fileUrl);
                return fileHost+fileUrl;
            }
        }catch (OSSException oe){
            logger.error(oe.getMessage());
        }catch (ClientException ce){
            logger.error(ce.getMessage());
        }finally {
            //关闭
            ossClient.shutdown();
        }
        return null;
    }
    public static String upload(File file,String type){ // type:   website  news    logs 
        //logger.info("=========>OSS文件上传开始："+file.getName());
        String endpoint=EvaluateConfigLoader.getEndpoint();
        String accessKeyId=EvaluateConfigLoader.getAccessKeyId();
        String accessKeySecret=EvaluateConfigLoader.getAccessKeySecret();
        String bucketName=EvaluateConfigLoader.getBucketName();
        String fileHost=EvaluateConfigLoader.getFileHost();
        String filePath=EvaluateConfigLoader.getFilepath();
        if (type.equals("website")) {
        	filePath=EvaluateConfigLoader.getFilepathWebsite();
		}else if (type.equals("news")) {
			filePath=EvaluateConfigLoader.getFilepathNews();
		}

        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = format.format(new Date());

        if(null == file){
            return null;
        }

        OSSClient ossClient = new OSSClient(endpoint,accessKeyId,accessKeySecret);
        try {
            //容器不存在，就创建
            if(! ossClient.doesBucketExist(bucketName)){
                ossClient.createBucket(bucketName);
                CreateBucketRequest createBucketRequest = new CreateBucketRequest(bucketName);
                createBucketRequest.setCannedACL(CannedAccessControlList.PublicRead);
                ossClient.createBucket(createBucketRequest);
            }
            String filename = file.getName();
            // 获取文件后缀名并转化为写，用于后续比较
            String fileType = filename.substring(filename.lastIndexOf(".") + 1, filename.length()).toLowerCase();
            //创建文件路径
            String fileUrl = filePath+"/"+dateStr + "/" + UUID.randomUUID().toString().replace("-","")+"."+fileType;
            //上传文件
            PutObjectResult result = ossClient.putObject(new PutObjectRequest(bucketName, fileUrl, file));
            //设置权限 这里是公开读
            ossClient.setBucketAcl(bucketName,CannedAccessControlList.PublicRead);
            if(null != result){
                logger.info("==========>OSS文件上传成功,OSS地址："+fileUrl);
                return fileHost+fileUrl;
            }
        }catch (OSSException oe){
            logger.error(oe.getMessage());
        }catch (ClientException ce){
            logger.error(ce.getMessage());
        }finally {
            //关闭
            ossClient.shutdown();
        }
        return null;
    }
}