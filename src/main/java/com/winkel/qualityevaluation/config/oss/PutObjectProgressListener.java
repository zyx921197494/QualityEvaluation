package com.winkel.qualityevaluation.config.oss;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.event.ProgressEvent;
import com.aliyun.oss.event.ProgressEventType;
import com.aliyun.oss.event.ProgressListener;
import com.aliyun.oss.model.PutObjectRequest;

import java.io.File;


public class PutObjectProgressListener implements ProgressListener {

    private long bytesWritten = 0;
    private long totalBytes = -1;
    private boolean succeed = false;

    @Override
    public void progressChanged(ProgressEvent progressEvent) {
        long bytes = progressEvent.getBytes();
        ProgressEventType eventType = progressEvent.getEventType();
        switch (eventType) {
            case TRANSFER_STARTED_EVENT:
                System.out.println("开始上传......");
                break;
            case REQUEST_CONTENT_LENGTH_EVENT:
                this.totalBytes = bytes;
                System.out.println("总计 " + this.totalBytes + " bytes ");
                break;
            case REQUEST_BYTE_TRANSFER_EVENT:
                this.bytesWritten += bytes;
                if (this.totalBytes != -1) {
                    int percent = (int) (this.bytesWritten * 100.0 / this.totalBytes);
                    System.out.println(bytes + " bytes 已上传，当前进度： " + percent + "% (" + this.bytesWritten + "/" + this.totalBytes + ")");
                } else {
                    System.out.println(bytes + " bytes 已上传，当前进度：未知 " + "(" + this.bytesWritten + "/...)");
                }
                break;
            case TRANSFER_COMPLETED_EVENT:
                this.succeed = true;
                System.out.println("上传成功，" + this.bytesWritten + " bytes 已上传");
                break;
            case TRANSFER_FAILED_EVENT:
                System.out.println("上传失败，" + this.bytesWritten + " bytes 已上传");
                break;
            default:
                break;
        }
    }

    public boolean isSucceed() {
        return succeed;
    }

    public static void main(String[] args) {
        // Endpoint以杭州为例，其它Region请按实际情况填写。
        String endpoint = "http://oss-cn-beijing.aliyuncs.com";
        // 阿里云主账号AccessKey拥有所有API的访问权限，风险很高。强烈建议您创建并使用RAM账号进行API访问或日常运维，请登录RAM控制台创建RAM账号。
        String accessKeyId = "LTAI4G8QTUwKAaiSoX3hPy8c";
        String accessKeySecret = "Oi7V9dhMokF6rLaBlTFIFYeIezX0tm";
        String bucketName = "winkel";
        String objectName = String.valueOf(System.currentTimeMillis());

        // 创建OSSClient实例。
        OSS ossClient = new OSSClientBuilder().build(endpoint, accessKeyId, accessKeySecret);

        try {
            // 上传文件的同时指定了进度条参数
            ossClient.putObject(new PutObjectRequest(bucketName, objectName, new File("F:\\素材模板\\高清图片\\1.jpg")).withProgressListener(new PutObjectProgressListener()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        // 关闭OSSClient
        ossClient.shutdown();
    }

}
        