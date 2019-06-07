package no.nav.foreldrepenger.melding.datamapper;

import static no.nav.foreldrepenger.melding.datamapper.mal.DokumentType.DEFAULT_PERSON_STATUS;
import static org.mockito.Mockito.doReturn;

import java.time.Period;
import java.util.UUID;

import org.mockito.Mockito;

import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametereImpl;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;

public class DatamapperTestUtil {

    public static String FRITEKST = "FRITEKST";


    public static BrevParametere getBrevParametere() {
        return new BrevParametereImpl(14, 14, Period.ofWeeks(6), Period.ofWeeks(6));
    }

    public static DokumentFelles getDokumentFelles() {
        DokumentFelles dokumentFelles = Mockito.mock(DokumentFelles.class);
        doReturn("Aleksander").when(dokumentFelles).getSakspartNavn();
        doReturn(DEFAULT_PERSON_STATUS).when(dokumentFelles).getSakspartPersonStatus();
        return dokumentFelles;
    }

    public static DokumentHendelse.Builder lagStandardHendelseBuilder() {
        return DokumentHendelse.builder()
                .medBestillingUuid(UUID.randomUUID())
                .medBehandlingUuid(UUID.randomUUID())
                .medFritekst(FRITEKST)
                .medYtelseType(FagsakYtelseType.FORELDREPENGER);
    }

    public static DokumentHendelse standardDokumenthendelse() {
        return lagStandardHendelseBuilder().build();
    }

}
