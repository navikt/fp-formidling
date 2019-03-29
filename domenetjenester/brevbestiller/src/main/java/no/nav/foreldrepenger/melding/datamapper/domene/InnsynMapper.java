package no.nav.foreldrepenger.melding.datamapper.domene;

import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.BehandlingRestKlient;
import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.Innsyn;
import no.nav.foreldrepenger.melding.behandling.InnsynResultatType;
import no.nav.foreldrepenger.melding.datamapper.DokumentMapperFeil;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innsyn.InnsynResultatTypeKode;

@ApplicationScoped
public class InnsynMapper {

    private BehandlingRestKlient behandlingRestKlient;

    public InnsynMapper() {
        //CDI
    }

    @Inject
    public InnsynMapper(BehandlingRestKlient behandlingRestKlient) {
        this.behandlingRestKlient = behandlingRestKlient;
    }

    public Innsyn hentInnsyn(Behandling behandling) {
        return new Innsyn(behandlingRestKlient.hentInnsynsbehandling(behandling.getResourceLinkDtos()));
    }

    public static InnsynResultatTypeKode mapInnsynResultatKode(String internResultatTypeKode) {
        if (Objects.equals(internResultatTypeKode, InnsynResultatType.INNVILGET.getKode())) {
            return InnsynResultatTypeKode.INNVILGET;
        } else if (Objects.equals(internResultatTypeKode, InnsynResultatType.DELVIS_INNVILGET.getKode())) {
            return InnsynResultatTypeKode.DELVISINNVILGET;
        } else if (Objects.equals(internResultatTypeKode, InnsynResultatType.AVVIST.getKode())) {
            return InnsynResultatTypeKode.AVVIST;
        }
        throw DokumentMapperFeil.FACTORY.innsynskravSvarHarUkjentResultatType(internResultatTypeKode).toException();
    }


}
