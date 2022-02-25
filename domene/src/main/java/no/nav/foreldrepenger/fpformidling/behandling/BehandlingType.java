package no.nav.foreldrepenger.fpformidling.behandling;

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
    FØRSTEGANGSSØKNAD("BT-002", 6, true),
    REVURDERING("BT-004", 6, false),

    KLAGE("BT-003", 12, false),
    ANKE("BT-008", 0, false),

    INNSYN("BT-006", 1, false),

    TILBAKEKREVING("BT-007", 0, false),
    TILBAKEKREVING_REVURDERING("BT-009", 0, false),

    UDEFINERT("-", 0, false),
    ;

    private static final Set<BehandlingType> YTELSE_BEHANDLING_TYPER = Set.of(FØRSTEGANGSSØKNAD, REVURDERING);
    private static final Set<BehandlingType> TILBAKEKREVING_TYPER = Set.of(TILBAKEKREVING, TILBAKEKREVING_REVURDERING);

    @JsonIgnore
    private int behandlingstidFristUker;
    @JsonIgnore
    private Boolean behandlingstidVarselbrev;

    @JsonValue
    private String kode;

    private BehandlingType(String kode, int behandlingstidFristUker, Boolean behandlingstidVarselbrev) {
        this.kode = kode;
        this.behandlingstidFristUker = behandlingstidFristUker;
        this.behandlingstidVarselbrev = behandlingstidVarselbrev;
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

    public boolean isBehandlingstidVarselbrev() {
        return behandlingstidVarselbrev;
    }

}
