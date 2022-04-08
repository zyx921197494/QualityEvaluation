##TodoList

一、Controller层参数校验 eg:@NotNull

二、Controller层接口权限限制 eg:@PreAuthorize

超过评估时间、账号无法登陆 --> Spring定时任务
对leader接口如开启自评做进一步权限限制

三、全局异常捕获：
1. SQLException：
2. BatchUpdateException: Cannot add or update a child row: a foreign key constraint fails
3. java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails
4. java.io.FileNotFoundException: F:\school.xls (系统找不到指定的文件。)
5. FileSizeLimitExceededException: The field file exceeds its maximum permitted size of 20971520 bytes 单个文件大小超过阈值
6. SizeLimitExceededException:the request was rejected because its size (63603752) exceeds the configured maximum 单词请求中总的文件过大
7. MalformedURLException:可能在上传文件时生成带签名的URL异常
8. OSS ClientException指客户端尝试向OSS发送请求以及数据传输时遇到的异常。例如，当发送请求时网络连接不可用，或当上传文件时发生IO异常，会抛出ClientException。继承自RuntimeException
9. OSS OSSException指服务器端异常，它来自于对服务器错误信息的解析。OSSException包含OSS返回的错误码和错误信息，便于定位问题，并做出适当的处理。继承自RuntimeException
10. OSS createOSSException：
11. FileNotFoundException：系统找不到指定的路径
12. IllegalArgumentException: argument type mismatch 某个实体类的ID策略有问题
13. java.io.FileNotFoundException: C:\Users\Public\Downloads\评估数据批量导出\评估数据.xlsx (另一个程序正在使用此文件，进程无法访问。)
   
```java
   @ControllerAdvice
   public class ExceptionHandlingControllerAdvice {

   protected Logger logger;

   public ExceptionHandlingControllerAdvice() {
   logger = LoggerFactory.getLogger(getClass());
   }
      @ExceptionHandler({ SQLException.class })
      public String databaseError(Exception exception) {
      logger.error("Request raised " + exception.getClass().getSimpleName());
      return "Global_databaseError";
      }
      }
```

四、Swagger接口文档方便前端查看

五、长的数据库操作使用事务