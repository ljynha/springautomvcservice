package com.ljy;

import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class LjyDispatcherServletInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    @Override
    protected Class<?>[] getRootConfigClasses() {
        return  getClasses("mvcclass");
    }

    @Override
    protected Class<?>[] getServletConfigClasses() {
        return getClasses("rootclass");
    }

    private Class<?>[] getClasses(String clazzname) {
        ResourceLoader loader=new DefaultResourceLoader();
        Resource resource = loader.getResource("classpath:AutowiredMvc.properties");
        Properties pro = new Properties();
        List<Class> classList = null;
        try {
            pro.load(resource.getInputStream());
            String classname=pro.getProperty(clazzname);
            String[] classnames=classname.split(";");
            classList=new ArrayList<Class>();
            for (String name:classnames) {
                classList.add(Class.forName(name));
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return classList.toArray(new Class[classList.size()]);
    }

    @Override
    protected String[] getServletMappings() {
        return new String[0];
    }
}
