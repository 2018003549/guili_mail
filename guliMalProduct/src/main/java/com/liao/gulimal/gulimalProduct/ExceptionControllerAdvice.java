package com.liao.gulimal.gulimalProduct;

import com.liao.common.utils.R;
import com.liao.exception.BizCodeEnume;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice(basePackages = "com.liao.gulimal.gulimalProduct.controller")//统一处理异常,指明需要处理异常的组件的位置
public class ExceptionControllerAdvice {
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public R handleValidException(MethodArgumentNotValidException e){
        log.error("数据校验出现问题：{},异常类型：{}",e.getMessage(),e.getClass());
        BindingResult bindingResult = e.getBindingResult();
        Map<String,String> errorMap=new HashMap<>();
        bindingResult.getFieldErrors().forEach((fieldError)->{
            errorMap.put(fieldError.getField(),fieldError.getDefaultMessage());
        });
        return R.error(BizCodeEnume.VAILD_EXCEPTION.getCode(),BizCodeEnume.VAILD_EXCEPTION.getMsg()
        ).put("data",errorMap);
    }
    @ExceptionHandler(value = Throwable.class)
    public R handleException(Throwable throwable){
        log.error("错误",throwable);
        return R.error(BizCodeEnume.UNKONW_EXCEPTINO.getCode(),BizCodeEnume.UNKONW_EXCEPTINO.getMsg());
    }
}
