package no.nav.foreldrepenger.melding.kodeverk;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import no.nav.foreldrepenger.melding.kodeverk.diff.DiffIgnore;
import no.nav.vedtak.sikkerhet.context.SubjectHandler;
import no.nav.vedtak.util.FPDateUtil;

/**
 * En basis {@link Entity} klasse som håndtere felles standarder for utformign av tabeller (eks. sporing av hvem som har
 * opprettet eller oppdatert en rad, og når).
 */
@MappedSuperclass
public class KodeverkBaseEntitet implements Serializable {

    private static final String BRUKERNAVN_NÅR_SIKKERHETSKONTEKST_IKKE_FINNES = "VL";

    @DiffIgnore
    @Column(name = "opprettet_av", nullable = false)
    private String opprettetAv;

    @DiffIgnore
    @Column(name = "opprettet_tid", nullable = false)
    private LocalDateTime opprettetTidspunkt; // NOSONAR

    @DiffIgnore
    @Column(name = "endret_av")
    private String endretAv;

    @DiffIgnore
    @Column(name = "endret_tid")
    private LocalDateTime endretTidspunkt; // NOSONAR

    private static String finnBrukernavn() {
        String brukerident = SubjectHandler.getSubjectHandler().getUid();
        return brukerident != null ? brukerident : BRUKERNAVN_NÅR_SIKKERHETSKONTEKST_IKKE_FINNES;
    }

    @PrePersist
    protected void onCreate() {
        this.opprettetAv = finnBrukernavn();
        this.opprettetTidspunkt = FPDateUtil.nå();
    }

    @PreUpdate
    protected void onUpdate() {
        endretAv = finnBrukernavn();
        endretTidspunkt = FPDateUtil.nå();
    }

    public String getOpprettetAv() {
        return opprettetAv;
    }

    public LocalDateTime getOpprettetTidspunkt() {
        return opprettetTidspunkt;
    }

    public String getEndretAv() {
        return endretAv;
    }

    public LocalDateTime getEndretTidspunkt() {
        return endretTidspunkt;
    }
}
