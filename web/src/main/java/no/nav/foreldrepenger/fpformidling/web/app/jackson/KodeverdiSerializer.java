package no.nav.foreldrepenger.fpformidling.web.app.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Kodeverdi;

/**
 * Enkel serialisering av KodeverkTabell klasser, uten at disse
 * trenger @JsonIgnore eller lignende. Deserialisering går av seg selv normalt
 * (får null for andre felter).
 */
@SuppressWarnings("java:S2055")
public class KodeverdiSerializer extends StdSerializer<Kodeverdi> {

    public KodeverdiSerializer() {
        super(Kodeverdi.class);
    }

    @Override
    public void serialize(Kodeverdi value, JsonGenerator jgen, SerializerProvider provider) throws IOException {
        jgen.writeStartObject();
        jgen.writeStringField("kode", value.getKode());
        jgen.writeStringField("kodeverk", value.getKodeverk());
        jgen.writeEndObject();
    }

}
