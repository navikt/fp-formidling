package no.nav.foreldrepenger.melding.web.app.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.BasisKodeverdi;

/**
 * Enkel serialisering av KodeverkTabell klasser, uten at disse trenger @JsonIgnore eller lignende. Deserialisering går
 * av seg selv normalt (får null for andre felter).
 */
public class KodeverdiSerializer extends StdSerializer<BasisKodeverdi> {


    public KodeverdiSerializer() {
        super(BasisKodeverdi.class);
    }


    @Override
    public void serialize(BasisKodeverdi value, JsonGenerator jgen, SerializerProvider provider) throws IOException {

        jgen.writeStartObject();

        jgen.writeStringField("kode", value.getKode());
        
        jgen.writeStringField("kodeverk", value.getKodeverk());

        jgen.writeEndObject();
    }

}
