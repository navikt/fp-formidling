package no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper;

import static no.nav.foreldrepenger.fpformidling.typer.Dato.formaterDato;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BeregningsgrunnlagMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.BrevParametere;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.DokumentdataMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.FellesMapper;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.LovhjemmelComparator;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.mapper.felles.SvpMapperUtil;
import no.nav.foreldrepenger.fpformidling.brevproduksjon.tjenester.arbeidsgiver.ArbeidsgiverTjeneste;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.fpformidling.domene.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.fpformidling.domene.geografisk.Språkkode;
import no.nav.foreldrepenger.fpformidling.domene.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.fpformidling.domene.uttak.svp.PeriodeIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.Avslagsårsak;
import no.nav.foreldrepenger.fpformidling.domene.vilkår.VilkårType;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.SvangerskapspengerAvslagDokumentdata;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.FritekstDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.BrevGrunnlagDto;
import no.nav.foreldrepenger.fpformidling.integrasjon.fpsak.KodeverkMapper;
import no.nav.foreldrepenger.fpformidling.kodeverk.kodeverdi.DokumentMalType;
import no.nav.vedtak.exception.TekniskException;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalType.SVANGERSKAPSPENGER_AVSLAG)
public class SvangerskapspengerAvslagDokumentdataMapper implements DokumentdataMapper {

    private final BrevParametere brevParametere;
    private final ArbeidsgiverTjeneste arbeidsgiverTjeneste;

    @Inject
    public SvangerskapspengerAvslagDokumentdataMapper(BrevParametere brevParametere, ArbeidsgiverTjeneste arbeidsgiverTjeneste) {
        this.brevParametere = brevParametere;
        this.arbeidsgiverTjeneste = arbeidsgiverTjeneste;
    }

    @Override
    public String getTemplateNavn() {
        return "svangerskapspenger-avslag";
    }

    @Override
    public SvangerskapspengerAvslagDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles,
                                                                   DokumentHendelse hendelse,
                                                                   BrevGrunnlagDto behandling,
                                                                   boolean erUtkast) {

        // Erstatte med behandling.getSpråkkode() når engelsk mal er på plass
        var språkkode = Språkkode.EN.equals(dokumentFelles.getSpråkkode()) ? Språkkode.NB : dokumentFelles.getSpråkkode();

        var beregningsgrunnlag = Optional.ofNullable(behandling.beregningsgrunnlag());
        var behandlingsresultat = behandling.behandlingsresultat();
        var svpUttaksresultat = Optional.ofNullable(behandling.svangerskapspenger());
        var inntektsmeldinger = behandling.inntektsmeldinger();

        var fellesBuilder = BrevMapperUtil.opprettFellesBuilder(dokumentFelles, erUtkast);
        fellesBuilder.medErAutomatiskBehandlet(dokumentFelles.getAutomatiskBehandlet());
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), språkkode) : null);
        FritekstDto.fraFritekst(hendelse, behandling.behandlingsresultat().fritekst()).ifPresent(fellesBuilder::medFritekst);

        var uttaksperioder = svpUttaksresultat.map(SvpMapperUtil::hentUttaksperioder).orElse(List.of());

        var uttakArbeidsforhold = svpUttaksresultat.map(BrevGrunnlagDto.Svangerskapspenger::uttakArbeidsforhold)
            .orElse(Collections.emptyList());
        var antallArbeidsgivere = SvpMapperUtil.finnAntallArbeidsgivere(uttakArbeidsforhold, inntektsmeldinger,
            arbeidsgiverTjeneste::hentArbeidsgiverNavn);
        var dokumentdataBuilder = SvangerskapspengerAvslagDokumentdata.ny()
            .medFelles(fellesBuilder.build())
            .medErSøkerDød(BrevMapperUtil.erDød(dokumentFelles))
            .medMottattDato(formaterDato(behandling.førsteSøknadMottattDato(), språkkode))
            .medAntallArbeidsgivere(antallArbeidsgivere)
            .medHalvG(BeregningsgrunnlagMapper.getHalvGOrElseZero(beregningsgrunnlag))
            .medKlagefristUker(brevParametere.getKlagefristUker());

        var vilkårTyper = behandling.behandlingsresultat().vilkårTyper()
            .stream().map(KodeverkMapper::mapVilkårType)
            .toList();
        var avslagsårsak = Avslagsårsak.fraKode(behandlingsresultat.avslagsårsak());
        mapÅrsakOgLovhjemmel(vilkårTyper, avslagsårsak, uttaksperioder, dokumentdataBuilder, behandling.uuid());

        SvpMapperUtil.finnFørsteAvslåtteUttakDato(uttaksperioder, behandling.behandlingsresultat())
            .ifPresent(d -> dokumentdataBuilder.medStønadsdatoFom(formaterDato(d, språkkode)));


        return dokumentdataBuilder.build();
    }

    private void mapÅrsakOgLovhjemmel(Collection<VilkårType> vilkår, Avslagsårsak årsak,
                                      List<BrevGrunnlagDto.Svangerskapspenger.Uttaksperiode> perioder,
                                      SvangerskapspengerAvslagDokumentdata.Builder dokumentdataBuilder,
                                      UUID uuid) {
        Set<String> lovreferanse = new TreeSet<>(new LovhjemmelComparator());
        if (Avslagsårsak.UDEFINERT.equals(årsak) || årsak == null) {
            var avslåttPeriode = perioder.stream()
                .filter(p -> BrevGrunnlagDto.PeriodeResultatType.AVSLÅTT.equals(p.periodeResultatType()))
                .findFirst()
                .orElseThrow(() -> new TekniskException("FPFORMIDLING-100003",
                    String.format("Kan ikke generere avslagsbrev uten avslagsårsak for behandling UUID %s", uuid)));

            var periodeIkkeOppfyltÅrsak = PeriodeIkkeOppfyltÅrsak.fra(avslåttPeriode.periodeIkkeOppfyltÅrsak());
            dokumentdataBuilder.medÅrsak(Årsak.of(periodeIkkeOppfyltÅrsak.getKode()));
            periodeIkkeOppfyltÅrsak.getLovHjemmelData().ifPresent(lovreferanse::add);

        } else {
            dokumentdataBuilder.medÅrsak(Årsak.of(årsak.getKode()));
            lovreferanse.add(SvpMapperUtil.leggTilLovreferanse(vilkår, årsak));
        }
        dokumentdataBuilder.medLovhjemmel(FellesMapper.formaterLovhjemlerUttak(lovreferanse));
    }
}
