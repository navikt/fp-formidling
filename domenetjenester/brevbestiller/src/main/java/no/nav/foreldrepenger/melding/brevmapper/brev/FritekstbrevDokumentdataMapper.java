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
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.FritekstbrevDokumentdata;
import no.nav.foreldrepenger.melding.integrasjon.dokgen.dto.felles.Fritekst;
import no.nav.foreldrepenger.melding.kodeverk.kodeverdi.DokumentMalTypeKode;

@ApplicationScoped
@DokumentMalTypeRef(DokumentMalTypeKode.FRITEKSTBREV)
public class FritekstbrevDokumentdataMapper implements DokumentdataMapper {

    @Inject
    public FritekstbrevDokumentdataMapper() {
    }

    @Override
    public String getTemplateNavn() {
        return "fritekstbrev";
    }

    @Override
    public FritekstbrevDokumentdata mapTilDokumentdata(DokumentFelles dokumentFelles, DokumentHendelse hendelse,
                                                       Behandling behandling, boolean erUtkast) {
        var fellesBuilder = opprettFellesBuilder(dokumentFelles, hendelse, behandling, erUtkast);
        fellesBuilder.medBrevDato(dokumentFelles.getDokumentDato() != null ? formaterDato(dokumentFelles.getDokumentDato(), behandling.getSpråkkode()) : null);

        return FritekstbrevDokumentdata.ny()
                .medFelles(fellesBuilder.build())
                .medOverskrift(finnOverskrift(hendelse, behandling))
                .medBrødtekst(Fritekst.fra(finnBrødtekst(hendelse, behandling)))
                .build();
    }

    private String finnOverskrift(DokumentHendelse hendelse, Behandling behandling) {
        return hendelse.getTittel() != null && !hendelse.getTittel().isEmpty() ? hendelse.getTittel() : behandling.getBehandlingsresultat().getOverskrift();
    }

    private String finnBrødtekst(DokumentHendelse hendelse, Behandling behandling) {
        return hendelse.getFritekst() != null && !hendelse.getFritekst().isEmpty() ?
                hendelse.getFritekst() : behandling.getBehandlingsresultat().getFritekstbrev();
    }
}