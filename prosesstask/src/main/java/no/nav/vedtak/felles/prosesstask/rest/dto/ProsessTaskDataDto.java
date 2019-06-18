package no.nav.vedtak.felles.prosesstask.rest.dto;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Properties;

import io.swagger.annotations.ApiModel;
import no.nav.vedtak.log.sporingslogg.Sporingsdata;
import no.nav.vedtak.log.sporingslogg.StandardSporingsloggId;

@ApiModel
public class ProsessTaskDataDto implements ProsessTaskDataInfo {

    private static final String AKTØR_ID = "aktoerId";
    private static final String BEHANDLING_ID = "behandlingId";
    private static final String FAGSAK_ID = "fagsakId";
    private static final String PNR_ID = "personidentifikator";

    private Long id;
    private String taskType;
    private LocalDateTime nesteKjøringEtter;
    private String gruppe;
    private String sekvens;
    private String status;
    private LocalDateTime sistKjørt;
    private String sisteFeilKode;
    private Properties taskParametre = new Properties();

    public ProsessTaskDataDto() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType;
    }

    public LocalDateTime getNesteKjøringEtter() {
        return nesteKjøringEtter;
    }

    public void setNesteKjøringEtter(LocalDateTime nesteKjøringEtter) {
        this.nesteKjøringEtter = nesteKjøringEtter;
    }

    public String getGruppe() {
        return gruppe;
    }

    public void setGruppe(String gruppe) {
        this.gruppe = gruppe;
    }

    public String getSekvens() {
        return sekvens;
    }

    public void setSekvens(String sekvens) {
        this.sekvens = sekvens;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getSistKjørt() {
        return sistKjørt;
    }

    public void setSistKjørt(LocalDateTime sistKjørt) {
        this.sistKjørt = sistKjørt;
    }

    public String getSisteFeilKode() {
        return sisteFeilKode;
    }

    public void setSisteFeilKode(String sisteFeilKode) {
        this.sisteFeilKode = sisteFeilKode;
    }

    public Properties getTaskParametre() {
        return taskParametre;
    }

    public void setTaskParametre(Properties taskParametre) {
        this.taskParametre = taskParametre;
    }

    @Override
    public Optional<Sporingsdata> lagSporingsloggData() {

        String aktørId = taskParametre.getProperty(AKTØR_ID);
        String fagsakId = taskParametre.getProperty(FAGSAK_ID);
        String behandlingId = taskParametre.getProperty(BEHANDLING_ID);
        String pnrId = taskParametre.getProperty(PNR_ID);

        if (aktørId == null && fagsakId == null && behandlingId == null && pnrId == null) {
            return Optional.empty();
        }

        Sporingsdata sporingsdata = Sporingsdata.opprett();
        if (aktørId != null) {
            sporingsdata.leggTilId(StandardSporingsloggId.AKTOR_ID, Long.valueOf(aktørId));
        }
        if (fagsakId != null) {
            sporingsdata.leggTilId(StandardSporingsloggId.FAGSAK_ID, Long.valueOf(fagsakId));
        }
        if (behandlingId != null) {
            sporingsdata.leggTilId(StandardSporingsloggId.BEHANDLING_ID, Long.valueOf(behandlingId));
        }
        if (pnrId != null) {
            sporingsdata.leggTilId(StandardSporingsloggId.FNR, Long.valueOf(pnrId));
        }
        return Optional.of(sporingsdata);
    }
}
