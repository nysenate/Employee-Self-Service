package gov.nysenate.ess.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import gov.nysenate.ess.core.config.CoreConfig;
import gov.nysenate.ess.web.util.AsciiArt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.ResourceHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.support.AllEncompassingFormHttpMessageConverter;
import org.springframework.http.converter.xml.MappingJackson2XmlHttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import javax.annotation.PostConstruct;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Main configuration class that imports all the other config classes.
 */
@Configuration
@EnableWebMvc
@ComponentScan("gov.nysenate.ess.web")
@Profile({"test", "dev", "prod"})
@Import({CoreConfig.class, SecurityConfig.class})
public class WebApplicationConfig implements WebMvcConfigurer
{
    private static final Logger logger = LoggerFactory.getLogger(WebApplicationConfig.class);

    private ObjectMapper jsonObjectMapper;
    private ObjectMapper xmlObjectMapper;

    @Autowired
    public WebApplicationConfig(ObjectMapper jsonObjectMapper, ObjectMapper xmlObjectMapper) {
        this.jsonObjectMapper = jsonObjectMapper;
        this.xmlObjectMapper = xmlObjectMapper;
    }

    @PostConstruct
    public void init() {
        logger.info("{}", AsciiArt.TS_LOGO.getText().replace("DATE", LocalDateTime.now().toString()));
    }

    @Value("${resource.path}") private String resourcePath;
    @Value("${resource.location}") private String resourceLocation;

    @Value("${data.dir}") private String dataDir;
    @Value("${data.ackdoc_subdir}") private String ackDocSubdir;
    @Value("${data.pecvid_subdir}") private String pecVidSubdir;

    /** Sets paths that should not be intercepted by a controller (e.g css/ js/). */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        if (resourcePath == null || resourceLocation == null) {
            logger.warn("Resource path/location for accessing public assets were not set!");
        } else {
            logger.info("Registering resource path {} for files under {}", resourcePath, resourceLocation);
            registry.addResourceHandler(resourcePath + "**", "/favicon.ico")
                    .addResourceLocations(resourceLocation);
        }

        // Serve PEC resources from external directories
        if (dataDir == null || resourceLocation == null) {
            logger.warn("Resource path/locations for accessing PEC resources were not set!");
        } else {
            if (ackDocSubdir == null) {
                logger.warn("Ack doc subdirectory not set in props.  Ack docs will not be served!");
            } else {
                String ackDocDir = Paths.get(dataDir, ackDocSubdir).toString() + "/";
                String ackDocUri = Paths.get(resourcePath, ackDocSubdir).toString() + "/";
                logger.info("Registering resource path {} for files under {}", ackDocUri, ackDocDir);
                registry.addResourceHandler(ackDocUri + "**")
                        .addResourceLocations("file:" + ackDocDir);
            }
            if (pecVidSubdir == null) {
                logger.warn("PEC video subdirectory not set in props.  PEC videos will not be served!");
            } else {
                String pecVidDir = Paths.get(dataDir, pecVidSubdir).toString() + "/";
                String pecVidUri = Paths.get(resourcePath, pecVidSubdir).toString() + "/";
                logger.info("Registering resource path {} for files under {}", pecVidUri, pecVidDir);
                registry.addResourceHandler(pecVidUri + "**")
                        .addResourceLocations("file:" + pecVidDir);
            }
        }
    }

    /**
     * This view resolver will map view names returned from the controllers to jsp files stored in the
     * configured 'prefix' path.
     */
    @Bean(name = "viewResolver")
    public InternalResourceViewResolver viewResolver() {
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/WEB-INF/view/");
        viewResolver.setSuffix(".jsp");
        return viewResolver;
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        StringHttpMessageConverter stringConverter = new StringHttpMessageConverter();
        stringConverter.setWriteAcceptCharset(false);
        converters.add(new ByteArrayHttpMessageConverter());
        converters.add(stringConverter);
        converters.add(new ResourceHttpMessageConverter());
        converters.add(new SourceHttpMessageConverter<>());
        converters.add(new AllEncompassingFormHttpMessageConverter());
        converters.add(jackson2XmlConverter());
        converters.add(jackson2Converter());
    }

    @Bean
    public MappingJackson2HttpMessageConverter jackson2Converter() {
        return new MappingJackson2HttpMessageConverter(jsonObjectMapper);
    }

    @Bean
    public MappingJackson2XmlHttpMessageConverter jackson2XmlConverter() {
        return new MappingJackson2XmlHttpMessageConverter(xmlObjectMapper);
    }

    /**
     * Configuration for handling file uploads
     */
    @Bean
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
        multipartResolver.setMaxUploadSize(10485760); // 10MB
        multipartResolver.setMaxUploadSizePerFile(5242880); // 5MB
        return multipartResolver;
    }
}
