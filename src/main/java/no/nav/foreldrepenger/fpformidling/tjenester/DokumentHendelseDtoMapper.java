package no.nav.foreldrepenger.fpformidling.tjenester;

import java.util.Optional;
import java.util.UUID;

import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.kontrakter.formidling.kodeverk.DokumentMal;
import no.nav.foreldrepenger.kontrakter.formidling.kodeverk.RevurderingÅrsak;
import no.nav.foreldrepenger.kontrakter.formidling.v3.DokumentBestillingDto;
import no.nav.foreldrepenger.kontrakter.formidling.v3.DokumentBestillingHtmlDto;
import no.nav.foreldrepenger.kontrakter.formidling.v3.DokumentForhåndsvisDto;

public final class DokumentHendelseDtoMapper {

    private DokumentHendelseDtoMapper() {
    }

    public static DokumentHendelse mapFra(DokumentBestillingHtmlDto dokumentBestillingHtmlDto) {
        return DokumentHendelse.builder()
                .medBehandlingUuid(dokumentBestillingHtmlDto.behandlingUuid())
                .medBestillingUuid(UUID.randomUUID())
                .medDokumentMal(mapDokumentMal(dokumentBestillingHtmlDto.dokumentMal()))
                .build();
    }

    public static DokumentHendelse mapFra(DokumentForhåndsvisDto forhåndsvisDto) {
        return DokumentHendelse.builder()
            .medBehandlingUuid(forhåndsvisDto.behandlingUuid())
            .medBestillingUuid(UUID.randomUUID())
            .medDokumentMal(mapDokumentMal(forhåndsvisDto.dokumentMal()))
            .medRevurderingÅrsak(mapRevurderingÅrsak(forhåndsvisDto.revurderingÅrsak()))
            .medFritekst(forhåndsvisDto.fritekst())
            .medTittel(forhåndsvisDto.tittel())
            .build();
    }

    public static DokumentHendelse mapFra(DokumentBestillingDto dokumentbestillingDto) {
        return DokumentHendelse.builder()
            .medBehandlingUuid(dokumentbestillingDto.behandlingUuid())
            .medBestillingUuid(dokumentbestillingDto.dokumentbestillingUuid())
            .medDokumentMal(mapDokumentMal(dokumentbestillingDto.dokumentMal()))
            .medRevurderingÅrsak(mapRevurderingÅrsak(dokumentbestillingDto.revurderingÅrsak()))
            .medFritekst(dokumentbestillingDto.fritekst())
            .medJournalførSom(Optional.ofNullable(dokumentbestillingDto.journalførSom()).map(DokumentHendelseDtoMapper::mapDokumentMal).orElse(null))
            .build();
    }

    public static no.nav.foreldrepenger.fpformidling.typer.DokumentMal mapDokumentMal(DokumentMal dokumentMal) {
        return switch (dokumentMal) {
            case FRITEKSTBREV -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.FRITEKSTBREV;
            case FRITEKSTBREV_HTML -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.FRITEKSTBREV_HTML;
            case ENGANGSSTØNAD_INNVILGELSE -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.ENGANGSSTØNAD_INNVILGELSE;
            case ENGANGSSTØNAD_AVSLAG -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.ENGANGSSTØNAD_AVSLAG;
            case FORELDREPENGER_INNVILGELSE -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.FORELDREPENGER_INNVILGELSE;
            case FORELDREPENGER_AVSLAG -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.FORELDREPENGER_AVSLAG;
            case FORELDREPENGER_OPPHØR -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.FORELDREPENGER_OPPHØR;
            case FORELDREPENGER_ANNULLERT -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.FORELDREPENGER_ANNULLERT;
            case FORELDREPENGER_INFO_TIL_ANNEN_FORELDER -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.FORELDREPENGER_INFOBREV_TIL_ANNEN_FORELDER;
            case FORELDREPENGER_FEIL_PRAKSIS_UTSETTELSE_INFOBREV -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.FORELDREPENGER_FEIL_PRAKSIS_UTSETTELSE_INFOBREV;
            case FORELDREPENGER_FEIL_PRAKSIS_UTSETTELSE_FORLENGET_SAKSBEHANDLINGSTID -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.FORELDREPENGER_FEIL_PRAKSIS_UTSETTELSE_FORLENGET_SAKSBEHANDLINGSTID;
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
            case FORLENGET_SAKSBEHANDLINGSTID_MEDL_FORUTGÅENDE -> no.nav.foreldrepenger.fpformidling.typer.DokumentMal.FORLENGET_SAKSBEHANDLINGSTID_MEDL_FORUTGÅENDE;
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
}
