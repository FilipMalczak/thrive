package com.github.filipmalczak.thrive.core;

import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

/**
 * Utility for configuration classes. Allows for imperative autowiring, making autoconfig
 * classes easier to write and centralize dependencies resolution.
 */
@AllArgsConstructor
public class Autowirer {
    private AutowireCapableBeanFactory beanFactory;

    /**
     * Autowire assuming concrete class with no inferface.
     */
    public  <T> T autowired(Class<T> type){
        return autowired(type, type);
    }

    /**
     * @param interface_ unused, but documentational; besides allows for type safety in compile time
     */
    @SneakyThrows
    public  <T, I extends T> T autowired(Class<T> interface_, Class<I> implementation){
        T component = implementation.newInstance();
        beanFactory.autowireBean(component);
        return component;
    }

}
