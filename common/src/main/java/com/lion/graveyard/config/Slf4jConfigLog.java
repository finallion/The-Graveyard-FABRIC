package com.lion.graveyard.config;

import org.slf4j.Logger;

public final class Slf4jConfigLog implements ConfigLog {
    private final Logger logger;

    public Slf4jConfigLog(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void info(String message) {
        logger.info(message);
    }

    @Override
    public void warn(String message) {
        logger.warn(message);
    }

    @Override
    public void error(String message) {
        logger.error(message);
    }
}
