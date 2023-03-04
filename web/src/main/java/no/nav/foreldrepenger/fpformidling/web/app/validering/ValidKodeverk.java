package no.nav.foreldrepenger.fpformidling.web.app.validering;

import javax.validation.Constraint;
import javax.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.TYPE_USE, ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {KodeverdiValidator.class})
@Documented
public @interface ValidKodeverk {

    String message() default "kodeverk kode feilet validering";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
