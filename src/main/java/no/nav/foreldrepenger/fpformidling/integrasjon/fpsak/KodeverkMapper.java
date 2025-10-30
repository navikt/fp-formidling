package no.nav.foreldrepenger.fpformidling.integrasjon.fpsak;

import no.nav.foreldrepenger.fpformidling.domene.behandling.BehandlingType;
import no.nav.foreldrepenger.fpformidling.domene.behandling.KonsekvensForYtelsen;
import no.nav.foreldrepenger.fpformidling.domene.beregningsgrunnlag.BeregningHjemmel;
import no.nav.foreldrepenger.fpformidling.domene.klage.KlageAvvistÅrsak;
import no.nav.foreldrepenger.fpformidling.domene.personopplysning.RelasjonsRolleType;
import no.nav.foreldrepenger.fpformidling.domene.uttak.fp.StønadskontoType;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.VilkårType;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.BehandlingÅrsakType;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.kodeverk.HjemmelDto;

public final class KodeverkMapper {
    private KodeverkMapper() {
    }

    public static VilkårType mapVilkårType(BrevGrunnlagDto.Behandlingsresultat.VilkårType v) {
        return switch (v) {
            case FØDSELSVILKÅRET_MOR -> VilkårType.FØDSELSVILKÅRET_MOR;
            case FØDSELSVILKÅRET_FAR_MEDMOR -> VilkårType.FØDSELSVILKÅRET_FAR_MEDMOR;
            case MEDLEMSKAPSVILKÅRET -> VilkårType.MEDLEMSKAPSVILKÅRET;
            case MEDLEMSKAPSVILKÅRET_FORUTGÅENDE -> VilkårType.MEDLEMSKAPSVILKÅRET_FORUTGÅENDE;
            case MEDLEMSKAPSVILKÅRET_LØPENDE -> VilkårType.MEDLEMSKAPSVILKÅRET_LØPENDE;
            case SØKNADSFRISTVILKÅRET -> VilkårType.SØKNADSFRISTVILKÅRET;
            case SØKERSOPPLYSNINGSPLIKT -> VilkårType.SØKERSOPPLYSNINGSPLIKT;
            case OPPTJENINGSPERIODEVILKÅR -> VilkårType.OPPTJENINGSPERIODEVILKÅR;
            case OPPTJENINGSVILKÅRET -> VilkårType.OPPTJENINGSVILKÅRET;
            case BEREGNINGSGRUNNLAGVILKÅR -> VilkårType.BEREGNINGSGRUNNLAGVILKÅR;
            case SVANGERSKAPSPENGERVILKÅR -> VilkårType.SVANGERSKAPSPENGERVILKÅR;
            case OMSORGSOVERTAKELSEVILKÅR -> VilkårType.OMSORGSOVERTAKELSEVILKÅR;
            case UDEFINERT -> VilkårType.UDEFINERT;
        };
    }

    public static RelasjonsRolleType mapRelasjonsRolle(BrevGrunnlagDto.RelasjonsRolleType relasjonsRolleType) {
        return switch (relasjonsRolleType) {
            case FARA -> RelasjonsRolleType.FARA;
            case MORA -> RelasjonsRolleType.MORA;
            case MEDMOR -> RelasjonsRolleType.MMOR;
        };
    }

