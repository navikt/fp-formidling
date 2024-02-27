package no.nav.foreldrepenger.fpformidling.domene.dokumentdata;

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

import jakarta.enterprise.context.Dependent;
import jakarta.enterprise.inject.Instance;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.enterprise.util.AnnotationLiteral;
import jakarta.inject.Qualifier;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;

@Qualifier
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Documented
public @interface DokumentMalTypeRef {

    DokumentMalType value();

    class DokumentMalTypeRefLiteral extends AnnotationLiteral<DokumentMalTypeRef> implements DokumentMalTypeRef { //NOSONAR

        private DokumentMalType navn;

        public DokumentMalTypeRefLiteral(DokumentMalType dokumentMalKode) {
            this.navn = dokumentMalKode;
        }

        @Override
        public DokumentMalType value() {
            return navn;
        }
    }

    @SuppressWarnings("unchecked")
    public static final class Lookup {

        private Lookup() {
        }

        public static <I> Optional<I> find(Class<I> cls, DokumentMalType dokumentMal) {
            return find(cls, (CDI<I>) CDI.current(), dokumentMal);
        }

        public static <I> Optional<I> find(Class<I> cls, Instance<I> instances, DokumentMalType dokumentMal) {
            Objects.requireNonNull(instances, "instances");

            for (var dokumentMalLiteral : coalesce(dokumentMal)) {
                var inst = select(cls, instances, new DokumentMalTypeRefLiteral(dokumentMalLiteral));
                if (inst.isResolvable()) {
                    return Optional.of(getInstance(inst));
                } else {
                    if (inst.isAmbiguous()) {
                        throw new IllegalStateException(
                            "Har flere matchende instanser for klasse : " + cls.getName() + ", dokumentMalLiteral=" + dokumentMalLiteral);
                    }
                }
            }

            return Optional.empty();
        }

        private static <I> Instance<I> select(Class<I> cls, Instance<I> instances, Annotation anno) {
            return cls != null ? instances.select(cls, anno) : instances.select(anno);
        }

        private static <I> I getInstance(Instance<I> inst) {
            var i = inst.get();
            if (i.getClass().isAnnotationPresent(Dependent.class)) {
                throw new IllegalStateException(
                    "Kan ikke ha @Dependent scope bean ved Instance lookup dersom en ikke også håndtere lifecycle selv: " + i.getClass());
            }
            return i;
        }

        private static List<DokumentMalType> coalesce(DokumentMalType... vals) {
            return Arrays.stream(vals).filter(Objects::nonNull).distinct().toList();
        }
    }
}
