package com.liangzhicheng.common.oss.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.liangzhicheng.common.exception.TransactionException;
import com.liangzhicheng.common.oss.object.CloudStorage;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

public class AliCloudStorageService extends CloudStorageService {

    private OSS client;

    public AliCloudStorageService(CloudStorage cloudStorage) {
        this.cloudStorage = cloudStorage;
        //初始化
        init();
    }

    private void init() {
//        client = new OSSClient(cloudStorage.getAliEndPoint(), cloudStorage.getAliAccessKey(),
//                cloudStorage.getAliSecretKey());
        client = new OSSClientBuilder().build(
                cloudStorage.getAliEndPoint(),
                cloudStorage.getAliAccessKey(),
                cloudStorage.getAliSecretKey());
    }

    @Override
    public String upload(MultipartFile file) throws Exception {
        String fileName = file.getOriginalFilename();
        String prefix = fileName.substring(fileName.lastIndexOf(".") + 1);
        return upload(file.getBytes(), super.getPath(cloudStorage.getAliPrefix()) + "." + prefix);
    }

    @Override
    public String upload(InputStream inputStream, String path) {
        try {
            client.putObject(cloudStorage.getAliBucketName(), path, inputStream);
        } catch (Exception e) {
            throw new TransactionException("上传文件失败，请检查阿里云配置信息", e);
        }
        return cloudStorage.getAliDomain() + "/" + path;
    }

    @Override
    public String upload(byte[] data, String path) {
        return upload(new ByteArrayInputStream(data), path);
    }

}
