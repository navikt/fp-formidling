package no.nav.foreldrepenger.melding.kodeverk;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import com.fasterxml.jackson.databind.ObjectMapper;

public class KodeverkFraJson {

    public static final String FILE_NAME_PREFIX = "kodeverk_";
    public static final String FILE_NAME_SUFFIX = ".json";

    public KodeverkFraJson() {

    }

    public <V> List<V> lesKodeverkFraFil(Class<?> cls) {

        String name;
        if (cls.isAnnotationPresent(DiscriminatorValue.class)) {
            name = cls.getAnnotation(DiscriminatorValue.class).value();
        } else if (cls.isAnnotationPresent(Entity.class)) {
            name = cls.getAnnotation(Entity.class).name();
        } else {
            throw new IllegalArgumentException("Mangler @Entity eller @DiscriminatorValue på " + cls);
        }

        ObjectMapper om = new ObjectMapper();
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        String fullName = FILE_NAME_PREFIX + name + FILE_NAME_SUFFIX;
        try (InputStream is = getClass().getResourceAsStream("/" + fullName);) {
            List<V> read = om.readerFor(List.class).readValue(is);
            return read;
        } catch (IOException e) {
            throw new IllegalStateException("Kunne ikke lese " + fullName + " for class:" + cls, e);
        }

    }

}
