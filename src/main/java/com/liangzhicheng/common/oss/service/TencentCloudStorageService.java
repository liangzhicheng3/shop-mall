package com.liangzhicheng.common.oss.service;

import com.liangzhicheng.common.exception.TransactionException;
import com.liangzhicheng.common.oss.object.CloudStorage;
import com.qcloud.cos.COSClient;
import com.qcloud.cos.ClientConfig;
import com.qcloud.cos.auth.BasicCOSCredentials;
import com.qcloud.cos.auth.COSCredentials;
import com.qcloud.cos.model.ObjectMetadata;
import com.qcloud.cos.model.PutObjectRequest;
import com.qcloud.cos.region.Region;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;

public class TencentCloudStorageService extends CloudStorageService {

    private COSClient client;

    public TencentCloudStorageService(CloudStorage cloudStorage) {
        this.cloudStorage = cloudStorage;
        //初始化
        init();
    }

    private void init() {
        COSCredentials credentials = new BasicCOSCredentials(
                cloudStorage.getTencentSecretId(),
                cloudStorage.getTencentSecretKey());
        //初始化客户端配置，设置bucket所在的区域，华南：gz，华北：tj，华东：sh
        ClientConfig clientConfig = new ClientConfig(new Region(cloudStorage.getTencentRegion()));
        client = new COSClient(credentials, clientConfig);

    }

    @Override
    public String upload(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        String prefix = fileName.substring(fileName.lastIndexOf(".") + 1);
        return upload(file.getInputStream(), super.getPath(cloudStorage.getTencentPrefix()) + "." + prefix);
    }

    @Override
    public String upload(InputStream inputStream, String path) {
        //腾讯云必需要以"/"开头
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        try {
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    cloudStorage.getTencentBucketName(),
                    path,
                    inputStream,
                    new ObjectMetadata());
            client.putObject(putObjectRequest);
            return cloudStorage.getTencentDomain() + path;
        } catch (Exception e) {
            throw new TransactionException("上传文件失败，请检查腾讯云配置信息", e);
        }
    }

    @Override
    public String upload(byte[] data, String path) {
        //新版sdk中已弃用
        return null;
    }

    public void delete(String path) {
        client.deleteObject(cloudStorage.getTencentBucketName(), path);
    }

}
