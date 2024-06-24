package config;

import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class ServerInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return new Class[]{
                WebConfiguration.class, DatabaseConfiguration.class
        };
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return null;
    }
}