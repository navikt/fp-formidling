package no.nav.foreldrepenger.fpsak;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

import no.nav.foreldrepenger.fpformidling.behandling.BehandlingRelLinkPayload;
import no.nav.foreldrepenger.fpformidling.behandling.BehandlingResourceLink;
import no.nav.foreldrepenger.fpsak.dto.DokumentMalTypeDto;
import no.nav.foreldrepenger.fpsak.dto.anke.AnkebehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.MottattDokumentDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.aksjonspunkt.AksjonspunktDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.familiehendelse.FamilieHendelseGrunnlagDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.innsyn.InnsynsbehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.behandling.vilkår.VilkårDto;
import no.nav.foreldrepenger.fpsak.dto.beregningsgrunnlag.v2.BeregningsgrunnlagDtoV2;
import no.nav.foreldrepenger.fpsak.dto.fagsak.FagsakDto;
import no.nav.foreldrepenger.fpsak.dto.inntektarbeidytelse.InntektsmeldingerDto;
import no.nav.foreldrepenger.fpsak.dto.klage.KlagebehandlingDto;
import no.nav.foreldrepenger.fpsak.dto.klage.MottattKlagedokumentDto;
import no.nav.foreldrepenger.fpsak.dto.personopplysning.VergeDto;
import no.nav.foreldrepenger.fpsak.dto.soknad.SoknadBackendDto;
import no.nav.foreldrepenger.fpsak.dto.tilkjentytelse.TilkjentYtelseEngangsstønadDto;
import no.nav.foreldrepenger.fpsak.dto.tilkjentytelse.TilkjentYtelseMedUttaksplanDto;
import no.nav.foreldrepenger.fpsak.dto.uttak.KreverSammenhengendeUttakDto;
import no.nav.foreldrepenger.fpsak.dto.uttak.StartdatoUtsattDto;
import no.nav.foreldrepenger.fpsak.dto.uttak.UttakResultatPerioderDto;
import no.nav.foreldrepenger.fpsak.dto.uttak.saldo.SaldoerDto;
import no.nav.foreldrepenger.fpsak.dto.uttak.svp.SvangerskapspengerUttakResultatDto;
import no.nav.foreldrepenger.fpsak.dto.ytelsefordeling.YtelseFordelingDto;
import no.nav.foreldrepenger.kontrakter.formidling.v1.DokumentProdusertDto;
import no.nav.foreldrepenger.kontrakter.fpsak.beregningsgrunnlag.v2.BeregningsgrunnlagDto;

public interface Behandlinger {

    <T> Optional<T> hentDtoFraLink(BehandlingResourceLink link, Class<T> clazz);

    void kvitterDokument(DokumentProdusertDto kvittering);

    default BeregningsgrunnlagDto hentBeregningsgrunnlagV2(UUID behandlingUuid) {
        return hentBeregningsgrunnlagV2HvisFinnes(behandlingUuid)
                .orElseThrow(
                        () -> new IllegalStateException("Klarte ikke hente beregningsgrunnlag for behandling: " + behandlingUuid ));
    }

    Optional<BeregningsgrunnlagDto> hentBeregningsgrunnlagV2HvisFinnes(UUID behandlingUuid);

    BehandlingDto hentBehandling(UUID behandlingId);

    default Optional<BehandlingDto> hentOriginalBehandling(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker
                .stream()
                .filter(dto -> "original-behandling".equals(dto.getRel()))
                .findFirst()
                .flatMap(link -> hentDtoFraLink(link, BehandlingDto.class));
    }

    default Optional<VergeDto> hentVergeHvisFinnes(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker
                .stream()
                .filter(dto -> "verge-backend".equals(dto.getRel()))
                .findFirst()
                .flatMap(link -> hentDtoFraLink(link, VergeDto.class));
    }

    default FamilieHendelseGrunnlagDto hentFamiliehendelse(List<BehandlingResourceLink> resourceLinker) {
        return hentFamiliehendelseHvisFinnes(resourceLinker)
                .orElseThrow(
                        () -> new IllegalStateException("Klarte ikke hente Familiehendelse for behandling: " + hentBehandlingId(resourceLinker)));
    }

    default Optional<FamilieHendelseGrunnlagDto> hentFamiliehendelseHvisFinnes(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker
                .stream()
                .filter(dto -> "familiehendelse-v2".equals(dto.getRel()))
                .findFirst()
                .flatMap(link -> hentDtoFraLink(link, FamilieHendelseGrunnlagDto.class));
    }

    default Optional<TilkjentYtelseEngangsstønadDto> hentTilkjentYtelseEngangsstønadHvisFinnes(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker
                .stream()
                .filter(dto -> "beregningsresultat-engangsstonad".equals(dto.getRel()))
                .findFirst()
                .flatMap(link -> hentDtoFraLink(link, TilkjentYtelseEngangsstønadDto.class));
    }

