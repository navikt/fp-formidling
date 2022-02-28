package no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility;
import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonValue;

@JsonFormat(shape = Shape.OBJECT)
@JsonAutoDetect(getterVisibility = Visibility.NONE, setterVisibility = Visibility.NONE, fieldVisibility = Visibility.ANY)
public enum BehandlingÅrsakType implements Kodeverdi {

    RE_MANGLER_FØDSEL("RE-MF"),
    RE_MANGLER_FØDSEL_I_PERIODE("RE-MFIP"),
    RE_AVVIK_ANTALL_BARN("RE-AVAB"),
    RE_FEIL_I_LOVANDVENDELSE("RE-LOV"),
    RE_FEIL_REGELVERKSFORSTÅELSE("RE-RGLF"),
    RE_FEIL_ELLER_ENDRET_FAKTA("RE-FEFAKTA"),
    RE_FEIL_PROSESSUELL("RE-PRSSL"),
    RE_ENDRING_FRA_BRUKER("RE-END-FRA-BRUKER"),
    RE_ENDRET_INNTEKTSMELDING("RE-END-INNTEKTSMELD"),
    BERØRT_BEHANDLING("BERØRT-BEHANDLING"),
    RE_ANNET("RE-ANNET"),
    RE_SATS_REGULERING("RE-SATS-REGULERING"),
    //For automatiske informasjonsbrev
    INFOBREV_BEHANDLING("INFOBREV_BEHANDLING"),
    INFOBREV_OPPHOLD("INFOBREV_OPPHOLD"),
    //For å vurdere opphør av ytelse
    OPPHØR_YTELSE_NYTT_BARN("OPPHØR-NYTT-BARN"),
    // Manuelt opprettet revurdering (obs: årsakene kan også bli satt på en automatisk opprettet revurdering)
    RE_KLAGE_UTEN_END_INNTEKT("RE-KLAG-U-INNTK"),
    RE_KLAGE_MED_END_INNTEKT("RE-KLAG-M-INNTK"),
    RE_OPPLYSNINGER_OM_MEDLEMSKAP("RE-MDL"),
    RE_OPPLYSNINGER_OM_OPPTJENING("RE-OPTJ"),
    RE_OPPLYSNINGER_OM_FORDELING("RE-FRDLING"),
    RE_OPPLYSNINGER_OM_INNTEKT("RE-INNTK"),
    RE_OPPLYSNINGER_OM_FØDSEL("RE-FØDSEL"),
    RE_OPPLYSNINGER_OM_DØD("RE-DØD"),
    RE_OPPLYSNINGER_OM_SØKERS_REL("RE-SRTB"),
    RE_OPPLYSNINGER_OM_SØKNAD_FRIST("RE-FRIST"),
    RE_OPPLYSNINGER_OM_BEREGNINGSGRUNNLAG("RE-BER-GRUN"),

    ETTER_KLAGE("ETTER_KLAGE"),

    RE_HENDELSE_FØDSEL("RE-HENDELSE-FØDSEL"),
    RE_HENDELSE_DØD_FORELDER("RE-HENDELSE-DØD-F"),
    RE_HENDELSE_DØD_BARN("RE-HENDELSE-DØD-B"),
    RE_HENDELSE_DØDFØDSEL("RE-HENDELSE-DØDFØD"),

    // La stå
    @JsonEnumDefaultValue
    UDEFINERT("-"),

    ;

    @JsonValue
    private String kode;


    private BehandlingÅrsakType(String kode) {
        this.kode = kode;
    }

    @Override
    public String getKode() {
        return kode;
    }

    public static Set<BehandlingÅrsakType> årsakerForAutomatiskRevurdering() {
        return Set.of(RE_MANGLER_FØDSEL, RE_MANGLER_FØDSEL_I_PERIODE, RE_AVVIK_ANTALL_BARN);
    }

    public static Set<BehandlingÅrsakType> årsakerForEtterkontroll() {
        return Set.of(RE_MANGLER_FØDSEL, RE_MANGLER_FØDSEL_I_PERIODE, RE_AVVIK_ANTALL_BARN);
    }

    public static Set<BehandlingÅrsakType> årsakerEtterKlageBehandling() {
        return Set.of(ETTER_KLAGE, RE_KLAGE_MED_END_INNTEKT, RE_KLAGE_UTEN_END_INNTEKT);
    }

    public static Set<BehandlingÅrsakType> årsakerRelatertTilDød() {
        return Set.of(RE_OPPLYSNINGER_OM_DØD, RE_HENDELSE_DØD_BARN, RE_HENDELSE_DØD_FORELDER, RE_HENDELSE_DØDFØDSEL);
    }
}
