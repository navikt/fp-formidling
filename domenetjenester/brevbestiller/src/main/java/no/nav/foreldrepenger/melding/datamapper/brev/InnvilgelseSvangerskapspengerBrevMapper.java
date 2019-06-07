package no.nav.foreldrepenger.melding.datamapper.brev;

import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.datatype.XMLGregorianCalendar;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.beregning.BeregningsresultatFP;
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

        BeregningsresultatFP beregningsresultatFP = domeneobjektProvider.hentBeregningsresultatFP(behandling);
        SvpUttaksresultat svpUttaksresultat = domeneobjektProvider.hentUttaksresultatSvp(behandling);

        svpUttaksresultat = SvpMapper.utvidOgTilpassBrev(svpUttaksresultat, beregningsresultatFP);
        int antallArbeidsgiverRefusjoner = SvpMapper.finnAntallRefusjonerTilArbeidsgivere(beregningsresultatFP);
        boolean harRefusjonTilBruker = BeregningsresultatMapper.finnTotalBrukerAndel(beregningsresultatFP) > 0;

        return new SvpMapper.SvpBrevData(dokumentFelles, fellesType,
                svpUttaksresultat, harRefusjonTilBruker, antallArbeidsgiverRefusjoner) {

            public long getManedsbelop() {
                return BeregningsresultatMapper.finnMånedsbeløp(beregningsresultatFP);
            }

            public String getMottattDato() {
                return PeriodeVerktøy.xmlGregorianTilLocalDate(søknadsDato).toString();
            }
        };
    }
}
