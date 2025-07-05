
package com.hu.oneclick.common.util;

import io.minio.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

/**
 * @Author: zhangqingyang
 * @Description
 * @Date: Created in 13:40 2019/10/22
 * @Modified By:
 */


public class MinioUtil {

    private final static Logger logger = LoggerFactory.getLogger(MinioUtil.class);

    /**
     * 创建具有给定区域的新存储桶
     */
    public static boolean createRegion(MinioClient client, String bucketName, String region) {
        boolean flag = false;
        try {
            flag = client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build();
            if (!flag) {
                client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).region(region).build();
            }
        } catch (Exception ignored) {
            logger.error("MinioUtil.class#createRegion() error:{}" + ignored.getMessage();
        }
        return flag;
    }

    /**
     * 如果存储桶不存在 创建存储桶
     *
     * @param client
     * @param bucketName
     * @throws Exception
     */
    public static void checkBucket(MinioClient client, String bucketName) throws Exception {

        // 如果存储桶不存在 创建存储桶
        if (!client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build() {
            client.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build();
            // 设置隐私权限 公开读
            String builder = "{\n" +
                "    \"Statement\": [\n" +
                "        {\n" +
                "            \"Action\": [\n" +
                "                \"s3:GetBucketLocation\",\n" +
                "                \"s3:ListBucket\"\n" +
                "            ],\n" +
                "            \"Effect\": \"Allow\",\n" +
                "            \"Principal\": \"*\",\n" +
                "            \"Resource\": \"arn:aws:s3:::" + bucketName + "\"\n" +
                "        },\n" +
                "        {\n" +
                "            \"Action\": \"s3:GetObject\",\n" +
                "            \"Effect\": \"Allow\",\n" +
                "            \"Principal\": \"*\",\n" +
                "            \"Resource\": \"arn:aws:s3:::" + bucketName + "/*\"\n" +
                "        }\n" +
                "    ],\n" +
                "    \"Version\": \"2012-10-17\"\n" +
                "}\n";
            client.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucketName).config(builder).build();
        }
    }

    /**
     * 删除一个桶
     *
     * @param client
     * @param bucketName
     * @return
     */
    public static boolean removeBucket(MinioClient client, String bucketName) {
        boolean flag = false;
        try {
            boolean found = client.bucketExists(BucketExistsArgs.builder().bucket(bucketName).build();
            if (found)
                client.removeBucket(RemoveBucketArgs.builder().bucket(bucketName).build();
        } catch (Exception e) {
            flag = false;
            logger.error("MinioUtil.class#removeBucket() error:{}" + e.getMessage();
        }
        return flag;
    }

    /**
     * 在给定存储桶中以InputStream的形式获取整个对象的数据。
     * 使用后必须关闭InputStream，否则连接将保持打开状态
     *
     * @param client
     * @param bucketName
     * @param objectName
     * @param offset
     * @return
     */
    public static InputStream getObject(MinioClient client, String bucketName, String objectName, long offset) {
        InputStream stream = null;
        try {
            client.statObject(StatObjectArgs.builder().bucket(bucketName).object(objectName).build();
            stream = client.getObject(GetObjectArgs.builder().bucket(bucketName).object(objectName).offset(offset).build();
            return stream;
        } catch (Exception e) {
            logger.error("MinioUtil.class#getObject() error:{}" + e.getMessage();
        }
        return stream;
    }

    /**
     * 使用指定的元数据将文件中的内容作为对象上传到给定存储桶，并使用sse密钥进行加密。
     * 如果对象大于5MB，客户端将自动使用多部分会话。
     * <p>
     * 如果会话失败，则用户可以通过尝试再次创建完全相同的对象来尝试重新上传对象。客户端将检查任何当前上传会话的所有部分，并尝试自动重用该会话。
     * 如果发现不匹配，则上传将失败，然后再上传更多数据。否则，它将在会话中断的地方继续上传。
     * <p>
     * 如果多部分会话失败，则用户负责恢复或删除会话。
     *
     * @param client
     * @param bucketName
     * @param objectName
     * @param stream
     */
    public static void putObject(MinioClient client, String bucketName, String objectName, InputStream stream, MultipartFile file) {
        try {
            checkBucket(client, bucketName);
            client.putObject(PutObjectArgs.builder()
                .bucket(bucketName)
                .object(objectName)
                .stream(stream, file.getSize(), -1)
                .contentType(file.getContentType()
                .build();
        } catch (Exception e) {
            logger.error("MinioUtil.class#putObject() error:{}" + e.getMessage();
        }
    }

    /**
     * 删除对象
     *
     * @param client
     * @param bucketName
     * @param objectName
     */
    public static void rmObject(MinioClient client, String bucketName, String objectName) {
        try {
            client.removeObject(RemoveObjectArgs.builder().bucket(bucketName).object(objectName).build();
        } catch (Exception e) {
            logger.error("MinioUtil.class#rmObject() error:{}" + e.getMessage();
        }
    }
}
}
}
