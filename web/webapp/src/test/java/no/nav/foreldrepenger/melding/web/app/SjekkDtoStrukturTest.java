package no.nav.foreldrepenger.melding.web.app;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

@RunWith(Parameterized.class)
public class SjekkDtoStrukturTest {
    private static final List<String> SKIPPED = Arrays.asList("class", "kode");
    private Class<?> cls;

    public SjekkDtoStrukturTest(Class<?> cls) {
        this.cls = cls;
    }

    @Parameterized.Parameters
    public static Collection<Object[]> parameters() throws URISyntaxException {
        IndexClasses indexClasses;
        indexClasses = IndexClasses.getIndexFor(IndexClasses.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        List<Class<?>> classes = indexClasses.getClasses(
                ci -> ci.name().toString().endsWith("Dto"),
                c -> !c.isInterface());

        List<Object[]> params = new ArrayList<>();
        for (int i = 0; i < classes.size(); i++) {
            params.add(new Object[]{classes.get(i)});
        }
        return params;
    }

    @Test
    public void skal_ha_riktig_navn_på_properties_i_dto_eller_konfiguret_med_annotations() throws Exception {
        sjekkJsonProperties(cls);
    }

    private void sjekkJsonProperties(Class<?> c) throws IntrospectionException {
        List<Field> fields = Arrays.asList(c.getDeclaredFields());
        Set<String> fieldNames = fields.stream()
                .filter(f -> !f.isSynthetic() && !Modifier.isStatic(f.getModifiers()))
                .filter(f -> f.getAnnotation(JsonProperty.class) == null)
                .filter(f -> f.getAnnotation(JsonValue.class) == null)
                .filter(f -> f.getAnnotation(JsonIgnore.class) == null)
                .map(f -> f.getName()).collect(Collectors.toSet());

        if (!fieldNames.isEmpty()) {
            for (PropertyDescriptor prop : Introspector.getBeanInfo(c, c.getSuperclass()).getPropertyDescriptors()) {
                if (prop.getReadMethod() != null) {
                    Method readName = prop.getReadMethod();
                    String propName = prop.getName();
                    if (!SKIPPED.contains(propName)) {
                        if (readName.getAnnotation(JsonIgnore.class) == null
                                && readName.getAnnotation(JsonProperty.class) == null) {
                            Assertions.assertThat(propName)
                                    .as("Gettere er ikke samstemt med felt i klasse, sørg for matchende bean navn og return type eller bruk @JsonProperty/@JsonIgnore/@JsonValue til å sette navn for json struktur: "
                                            + c.getName())
                                    .isIn(fieldNames);
                        }
                    }
                }

                if (prop.getWriteMethod() != null) {
                    Method readName = prop.getWriteMethod();
                    String propName = prop.getName();
                    if (!SKIPPED.contains(propName)) {
                        if (readName.getAnnotation(JsonIgnore.class) == null
                                && readName.getAnnotation(JsonProperty.class) == null) {
                            Assertions.assertThat(propName)
                                    .as("Settere er ikke samstemt med felt i klasse, sørg for matchende bean navn og return type eller bruk @JsonProperty/@JsonIgnore/@JsonValue til å sette navn for json struktur: "
                                            + c.getName())
                                    .isIn(fieldNames);
                        }
                    }
                }
            }
        }
    }

}
