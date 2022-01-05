package no.nav.foreldrepenger.fpformidling;


import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchIgnore;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.library.GeneralCodingRules;

@AnalyzeClasses(packages = ArchitectureTest.ROOT, importOptions = {ImportOption.DoNotIncludeTests.class})
public class ArchitectureTest {
    public static final String ROOT = "no.nav.foreldrepenger.fpformidling";

    @ArchTest @ArchIgnore(reason = "There are cycles")
    public static final ArchRule no_cycles_between_slices_based_on_top_level_packages =
        slices()
            .matching(ROOT + ".(*)..")
            .should().beFreeOfCycles();

    @ArchTest
    public static final ArchRule no_printing_to_system_out =
        GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS
            .because("we have logging for ... logging");

    @ArchTest
    public static void sanity_check_for_number_of_classes_to_analyze(JavaClasses classes) {
        assertTrue(classes.size() > 100, "There is something wrong - there should be at least 100 classes to analyze");
    }
}

