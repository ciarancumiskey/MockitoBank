package com.ciarancumiskey.mockitobank;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestcontainersConfiguration.class)
@SpringBootTest
class MockitoBankApplicationTests {

    @Test
    void contextLoads() {
        /**fixme
         * Failed to load ApplicationContext for [WebMergedContextConfiguration@1ab14636 testClass =
         * com.ciarancumiskey.mockitobank.MockitoBankApplicationTests, locations = [],
         * classes = [com.ciarancumiskey.mockitobank.MockitoBankApplication], contextInitializerClasses = [],
         * activeProfiles = [], propertySourceDescriptors = [],
         * propertySourceProperties = ["org.springframework.boot.test.context.SpringBootTestContextBootstrapper=true"],
         * contextCustomizers = [[ImportsContextCustomizer@16b3c905 key =
         * [com.ciarancumiskey.mockitobank.TestcontainersConfiguration]],
         * org.springframework.boot.test.context.filter.ExcludeFilterContextCustomizer@421e361,
         * org.springframework.boot.test.json.DuplicateJsonObjectContextCustomizerFactory$DuplicateJsonObjectContextCustomizer@4b34fff9,
         * org.springframework.boot.test.mock.mockito.MockitoContextCustomizer@0,
         * org.springframework.boot.test.web.client.TestRestTemplateContextCustomizer@12a94400,
         * org.springframework.boot.test.web.reactor.netty.DisableReactorResourceFactoryGlobalResourcesContextCustomizerFactory$DisableReactorResourceFactoryGlobalResourcesContextCustomizerCustomizer@934b6cb,
         * org.springframework.boot.test.autoconfigure.OnFailureConditionReportContextCustomizerFactory$OnFailureConditionReportContextCustomizer@27adc16e,
         * org.springframework.boot.test.autoconfigure.actuate.observability.ObservabilityContextCustomizerFactory$DisableObservabilityContextCustomizer@1f,
         * org.springframework.boot.test.autoconfigure.properties.PropertyMappingContextCustomizer@0,
         * org.springframework.boot.test.autoconfigure.web.servlet.WebDriverContextCustomizer@68f4865,
         * org.springframework.test.context.support.DynamicPropertiesContextCustomizer@0,
         * org.springframework.boot.testcontainers.service.connection.ServiceConnectionContextCustomizer@0,
         * org.springframework.boot.test.context.SpringBootTestAnnotation@2009641e],
         * resourceBasePath = "src/main/webapp",
         * contextLoader = org.springframework.boot.test.context.SpringBootContextLoader, parent = null]
         */
    }

}
