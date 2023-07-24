package ch.dvbern.stip.test.arch;

import ch.dvbern.stip.test.arch.util.ArchTestUtil;
import jakarta.enterprise.context.RequestScoped;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

class CdiTest {

    @Test
    public void resources_are_request_scoped() {
        var rule = classes()
                .that()
                .resideInAPackage("..resource..")
                .and()
                .haveSimpleNameContaining("Resource")
                .should()
                .beAnnotatedWith(RequestScoped.class);

        rule.check(ArchTestUtil.APP_CLASSES);
    }
}
