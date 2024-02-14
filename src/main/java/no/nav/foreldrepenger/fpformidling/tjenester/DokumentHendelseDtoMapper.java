package no.nav.foreldrepenger.fpformidling.tjenester;

import java.util.Optional;
import java.util.UUID;

import no.nav.foreldrepenger.fpformidling.domene.behandling.RevurderingVarslingÅrsak;
import no.nav.foreldrepenger.fpformidling.domene.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.domene.vedtak.Vedtaksbrev;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.kontrakter.formidling.kodeverk.DokumentMal;
import no.nav.foreldrepenger.kontrakter.formidling.kodeverk.RevurderingÅrsak;
import no.nav.foreldrepenger.kontrakter.formidling.kodeverk.YtelseType;
import no.nav.foreldrepenger.kontrakter.formidling.v1.DokumentbestillingDto;
import no.nav.foreldrepenger.kontrakter.formidling.v1.DokumentbestillingV2Dto;
import no.nav.foreldrepenger.kontrakter.formidling.v3.DokumentBestillingDto;
import no.nav.foreldrepenger.kontrakter.formidling.v3.DokumentForhåndsvisDto;

public final class DokumentHendelseDtoMapper {

    private DokumentHendelseDtoMapper() {
    }

    public static DokumentHendelse mapFra(DokumentbestillingV2Dto dokumentbestilling) {
        return DokumentHendelse.builder()
            .medBehandlingUuid(dokumentbestilling.behandlingUuid())
            .medBestillingUuid(dokumentbestilling.dokumentbestillingUuid())
            .medYtelseType(utledYtelseType(dokumentbestilling.ytelseType()))
            .medFritekst(dokumentbestilling.fritekst())
            .medDokumentMalType(utleddokumentMalType(dokumentbestilling.dokumentMal()))
            .medRevurderingVarslingÅrsak(utledRevurderingVarslingsårsak(dokumentbestilling.arsakskode()))
            .build();
    }

    public static DokumentHendelse mapFra(DokumentbestillingDto brevDto) {
        return DokumentHendelse.builder()
            .medBehandlingUuid(brevDto.getBehandlingUuid())
            .medBestillingUuid(brevDto.getDokumentbestillingUuid() != null ? brevDto.getDokumentbestillingUuid() : UUID.randomUUID())
            .medYtelseType(utledYtelseType(brevDto.getFagsakYtelseType()))
            .medFritekst(brevDto.getFritekst())
            .medTittel(brevDto.getTittel())
            .medDokumentMalType(utleddokumentMalType(brevDto.getDokumentMal()))
            .medRevurderingVarslingÅrsak(utledRevurderingVarslingsårsak(brevDto.getArsakskode()))
            .medGjelderVedtak(brevDto.isGjelderVedtak())
            .medVedtaksbrev(utledVedtaksbrev(brevDto.getAutomatiskVedtaksbrev()))
            .build();
    }

    public static DokumentHendelse mapFra(DokumentForhåndsvisDto forhåndsvisDto) {
        return DokumentHendelse.builder()
            .medBehandlingUuid(forhåndsvisDto.behandlingUuid())
            .medBestillingUuid(UUID.randomUUID())
            .medDokumentMalType(mapDokumentMalType(forhåndsvisDto.dokumentMal()))
            .medDokumentMal(mapDokumentMal(forhåndsvisDto.dokumentMal()))
            .medRevurderingVarslingÅrsak(mapRevurderingVarslingÅrsak(forhåndsvisDto.revurderingÅrsak()))
            .medRevurderingÅrsak(mapRevurderingÅrsak(forhåndsvisDto.revurderingÅrsak()))
            .medFritekst(forhåndsvisDto.fritekst())
            .medTittel(forhåndsvisDto.tittel())
            .build();
    }

