package tennis.score.board.config;


import jakarta.servlet.*;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

public class WebMvcDispatcherServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?> [] getRootConfigClasses() {
        return new Class[0]; // Здесь можно добавить загрузку PersistenceConfiguration.class
    }

    @Override
    protected Class<?> [] getServletConfigClasses() {
        return new Class[] {WebMvcConfiguration.class};
    }

    @Override
    protected String[] getServletMappings() {
        return new String[] {"/"};
    }

    @Override
    protected Filter[] getServletFilters() {
        return new Filter[] {
                getCharacterEncodingFilter(),
                getHiddenHttpMethodFilter()
        };
    }

    private static Filter getHiddenHttpMethodFilter() {
        return new HiddenHttpMethodFilter();
    }

    private static Filter getCharacterEncodingFilter() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8"); // Можно использовать StandardCharsets.UTF_8.name()
        characterEncodingFilter.setForceEncoding(true);

        return characterEncodingFilter;
    }
}
