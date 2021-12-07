package com.liangzhicheng.common.oss.service;

import com.liangzhicheng.common.oss.object.CloudStorage;
import com.liangzhicheng.common.utils.TimeUtil;
import com.liangzhicheng.common.utils.ToolUtil;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Date;
import java.util.UUID;

/**
 * 云存储(支持七牛云、阿里云、腾讯云)
 */
public abstract class CloudStorageService {

    CloudStorage cloudStorage;

    /**
     * 文件路径
     * @param prefix 前缀
     * @return 返回上传路径
     */
    public String getPath(String prefix) {
        //生成uuid
        String uuid = UUID.randomUUID().toString().replaceAll("-", "");
        //文件路径
        String path = TimeUtil.format(new Date(), "yyyyMMdd") + "/" + TimeUtil.format(new Date(), "HHmmssS") + uuid.substring(0, 5);
        if (ToolUtil.isNotBlank(prefix)) {
            path = prefix + "/" + path;
        }
        return path;
    }

    /**
     * 文件上传
     * @param file 文件
     * @return 返回http地址
     * @throws Exception
     */
    public abstract String upload(MultipartFile file) throws Exception;

    /**
     * 文件上传，InputStream方式
     * @param input 字节流
     * @param path 文件路径，包含文件名
     * @return 返回http地址
     */
    public abstract String upload(InputStream input, String path);

    /**
     * 文件上传，byte[]方式
     * @param data 文件字节数组
     * @param path 文件路径，包含文件名
     * @return 返回http地址
     */
    public abstract String upload(byte[] data, String path);

}
