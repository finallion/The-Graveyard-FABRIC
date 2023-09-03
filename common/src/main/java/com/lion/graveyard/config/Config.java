package com.lion.graveyard.config;


/**
 * @author Draylar
 * <a href="https://github.com/Draylar/omega-config">https://github.com/Draylar/omega-config</a>
 */
@SuppressWarnings({"unchecked"})
public interface Config {

    default void save() {
        OmegaConfig.writeConfig((Class<Config>) getClass(), this);
    }

    String getName();

    default String getModid() {
        return null;
    }

    default String getExtension() {
        return "json";
    }

    default String getDirectory() {
        return "";
    }
}