    default TilkjentYtelseEngangsstønadDto hentTilkjentYtelseEngangsstønad(List<BehandlingResourceLink> resourceLinker) {
        return hentTilkjentYtelseEngangsstønadHvisFinnes(resourceLinker)
                .orElseThrow(() -> new IllegalStateException(
                        "Klarte ikke hente Tilkjent ytelse engangsstønad for behandling: " + hentBehandlingId(resourceLinker)));
    }

    default TilkjentYtelseMedUttaksplanDto hentTilkjentYtelseForeldrepenger(List<BehandlingResourceLink> resourceLinker) {
        return hentTilkjentYtelseForeldrepengerHvisFinnes(resourceLinker).orElseThrow(() -> {
            throw new IllegalStateException(
                    "Klarte ikke hente Tilkjent ytelse foreldrepenger for behandling: " + hentBehandlingId(resourceLinker));
        });
    }

    default Optional<TilkjentYtelseMedUttaksplanDto> hentTilkjentYtelseForeldrepengerHvisFinnes(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker
                .stream()
                .filter(dto -> "beregningsresultat-foreldrepenger".equals(dto.getRel()))
                .findFirst()
                .flatMap(link -> hentDtoFraLink(link, TilkjentYtelseMedUttaksplanDto.class));
    }

    default Optional<SoknadBackendDto> hentSoknadHvisFinnes(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker
                .stream()
                .filter(dto -> "soknad-backend".equals(dto.getRel()))
                .findFirst()
                .flatMap(link -> hentDtoFraLink(link, SoknadBackendDto.class));
    }

    default InntektsmeldingerDto hentInntektsmeldingerDto(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker
                .stream()
                .filter(dto -> "inntektsmeldinger".equals(dto.getRel()))
                .findFirst()
                .flatMap(link -> hentDtoFraLink(link, InntektsmeldingerDto.class))
                .orElseThrow(() -> new IllegalStateException("Klarte ikke hente Inntektsmeldinger dto for behandling: " + hentBehandlingId(resourceLinker)));
    }

    default KlagebehandlingDto hentKlagebehandling(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker
                .stream()
                .filter(dto -> "klage-vurdering".equals(dto.getRel()))
                .findFirst()
                .flatMap(link -> hentDtoFraLink(link, KlagebehandlingDto.class))
                .orElseThrow(() -> new IllegalStateException("Klarte ikke hente klage for behandling: " + hentBehandlingId(resourceLinker)));
    }

    default AnkebehandlingDto hentAnkebehandling(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker
                .stream()
                .filter(dto -> "anke-vurdering".equals(dto.getRel()))
                .findFirst()
                .flatMap(link -> hentDtoFraLink(link, AnkebehandlingDto.class))
                .orElseThrow(() -> new IllegalStateException("Klarte ikke hente klage for behandling: " + hentBehandlingId(resourceLinker)));
    }

    default InnsynsbehandlingDto hentInnsynsbehandling(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker
                .stream()
                .filter(dto -> "innsyn".equals(dto.getRel()))
                .findFirst()
                .flatMap(link -> hentDtoFraLink(link, InnsynsbehandlingDto.class))
                .orElseThrow(() -> new IllegalStateException("Klarte ikke hente innsyn for behandling: " + hentBehandlingId(resourceLinker)));
    }

    default BeregningsgrunnlagDtoV2 hentBeregningsgrunnlag(List<BehandlingResourceLink> resourceLinker) {
        return hentFormidlingBeregningsgrunnlagHvisFinnes(resourceLinker)
                .orElseThrow(
                        () -> new IllegalStateException("Klarte ikke hente beregningsgrunnlag for behandling: " + hentBehandlingId(resourceLinker)));
    }

    default Optional<BeregningsgrunnlagDtoV2> hentFormidlingBeregningsgrunnlagHvisFinnes(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker
                .stream()
                .filter(dto -> "beregningsgrunnlag".equals(dto.getRel()))
                .findFirst()
                .flatMap(link -> hentDtoFraLink(link, BeregningsgrunnlagDtoV2.class));
    }

    default List<VilkårDto> hentVilkår(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker
                .stream()
                .filter(dto -> "vilkar".equals(dto.getRel()))
                .findFirst()
                .flatMap(link -> hentDtoFraLink(link, VilkårDto[].class))
                .map(Arrays::asList)
                .orElseThrow(() -> new IllegalStateException("Klarte ikke hente vilkår for behandling: " + hentBehandlingId(resourceLinker)));
    }

    default UttakResultatPerioderDto hentUttaksresultat(List<BehandlingResourceLink> resourceLinker) {
        return hentUttaksresultatHvisFinnes(resourceLinker)
                .orElseThrow(() -> new IllegalStateException("Klarte ikke hente uttaksperioder for behandling: " + hentBehandlingId(resourceLinker)));
    }

    default Optional<UttakResultatPerioderDto> hentUttaksresultatHvisFinnes(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker
                .stream()
                .filter(dto -> "uttaksresultat-perioder-formidling".equals(dto.getRel()))
                .findFirst()
                .flatMap(link -> hentDtoFraLink(link, UttakResultatPerioderDto.class));

    }

