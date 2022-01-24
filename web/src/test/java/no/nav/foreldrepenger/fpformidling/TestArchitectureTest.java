package no.nav.foreldrepenger.fpformidling;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.methods;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import com.tngtech.archunit.base.DescribedPredicate;
import com.tngtech.archunit.core.domain.JavaCall;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.domain.JavaMember;
import com.tngtech.archunit.core.domain.JavaMethod;
import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchIgnore;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ArchRule;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import com.tngtech.archunit.library.GeneralCodingRules;

@AnalyzeClasses(packages = ArchitectureTest.ROOT, importOptions = {ImportOption.OnlyIncludeTests.class})
public class TestArchitectureTest {

    @ArchTest
    public static final ArchRule no_printing_to_system_out =
        GeneralCodingRules.NO_CLASSES_SHOULD_ACCESS_STANDARD_STREAMS
            .because("we use assertions in tests");


    private static final DescribedPredicate<JavaMember> isAnnotatedWithTest =
        new DescribedPredicate<>("is annotated with @Test") {
            @Override
            public boolean apply(JavaMember input) {
                return input.isAnnotatedWith(Test.class);
            }
        };

    @ArchTest
    public static final ArchRule test_should_assert_not_log =
        noClasses().that().containAnyMembersThat(isAnnotatedWithTest)
            .should().dependOnClassesThat().resideInAPackage("org.slf4j..")
            .because("a test should assert, not log; after you made your test the logs will not be checked");


    private static final DescribedPredicate<JavaClass> testSupportClasses =
        new DescribedPredicate<>("test support classes") {
            @Override
            public boolean apply(JavaClass input) {
                return input != null &&
                    (!input.getSimpleName().endsWith("Test") &&
                        (!input.getSimpleName().endsWith("Tests")
                            && !input.getName().equals("java.lang.Object")));
            }
        };

    @ArchTest @ArchIgnore(reason = "There are tests that inherit from test support classes")
    public static final ArchRule no_test_classes_should_use_inheritance =
        noClasses().that().haveSimpleNameEndingWith("Test").or().haveSimpleNameEndingWith("Tests")
            .should().beAssignableTo(testSupportClasses)
            .because("it is harder to understand the logic of tests that inherit from other classes");

    @ArchTest
    public static void no_joda_time(JavaClasses classes) {
        GeneralCodingRules.NO_CLASSES_SHOULD_USE_JODATIME.because("Joda is outdated").check(classes);
    }

    @ArchTest
    public static void sanity_check_for_number_of_classes_to_analyze(JavaClasses classes) {
        assertTrue(classes.size() > 17, "There is something wrong - there should be at least! 20 test classes to analyze");
    }

    private static final ArchCondition<JavaMethod> callCheckMethod =
        new ArchCondition<>("call check()-method") {
            @Override
            public void check(JavaMethod method, ConditionEvents events) {
                assertNotNull(method); assertNotNull(events);
                var called = false;
                for (JavaCall<?> javaCall : method.getCallsFromSelf()) {
                    if (javaCall.getTarget().getName().equals("check") && javaCall.getTargetOwner().isAssignableTo(ArchRule.class)) {
                        called = true;
                    }
                }
                if (!called) {
                    events.add(SimpleConditionEvent.violated(method, "Method " + method.getFullName() + " does not call check()"));
                }
            }
        };


    @ArchTest
    public static final ArchRule everyArchTestMustCallCheck  =
        methods().that()
            .areAnnotatedWith(ArchTest.class)
            .and().haveNameNotStartingWith("sanity_check")
            .should(callCheckMethod);
}
