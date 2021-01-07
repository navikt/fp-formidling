package no.nav.foreldrepenger.melding.brevmapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.formaterPersonnummer;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDatoNorsk;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brevmapper.DokumentdataMapper;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.FellesDokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.HenleggelseDokumentdata;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.INFO_OM_HENLEGGELSE)
public class HenleggeDokumentDataMapper implements DokumentdataMapper {
    private static final String FAMPEN = "NAV Familie- og pensjonsytelser";

    @Inject
    public HenleggeDokumentDataMapper() {
    }

    @Override
    public String getTemplateNavn() {
        return "henleggelse";
    }

    @Override
    public HenleggelseDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse hendelse, Behandling behandling) {
        var fellesDataBuilder = FellesDokumentdata.ny()
                .medSøkerNavn(dokumentFelles.getSakspartNavn())
                .medSøkerPersonnummer(formaterPersonnummer(dokumentFelles.getSakspartId()))
                .medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDatoNorsk(dokumentFelles.getDokumentDato()) : null)
                .medErAutomatiskBehandlet(dokumentFelles.getAutomatiskBehandlet())
                .medSaksnummer(dokumentFelles.getSaksnummer().getVerdi())
                .medYtelseType(hendelse.getYtelseType().getKode());

        return  HenleggelseDokumentdata.ny()
                .medFelles(fellesDataBuilder.build())
                .medVanligBehandling(behandling.getBehandlingType().erYtelseBehandlingType())
                .medAnke(behandling.erAnke())
                .medInnsyn(behandling.erInnsyn())
                .medKlage(behandling.erKlage())
                .medOpphavType(utledOpphavType(hendelse.getBehandlendeEnhetNavn() == null || hendelse.getBehandlendeEnhetNavn().isEmpty() ? behandling.getBehandlendeEnhetNavn() : hendelse.getBehandlendeEnhetNavn()))
                .build();
    }

    private String utledOpphavType(String behandlendeEnhetNavn) {
        if (behandlendeEnhetNavn.contains(FAMPEN)) {
            return "FAMPEN";
        }
        return "KLAGE";
    }

}
