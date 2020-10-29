package no.nav.foreldrepenger.melding.behandling;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.Kodeverdi;

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

    TILBAKEBETALING_ENDRING("BT-005", 6, false),
    TILBAKEKREVING("BT-007", 0, false),
    TILBAKEKREVING_REVURDERING("BT-009", 0, false),

    UDEFINERT("-", 0, false),
    ;

    private static final Set<BehandlingType> YTELSE_BEHANDLING_TYPER = Set.of(FØRSTEGANGSSØKNAD, REVURDERING);
    private static final Set<BehandlingType> ANDRE_BEHANDLING_TYPER = Set.of(KLAGE, ANKE, INNSYN);
    private static final Set<BehandlingType> TILBAKEKREVING_TYPER = Set.of(TILBAKEBETALING_ENDRING, TILBAKEKREVING, TILBAKEKREVING_REVURDERING);

    public static final String KODEVERK = "BEHANDLING_TYPE";
    private static final Map<String, BehandlingType> KODER = new LinkedHashMap<>();

    static {
        for (var v : values()) {
            if (KODER.putIfAbsent(v.kode, v) != null) {
                throw new IllegalArgumentException("Duplikat : " + v.kode);
            }
        }
    }

    @JsonIgnore
    private int behandlingstidFristUker;
    @JsonIgnore
    private Boolean behandlingstidVarselbrev;

    private String kode;

    private BehandlingType(String kode, int behandlingstidFristUker, Boolean behandlingstidVarselbrev) {
        this.kode = kode;
        this.behandlingstidFristUker = behandlingstidFristUker;
        this.behandlingstidVarselbrev = behandlingstidVarselbrev;
    }

    @JsonCreator
    public static BehandlingType fraKode(@JsonProperty(value = "kode") String kode) {
        var ad = KODER.get(kode);
        if (ad == null) {
            throw new IllegalArgumentException("Ukjent BehandlingType: " + kode);
        }
        return ad;
    }

    public static Map<String, BehandlingType> kodeMap() {
        return Collections.unmodifiableMap(KODER);
    }

    @JsonProperty
    @Override
    public String getKode() {
        return kode;
    }

    @JsonProperty
    @Override
    public String getKodeverk() {
        return KODEVERK;
    }

    public static Set<BehandlingType> getYtelseBehandlingTyper() {
        return YTELSE_BEHANDLING_TYPER;
    }

    public static Set<BehandlingType> getAndreBehandlingTyper() {
        return ANDRE_BEHANDLING_TYPER;
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
