package no.nav.foreldrepenger.melding.brevmapper.brev;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brevmapper.DokumentdataMapper;
import no.nav.foreldrepenger.melding.brevmapper.brev.felles.SvpMapperUtil;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.BeregningsgrunnlagMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.FellesMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.sortering.LovhjemmelComparator;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.SvangerskapspengerAvslagDokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Årsak;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.melding.uttak.svp.PeriodeIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttaksresultat;
import no.nav.foreldrepenger.melding.vilkår.Avslagsårsak;
import no.nav.vedtak.exception.TekniskException;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

import static no.nav.foreldrepenger.melding.datamapper.domene.MottattdokumentMapper.finnSøknadsdatoFraMottatteDokumenter;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.erDød;
import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.opprettFellesBuilder;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDato;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.SVANGERSKAPSPENGER_AVSLAG)
public class SvangerskapspengerAvslagDokumentdataMapper implements DokumentdataMapper {
    private BrevParametere brevParametere;
    private DomeneobjektProvider domeneobjektProvider;

    SvangerskapspengerAvslagDokumentdataMapper() {
        //CDI
    }

    @Inject
    public SvangerskapspengerAvslagDokumentdataMapper(BrevParametere brevParametere, DomeneobjektProvider domeneobjektProvider) {
        this.brevParametere = brevParametere;
        this.domeneobjektProvider = domeneobjektProvider;
    }

    @Override
    public String getTemplateNavn() {
        return "svangerskapspenger-avslag";
    }

    @Override
    public SvangerskapspengerAvslagDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse hendelse, Behandling behandling, boolean erUtkast) {

        //TODO: Erstatte med behandling.getSpråkkode() når engelsk mal er på plass
        Språkkode språkkode = Språkkode.EN.equals(behandling.getSpråkkode()) ? Språkkode.NB : behandling.getSpråkkode();

        var beregningsgrunnlag = domeneobjektProvider.hentBeregningsgrunnlagHvisFinnes(behandling);
        var mottatteDokumenter = domeneobjektProvider.hentMottatteDokumenter(behandling);
        var behandlingsresultat = behandling.getBehandlingsresultat();
        var svpUttaksresultat = domeneobjektProvider.hentUttaksresultatSvpHvisFinnes(behandling);
        var iay = domeneobjektProvider.hentInntektArbeidYtelse(behandling);

        var fellesBuilder = opprettFellesBuilder(dokumentFelles, hendelse, behandling, erUtkast);
        fellesBuilder.medErAutomatiskBehandlet(dokumentFelles.getAutomatiskBehandlet());
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), språkkode) : null);

        var uttaksperioder = SvpMapperUtil.hentUttaksperioder(svpUttaksresultat);

        var dokumentdataBuilder = SvangerskapspengerAvslagDokumentdata.ny()
                .medFelles(fellesBuilder.build())
                .medErSøkerDød(erDød(dokumentFelles))
                .medMottattDato(formaterDato(finnSøknadsdatoFraMottatteDokumenter(behandling, mottatteDokumenter), språkkode))
                .medAntallArbeidsgivere(SvpMapperUtil.finnAntallArbeidsgivere(svpUttaksresultat.map(SvpUttaksresultat::getUttakResultatArbeidsforhold).orElse(Collections.emptyList()), iay))
                .medHalvG(BeregningsgrunnlagMapper.getHalvGOrElseZero(beregningsgrunnlag))
                .medKlagefristUker(brevParametere.getKlagefristUker());

        mapÅrsakOgLovhjemmel(behandlingsresultat.getAvslagsårsak(), uttaksperioder, dokumentdataBuilder, behandling.getUuid());

        SvpMapperUtil.finnFørsteUttakssdato(uttaksperioder, behandling.getBehandlingsresultat())
                .ifPresent(d -> dokumentdataBuilder.medStønadsdatoFom(formaterDato(d, språkkode)));


        return dokumentdataBuilder.build();
    }

    private void mapÅrsakOgLovhjemmel(Avslagsårsak årsak, List<SvpUttakResultatPeriode> perioder, SvangerskapspengerAvslagDokumentdata.Builder dokumentdataBuilder, UUID uuid) {
        Set<String> lovreferanse = new TreeSet<>(new LovhjemmelComparator());
        if (Avslagsårsak.UDEFINERT.equals(årsak) || årsak == null) {
            PeriodeIkkeOppfyltÅrsak periodeÅrsak = perioder.stream()
                    .filter(p-> PeriodeResultatType.AVSLÅTT.equals(p.getPeriodeResultatType()))
                    .map(SvpUttakResultatPeriode::getPeriodeIkkeOppfyltÅrsak)
                    .findFirst()
                    .orElseThrow(() -> new TekniskException("FPFORMIDLING-100003", String.format("Kan ikke generere avslagsbrev uten avslagsårsak for behandling UUID %s", uuid)));

            dokumentdataBuilder.medÅrsak(Årsak.of(periodeÅrsak.getKode()));
            lovreferanse.add(periodeÅrsak.getLovHjemmelData());

        } else {
            dokumentdataBuilder.medÅrsak(Årsak.of(årsak.getKode()));
            lovreferanse.add(SvpMapperUtil.leggTilLovreferanse(årsak));
        }
        dokumentdataBuilder.medLovhjemmel(FellesMapper.formaterLovhjemlerUttak(lovreferanse));
    }
}
