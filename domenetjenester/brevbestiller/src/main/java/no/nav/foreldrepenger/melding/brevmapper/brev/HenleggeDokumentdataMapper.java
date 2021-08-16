package no.nav.foreldrepenger.melding.brevmapper.brev;

import static no.nav.foreldrepenger.melding.datamapper.util.BrevMapperUtil.opprettFellesBuilder;
import static no.nav.foreldrepenger.melding.typer.Dato.formaterDato;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.brevmapper.DokumentdataMapper;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentFelles;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalTypeRef;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.HenleggelseDokumentdata;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.INFO_OM_HENLEGGELSE)
public class HenleggeDokumentdataMapper implements DokumentdataMapper {
    private static final String FAMPEN = "NAV Familie- og pensjonsytelser";

    @Inject
    public HenleggeDokumentdataMapper() {
    }

    @Override
    public String getTemplateNavn() {
        return "henleggelse";
    }

    @Override
    public HenleggelseDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse hendelse,
                                                      Behandling behandling, boolean erUtkast) {

        var fellesBuilder = opprettFellesBuilder(dokumentFelles, hendelse, behandling, erUtkast);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), behandling.getSpråkkode()) : null);
        fellesBuilder.medErAutomatiskBehandlet(dokumentFelles.getAutomatiskBehandlet());

        return HenleggelseDokumentdata.ny()
                .medFelles(fellesBuilder.build())
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
