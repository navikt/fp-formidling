package no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi;

import java.util.Set;

public enum BehandlingÅrsakType {

    RE_MANGLER_FØDSEL,
    RE_MANGLER_FØDSEL_I_PERIODE,
    RE_AVVIK_ANTALL_BARN,
    RE_FEIL_I_LOVANDVENDELSE,
    RE_FEIL_REGELVERKSFORSTÅELSE,
    RE_FEIL_ELLER_ENDRET_FAKTA,
    RE_FEIL_PROSESSUELL,
    RE_ENDRING_FRA_BRUKER,
    RE_ENDRET_INNTEKTSMELDING,
    BERØRT_BEHANDLING,
    RE_ANNET,
    RE_SATS_REGULERING,

    FEIL_PRAKSIS_BG_AAP_KOMBI,
    //For automatiske informasjonsbrev
    INFOBREV_BEHANDLING,
    INFOBREV_OPPHOLD,
    //For å vurdere opphør av ytelse
    OPPHØR_YTELSE_NYTT_BARN,
    // Manuelt opprettet revurdering (obs: årsakene kan også bli satt på en automatisk opprettet revurdering)
    RE_KLAGE_UTEN_END_INNTEKT,
    RE_KLAGE_MED_END_INNTEKT,
    RE_OPPLYSNINGER_OM_MEDLEMSKAP,
    RE_OPPLYSNINGER_OM_OPPTJENING,
    RE_OPPLYSNINGER_OM_FORDELING,
    RE_OPPLYSNINGER_OM_INNTEKT,
    RE_OPPLYSNINGER_OM_FØDSEL,
    RE_OPPLYSNINGER_OM_DØD,
    RE_OPPLYSNINGER_OM_SØKERS_REL,
    RE_OPPLYSNINGER_OM_SØKNAD_FRIST,
    RE_OPPLYSNINGER_OM_BEREGNINGSGRUNNLAG,

    ETTER_KLAGE,

    RE_HENDELSE_FØDSEL,
    RE_HENDELSE_DØD_FORELDER,
    RE_HENDELSE_DØD_BARN,
    RE_HENDELSE_DØDFØDSEL,

    // La stå
    UDEFINERT;

    public static Set<BehandlingÅrsakType> årsakerEtterKlageBehandling() {
        return Set.of(ETTER_KLAGE, RE_KLAGE_MED_END_INNTEKT, RE_KLAGE_UTEN_END_INNTEKT);
    }
}
