package no.nav.foreldrepenger.melding.datamapper.mal;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * Håndterer serialisering/deserialisering av data strukturer til json for strukturerte felter til dokumentproduksjon.
 * Kun felter vil serialiseres/deserialiseres, så endring i navn må en være forsiktig med (bør annoteres med
 * {@link JsonProperty} for å beskytte mot det)
 */
public class FlettefeltJsonObjectMapper {

    private static final ObjectMapper OM;

    private FlettefeltJsonObjectMapper() {
        // Skal ikke instantieres
    }

    static {
        OM = new ObjectMapper();
        OM.registerModule(new JavaTimeModule());
        OM.registerModule(new Jdk8Module());
        OM.setVisibility(PropertyAccessor.GETTER, Visibility.NONE);
        OM.setVisibility(PropertyAccessor.SETTER, Visibility.NONE);
        OM.setVisibility(PropertyAccessor.FIELD, Visibility.ANY);
        OM.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OM.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        OM.setVisibility(PropertyAccessor.CREATOR, Visibility.ANY);
    }

    public static String toJson(Object data) {
        try {
            return OM.writerWithDefaultPrettyPrinter().writeValueAsString(data);
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("Kunne ikke serialiseres til json: " + data, e);
        }
    }

    public static <T> T readValue(String src, Class<T> targetClass) {
        try {
            return OM.readerFor(targetClass).readValue(src);
        } catch (IOException e) {
            throw new IllegalArgumentException("Kunne ikke deserialiser fra json til [" + targetClass.getName() + "]: " + src, e);
        }
    }
}
