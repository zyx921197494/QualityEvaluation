package com.winkel.qualityevaluation.util;
/*
  @ClassName OssUtil
  @Description
  @Author winkel
  @Date 2022-03-23 18:21
  */

import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.OSSClient;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.*;
import com.winkel.qualityevaluation.config.oss.OSSConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
public class OssUtil {

    @Autowired
    private OSSClient ossClient;

    @Autowired
    private OSSConfig config;

    public static final String[] EXCEL_SUFFIX = new String[]{"xlsx", "xls"};

    public static final String[] EVIDENCE_SUFFIX = new String[]{"png", "jpg", "jpeg", "docx", "doc", "txt", "pdf", "md"};

    public static final String[] REPORT_SUFFIX = new String[]{"docx", "doc", "txt", "pdf", "md"};

    /**
     * desc: 分片上传文件
     * params: [uploadFile]
     * return: com.winkel.qualityevaluation.util.UploadResult
     * exception:
     **/
    public UploadResult uploadWithMultipart(MultipartFile uploadFile, String[] suffixes) {
        boolean isLegal = checkFileType(uploadFile, suffixes);
        UploadResult result = new UploadResult();
        if (!isLegal) {
            result.setStatus(UploadStatus.ERROR.getStatus());
            result.setMsg("文件格式错误");
            return result;
        }

        String filePath = getFilePath(uploadFile.getOriginalFilename());
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setCacheControl("no-cache");

        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest(config.getBucketName(), filePath);

        // 初始化分片
        InitiateMultipartUploadResult uploadResult = ossClient.initiateMultipartUpload(request);
        // 分片上传事件的唯一标识
        String uploadId = uploadResult.getUploadId();
        List<PartETag> partETags = new ArrayList<>();
        final long partSize = 3 * 1024 * 1024L;  // 3MB
        try {
            File file = multifile2File(uploadFile);

            long fileLength = file.length();
            int partCount = (int) (fileLength / partSize);
            if (fileLength % partSize != 0) partCount++;

            // 遍历上传
            for (int i = 0; i < partCount; i++) {
                long startPosition = i * partSize;
                long currentPartSize = i + 1 == partCount ? fileLength - startPosition : partSize;
                InputStream inStream = new FileInputStream(file);
                inStream.skip(startPosition);  // 跳过已上传的分片

                UploadPartRequest partRequest = new UploadPartRequest();
                partRequest.setBucketName(config.getBucketName());
                partRequest.setKey(filePath);
                partRequest.setUploadId(uploadId);
                partRequest.setInputStream(inStream);
                partRequest.setPartSize(currentPartSize);
                partRequest.setPartNumber(i + 1); // 分片号从1开始
                UploadPartResult partResult = ossClient.uploadPart(partRequest);
                partETags.add(partResult.getPartETag());  // 上传分片返回的结果保存在PartETag中，再存入集合
            }

            // 逐一验证每个分片的有效性。当所有的数据分片验证通过后，把这些分片组合成一个完整的文件
            CompleteMultipartUploadRequest completeRequest = new CompleteMultipartUploadRequest(config.getBucketName(), filePath, uploadId, partETags);
            // 完成分片上传
            ossClient.completeMultipartUpload(completeRequest);
//        completeRequest.setCallback();
//        System.out.println(completeResult.getLocation());  // 完整路径
//        System.out.println(config.getUrlPrefix() + filePath);  // 完整路径
//        System.out.println(completeResult.getKey());  // file/16480366117063802.pdf
//        System.out.println(completeResult.getETag());  // 417F0E1133E0D5A1E8A823C920A440CA-4
        } catch (Exception e) {
            return new UploadResult()
                    .setStatus(UploadStatus.ERROR.getStatus())
                    .setMsg("上传到OSS时出错");
        }

        return new UploadResult()
                .setFilename(filePath)
                .setUrl(config.getUrlPrefix() + filePath)
                .setStatus(UploadStatus.DONE.getStatus())
                .setMsg("上传成功");
    }

