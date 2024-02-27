package no.nav.foreldrepenger.fpformidling.tjenester;

import java.util.Optional;
import java.util.UUID;

import no.nav.foreldrepenger.fpformidling.brevproduksjon.bestiller.DokumentHendelseEntitet;
import no.nav.foreldrepenger.fpformidling.typer.DokumentMalEnum;
import no.nav.foreldrepenger.fpformidling.typer.RevurderingÅrsakEnum;
import no.nav.foreldrepenger.kontrakter.formidling.kodeverk.DokumentMal;
import no.nav.foreldrepenger.kontrakter.formidling.kodeverk.RevurderingÅrsak;
import no.nav.foreldrepenger.kontrakter.formidling.v3.DokumentBestillingDto;
import no.nav.foreldrepenger.kontrakter.formidling.v3.DokumentForhåndsvisDto;

public final class DokumentHendelseDtoMapper {

    private DokumentHendelseDtoMapper() {
    }

    public static DokumentHendelseEntitet mapFra(DokumentForhåndsvisDto forhåndsvisDto) {
        return DokumentHendelseEntitet.builder()
            .medBehandlingUuid(forhåndsvisDto.behandlingUuid())
            .medBestillingUuid(UUID.randomUUID())
            .medDokumentMal(mapDokumentMal(forhåndsvisDto.dokumentMal()))
            .medRevurderingÅrsak(mapRevurderingÅrsak(forhåndsvisDto.revurderingÅrsak()))
            .medFritekst(forhåndsvisDto.fritekst())
            .build();
    }

    public static DokumentHendelseEntitet mapFra(DokumentBestillingDto dokumentbestillingDto) {
        return DokumentHendelseEntitet.builder()
            .medBehandlingUuid(dokumentbestillingDto.behandlingUuid())
            .medBestillingUuid(dokumentbestillingDto.dokumentbestillingUuid())
            .medDokumentMal(mapDokumentMal(dokumentbestillingDto.dokumentMal()))
            .medRevurderingÅrsak(mapRevurderingÅrsak(dokumentbestillingDto.revurderingÅrsak()))
            .medFritekst(dokumentbestillingDto.fritekst())
            .medJournalførSom(Optional.ofNullable(dokumentbestillingDto.journalførSom()).map(DokumentHendelseDtoMapper::mapDokumentMal).orElse(null))
            .build();
    }

    private static DokumentMalEnum mapDokumentMal(DokumentMal dokumentMal) {
        return switch (dokumentMal) {
            case FRITEKSTBREV -> DokumentMalEnum.FRITEKSTBREV;
            case ENGANGSSTØNAD_INNVILGELSE -> DokumentMalEnum.ENGANGSSTØNAD_INNVILGELSE;
            case ENGANGSSTØNAD_AVSLAG -> DokumentMalEnum.ENGANGSSTØNAD_AVSLAG;
            case FORELDREPENGER_INNVILGELSE -> DokumentMalEnum.FORELDREPENGER_INNVILGELSE;
            case FORELDREPENGER_AVSLAG -> DokumentMalEnum.FORELDREPENGER_AVSLAG;
            case FORELDREPENGER_OPPHØR -> DokumentMalEnum.FORELDREPENGER_OPPHØR;
            case FORELDREPENGER_ANNULLERT -> DokumentMalEnum.FORELDREPENGER_ANNULLERT;
            case FORELDREPENGER_INFO_TIL_ANNEN_FORELDER -> DokumentMalEnum.FORELDREPENGER_INFOBREV_TIL_ANNEN_FORELDER;
            case SVANGERSKAPSPENGER_INNVILGELSE -> DokumentMalEnum.SVANGERSKAPSPENGER_INNVILGELSE;
            case SVANGERSKAPSPENGER_OPPHØR -> DokumentMalEnum.SVANGERSKAPSPENGER_OPPHØR;
            case SVANGERSKAPSPENGER_AVSLAG -> DokumentMalEnum.SVANGERSKAPSPENGER_AVSLAG;
            case INNHENTE_OPPLYSNINGER -> DokumentMalEnum.INNHENTE_OPPLYSNINGER;
            case VARSEL_OM_REVURDERING -> DokumentMalEnum.VARSEL_OM_REVURDERING;
            case INFO_OM_HENLEGGELSE -> DokumentMalEnum.INFO_OM_HENLEGGELSE;
            case INNSYN_SVAR -> DokumentMalEnum.INNSYN_SVAR;
            case IKKE_SØKT -> DokumentMalEnum.IKKE_SØKT;
            case INGEN_ENDRING -> DokumentMalEnum.INGEN_ENDRING;
            case FORLENGET_SAKSBEHANDLINGSTID -> DokumentMalEnum.FORLENGET_SAKSBEHANDLINGSTID;
            case FORLENGET_SAKSBEHANDLINGSTID_MEDL -> DokumentMalEnum.FORLENGET_SAKSBEHANDLINGSTID_MEDL;
            case FORLENGET_SAKSBEHANDLINGSTID_TIDLIG -> DokumentMalEnum.FORLENGET_SAKSBEHANDLINGSTID_TIDLIG;
            case KLAGE_AVVIST -> DokumentMalEnum.KLAGE_AVVIST;
            case KLAGE_OMGJORT -> DokumentMalEnum.KLAGE_OMGJORT;
            case KLAGE_OVERSENDT -> DokumentMalEnum.KLAGE_OVERSENDT;
            case ETTERLYS_INNTEKTSMELDING -> DokumentMalEnum.ETTERLYS_INNTEKTSMELDING;
            case ENDRING_UTBETALING -> DokumentMalEnum.ENDRING_UTBETALING;
        };
    }

    private static RevurderingÅrsakEnum mapRevurderingÅrsak(RevurderingÅrsak revurderingÅrsak) {
        return switch (revurderingÅrsak) {
            case ANNET -> RevurderingÅrsakEnum.ANNET;
            case BARN_IKKE_REGISTRERT_FOLKEREGISTER -> RevurderingÅrsakEnum.BARN_IKKE_REGISTRERT_FOLKEREGISTER;
            case BRUKER_REGISTRERT_UTVANDRET -> RevurderingÅrsakEnum.BRUKER_REGISTRERT_UTVANDRET;
            case BEREGNINGSGRUNNLAG_UNDER_HALV_G -> RevurderingÅrsakEnum.BEREGNINGSGRUNNLAG_UNDER_HALV_G;
            case ARBEIDS_I_STØNADSPERIODEN -> RevurderingÅrsakEnum.ARBEIDS_I_STØNADSPERIODEN;
            case OPPTJENING_IKKE_OPPFYLT -> RevurderingÅrsakEnum.OPPTJENING_IKKE_OPPFYLT;
            case IKKE_LOVLIG_OPPHOLD -> RevurderingÅrsakEnum.IKKE_LOVLIG_OPPHOLD;
            case ARBEID_I_UTLANDET -> RevurderingÅrsakEnum.ARBEID_I_UTLANDET;
            case MOR_AKTIVITET_IKKE_OPPFYLT -> RevurderingÅrsakEnum.MOR_AKTIVITET_IKKE_OPPFYLT;
            case null -> null;
        };
    }

}
