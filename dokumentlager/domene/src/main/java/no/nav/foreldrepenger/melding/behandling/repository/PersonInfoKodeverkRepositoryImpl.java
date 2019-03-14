package no.nav.foreldrepenger.melding.behandling.repository;

import static java.util.stream.Collectors.toList;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;

import no.nav.foreldrepenger.melding.aktør.NavBrukerKjønn;
import no.nav.foreldrepenger.melding.aktør.PersonstatusType;
import no.nav.foreldrepenger.melding.behandling.personopplysning.SivilstandType;
import no.nav.foreldrepenger.melding.geografisk.Landkoder;
import no.nav.foreldrepenger.melding.geografisk.Region;
import no.nav.foreldrepenger.melding.kodeverk.KodelisteRelasjon;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepository;
import no.nav.foreldrepenger.melding.kodeverk.KodeverkRepositoryImpl;
import no.nav.vedtak.felles.jpa.VLPersistenceUnit;

@ApplicationScoped
public class PersonInfoKodeverkRepositoryImpl implements PersonInfoKodeverkRepository {
    private EntityManager entityManager;
    private KodeverkRepository kodeverkRepository;

    PersonInfoKodeverkRepositoryImpl() {
        // for CDI proxy
    }

    @Inject
    public PersonInfoKodeverkRepositoryImpl(@VLPersistenceUnit EntityManager entityManager,
                                            KodeverkRepository kodeverkRepository) {
        Objects.requireNonNull(entityManager, "entityManager"); //$NON-NLS-1$
        this.entityManager = entityManager;
        this.kodeverkRepository = kodeverkRepository;
    }

    public PersonInfoKodeverkRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
        if (entityManager != null) {
            this.kodeverkRepository = new KodeverkRepositoryImpl(entityManager);
        }
    }

    @Override
    public SivilstandType finnSivilstandType(String kode) {
        return kodeverkRepository.finn(SivilstandType.class, kode);
    }

    @Override
    public Landkoder finnLandkode(String kode) {
        return kodeverkRepository.finn(Landkoder.class, kode);
    }

    @Override
    public Region finnHøyestRangertRegion(List<String> statsborgerskap) {
        Set<Region> regioner = new HashSet<>();
        for (String skap : statsborgerskap) {
            regioner.addAll(finnRegioner(skap));
        }
        return regioner.stream().min(Comparator.comparing(this::rangerRegion)).get();
    }

    // Det finnes ingen definert rangering for regioner. Men venter med å generalisere til det finnes use-caser som
    // krever en annen rangering enn nedenfor.
    private Integer rangerRegion(Region region) {
        if (region.equals(Region.NORDEN)) {
            return 1;
        }
        if (region.equals(Region.EOS)) {
            return 2;
        }
        return 3;
    }

    @Override
    public List<Region> finnRegioner(String kode) {
        TypedQuery<KodelisteRelasjon> query = entityManager.createQuery("from KodelisteRelasjon where kodeverk1 = 'REGION' AND kodeverk2 = 'LANDKODER' AND kode2=:kode", KodelisteRelasjon.class);
        query.setParameter("kode", kode);
        List<String> regionKoder = query.getResultList().stream()
                .map(KodelisteRelasjon::getKode1)
                .collect(toList());
        if (regionKoder.isEmpty()) {
            return Arrays.asList(Region.TREDJELANDS_BORGER);
        }

        return kodeverkRepository.finnListe(Region.class, regionKoder);
    }

    @Override
    public NavBrukerKjønn finnBrukerKjønn(String kode) {
        return kodeverkRepository.finn(NavBrukerKjønn.class, kode);
    }

    @Override
    public PersonstatusType finnPersonstatus(String kode) {
        return kodeverkRepository.finn(PersonstatusType.class, kode);
    }
}
