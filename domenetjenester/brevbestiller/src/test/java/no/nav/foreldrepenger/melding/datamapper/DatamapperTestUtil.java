package no.nav.foreldrepenger.melding.datamapper;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentAdresse;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentData;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.felles.FellesType;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalType;
import no.nav.foreldrepenger.melding.typer.Saksnummer;
import org.mockito.Mockito;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.Optional;
import java.util.UUID;

import static org.mockito.Mockito.when;

public class DatamapperTestUtil {

    public static final String SOEKERS_NAVN = "SOEKERS_NAVN";
    public static final String FRITEKST = "FRITEKST";
    public static final LocalDate FØRSTE_JANUAR_TJUENITTEN = LocalDate.of(2019, 1, 1);
    public static final Period SVARFRIST = Period.ofWeeks(6);

    private static final int KLAGEFRIST = 14;
    private static final int KLAGEFRIST_INNSYN = 14;
    private static final Period SØK_ANTALL_UKER = Period.ofWeeks(6);

    private static BrevParametere brevParametere = new BrevParametere(KLAGEFRIST, KLAGEFRIST_INNSYN, SVARFRIST, SØK_ANTALL_UKER);

    public static BrevParametere getBrevParametere() {
        return brevParametere;
    }

    public static DokumentFelles getDokumentFelles() {
        DokumentFelles dokumentFelles = Mockito.mock(DokumentFelles.class);
        when(dokumentFelles.getSakspartNavn()).thenReturn(SOEKERS_NAVN);
        when(dokumentFelles.getSakspartPersonStatus()).thenReturn("ANNET");
        return dokumentFelles;
    }

    public static DokumentFelles lagStandardDokumentFelles(DokumentData dokumentdata, String mottakerNavn, DokumentFelles.Kopi kopi) {
        DokumentAdresse dokumentAdresse = new DokumentAdresse.Builder()
                .medAdresselinje1("Adresse 1")
                .medPostNummer("0491")
                .medPoststed("OSLO")
                .medMottakerNavn(SOEKERS_NAVN)
                .build();

        return DokumentFelles.builder(dokumentdata)
                .medAutomatiskBehandlet(Boolean.TRUE)
                .medDokumentDato(LocalDate.now())
                .medKontaktTelefonNummer("22222222")
                .medMottakerAdresse(dokumentAdresse)
                .medNavnAvsenderEnhet("NAV Familie og pensjonsytelser")
                .medPostadresse(dokumentAdresse)
                .medReturadresse(dokumentAdresse)
                .medMottakerId("123456789")
                .medMottakerNavn(mottakerNavn == null ? SOEKERS_NAVN : mottakerNavn)
                .medSaksnummer(new Saksnummer("123456"))
                .medSakspartId("99999999999")
                .medSakspartNavn(SOEKERS_NAVN)
                .medErKopi(kopi == null ? Optional.empty() : Optional.of(kopi))
                .medMottakerType(DokumentFelles.MottakerType.PERSON)
                .medSpråkkode(Språkkode.nb)
                .medSakspartPersonStatus("ANNET")
                .build();
    }

    public static DokumentData lagDokumentData() {
        return   DokumentData.builder()
                .medDokumentMalType(DokumentMalType.INNVILGELSE_ENGANGSSTØNAD)
                .medBehandlingUuid(UUID.randomUUID())
                .medBestillingType("B")
                .medBestiltTid(LocalDateTime.now())
                .build();
    }

    public static FellesType getFellesType() {
        return new FellesType();
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

    public static Behandling.Builder standardBehandlingBuilder() {
        return Behandling.builder().medId(123L).medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD).medSpråkkode(Språkkode.nb);
    }

    public static Behandling standardBehandling() {
        return standardBehandlingBuilder().build();
    }

}
