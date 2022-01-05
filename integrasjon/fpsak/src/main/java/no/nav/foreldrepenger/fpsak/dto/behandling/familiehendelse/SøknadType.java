package no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse;

import java.io.IOException;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;


@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@JsonDeserialize(using = SøknadType.SøknadTypeDeserializer.class)
public enum SøknadType {
    FØDSEL("ST-001"), //$NON-NLS-1$
    ADOPSJON("ST-002"), //$NON-NLS-1$
    ;

    private final String kode;

    private SøknadType(String kode) {
        this.kode = kode;
    }

    public static SøknadType fra(String kode) {
        for (SøknadType st : values()) {
            if (Objects.equals(st.kode, kode)) {
                return st;
            }
        }
        throw new IllegalArgumentException("Ukjent " + SøknadType.class.getSimpleName() + ": " + kode); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public String getKode() {
        return kode;
    }

    static class SøknadTypeDeserializer extends StdDeserializer<SøknadType> {

        public SøknadTypeDeserializer() {
            super(SøknadType.class);
        }

        @Override
        public SøknadType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            // [JACKSON-620] Empty String can become null...

            if (p.hasToken(JsonToken.VALUE_STRING)
                    && ctxt.isEnabled(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
                    && p.getText().length() == 0) {
                return null;
            }

            String kode = null;

            if (Objects.equals(p.getCurrentToken(), JsonToken.START_OBJECT)) {
                while (!(JsonToken.END_OBJECT.equals(p.getCurrentToken()))) {
                    p.nextToken();
                    String name = p.getCurrentName();
                    String value = p.getValueAsString();
                    if (Objects.equals("kode", name) && !Objects.equals("kode", value)) { //$NON-NLS-1$ //$NON-NLS-2$
                        kode = value;
                    }
                }
            }

            return SøknadType.fra(kode);
        }

    }


}