    default SvangerskapspengerUttakResultatDto hentUttaksresultatSvp(List<BehandlingResourceLink> resourceLinker) {
        return hentUttaksresultatSvpHvisFinnes(resourceLinker)
                .orElseThrow(() -> new IllegalStateException("Klarte ikke hente uttaksresultat for behandling: " + hentBehandlingId(resourceLinker)));
    }

    default Optional<SvangerskapspengerUttakResultatDto> hentUttaksresultatSvpHvisFinnes(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker
                .stream()
                .filter(dto -> "uttaksresultat-svangerskapspenger".equals(dto.getRel()))
                .findFirst()
                .flatMap(link -> hentDtoFraLink(link, SvangerskapspengerUttakResultatDto.class));
    }

    default FagsakDto hentFagsak(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker
                .stream()
                .filter(dto -> "fagsak".equals(dto.getRel()))
                .findFirst()
                .flatMap(link -> hentDtoFraLink(link, FagsakDto.class))
                .orElseThrow(() -> new IllegalStateException("Klarte ikke hente fagsak for behandling: " + hentBehandlingId(resourceLinker)));
    }

    default YtelseFordelingDto hentYtelseFordeling(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker
                .stream()
                .filter(dto -> "ytelsefordeling".equals(dto.getRel()))
                .findFirst()
                .flatMap(link -> hentDtoFraLink(link, YtelseFordelingDto.class))
                .orElseThrow(
                        () -> new IllegalStateException("Klarte ikke hente ytelsefordeling for behandling: " + hentBehandlingId(resourceLinker)));
    }

    default SaldoerDto hentSaldoer(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker
                .stream()
                .filter(dto -> "uttak-stonadskontoer".equals(dto.getRel()))
                .findFirst()
                .flatMap(link -> hentDtoFraLink(link, SaldoerDto.class))
                .orElseThrow(() -> new IllegalStateException("Klarte ikke hente uttakssaldoer for behandling: " + hentBehandlingId(resourceLinker)));
    }

    default List<AksjonspunktDto> hentAksjonspunkter(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker
                .stream()
                .filter(dto -> "aksjonspunkter".equals(dto.getRel()))
                .findFirst()
                .flatMap(link -> hentDtoFraLink(link, AksjonspunktDto[].class))
                .map(Arrays::asList)
                .orElseThrow(() -> new IllegalStateException("Klarte ikke hente vilkår for behandling: " + hentBehandlingId(resourceLinker)));
    }

    default MottattKlagedokumentDto hentKlagedokument(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker
                .stream()
                .filter(dto -> "mottatt-klagedokument".equals(dto.getRel()))
                .findFirst()
                .flatMap(link -> hentDtoFraLink(link, MottattKlagedokumentDto.class))
                .orElseThrow(() -> new IllegalStateException("Klarte ikke hente klagedokument for behandling: " + hentBehandlingId(resourceLinker)));
    }

    default List<MottattDokumentDto> hentMottatteDokumenter(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker
                .stream()
                .filter(dto -> "mottattdokument".equals(dto.getRel()))
                .findFirst()
                .flatMap(link -> hentDtoFraLink(link, MottattDokumentDto[].class))
                .map(Arrays::asList)
                .orElse(List.of());
    }

    default Optional<Boolean> harSendtVarselOmRevurdering(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker
                .stream()
                .filter(dto -> "sendt-varsel-om-revurdering".equals(dto.getRel()))
                .findFirst()
                .flatMap(link -> hentDtoFraLink(link, Boolean.class));
    }

    default Optional<DokumentMalTypeDto> hentInnvilgelseForeldrepengerDokumentmal(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker
                .stream()
                .filter(dto -> "dokmal-innvfp".equals(dto.getRel()))
                .findFirst()
                .flatMap(link -> hentDtoFraLink(link, DokumentMalTypeDto.class));
    }

    default KreverSammenhengendeUttakDto kreverSammenhengendeUttak(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker
                .stream()
                .filter(dto -> "krever-sammenhengende-uttak".equals(dto.getRel()))
                .findFirst()
                .flatMap(link -> hentDtoFraLink(link, KreverSammenhengendeUttakDto.class))
                .orElseThrow(() -> new IllegalStateException("Klarte ikke hente om behandlingen krever sammenhengende uttak: " + hentBehandlingId(resourceLinker)));
    }

    default StartdatoUtsattDto hentStartdatoUtsatt(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker
                .stream()
                .filter(dto -> "utsatt-oppstart".equals(dto.getRel()))
                .findFirst()
                .flatMap(link -> hentDtoFraLink(link, StartdatoUtsattDto.class))
                .orElseThrow(() -> new IllegalStateException("Klarte ikke hente informasjon om utsatt startdato for behandling: " + hentBehandlingId(resourceLinker)));
    }

    private static UUID hentBehandlingId(List<BehandlingResourceLink> resourceLinker) {
        return resourceLinker
                .stream()
                .map(BehandlingResourceLink::getRequestPayload)
                .filter(Objects::nonNull)
                .map(BehandlingRelLinkPayload::behandlingUuid)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);
    }

}
