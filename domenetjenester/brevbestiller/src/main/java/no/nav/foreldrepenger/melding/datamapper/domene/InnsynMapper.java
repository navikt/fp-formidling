package no.nav.foreldrepenger.melding.datamapper.domene;

import java.util.Objects;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.fpsak.InnsynRestKlient;
import no.nav.foreldrepenger.fpsak.dto.behandling.BehandlingIdDto;
import no.nav.foreldrepenger.melding.behandling.InnsynResultatType;
import no.nav.foreldrepenger.melding.brevbestiller.api.dto.Innsyn;
import no.nav.foreldrepenger.melding.datamapper.DokumentMapperFeil;
import no.nav.foreldrepenger.melding.integrasjon.dokument.innsyn.InnsynResultatTypeKode;

@ApplicationScoped
public class InnsynMapper {

    private InnsynRestKlient innsynRestKlient;

    public InnsynMapper() {
        //CDI
    }

    @Inject
    public InnsynMapper(InnsynRestKlient innsynRestKlient) {
        this.innsynRestKlient = innsynRestKlient;
    }

    public Innsyn hentInnsyn(long behandlingId) {
        return new Innsyn(innsynRestKlient.hentInnsynsbehandling(new BehandlingIdDto(behandlingId)));
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
