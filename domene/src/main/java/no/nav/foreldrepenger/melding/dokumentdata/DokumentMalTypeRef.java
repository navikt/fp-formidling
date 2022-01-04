package no.nav.foreldrepenger.melding.dokumentdata;

import java.lang.annotation.Annotation;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.enterprise.context.Dependent;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.spi.CDI;
import javax.enterprise.util.AnnotationLiteral;
import javax.inject.Qualifier;

import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;

@Qualifier
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Documented
public @interface DokumentMalTypeRef {

    String value();

    class DokumentMalTypeRefLiteral extends AnnotationLiteral<DokumentMalTypeRef> implements DokumentMalTypeRef { //NOSONAR

        private String navn;

        public DokumentMalTypeRefLiteral(String dokumentMalKode) {
            this.navn = dokumentMalKode;
        }

        @Override
        public String value() {
            return navn;
        }
    }

    @SuppressWarnings("unchecked")
    public static final class Lookup {

        private Lookup() {
        }

        public static <I> Optional<I> find(Class<I> cls, String ytelseTypeKode) {
            return find(cls, (CDI<I>) CDI.current(), ytelseTypeKode);
        }

        public static <I> Optional<I> find(Class<I> cls, DokumentMalType dokumentMalType) {
            return find(cls, (CDI<I>) CDI.current(), dokumentMalType.getKode());
        }

        public static <I> Optional<I> find(Class<I> cls, Instance<I> instances, String dokumentMalKode) {
            Objects.requireNonNull(instances, "instances");

            for (var dokumentMalLiteral : coalesce(dokumentMalKode, "*")) {
                var inst = select(cls, instances, new DokumentMalTypeRefLiteral(dokumentMalLiteral));
                if (inst.isResolvable()) {
                    return Optional.of(getInstance(inst));
                } else {
                    if (inst.isAmbiguous()) {
                        throw new IllegalStateException("Har flere matchende instanser for klasse : " + cls.getName() + ", dokumentMalLiteral=" + dokumentMalLiteral);
                    }
                }
            }

            return Optional.empty();
        }

        private static <I> Instance<I> select(Class<I> cls, Instance<I> instances, Annotation anno) {
            return cls != null
                    ? instances.select(cls, anno)
                    : instances.select(anno);
        }

        private static <I> I getInstance(Instance<I> inst) {
            var i = inst.get();
            if (i.getClass().isAnnotationPresent(Dependent.class)) {
                throw new IllegalStateException(
                        "Kan ikke ha @Dependent scope bean ved Instance lookup dersom en ikke også håndtere lifecycle selv: " + i.getClass());
            }
            return i;
        }

        private static List<String> coalesce(String... vals) {
            return Arrays.asList(vals).stream().filter(v -> v != null).distinct().collect(Collectors.toList());
        }
    }
}
