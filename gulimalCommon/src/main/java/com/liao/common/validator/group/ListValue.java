package com.liao.common.validator.group;

import javax.validation.Constraint;
import javax.validation.Payload;
import javax.validation.constraintvalidation.SupportedValidationTarget;
import javax.validation.constraintvalidation.ValidationTarget;
import java.lang.annotation.*;

@Documented
@Constraint(
        validatedBy = {ListValueConstraintValidator.class}
)
@SupportedValidationTarget({ValidationTarget.ANNOTATED_ELEMENT})
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ListValue {
    String message() default "{com.liao.common.validator.group.ListValue.message}";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
    int[] vals() default {};
}