    public static DokumentHendelse mapFra(DokumentBestillingDto dokumentbestillingDto) {
        return DokumentHendelse.builder()
            .medBehandlingUuid(dokumentbestillingDto.behandlingUuid())
            .medBestillingUuid(dokumentbestillingDto.dokumentbestillingUuid())
            .medDokumentMalType(mapDokumentMalType(dokumentbestillingDto.dokumentMal()))
            .medDokumentMal(mapDokumentMal(dokumentbestillingDto.dokumentMal()))
            .medRevurderingVarslingÅrsak(mapRevurderingVarslingÅrsak(dokumentbestillingDto.revurderingÅrsak()))
            .medRevurderingÅrsak(mapRevurderingÅrsak(dokumentbestillingDto.revurderingÅrsak()))
            .medFritekst(dokumentbestillingDto.fritekst())
            .medJournalførSom(Optional.ofNullable(dokumentbestillingDto.journalførSom()).map(DokumentHendelseDtoMapper::mapDokumentMal).orElse(null))
            .build();
    }

    private static no.nav.foreldrepenger.fpformidling.typer.DokumentMal mapDokumentMal(DokumentMal dokumentMal) {
        return switch (dokumentMal) {
            case FRITEKSTBREV -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.FRITEKSTBREV;
            case ENGANGSSTØNAD_INNVILGELSE -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.ENGANGSSTØNAD_INNVILGELSE;
            case ENGANGSSTØNAD_AVSLAG -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.ENGANGSSTØNAD_AVSLAG;
            case FORELDREPENGER_INNVILGELSE -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.FORELDREPENGER_INNVILGELSE;
            case FORELDREPENGER_AVSLAG -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.FORELDREPENGER_AVSLAG;
            case FORELDREPENGER_OPPHØR -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.FORELDREPENGER_OPPHØR;
            case FORELDREPENGER_ANNULLERT -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.FORELDREPENGER_ANNULLERT;
            case FORELDREPENGER_INFO_TIL_ANNEN_FORELDER -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.FORELDREPENGER_INFOBREV_TIL_ANNEN_FORELDER;
            case SVANGERSKAPSPENGER_INNVILGELSE -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.SVANGERSKAPSPENGER_INNVILGELSE;
            case SVANGERSKAPSPENGER_OPPHØR -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.SVANGERSKAPSPENGER_OPPHØR;
            case SVANGERSKAPSPENGER_AVSLAG -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.SVANGERSKAPSPENGER_AVSLAG;
            case INNHENTE_OPPLYSNINGER -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.INNHENTE_OPPLYSNINGER;
            case VARSEL_OM_REVURDERING -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.VARSEL_OM_REVURDERING;
            case INFO_OM_HENLEGGELSE -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.INFO_OM_HENLEGGELSE;
            case INNSYN_SVAR -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.INNSYN_SVAR;
            case IKKE_SØKT -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.IKKE_SØKT;
            case INGEN_ENDRING -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.INGEN_ENDRING;
            case FORLENGET_SAKSBEHANDLINGSTID -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.FORLENGET_SAKSBEHANDLINGSTID;
            case FORLENGET_SAKSBEHANDLINGSTID_MEDL -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.FORLENGET_SAKSBEHANDLINGSTID_MEDL;
            case FORLENGET_SAKSBEHANDLINGSTID_TIDLIG -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.FORLENGET_SAKSBEHANDLINGSTID_TIDLIG;
            case KLAGE_AVVIST -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.KLAGE_AVVIST;
            case KLAGE_OMGJORT -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.KLAGE_OMGJORT;
            case KLAGE_OVERSENDT -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.KLAGE_OVERSENDT;
            case ETTERLYS_INNTEKTSMELDING -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.ETTERLYS_INNTEKTSMELDING;
            case ENDRING_UTBETALING -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.ENDRING_UTBETALING;
        };
    }

