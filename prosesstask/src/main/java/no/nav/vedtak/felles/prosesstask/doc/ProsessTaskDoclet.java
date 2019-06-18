package no.nav.vedtak.felles.prosesstask.doc;

import java.io.File;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Stream;

import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementFilter;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceException;
import javax.tools.Diagnostic.Kind;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import no.nav.vedtak.felles.prosesstask.api.ProsessTask;
import no.nav.vedtak.felles.prosesstask.impl.ProsessTaskType;

public class ProsessTaskDoclet implements Doclet {

    @SuppressWarnings("unused")
    private Locale locale;
    private Reporter reporter;

    @Override
    public void init(Locale locale, Reporter reporter) {
        this.locale = locale;
        this.reporter = reporter;

    }

    @Override
    public String getName() {
        return getClass().getSimpleName();
    }

    @Override
    public Set<? extends Option> getSupportedOptions() {
        return Collections.emptySet();
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.RELEASE_10;
    }

    @Override
    public boolean run(DocletEnvironment environment) {
        System.out.println("Kjører Javadoc Doclet - " + getClass().getSimpleName());
        try {
            doRun(environment);
            return true;
        } catch (Error | RuntimeException e) {
            reporter.print(Kind.ERROR, e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private void doRun(DocletEnvironment docEnv) {
        ProsessTaskModell resultat = new ProsessTaskModell();

        Set<TypeElement> types = ElementFilter.typesIn(docEnv.getIncludedElements());
        Stream<TypeElement> feilFilter = types.stream().filter(te -> te.getAnnotation(ProsessTask.class) != null);

        feilFilter.forEach(te -> {
            String qualifiedName = te.getQualifiedName().toString();
            String comment = docEnv.getElementUtils().getDocComment(te);
            ProsessTask prosessTaskAnnotation = te.getAnnotation(ProsessTask.class);
            String kode = prosessTaskAnnotation.value();
            resultat.leggTil(kode, qualifiedName, comment);
        });

        EntityManager em = new OpprettH2EntityManager().createEntityManager();
        resultat.getEntries().forEach(e -> {
            try {
                ProsessTaskType ptt = em.find(ProsessTaskType.class, e.getKode());
                if (ptt == null) {
                    reporter.print(Kind.WARNING, String.format("Mangler %s for kode: %s", ProsessTaskType.class.getSimpleName(), e.getKode()));
                } else {
                    e.leggTil(ptt.getBeskrivelse());
                    e.setNavn(ptt.getNavn());
                    e.setProsessTaskType(ptt);
                }
            } catch (PersistenceException ex) {
                reporter.print(Kind.WARNING, String.format("Fant ikke database innslag %s for kode: %s", ProsessTaskType.class.getSimpleName(), e.getKode()));
                ex.printStackTrace();
            }
        });

        File outputFile = new File(getOutputLocation(), "prosessTask");
        new AsciidocMapper().writeTo(outputFile.toPath(), resultat);
    }

    private File getOutputLocation() {
        File dir = new File(System.getProperty("destDir", "target/docs"));
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IllegalStateException("Could not create output directory:" + dir);
            }
        }
        return dir;
    }

}
