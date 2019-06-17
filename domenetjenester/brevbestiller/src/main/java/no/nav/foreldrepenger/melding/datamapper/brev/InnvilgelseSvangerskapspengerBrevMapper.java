package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.typer.Dato.medFormatering;

import java.util.List;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.datatype.XMLGregorianCalendar;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatFP;
import no.nav.foreldrepenger.melding.beregningsgrunnlag.Beregningsgrunnlag;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.BeregningsresultatMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.MottattdokumentMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.SvpMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeVerktøy;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.mottattdokument.MottattDokument;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttaksresultat;

@ApplicationScoped
@Named(DokumentMalType.INNVILGELSE_SVANGERSKAPSPENGER_DOK)
public class InnvilgelseSvangerskapspengerBrevMapper extends FritekstmalBrevMapper {

    public InnvilgelseSvangerskapspengerBrevMapper() {
        //CDI
    }

    @Inject
    public InnvilgelseSvangerskapspengerBrevMapper(BrevParametere brevParametere, DomeneobjektProvider domeneobjektProvider) {
        super(brevParametere, domeneobjektProvider);
    }

    @Override
    public String displayName() {
        return "Innvilget svangerskapspenger";
    }

    @Override
    public String templateFolder() {
        return "innvilgelsesvangerskapspenger";
    }

    @Override
    Brevdata mapTilBrevfelter(DokumentHendelse hendelse, Behandling behandling) {
        List<MottattDokument> mottatteDokumenter = domeneobjektProvider.hentMottatteDokumenter(behandling);
        XMLGregorianCalendar søknadsDato = MottattdokumentMapper.finnSøknadsDatoFraMottatteDokumenter(behandling, mottatteDokumenter);

        Beregningsgrunnlag beregningsgrunnlag = domeneobjektProvider.hentBeregningsgrunnlag(behandling);
        BeregningsresultatFP beregningsresultatFP = domeneobjektProvider.hentBeregningsresultatFP(behandling);
        SvpUttaksresultat svpUttaksresultat = domeneobjektProvider.hentUttaksresultatSvp(behandling);

        Map<String, Object> beregning = SvpMapper.mapFra(svpUttaksresultat, hendelse, beregningsgrunnlag, beregningsresultatFP, behandling);
        svpUttaksresultat = SvpMapper.utvidOgTilpassBrev(svpUttaksresultat, beregningsresultatFP);

        return new Brevdata()
                .leggTil("resultat", svpUttaksresultat)
                .leggTil("beregning", beregning)
                .leggTil("manedsbelop", BeregningsresultatMapper.finnMånedsbeløp(beregningsresultatFP))
                .leggTil("mottattDato", medFormatering(PeriodeVerktøy.xmlGregorianTilLocalDate(søknadsDato)))
                .leggTil("periodeDagsats", SvpMapper.getPeriodeDagsats(beregningsresultatFP))
                .leggTil("antallPerioder", SvpMapper.getAntallPerioder(svpUttaksresultat))
                .leggTil("antallAvslag", svpUttaksresultat.getAvslagPerioder().size())
                .leggTil("refusjonTilBruker", 0 < BeregningsresultatMapper.finnTotalBrukerAndel(beregningsresultatFP))
                .leggTil("refusjonerTilArbeidsgivere", SvpMapper.finnAntallRefusjonerTilArbeidsgivere(beregningsresultatFP));
    }
}
