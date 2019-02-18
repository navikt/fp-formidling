package no.nav.foreldrepenger.melding.kodeverk;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.junit.Rule;
import org.junit.Test;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import io.github.lukehutch.fastclasspathscanner.FastClasspathScanner;
import io.github.lukehutch.fastclasspathscanner.scanner.ScanResult;
import no.nav.foreldrepenger.melding.dbstoette.UnittestRepositoryRule;

/**
 * Genererer kodeverk til bruk i enhetstester ved bruk av AbstractTestScenario.
 */
public class KodeverkTilJsonProducerTest {

    @Rule
    public final UnittestRepositoryRule repoRule = new UnittestRepositoryRule();
    private final EntityManager em = repoRule.getEntityManager();

    public static File getOutputDir() {
        File currentDir = new File(".");

        File path = new File(currentDir, "target/test-classes");
        while (!path.exists() && currentDir.getParentFile() != null) {
            currentDir = currentDir.getParentFile();
            path = new File(currentDir, "target/test-classes");
        }

        return path;

    }

    @Test
    public void skal_dumpe_kodeverk_til_json_format_for_bruk_i_scenario_tester() throws Exception {

        Map<Class<?>, Object> dump = new TreeMap<>(Comparator.comparing((Class<?> c) -> c.getName()));

        ScanResult scan = new FastClasspathScanner("no.nav.foreldrepenger")
                .strictWhitelist()
                .scan();

        dump.putAll(getSubclassesOf(scan, Kodeliste.class));

        writeToFile(dump);

    }

    private Map<? extends Class<?>, ? extends Object> getSubclassesOf(ScanResult scan, Class<?> superClass) throws ClassNotFoundException {
        List<String> subclassesOfKodeliste = scan.getNamesOfSubclassesOf(superClass);
        Map<Class<?>, Object> dump = new LinkedHashMap<>();
        for (String s : subclassesOfKodeliste) {
            Class<?> cls = Class.forName(s);
            Query query = em.createQuery("from " + cls.getName());
            List<?> results = query.getResultList();
            dump.put(cls, results);
        }
        return dump;
    }

    private void writeToFile(Map<Class<?>, Object> dump) throws IOException, JsonGenerationException, JsonMappingException {
        ObjectMapper om = new ObjectMapper();
        om.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        om.registerModule(new MyModule());
        om.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL);

        ObjectWriter objectWriter = om.writerWithDefaultPrettyPrinter();

        File outputDir = getOutputDir();
        for (Map.Entry<Class<?>, Object> entry : dump.entrySet()) {

            if (!entry.getKey().isAnnotationPresent(DiscriminatorValue.class)) {
                if (entry.getKey().isAnnotationPresent(Entity.class)) {
                    String name = entry.getKey().getAnnotation(Entity.class).name();
                    writeKodeverk(objectWriter, outputDir, entry.getValue(), name);
                } else {
                    System.out.println("Mangler @Entity eller @Discriminator:" + entry.getKey());
                }
            } else {
                String name = entry.getKey().getAnnotation(DiscriminatorValue.class).value();
                writeKodeverk(objectWriter, outputDir, entry.getValue(), name);
            }
        }
    }

    private void writeKodeverk(ObjectWriter objectWriter, File outputDir, Object value, String name)
            throws IOException, JsonGenerationException, JsonMappingException {
        File outputFile = new File(outputDir, KodeverkFraJson.FILE_NAME_PREFIX + name + KodeverkFraJson.FILE_NAME_SUFFIX);
        outputFile.delete();
        objectWriter.writeValue(outputFile, value);
    }

    @JsonIdentityInfo(generator = ObjectIdGenerators.IntSequenceGenerator.class, property = "@kode")
    @JsonIgnoreProperties({"endretAv", "opprettetAv", "opprettetTidspunkt", "endretTidspunkt", "id", "gyldigTilOgMed",
            "gyldigFraOgMed", "displayNavn", "beskrivelse"})
    public static class PropertyFilterKodeverkTabellMixIn {
    }

    @JsonIgnoreProperties({"endretAv", "opprettetAv", "opprettetTidspunkt", "endretTidspunkt", "id", "gyldigTilOgMed",
            "gyldigFraOgMed", "displayNavn", "beskrivelse"})
    public static class PropertyFilterMixIn {
    }

    public class MyModule extends SimpleModule {
        @SuppressWarnings("deprecation")
        public MyModule() {
            super("ModuleName", new Version(0, 0, 1, null));
        }

        @Override
        public void setupModule(SetupContext context) {
            context.setMixInAnnotations(Kodeliste.class, PropertyFilterMixIn.class);
            context.setMixInAnnotations(KodeverkTabell.class, PropertyFilterKodeverkTabellMixIn.class);
        }
    }

}
