package no.nav.foreldrepenger.melding.datamapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.mal.fritekst.BrevmalKilder.ROTMAPPE;
import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.UUID;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.behandling.BehandlingType;
import no.nav.foreldrepenger.melding.datamapper.domene.SvpMapper;
import no.nav.foreldrepenger.melding.fagsak.FagsakYtelseType;
import no.nav.foreldrepenger.melding.geografisk.Språkkode;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokument.fritekstbrev.FagType;
import no.nav.foreldrepenger.melding.typer.DatoIntervall;
import no.nav.foreldrepenger.melding.uttak.PeriodeResultatType;
import no.nav.foreldrepenger.melding.uttak.svp.PeriodeIkkeOppfyltÅrsak;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatPeriode;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttakResultatPerioder;
import no.nav.foreldrepenger.melding.uttak.svp.SvpUttaksresultat;

public class InnvilgelseSvangerskapspengerBrevMapperTest {
    private static final long ID = 123L;
    private Behandling behandling;
    private DokumentHendelse dokumentHendelse;

    @InjectMocks
    private InnvilgelseSvangerskapspengerBrevMapper mapper;

    @Before
    public void setup() {
        behandling = Behandling.builder().medId(ID)
                .medBehandlingType(BehandlingType.FØRSTEGANGSSØKNAD)
                .medBehandlendeEnhetNavn(HenleggBehandlingBrevMapper.FAMPEN)
                .medSpråkkode(Språkkode.nb)
                .build();
        dokumentHendelse = DokumentHendelse.builder()
                .medBehandlingUuid(UUID.randomUUID())
                .medBestillingUuid(UUID.randomUUID())
                .medYtelseType(FagsakYtelseType.FORELDREPENGER)
                .build();
        SvpUttaksresultat uttakResultat = mockUttaksresultat();

        mapper = new InnvilgelseSvangerskapspengerBrevMapper() {
            @Override
            Brevdata mapTilBrevfelter(DokumentHendelse hendelse, Behandling behandling) {
                return new SvpMapper.SvpBrevData(null, null, uttakResultat, true, 2) {
                    public long getManedsbelop() {
                        return 25342L;
                    }

                    public String getMottattDato() {
                        return "1. januar 2000";
                    }

                    public String getKontaktTelefonnummer() {
                        return "11111111";
                    }

                    public String getNavnAvsenderEnhet() {
                        return "Avsender";
                    }

                    public boolean getErAutomatiskVedtak() {
                        return true;
                    }
                };
            }
        };
        MockitoAnnotations.initMocks(this);
    }

    @Ignore
    @Test
    public void test_map_fagtype() {
        ResourceBundle expectedValues = ResourceBundle.getBundle(
                String.join("/", ROTMAPPE, mapper.templateFolder(), "expected"),
                new Locale("nb", "NO"));

        FagType fagType = mapper.mapFagType(dokumentHendelse, behandling);
        assertThat(fagType.getBrødtekst()).isEqualToNormalizingNewlines(expectedValues.getString("brødtekst"));
        assertThat(fagType.getHovedoverskrift()).isEqualToIgnoringWhitespace(expectedValues.getString("overskrift"));
    }

    private SvpUttaksresultat mockUttaksresultat() {
        SvpUttakResultatPeriode innvilgetPeriode = SvpUttakResultatPeriode.ny()
                .medTidsperiode(DatoIntervall.fraOgMed(LocalDate.now().plusMonths(1)))
                .medPeriodeResultatType(PeriodeResultatType.INNVILGET)
                .medUtbetalingsgrad(50L)
                .medAktivitetDagsats(2452).build();
        SvpUttakResultatPeriode avslåttPeriode = SvpUttakResultatPeriode.ny()
                .medTidsperiode(DatoIntervall.fraOgMedTilOgMed(LocalDate.now(), LocalDate.now().plusMonths(1)))
                .medPeriodeResultatType(PeriodeResultatType.AVSLÅTT)
                .medPeriodeIkkeOppfyltÅrsak(PeriodeIkkeOppfyltÅrsak.SVANGERSKAPSVILKÅRET_IKKE_OPPFYLT)
                .medAktivitetDagsats(3126).build();
        SvpUttakResultatPerioder resArbeidsforhold = SvpUttakResultatPerioder.ny()
                .medArbeidsgiverNavn("Tine")
                .medPeriode(innvilgetPeriode).build();
        return SvpUttaksresultat.ny()
                .medUttakResultatPerioder(resArbeidsforhold)
                .medAvslåttePerioder(List.of(avslåttPeriode))
                .build();
    }

}
