package no.nav.foreldrepenger.melding.datamapper.brev;

import java.util.Optional;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.inject.Named;

import no.nav.foreldrepenger.melding.behandling.Behandling;
import no.nav.foreldrepenger.melding.datamapper.BrevMapperUtil;
import no.nav.foreldrepenger.melding.datamapper.DomeneobjektProvider;
import no.nav.foreldrepenger.melding.datamapper.domene.BehandlingMapper;
import no.nav.foreldrepenger.melding.datamapper.domene.KlageMapper;
import no.nav.foreldrepenger.melding.datamapper.konfig.BrevParametere;
import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.foreldrepenger.melding.hendelser.DokumentHendelse;
import no.nav.foreldrepenger.melding.klage.Klage;
import no.nav.foreldrepenger.melding.typer.Dato;

@ApplicationScoped
@Named(DokumentMalType.KLAGE_HJEMSENDT)
public class KlageHjemsendtBrevMapper extends FritekstmalBrevMapper {

    public KlageHjemsendtBrevMapper() {
        //CDI
    }

    @Inject
    public KlageHjemsendtBrevMapper(BrevParametere brevParametere, DomeneobjektProvider domeneobjektProvider) {
        super(brevParametere, domeneobjektProvider);
    }

    @Override
    public String displayName() {
        return "Vedtak om hjemsending og evt. oppheving i klagesak";
    }

    @Override
    public String templateFolder() {
        return "vedtakomhjemsendingiklagesak";
    }

    @Override
    Brevdata mapTilBrevfelter(DokumentHendelse hendelse, Behandling behandling) {
        Brevdata brevdata = new Brevdata();
        Klage klage = domeneobjektProvider.hentKlagebehandling(behandling);

        Optional<String> fritekstOpt = KlageMapper.avklarFritekstKlage(hendelse, klage);
        fritekstOpt.ifPresent(s -> brevdata.leggTil("mintekst", s));

        brevdata.leggTil("ytelseType", hendelse.getYtelseType().getKode());
        brevdata.leggTil("opphevet", KlageMapper.erOpphevet(klage, hendelse));
        brevdata.leggTil("saksbehandlingstid", BehandlingMapper.finnAntallUkerBehandlingsfrist(behandling.getBehandlingType()));
        brevdata.leggTil("ettersendelsesfrist", Dato.formaterDato(BrevMapperUtil.getSvarFrist(brevParametere)));

        return brevdata;
    }
}
