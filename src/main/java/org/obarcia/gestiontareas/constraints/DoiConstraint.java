package org.obarcia.gestiontareas.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Constraint para el DOI.
 * 
 * @author obarcia
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Constraint(validatedBy = DoiConstraintValidator.class)
public @interface DoiConstraint
{
    String message();
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}