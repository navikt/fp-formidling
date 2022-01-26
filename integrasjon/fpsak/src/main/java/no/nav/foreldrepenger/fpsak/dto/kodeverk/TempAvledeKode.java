package no.nav.foreldrepenger.fpsak.dto.kodeverk;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.TextNode;

/**
 * @deprecated skal fjernes når alle apps serialiserer kodeverdi-enums som string isf objekt
 */
@Deprecated(since = "2020-09-17") // NOSONAR
public class TempAvledeKode {

    private static final Logger LOG = LoggerFactory.getLogger(TempAvledeKode.class);

    @SuppressWarnings("rawtypes")
    public static String getVerdi(Class<? extends Enum> enumCls, Object node, String key) {
        String kode;
        if (node instanceof String s) { // NOSONAR
            kode = s;
        } else {
            if (node instanceof JsonNode j) {
                kode = j.get(key).asText();
            } else if (node instanceof TextNode t) { // NOSONAR
                kode = t.asText();
            } else if (node instanceof Map m) {
                kode = (String) m.get(key);
            } else {
                throw new IllegalArgumentException("Støtter ikke node av type: " + node.getClass() + " for enum:" + enumCls.getName());
            }
            String kodeverk = "uspesifisert"; // NOSONAR
            try {
                if (node instanceof JsonNode j) {
                    kodeverk = j.get("kodeverk").asText(); // NOSONAR
                } else if (node instanceof TextNode t) { // NOSONAR
                    kodeverk = t.asText(); //NOSONAR
                } else if (node instanceof Map m) { //  NOSONAR
                    kodeverk = (String) m.get("kodeverk"); // NOSONAR
                }
            } catch (Exception e) {
                LOG.info("KODEVERK-OBJEKT: tempavledekode kalt uten at det finnes kodeverk - kode {}", kode);
            }
            // NOSONAR nable når gått over LOG.info("KODEVERK-OBJEKT: mottok kodeverdiobjekt som ikke var String - kode {} fra kodeverk {} ", kode, kodeverk);
        }
        return kode;
    }

    @SuppressWarnings("rawtypes")
    public static KodeDto getVerdiKodeDto(Object node, String key) {
        String kode;
        String kodeverk = null;
        if (node instanceof String s) {
            kode = s;
        } else {
            if (node instanceof JsonNode j) {
                kode = j.get(key).asText();
            } else if (node instanceof TextNode t) { // NOSONAR
                kode = t.asText();
            } else if (node instanceof Map m) {
                kode = (String) m.get(key);
            } else {
                throw new IllegalArgumentException("Støtter ikke node av type: " + node.getClass() + " for KodeDto");
            }
            kodeverk = "uspesifisert";
            try {
                if (node instanceof JsonNode j) {
                    kodeverk = j.get("kodeverk").asText();
                } else if (node instanceof TextNode t) { // NOSONAR
                    kodeverk = t.asText();
                } else if (node instanceof Map m) { // NOSONAR
                    kodeverk = (String) m.get("kodeverk");
                }
            } catch (Exception e) {
                LOG.info("KODEVERK-OBJEKT: tempavledekode kalt uten at det finnes kodeverk - kode {}", kode);
            }
            // NOSONAR Enable når gått over LOG.info("KODEVERK-OBJEKT: mottok kodeverdiobjekt som ikke var String - kode {} fra kodeverk {} ", kode, kodeverk);
        }
        return new KodeDto(kodeverk, kode);
    }

}