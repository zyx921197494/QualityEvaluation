##TodoList

一、Controller层参数校验 eg:@NotNull

二、Controller层接口权限限制 eg:@PreAuthorize

三、全局异常捕获：
1. SQLException：
2. BatchUpdateException: Cannot add or update a child row: a foreign key constraint fails
3. java.sql.SQLIntegrityConstraintViolationException: Cannot add or update a child row: a foreign key constraint fails
4. java.io.FileNotFoundException: F:\school.xls (系统找不到指定的文件。)
   
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