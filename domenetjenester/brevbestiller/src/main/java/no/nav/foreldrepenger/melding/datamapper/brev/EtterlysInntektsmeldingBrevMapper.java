package no.nav.foreldrepenger.melding.datamapper.brev;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.BrevMapperUtil;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;

@ApplicationScoped
@Named(DokumentMalType.ETTERLYS_INNTEKTSMELDING_DOK)
public class EtterlysInntektsmeldingBrevMapper extends FritekstmalBrevMapper {
    @Override
    String getSubfolder() {
        return "etterlysinntektsmelding";
    }

    @Override
    Brevdata mapTilBrevfelter(DokumentHendelse hendelse, Behandling behandling) {
        return new Brevdata(behandling.getSpråkkode()) {
            String soknadDato = domeneobjektProvider.hentSøknad(behandling).getMottattDato().toString();
            String fristDato = BrevMapperUtil.getSvarFrist(brevParametere).toString();
            String kontaktTelefonnummer = dokumentFelles.getKontaktTlf();
            String navnAvsenderEnhet = dokumentFelles.getNavnAvsenderEnhet();

            public String getSoknadDato() {
                return soknadDato;
            }

            public String getFristDato() {
                return fristDato;
            }

            public String getKontaktTelefonnummer() {
                return kontaktTelefonnummer;
            }

            public String getNavnAvsenderEnhet() {
                return navnAvsenderEnhet;
            }
        };
    }
}
