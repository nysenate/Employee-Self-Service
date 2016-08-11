package gov.nysenate.ess.core.config;


import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;
import freemarker.template.Version;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

/**
 * Configures an instance of the Freemarker template engine
 */
@org.springframework.context.annotation.Configuration
public class FreemarkerConfig {

    /** Freemarker will be configured to operate under the standards of this version */
    private static final Version cfgVersion = Configuration.VERSION_2_3_23;

    /** The path to the freemarker template directory */
    @Value("${freemarker.template_directory:freemarker_templates}")
    private String templateDirectoryPath;

    /**
     * Configures a {@link Configuration Freemarker configuration object} that can be used for templating
     * @return {@link Configuration} - a Freemarker configuration object
     * @throws IOException If the template directory is poorly configured
     */
    @Bean(name = "freemarkerCfg")
    public Configuration freemarkerCfg() throws IOException {
        Configuration cfg = new Configuration(cfgVersion);

        // Set template directory
        ClassPathResource templateResource = new ClassPathResource(templateDirectoryPath);
        cfg.setDirectoryForTemplateLoading(templateResource.getFile());

        // Set template encoding
        cfg.setDefaultEncoding("UTF-8");
        // Rethrow templating exceptions
        cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
        // Don't log templating exceptions since they are rethrown
        cfg.setLogTemplateExceptions(false);
        return cfg;
    }
}