    public static BrevGrunnlagDto.BehandlingÅrsakType mapBehandlingÅrsak(BehandlingÅrsakType bå) {
        return switch (bå) {
            case RE_MANGLER_FØDSEL -> BrevGrunnlagDto.BehandlingÅrsakType.RE_MANGLER_FØDSEL;
            case RE_MANGLER_FØDSEL_I_PERIODE -> BrevGrunnlagDto.BehandlingÅrsakType.RE_MANGLER_FØDSEL_I_PERIODE;
            case RE_AVVIK_ANTALL_BARN -> BrevGrunnlagDto.BehandlingÅrsakType.RE_AVVIK_ANTALL_BARN;
            case RE_FEIL_I_LOVANDVENDELSE -> BrevGrunnlagDto.BehandlingÅrsakType.RE_FEIL_I_LOVANDVENDELSE;
            case RE_FEIL_REGELVERKSFORSTÅELSE -> BrevGrunnlagDto.BehandlingÅrsakType.RE_FEIL_REGELVERKSFORSTÅELSE;
            case RE_FEIL_ELLER_ENDRET_FAKTA -> BrevGrunnlagDto.BehandlingÅrsakType.RE_FEIL_ELLER_ENDRET_FAKTA;
            case RE_FEIL_PROSESSUELL -> BrevGrunnlagDto.BehandlingÅrsakType.RE_FEIL_PROSESSUELL;
            case RE_ENDRING_FRA_BRUKER -> BrevGrunnlagDto.BehandlingÅrsakType.RE_ENDRING_FRA_BRUKER;
            case RE_ENDRET_INNTEKTSMELDING -> BrevGrunnlagDto.BehandlingÅrsakType.RE_ENDRET_INNTEKTSMELDING;
            case BERØRT_BEHANDLING -> BrevGrunnlagDto.BehandlingÅrsakType.BERØRT_BEHANDLING;
            case RE_ANNET -> BrevGrunnlagDto.BehandlingÅrsakType.RE_ANNET;
            case RE_SATS_REGULERING -> BrevGrunnlagDto.BehandlingÅrsakType.RE_SATS_REGULERING;
            case FEIL_PRAKSIS_BG_AAP_KOMBI -> BrevGrunnlagDto.BehandlingÅrsakType.FEIL_PRAKSIS_BG_AAP_KOMBI;
            case INFOBREV_BEHANDLING -> BrevGrunnlagDto.BehandlingÅrsakType.INFOBREV_BEHANDLING;
            case INFOBREV_OPPHOLD -> BrevGrunnlagDto.BehandlingÅrsakType.INFOBREV_OPPHOLD;
            case OPPHØR_YTELSE_NYTT_BARN -> BrevGrunnlagDto.BehandlingÅrsakType.OPPHØR_YTELSE_NYTT_BARN;
            case RE_KLAGE_UTEN_END_INNTEKT -> BrevGrunnlagDto.BehandlingÅrsakType.RE_KLAGE_UTEN_END_INNTEKT;
            case RE_KLAGE_MED_END_INNTEKT -> BrevGrunnlagDto.BehandlingÅrsakType.RE_KLAGE_MED_END_INNTEKT;
            case RE_OPPLYSNINGER_OM_MEDLEMSKAP -> BrevGrunnlagDto.BehandlingÅrsakType.RE_OPPLYSNINGER_OM_MEDLEMSKAP;
            case RE_OPPLYSNINGER_OM_OPPTJENING -> BrevGrunnlagDto.BehandlingÅrsakType.RE_OPPLYSNINGER_OM_OPPTJENING;
            case RE_OPPLYSNINGER_OM_FORDELING -> BrevGrunnlagDto.BehandlingÅrsakType.RE_OPPLYSNINGER_OM_FORDELING;
            case RE_OPPLYSNINGER_OM_INNTEKT -> BrevGrunnlagDto.BehandlingÅrsakType.RE_OPPLYSNINGER_OM_INNTEKT;
            case RE_OPPLYSNINGER_OM_FØDSEL -> BrevGrunnlagDto.BehandlingÅrsakType.RE_OPPLYSNINGER_OM_FØDSEL;
            case RE_OPPLYSNINGER_OM_DØD -> BrevGrunnlagDto.BehandlingÅrsakType.RE_OPPLYSNINGER_OM_DØD;
            case RE_OPPLYSNINGER_OM_SØKERS_REL -> BrevGrunnlagDto.BehandlingÅrsakType.RE_OPPLYSNINGER_OM_SØKERS_REL;
            case RE_OPPLYSNINGER_OM_SØKNAD_FRIST -> BrevGrunnlagDto.BehandlingÅrsakType.RE_OPPLYSNINGER_OM_SØKNAD_FRIST;
            case RE_OPPLYSNINGER_OM_BEREGNINGSGRUNNLAG -> BrevGrunnlagDto.BehandlingÅrsakType.RE_OPPLYSNINGER_OM_BEREGNINGSGRUNNLAG;
            case ETTER_KLAGE -> BrevGrunnlagDto.BehandlingÅrsakType.ETTER_KLAGE;
            case RE_HENDELSE_FØDSEL -> BrevGrunnlagDto.BehandlingÅrsakType.RE_HENDELSE_FØDSEL;
            case RE_HENDELSE_DØD_FORELDER -> BrevGrunnlagDto.BehandlingÅrsakType.RE_HENDELSE_DØD_FORELDER;
            case RE_HENDELSE_DØD_BARN -> BrevGrunnlagDto.BehandlingÅrsakType.RE_HENDELSE_DØD_BARN;
            case RE_HENDELSE_DØDFØDSEL -> BrevGrunnlagDto.BehandlingÅrsakType.RE_HENDELSE_DØDFØDSEL;
            case UDEFINERT -> BrevGrunnlagDto.BehandlingÅrsakType.UDEFINERT;
        };
    }

