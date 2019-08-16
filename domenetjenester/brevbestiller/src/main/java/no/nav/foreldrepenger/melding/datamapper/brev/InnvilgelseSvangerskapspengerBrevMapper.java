package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper.erEndretFraAvslått;
import static no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper.erRevurderingPgaEndretBeregningsgrunnlag;
import static no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper.erTermindatoEndret;
import static no.nav.foreldrepenger.melding.datamapper.domene.SvpMapper.mapFra;
import static no.nav.foreldrepenger.melding.typer.Dato.medFormatering;

import java.util.List;
import java.util.Optional;

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
import no.nav.foreldrepenger.melding.familiehendelse.FamilieHendelse;
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
        Brevdata brevdata = new Brevdata();
        Beregningsgrunnlag beregningsgrunnlag = domeneobjektProvider.hentBeregningsgrunnlag(behandling);
        BeregningsresultatFP beregningsresultatFP = domeneobjektProvider.hentBeregningsresultatFP(behandling);
        SvpUttaksresultat svpUttaksresultat = domeneobjektProvider.hentUttaksresultatSvp(behandling);
        List<MottattDokument> mottatteDokumenter = domeneobjektProvider.hentMottatteDokumenter(behandling);

        XMLGregorianCalendar søknadsDato = MottattdokumentMapper.finnSøknadsDatoFraMottatteDokumenter(behandling, mottatteDokumenter);
        brevdata.leggTilAlle(mapFra(svpUttaksresultat, hendelse, beregningsgrunnlag, beregningsresultatFP, behandling))
                .leggTil("manedsbelop", BeregningsresultatMapper.finnMånedsbeløp(beregningsresultatFP))
                .leggTil("mottattDato", medFormatering(PeriodeVerktøy.xmlGregorianTilLocalDate(søknadsDato)))
                .leggTil("refusjonTilBruker", 0 < BeregningsresultatMapper.finnTotalBrukerAndel(beregningsresultatFP))
                .leggTil("refusjonerTilArbeidsgivere", SvpMapper.finnAntallRefusjonerTilArbeidsgivere(beregningsresultatFP));

        if (behandling.erRevurdering()) {
            Optional<Behandling> orginalBehandling = domeneobjektProvider.hentOriginalBehandlingHvisFinnes(behandling);
            Optional<FamilieHendelse> originalFamiliehendelse = orginalBehandling.map(domeneobjektProvider::hentFamiliehendelse);
            FamilieHendelse familieHendelse = domeneobjektProvider.hentFamiliehendelse(behandling);

            brevdata.leggTil("erEndretFraAvslag", erEndretFraAvslått(orginalBehandling))
                    .leggTil("erUtbetalingEndret", erRevurderingPgaEndretBeregningsgrunnlag(behandling))
                    .leggTil("erTermindatoEndret", erTermindatoEndret(familieHendelse, originalFamiliehendelse));
        }
        return brevdata;
    }
}
