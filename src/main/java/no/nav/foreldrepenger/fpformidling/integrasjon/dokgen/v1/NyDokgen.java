package no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.v1;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.inject.Qualifier;

@Qualifier
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD, ElementType.PARAMETER})
public @interface NyDokgen {

    class GammelDokgenAnnotationLiteral extends AnnotationLiteral<NyDokgen> implements NyDokgen {
    }
}