    private static no.nav.foreldrepenger.fpformidling.typer.RevurderingÅrsak mapRevurderingÅrsak(RevurderingÅrsak revurderingÅrsak) {
        return switch (revurderingÅrsak) {
            case ANNET -> no.nav.foreldrepenger.fpformidling.typer.RevurderingÅrsak.ANNET;
            case BARN_IKKE_REGISTRERT_FOLKEREGISTER -> no.nav.foreldrepenger.fpformidling.typer.RevurderingÅrsak.BARN_IKKE_REGISTRERT_FOLKEREGISTER;
            case BRUKER_REGISTRERT_UTVANDRET -> no.nav.foreldrepenger.fpformidling.typer.RevurderingÅrsak.BRUKER_REGISTRERT_UTVANDRET;
            case BEREGNINGSGRUNNLAG_UNDER_HALV_G -> no.nav.foreldrepenger.fpformidling.typer.RevurderingÅrsak.BEREGNINGSGRUNNLAG_UNDER_HALV_G;
            case ARBEIDS_I_STØNADSPERIODEN -> no.nav.foreldrepenger.fpformidling.typer.RevurderingÅrsak.ARBEIDS_I_STØNADSPERIODEN;
            case OPPTJENING_IKKE_OPPFYLT -> no.nav.foreldrepenger.fpformidling.typer.RevurderingÅrsak.OPPTJENING_IKKE_OPPFYLT;
            case IKKE_LOVLIG_OPPHOLD -> no.nav.foreldrepenger.fpformidling.typer.RevurderingÅrsak.IKKE_LOVLIG_OPPHOLD;
            case ARBEID_I_UTLANDET -> no.nav.foreldrepenger.fpformidling.typer.RevurderingÅrsak.ARBEID_I_UTLANDET;
            case MOR_AKTIVITET_IKKE_OPPFYLT -> no.nav.foreldrepenger.fpformidling.typer.RevurderingÅrsak.MOR_AKTIVITET_IKKE_OPPFYLT;
            case null -> null;
        };
    }

    private static RevurderingVarslingÅrsak mapRevurderingVarslingÅrsak(RevurderingÅrsak revurderingÅrsak) {
        return switch (revurderingÅrsak) {
            case ANNET -> RevurderingVarslingÅrsak.ANNET;
            case BARN_IKKE_REGISTRERT_FOLKEREGISTER -> RevurderingVarslingÅrsak.BARN_IKKE_REGISTRERT_FOLKEREGISTER;
            case BRUKER_REGISTRERT_UTVANDRET -> RevurderingVarslingÅrsak.BRUKER_REGISTRERT_UTVANDRET;
            case BEREGNINGSGRUNNLAG_UNDER_HALV_G -> RevurderingVarslingÅrsak.BEREGNINGSGRUNNLAG_UNDER_HALV_G;
            case ARBEIDS_I_STØNADSPERIODEN -> RevurderingVarslingÅrsak.ARBEIDS_I_STØNADSPERIODEN;
            case OPPTJENING_IKKE_OPPFYLT -> RevurderingVarslingÅrsak.OPPTJENING_IKKE_OPPFYLT;
            case IKKE_LOVLIG_OPPHOLD -> RevurderingVarslingÅrsak.IKKE_LOVLIG_OPPHOLD;
            case ARBEID_I_UTLANDET -> RevurderingVarslingÅrsak.ARBEID_I_UTLANDET;
            case MOR_AKTIVITET_IKKE_OPPFYLT -> RevurderingVarslingÅrsak.MOR_AKTIVITET_IKKE_OPPFYLT;
            case null -> null;
        };
    }