    public static BehandlingType mapBehandlingType(BrevGrunnlagDto.BehandlingType behandlingType) {
        return switch (behandlingType) {
            case FØRSTEGANGSSØKNAD -> BehandlingType.FØRSTEGANGSSØKNAD;
            case REVURDERING -> BehandlingType.REVURDERING;
            case KLAGE -> BehandlingType.KLAGE;
            case ANKE -> BehandlingType.ANKE;
            case INNSYN -> BehandlingType.INNSYN;
            case TILBAKEKREVING -> BehandlingType.TILBAKEKREVING;
            case TILBAKEKREVING_REVURDERING -> BehandlingType.TILBAKEKREVING_REVURDERING;
        };
    }

    public static KlageAvvistÅrsak mapKlageAvvistÅrsak(BrevGrunnlagDto.KlageBehandling.KlageAvvistÅrsak årsak) {
        return switch (årsak) {
            case KLAGET_FOR_SENT -> KlageAvvistÅrsak.KLAGET_FOR_SENT;
            case KLAGE_UGYLDIG -> KlageAvvistÅrsak.KLAGE_UGYLDIG;
            case IKKE_PÅKLAGD_VEDTAK -> KlageAvvistÅrsak.IKKE_PAKLAGD_VEDTAK;
            case KLAGER_IKKE_PART -> KlageAvvistÅrsak.KLAGER_IKKE_PART;
            case IKKE_KONKRET -> KlageAvvistÅrsak.IKKE_KONKRET;
            case IKKE_SIGNERT -> KlageAvvistÅrsak.IKKE_SIGNERT;
            case UDEFINERT -> KlageAvvistÅrsak.UDEFINERT;
        };
    }

    public static KonsekvensForYtelsen mapKonsekvensForYtelsen(BrevGrunnlagDto.Behandlingsresultat.KonsekvensForYtelsen konsekvensForYtelsen) {
        return switch (konsekvensForYtelsen) {
            case FORELDREPENGER_OPPHØRER -> KonsekvensForYtelsen.FORELDREPENGER_OPPHØRER;
            case ENDRING_I_BEREGNING -> KonsekvensForYtelsen.ENDRING_I_BEREGNING;
            case ENDRING_I_UTTAK -> KonsekvensForYtelsen.ENDRING_I_UTTAK;
            case ENDRING_I_FORDELING_AV_YTELSEN -> KonsekvensForYtelsen.ENDRING_I_FORDELING_AV_YTELSEN;
            case INGEN_ENDRING -> KonsekvensForYtelsen.INGEN_ENDRING;
            case UDEFINERT -> KonsekvensForYtelsen.UDEFINERT;
        };
    }

    public static BeregningHjemmel mapBeregningHjemmel(HjemmelDto hjemmel) {
        return switch (hjemmel) {
            case F_14_7 -> BeregningHjemmel.F_14_7;
            case F_14_7_8_28_8_30 -> BeregningHjemmel.F_14_7_8_28_8_30;
            case F_14_7_8_30 -> BeregningHjemmel.F_14_7_8_30;
            case F_14_7_8_35 -> BeregningHjemmel.F_14_7_8_35;
            case F_14_7_8_38 -> BeregningHjemmel.F_14_7_8_38;
            case F_14_7_8_40 -> BeregningHjemmel.F_14_7_8_40;
            case F_14_7_8_41 -> BeregningHjemmel.F_14_7_8_41;
            case F_14_7_8_42 -> BeregningHjemmel.F_14_7_8_42;
            case F_14_7_8_43 -> BeregningHjemmel.F_14_7_8_43;
            case F_14_7_8_47 -> BeregningHjemmel.F_14_7_8_47;
            case F_14_7_8_49 -> BeregningHjemmel.F_14_7_8_49;
            case null -> BeregningHjemmel.UDEFINERT;
        };
    }

    public static StønadskontoType mapStønadskontoType(BrevGrunnlagDto.Foreldrepenger.TrekkontoType trekkontoType) {
        return switch (trekkontoType) {
            case FELLESPERIODE -> StønadskontoType.FELLESPERIODE;
            case MØDREKVOTE -> StønadskontoType.MØDREKVOTE;
            case FEDREKVOTE -> StønadskontoType.FEDREKVOTE;
            case FORELDREPENGER -> StønadskontoType.FORELDREPENGER;
            case FORELDREPENGER_FØR_FØDSEL -> StønadskontoType.FORELDREPENGER_FØR_FØDSEL;
            case UDEFINERT -> StønadskontoType.UDEFINERT;
        };
    }
}
