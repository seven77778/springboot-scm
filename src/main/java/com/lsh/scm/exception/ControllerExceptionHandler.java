package com.lsh.scm.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mysql.jdbc.exceptions.jdbc4.MySQLIntegrityConstraintViolationException;
import com.lsh.scm.constants.BusinessStatus;
import com.lsh.scm.entity.response.ResponseMessage;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ResponseMessage handlerException(Exception ex) {
        System.out.println(ex + "异常****");
        ex.printStackTrace();
        ResponseMessage result = new ResponseMessage();

        result.setCode(BusinessStatus.FAIL);
        if (ex instanceof DataIntegrityViolationException) {
            System.out.println("---");
            result.setMessage("操作失败：存在依赖的记录 或 " + ex.getMessage());
        } else if (ex instanceof DuplicateKeyException) {
            result.setMessage("编号重复，添加失败");
        } else if (ex instanceof TransactionException) {
            result.setMessage(ex.getMessage());
        } else if (ex instanceof NumberFormatException) {
            result.setMessage("必须是数值：" + ex.getMessage());
        } else {
            result.setMessage(ex.getMessage());
        }
        return result;
    }
}