    private static DokumentMalType mapDokumentMalType(DokumentMal dokumentMal) {
        return switch (dokumentMal) {
            case FRITEKSTBREV -> DokumentMalType.FRITEKSTBREV;
            case ENGANGSSTØNAD_INNVILGELSE -> DokumentMalType.ENGANGSSTØNAD_INNVILGELSE;
            case ENGANGSSTØNAD_AVSLAG -> DokumentMalType.ENGANGSSTØNAD_AVSLAG;
            case FORELDREPENGER_INNVILGELSE -> DokumentMalType.FORELDREPENGER_INNVILGELSE;
            case FORELDREPENGER_AVSLAG -> DokumentMalType.FORELDREPENGER_AVSLAG;
            case FORELDREPENGER_OPPHØR -> DokumentMalType.FORELDREPENGER_OPPHØR;
            case FORELDREPENGER_ANNULLERT -> DokumentMalType.FORELDREPENGER_ANNULLERT;
            case FORELDREPENGER_INFO_TIL_ANNEN_FORELDER -> DokumentMalType.FORELDREPENGER_INFOBREV_TIL_ANNEN_FORELDER;
            case SVANGERSKAPSPENGER_INNVILGELSE -> DokumentMalType.SVANGERSKAPSPENGER_INNVILGELSE;
            case SVANGERSKAPSPENGER_OPPHØR -> DokumentMalType.SVANGERSKAPSPENGER_OPPHØR;
            case SVANGERSKAPSPENGER_AVSLAG -> DokumentMalType.SVANGERSKAPSPENGER_AVSLAG;
            case INNHENTE_OPPLYSNINGER -> DokumentMalType.INNHENTE_OPPLYSNINGER;
            case VARSEL_OM_REVURDERING -> DokumentMalType.VARSEL_OM_REVURDERING;
            case INFO_OM_HENLEGGELSE -> DokumentMalType.INFO_OM_HENLEGGELSE;
            case INNSYN_SVAR -> DokumentMalType.INNSYN_SVAR;
            case IKKE_SØKT -> DokumentMalType.IKKE_SØKT;
            case INGEN_ENDRING -> DokumentMalType.INGEN_ENDRING;
            case FORLENGET_SAKSBEHANDLINGSTID -> DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID;
            case FORLENGET_SAKSBEHANDLINGSTID_MEDL -> DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID_MEDL;
            case FORLENGET_SAKSBEHANDLINGSTID_TIDLIG -> DokumentMalType.FORLENGET_SAKSBEHANDLINGSTID_TIDLIG;
            case KLAGE_AVVIST -> DokumentMalType.KLAGE_AVVIST;
            case KLAGE_OMGJORT -> DokumentMalType.KLAGE_OMGJORT;
            case KLAGE_OVERSENDT -> DokumentMalType.KLAGE_OVERSENDT;
            case ETTERLYS_INNTEKTSMELDING -> DokumentMalType.ETTERLYS_INNTEKTSMELDING;
            case ENDRING_UTBETALING -> DokumentMalType.ENDRING_UTBETALING;
        };
    }

    private static RevurderingVarslingÅrsak utledRevurderingVarslingsårsak(String varslingsårsak) {
        if (varslingsårsak == null || varslingsårsak.isEmpty()) {
            return RevurderingVarslingÅrsak.UDEFINERT;
        }
        return RevurderingVarslingÅrsak.fraKode(varslingsårsak);
    }

    private static FagsakYtelseType utledYtelseType(YtelseType ytelseType) {
        if (ytelseType == null) {
            return null;
        }
        return switch (ytelseType) {
            case ES -> FagsakYtelseType.ENGANGSTØNAD;
            case FP -> FagsakYtelseType.FORELDREPENGER;
            case SVP -> FagsakYtelseType.SVANGERSKAPSPENGER;
        };
    }

    private static Vedtaksbrev utledVedtaksbrev(Boolean automatiskBrev) {
        return Optional.ofNullable(automatiskBrev).filter(ab -> ab).map(ab -> Vedtaksbrev.AUTOMATISK).orElse(Vedtaksbrev.UDEFINERT);
    }

    private static DokumentMalType utleddokumentMalType(String dokumentmal) {
        if (dokumentmal == null || dokumentmal.isEmpty()) {
            return null;
        }
        return DokumentMalType.fraKode(dokumentmal);
    }
}
