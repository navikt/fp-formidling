package no.nav.foreldrepenger.melding.datamapper;

import java.util.HashMap;
import java.util.Map;

import no.nav.foreldrepenger.melding.dokumentdata.DokumentMalType;
import no.nav.vedtak.feil.FeilFactory;

public class DokumentTypeRuter {

    // Liste over alle tilgjengelige mappere som instansieres uten argumenter. Vurdere å skrive om til CDI.
    private static Map<String, Class<? extends DokumentTypeMapper>> mappere = new HashMap<>();

    static {
//        mappere.put(DokumentMalType.POSITIVT_VEDTAK_DOK, VedtaksbrevMapper.class);
//        mappere.put(DokumentMalType.INNVILGELSE_FORELDREPENGER_DOK, InnvilgelseForeldrepengerMapper.class);
//        mappere.put(DokumentMalType.INNHENT_DOK, InnhentopplysningerBrevMapper.class);
        mappere.put(DokumentMalType.HENLEGG_BEHANDLING_DOK, HenleggBehandlingBrevMapper.class);
//        mappere.put(DokumentMalType.AVSLAGSVEDTAK_DOK, AvslagsbrevMapper.class);
        mappere.put(DokumentMalType.UENDRETUTFALL_DOK, UendretutfallBrevMapper.class);
//        mappere.put(DokumentMalType.REVURDERING_DOK, RevurderingBrevMapper.class);
//        mappere.put(DokumentMalType.FORLENGET_DOK, ForlengetSaksbehandlingstidBrevMapper.class);
//        mappere.put(DokumentMalType.FORLENGET_MEDL_DOK, ForlengetSaksbehandlingstidBrevMapper.class);
//        mappere.put(DokumentMalType.FORLENGET_TIDLIG_SOK, ForlengetSaksbehandlingstidBrevMapper.class);
//        mappere.put(DokumentMalType.FORLENGET_OPPTJENING, ForlengetSaksbehandlingstidBrevMapper.class);
//        mappere.put(DokumentMalType.KLAGE_OVERSENDT_KLAGEINSTANS_DOK, KlageOversendtKlageinstansBrevMapper.class);
//        mappere.put(DokumentMalType.KLAGE_YTELSESVEDTAK_OPPHEVET_DOK, KlageYtelsesvedtakOpphevetBrevMapper.class);
//        mappere.put(DokumentMalType.KLAGE_YTELSESVEDTAK_STADFESTET_DOK, KlageYtelsesvedtakStadfestetBrevMapper.class);
//        mappere.put(DokumentMalType.INNSYNSKRAV_SVAR, InnsynskravSvarBrevMapper.class);
//        mappere.put(DokumentMalType.OPPHØR_DOK, OpphørbrevMapper.class);
//        mappere.put(DokumentMalType.INNTEKTSMELDING_FOR_TIDLIG_DOK, InntektsmeldingForTidligMapper.class);
//        mappere.put(DokumentMalType.AVSLAG_FORELDREPENGER_DOK, AvslagForeldrepengerMapper.class);
//        mappere.put(DokumentMalType.FRITEKST_DOK, FritekstbrevMapper.class);
//        mappere.put(DokumentMalType.VEDTAK_MEDHOLD, VedtakMedholdBrevMapper.class);
    }

    private DokumentTypeRuter() {
        // Skal ikke instansieres
    }

    public static DokumentTypeMapper dokumentTypeMapper(DokumentMalType dokumentMalType) throws InstantiationException, IllegalAccessException {
        String kode = dokumentMalType.getKode();
        if (mappere.containsKey(kode)) {
            return mappere.get(kode).newInstance();
        }
        throw FeilFactory.create(DokumentBestillerFeil.class).ukjentDokumentType(kode).toException();
    }
}