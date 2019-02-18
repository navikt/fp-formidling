package no.nav.foreldrepenger.melding.docs;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.lang.model.SourceVersion;
import javax.tools.DiagnosticCollector;
import javax.tools.DocumentationTool;
import javax.tools.DocumentationTool.DocumentationTask;
import javax.tools.JavaCompiler;
import javax.tools.JavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

import jdk.javadoc.doclet.Doclet;
import jdk.javadoc.doclet.DocletEnvironment;
import jdk.javadoc.doclet.Reporter;
import no.nav.vedtak.feil.doc.FeilmeldingDoclet;
import no.nav.vedtak.felles.db.doc.JdbcDoclet;
import no.nav.vedtak.felles.integrasjon.felles.ws.doc.WebServiceDoclet;
import no.nav.vedtak.konfig.doc.KonfigverdiDoclet;

//import no.nav.vedtak.felles.prosesstask.doc.ProsessTaskDoclet;

/**
 * Aggregert doclet for å kunne håndtere alle sammen samtidig.
 */
public class Sysdoclet implements Doclet {

    static {
        // database connection settings
        System.setProperty("doc.plugin.jdbc.url", System.getProperty("doc.plugin.jdbc.url", "jdbc:oracle:thin:@localhost:1521:XE"));
        System.setProperty("doc.plugin.jdbc.dslist", System.getProperty("doc.plugin.jdbc.dslist", "defaultDS"));
        System.setProperty("doc.plugin.jdbc.db.migration.defaultDS", "filesystem:../migreringer/src/main/resources/db/migration/defaultDS");
        System.setProperty("doc.plugin.jdbc.username.defaultDS", "fpformidling");
    }

    private List<Doclet> doclets = Arrays.asList(
            new FeilmeldingDoclet(),
            new JdbcDoclet(),
            new WebServiceDoclet(),
            new KonfigverdiDoclet()
    );

    /**
     * NB: Antar workingdir er satt til root av multi-module maven project.
     */
    public static void main(String[] args) throws IOException {

        DocumentationTool documentationTool = ToolProvider.getSystemDocumentationTool();
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> diagnostics = new DiagnosticCollector<>();

        Path start = Paths.get(".").toAbsolutePath();
        List<Path> sourceFiles = findSourceFiles(start);

        try (StandardJavaFileManager fm = compiler.getStandardFileManager(diagnostics, null, Charset.forName("UTF-8"))) {
            Iterable<? extends JavaFileObject> javaFileObjects = fm.getJavaFileObjectsFromPaths(sourceFiles);
            DocumentationTask task = documentationTool.getTask(null, null, null, Sysdoclet.class, Arrays.asList("-Xmaxerrs", "10000", "-Xmaxwarns", "10000"),
                    javaFileObjects);
            task.call();

        }
    }

    private static List<Path> findSourceFiles(Path start) throws IOException {
        List<Path> sourceFiles = new ArrayList<>(10000);
        Files.walkFileTree(start, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                boolean hiddenDir = dir.getFileName().startsWith(".") && !dir.getFileName().endsWith(".");
                if (dir.endsWith("target") || dir.endsWith("test") || dir.endsWith("testutil") || dir.endsWith(".git") || hiddenDir
                        || dir.endsWith("node_modules")) {
                    return FileVisitResult.SKIP_SUBTREE;
                } else if (dir.endsWith("./web/server/")) {
                    // gammel web server modul
                    return FileVisitResult.SKIP_SUBTREE;
                } else {
                    return super.preVisitDirectory(dir, attrs);
                }
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                if (file.toString().endsWith(".java")) {
                    sourceFiles.add(file);
                }
                return super.visitFile(file, attrs);
            }
        });
        return sourceFiles;
    }

    @Override
    public void init(Locale locale, Reporter reporter) {
        doclets.forEach(d -> d.init(locale, reporter));
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
        return SourceVersion.latestSupported();
    }

    @Override
    public boolean run(DocletEnvironment docEnv) {
        System.out.println("running Sysdoclets");
        return doclets.stream().allMatch(d -> {
            return d.run(docEnv);
        });
    }

}
