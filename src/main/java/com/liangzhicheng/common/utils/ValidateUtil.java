package com.liangzhicheng.common.utils;

import com.liangzhicheng.common.exception.TransactionException;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

public class ValidateUtil {

    private static Validator validator;

    static {
        validator = Validation.buildDefaultValidatorFactory().getValidator();
    }

    /**
     * 校验对象
     * @param object 待校验对象
     * @param clazz 待校验的class
     * @throws TransactionException 校验不通过，则报TransactionException异常
     */
    public static void validate(Object object, Class<?> ... clazz) throws TransactionException {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(object, clazz);
        if(ToolUtil.listSizeGT(constraintViolations)){
            ConstraintViolation<Object> constraint = (ConstraintViolation<Object>) constraintViolations.iterator().next();
            throw new TransactionException(constraint.getMessage());
        }
    }

}
