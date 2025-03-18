package com.ciarancumiskey.mockitobank.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public class ParameterisedTypeList<T> implements ParameterizedType {

    private Class<?> wrappedClass;

    public ParameterisedTypeList(Class<T> wrappedClass){
        this.wrappedClass = wrappedClass;
    }

    @Override
    public Type[] getActualTypeArguments() {
        return new Type[]{wrappedClass};
    }

    @Override
    public Type getRawType() {
        return List.class;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }
}