    /**
     * desc: 获取签名并使用签名上传文件
     * params: [file, method, seconds]
     * return: com.winkel.qualityevaluation.util.UploadResult
     * exception:
     **/
    public UploadResult uploadWithSignature(MultipartFile file, String[] suffixes) {

        //校验文件格式
        boolean isLegal = checkFileType(file, suffixes);
        UploadResult result = new UploadResult();
        if (!isLegal) {
            result.setStatus(UploadStatus.ERROR.getStatus());
            result.setMsg("文件格式错误");
            return result;
        }

        String filename = file.getOriginalFilename();
        String filepath = getFilePath(filename);

        Date expiration = new Date(new Date().getTime() + 3600 * 1000); // 单位ms 1h
        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(config.getBucketName(), filepath, HttpMethod.PUT);
        request.setExpiration(expiration);
        // request中添加参数，使用url访问时需要加在httpHeaders中
        request.setContentType("application/octet-stream");
        request.addUserMetadata("action", "upload");

        URL url = ossClient.generatePresignedUrl(request);

        HashMap<String, String> header = new HashMap<>(3);
        header.put("Content-Type", "application/octet-stream");
        header.put("x-oss-meta-action", "upload");
        try {
            ossClient.putObject(url, new ByteArrayInputStream(file.getBytes()), file.getBytes().length, header);
        } catch (Exception e) {
            return new UploadResult()
                    .setStatus(UploadStatus.ERROR.getStatus())
                    .setMsg("上传到OSS时出错");
        }

        //上传成功
        result.setFilename(filepath);
        result.setUrl(config.getUrlPrefix() + filepath);
        result.setStatus(UploadStatus.DONE.getStatus());
        result.setMsg("上传成功");
        return result;
    }


    /**
     * desc: 断点续传下载 分片大小为2MB
     * params: [fileUrl, localpath]
     * return: boolean
     * exception:
     **/
    public boolean downloadWithBreakpoint(String fileUrl, String localpath) {  // 这里的fileUrl完整路径不能包括BucketName
        String defaultpath = "C:\\Users\\Public\\Downloads\\";  // 默认下载到的本地路径
        // 从OSS路径获取OSS文件名
        String[] split = fileUrl.split("/");
        String filename = split[split.length - 1];

        String path = StringUtils.isBlank(localpath) ? defaultpath + filename : localpath + filename;
        File file = new File(path);

        // 这里的fileUrl完整路径不能包括BucketName
        DownloadFileRequest request = new DownloadFileRequest(config.getBucketName(), fileUrl);//withProgressListener(new GetObjectProgressListener());
        request.setDownloadFile(file.getAbsolutePath());
        request.setPartSize(2 * 1024 * 1024L);
        request.setTaskNum(3);
        request.setEnableCheckpoint(true);
        try {
            ossClient.downloadFile(request);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * desc: 小文件直接下载
     * params: [fileUrl, localpath]
     * return: boolean
     * exception:
     **/
    public boolean downloadSimple(String fileUrl, String localpath) {
        String defaultpath = "C:\\Users\\Public\\Downloads\\";
        // 从OSS路径获取OSS文件名
        String[] split = fileUrl.split("/");
        String filename = split[split.length - 1];

        String path = StringUtils.isBlank(localpath) ? defaultpath + filename : localpath + filename;
        try {
            ossClient.getObject(new GetObjectRequest(config.getBucketName(), fileUrl), new File(path));

        } catch (OSSException e) {
            return false;
        }
        return true;
    }

    public UploadResult upload(MultipartFile file) {
        UploadResult result;
        if (file.getSize() < 1000000L) {
            result = uploadWithSignature(file, OssUtil.EVIDENCE_SUFFIX);
            log.info("签名上传证据，当前文件大小 {} MB", file.getSize() >> 20);
        } else {
            result = uploadWithMultipart(file, OssUtil.EVIDENCE_SUFFIX);
            log.info("分片上传证据，当前文件大小 {} MB", file.getSize() >> 20);
        }
        return result;
    }


    public boolean deleteFile(String filename) {
        try {
            ossClient.deleteObject(config.getBucketName(), filename);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public long getFileSize(String filename) {
        SimplifiedObjectMeta objectMeta = ossClient.getSimplifiedObjectMeta(config.getBucketName(), filename);
        return objectMeta.getSize();
    }


    public boolean isExist(String filename) {
        return ossClient.doesObjectExist(config.getBucketName(), filename);
    }


    private String getFilePath(String filename) {
        return "file/" + System.currentTimeMillis() + RandomUtils.nextInt(100, 999) + "." + StringUtils.substringAfterLast(filename, ".");
    }

    private boolean checkFileType(MultipartFile file, String[] suffixes) {
        for (String type : suffixes) {
            if (StringUtils.equalsIgnoreCase(StringUtils.substringAfterLast(file.getOriginalFilename(), "."), type)) {
                return true;
            }
        }
        return false;
    }

    private File multifile2File(MultipartFile file) throws IOException {
        File multifile = File.createTempFile("temp", null);
        file.transferTo(multifile);
        return multifile;
    }

    @Test
    public void test() {

    }

}
