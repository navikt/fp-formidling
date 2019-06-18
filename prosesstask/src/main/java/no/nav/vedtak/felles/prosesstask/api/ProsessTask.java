package no.nav.vedtak.felles.prosesstask.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.enterprise.inject.Stereotype;
import javax.inject.Qualifier;

import no.nav.vedtak.felles.jpa.Transaction;

/**
 * Marker type som implementerer interface {@link ProsessTaskHandler}.<br>
 * Dette er en CDI stereotype som også angir at den skal kjøres i en transaksjon.
 * <p>
 * <h3>Eksempel</h3>
 * Merk skal ha både {@link ProsessTask} annotation + {@link ProsessTaskHandler} interface!!!
 * <p>
 * <p>
 * <pre>
 * &#64;Dependent
 * &#64;ProsessTask("vuin.happyTask")
 * public class HappyTask implements ProsessTaskHandler {
 *
 *     private static final Logger log = LoggerFactory.getLogger(HappyTask.class);
 *
 *     &#64;Override
 *     public void doTask(ProsessTaskData prosessTaskData) {
 *         log.info("I am a HAPPY task :-)");
 *     }
 *
 * }
 * </pre>
 * <p>
 * Denne må matche ett innslag i <code>PROSESS_TASK_TYPE</code> tabell for å kunne kjøres. <br/>
 * En konkret kjøring utføres
 * ved å registrere typen i tillegg i <code>PROSESS_TASK</code> tabellen.
 */
@Qualifier
@Stereotype
@Transaction
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Documented
public @interface ProsessTask {

    /**
     * Settes til task type, slik det er definert i PROSESS_TASK_TYPE tabell.
     * Markerer implementasjonen slik at det kan oppdages runtime.
     * <p>
     * Må spesifiseres.
     */
    String value();

}
