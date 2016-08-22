package com.ybg.rp.vmbase.utils;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

/**
 * Created by yangbagang on 16/8/9.
 */
public class GsonUtil {

    public static Gson createGson() {
        return new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd").create();
    }

    public static Gson createGson(String dateFormate) {
        return new GsonBuilder().serializeNulls().setDateFormat(dateFormate).create();
    }

    public static Gson createWithoutNulls() {
        return new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd").excludeFieldsWithoutExposeAnnotation().setPrettyPrinting().create();
    }

    public static Gson createWithoutNullsDisableHtmlEscaping() {
        return new GsonBuilder().serializeNulls().setDateFormat("yyyy-MM-dd").excludeFieldsWithoutExposeAnnotation().setPrettyPrinting()
                .disableHtmlEscaping().create();
    }

    public static String toJsonProperties(Object o, String... properties) {
        GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls().setExclusionStrategies(new PropertiesInclude(properties));
        gsonBuilder.setDateFormat("yyyy-MM-dd");
        return gsonBuilder.create().toJson(o);
    }

    public static String toJsonPropertiesDes(Object o, String... properties) {
        GsonBuilder gsonBuilder = new GsonBuilder().serializeNulls().setExclusionStrategies(new PropertiesInclude(properties));
        gsonBuilder.setDateFormat("yyyy-MM-dd HH:mm:ss");
        return gsonBuilder.create().toJson(o);
    }

    static class PropertiesInclude implements ExclusionStrategy {
        HashSet<String> includeProSet = null;

        public PropertiesInclude(String[] pros) {
            includeProSet = new HashSet<String>(pros.length);
            for (String s : pros) {
                includeProSet.add(s);
            }
        }

        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            String name = f.getName();
            return !includeProSet.contains(name);
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    }

    public static <T> T getPerson(String jsonString, Class<T> cls) {
        T t = null;
        try {
            t = createGson().fromJson(jsonString, cls);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    public static <T> List<T> getPersons(String jsonString, Class<T> cls) {
        List<T> list = new ArrayList<T>();
        try {
            list = createGson().fromJson(jsonString, new TypeToken<List<T>>() {
            }.getType());

        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public static List<String> getList(String jsonString) {
        List<String> list = new ArrayList<String>();
        try {
            list = createGson().fromJson(jsonString, new TypeToken<List<String>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;

    }

    public static List<Map<String, Object>> listKeyMap(String jsonString) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            list = createGson().fromJson(jsonString, new TypeToken<List<Map<String, Object>>>() {
            }.getType());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

}
