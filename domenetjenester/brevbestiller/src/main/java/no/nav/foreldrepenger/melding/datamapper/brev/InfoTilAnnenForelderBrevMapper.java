package no.nav.foreldrepenger.melding.datamapper.brev;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;
import javax.xml.datatype.XMLGregorianCalendar;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.DokumentBestillerFeil;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.UttakMapper;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.uttak.UttakResultatPerioder;
import no.nav.vedtak.feil.FeilFactory;

@ApplicationScoped
@Named(DokumentMalType.INFO_TIL_ANNEN_FORELDER_DOK)
public class InfoTilAnnenForelderBrevMapper extends FritekstmalBrevMapper {

    public InfoTilAnnenForelderBrevMapper() {
        //CDI
    }

    @Inject
    public InfoTilAnnenForelderBrevMapper(BrevParametere brevParametere, DomeneobjektProvider domeneobjektProvider) {
        super(brevParametere, domeneobjektProvider);
    }

    @Override
    public String displayName() {
        return "Infobrev: Annen forelder";
    }

    @Override
    String templateFolder() {
        return "informasjontilannenforelder";
    }

    @Override
    Brevdata mapTilBrevfelter(DokumentHendelse hendelse, Behandling behandling) {
        UttakResultatPerioder uttakResultatPerioder = domeneobjektProvider.hentUttaksresultat(behandling);
        String fristDato = UttakMapper.finnSisteDagIFelleseriodeHvisFinnes(uttakResultatPerioder)
                .map(XMLGregorianCalendar::toString)
                .orElseThrow(() -> FeilFactory.create(DokumentBestillerFeil.class).feltManglerVerdi("dato").toException());

        return new Brevdata(behandling.getSpråkkode()) {
            String kontaktTelefonnummer = dokumentFelles.getKontaktTlf();
            String navnAvsenderEnhet = dokumentFelles.getNavnAvsenderEnhet();
            boolean erAutomatiskVedtak = fellesType.isAutomatiskBehandlet();

            public String getDato() {
                return fristDato;
            }

            public String getKontaktTelefonnummer() {
                return kontaktTelefonnummer;
            }

            public String getNavnAvsenderEnhet() {
                return navnAvsenderEnhet;
            }

            public boolean getErAutomatiskVedtak() {
                return erAutomatiskVedtak;
            }
        };
    }
}
