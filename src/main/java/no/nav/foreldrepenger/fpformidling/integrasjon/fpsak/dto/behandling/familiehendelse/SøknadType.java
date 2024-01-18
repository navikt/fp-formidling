package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.dto.behandling.familiehendelse;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonValue;


@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SøknadType {
    FØDSEL("ST-001"), //$NON-NLS-1$
    ADOPSJON("ST-002"), //$NON-NLS-1$
    ;

    @JsonValue
    private final String kode;

    private SøknadType(String kode) {
        this.kode = kode;
    }

    public String getKode() {
        return kode;
    }

}

