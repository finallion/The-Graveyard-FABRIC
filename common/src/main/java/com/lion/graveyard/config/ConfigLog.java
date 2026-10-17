package com.lion.graveyard.config;

public interface ConfigLog {
    void info(String message);

    void warn(String message);

    void error(String message);
}
