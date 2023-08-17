package ch.dvbern.stip.test.arch;

import ch.dvbern.stip.api.common.service.MappingConfig;
import ch.dvbern.stip.api.common.service.MappingQualifierConfig;
import ch.dvbern.stip.test.arch.util.ArchTestUtil;
import com.tngtech.archunit.core.domain.JavaClass;
import com.tngtech.archunit.lang.ArchCondition;
import com.tngtech.archunit.lang.ConditionEvents;
import com.tngtech.archunit.lang.SimpleConditionEvent;
import org.junit.jupiter.api.Test;
import org.mapstruct.Mapper;

import java.util.Objects;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;

class MapperTest {

    @Test
    void ensure_mapping_config_is_used() {
        var rule = classes()
                .that()
                .areAnnotatedWith(Mapper.class)
                .should(new ArchCondition<>("use a MappingConfig") {

                    private static final Class<?> MAPPING_CONFIG = MappingConfig.class;
                    private static final Class<?> MAPPING_QUALIFIER_CONFIG = MappingQualifierConfig.class;

                    @Override
                    public void check(JavaClass javaClass, ConditionEvents conditionEvents) {
                        var annotation = javaClass.getAnnotationOfType(Mapper.class);
                        if (Objects.equals(annotation.config(), MAPPING_CONFIG) || Objects.equals(annotation.config(), MAPPING_QUALIFIER_CONFIG)) {
                            return;
                        }

                        String message = String.format("Class %s @Mapper must specify mapping config, expected annotation @Mapper(config = %s)",
                                javaClass.getFullName(), MAPPING_CONFIG.getName());

                        conditionEvents.add(SimpleConditionEvent.violated(javaClass, message));

                    }
                });

        rule.check(ArchTestUtil.APP_CLASSES);
    }

}
