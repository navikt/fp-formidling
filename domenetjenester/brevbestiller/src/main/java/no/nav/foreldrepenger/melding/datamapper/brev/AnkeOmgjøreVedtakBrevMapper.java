
package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.typer.Dato.medFormatering;

import java.time.LocalDate;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.datatype.XMLGregorianCalendar;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.BrevMapperUtil;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.MottattdokumentMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.sammenslåperioder.PeriodeVerktøy;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.mottattdokument.MottattDokument;

@ApplicationScoped
@Named(DokumentMalType.ANKE_VEDTAK_OMGJORING_DOK)
public class AnkeOmgjøreVedtakBrevMapper  extends FritekstmalBrevMapper {


    public AnkeOmgjøreVedtakBrevMapper() {
        //CDI
    }

    @Inject
    public AnkeOmgjøreVedtakBrevMapper(BrevParametere brevParametere, DomeneobjektProvider domeneobjektProvider) {
        super(brevParametere, domeneobjektProvider);
    }

    @Override
    public String displayName() {
        return "Vedtak om omgjøring i ankesak";
    }

    @Override
    public String templateFolder() {
        return "vedtakomomgjøringiankesak";
    }

    @Override
    Brevdata mapTilBrevfelter(DokumentHendelse hendelse, Behandling behandling) {


        return new Brevdata()
                .leggTil("mintekst", hendelse.getFritekst())
                .leggTil("saksbehandler", behandling.getAnsvarligSaksbehandler())
                .leggTil("medunderskriver",behandling.getAnsvarligBeslutter())
                .leggTil("behandlingtype",behandling.getBehandlingType().getKode())
                .leggTil("vedtaksDato",behandling.getOriginalVedtaksDato()!=null?medFormatering(behandling.getOriginalVedtaksDato()):null);


    }

}






