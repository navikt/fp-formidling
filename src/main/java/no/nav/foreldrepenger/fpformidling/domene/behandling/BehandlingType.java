package no.nav.foreldrepenger.fpformidling.domene.behandling;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonValue;

import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.Kodeverdi;

@JsonFormat(shape = Shape.OBJECT)
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public enum BehandlingType implements Kodeverdi {

    /**
     * Konstanter for å skrive ned kodeverdi. For å hente ut andre data konfigurert, må disse leses fra databasen (eks.
     * for å hente offisiell kode for et Nav kodeverk).
     */
    FØRSTEGANGSSØKNAD("BT-002", 6),
    REVURDERING("BT-004", 6),

    KLAGE("BT-003", 12),
    ANKE("BT-008", 0),

    INNSYN("BT-006", 1),

    TILBAKEKREVING("BT-007", 0),
    TILBAKEKREVING_REVURDERING("BT-009", 0),

    UDEFINERT("-", 0),
    ;

    private static final Set<BehandlingType> YTELSE_BEHANDLING_TYPER = Set.of(FØRSTEGANGSSØKNAD, REVURDERING);
    private static final Set<BehandlingType> TILBAKEKREVING_TYPER = Set.of(TILBAKEKREVING, TILBAKEKREVING_REVURDERING);

    @JsonIgnore
    private int behandlingstidFristUker;

    @JsonValue
    private String kode;

    BehandlingType(String kode, int behandlingstidFristUker) {
        this.kode = kode;
        this.behandlingstidFristUker = behandlingstidFristUker;
    }

    @Override
    public String getKode() {
        return kode;
    }

    public boolean erYtelseBehandlingType() {
        return YTELSE_BEHANDLING_TYPER.contains(this);
    }

    public boolean erTilbakekrevingBehandlingType() {
        return TILBAKEKREVING_TYPER.contains(this);
    }

    public int getBehandlingstidFristUker() {
        return behandlingstidFristUker;
    }

}
