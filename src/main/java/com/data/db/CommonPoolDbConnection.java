package com.data.db;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class CommonPoolDbConnection {
    private static final CommonPoolDbConnection DB_CONNECTION = new CommonPoolDbConnection();
    private final BasicDataSource prepareConnection = new BasicDataSource();
    private final static Logger LOG = LogManager.getLogger(CommonPoolDbConnection.class.getName());

    private CommonPoolDbConnection() {
        try (InputStream inputStream = CommonPoolDbConnection.class.getClassLoader().getResourceAsStream("app.properties")) {
            Properties config = new Properties();
            config.load(inputStream);
            prepareConnection.setDriverClassName(config.getProperty("jdbc.driver-class-name"));
            prepareConnection.setUrl(config.getProperty("jdbc.url"));
            prepareConnection.setUsername(config.getProperty("jdbc.username"));
            prepareConnection.setPassword(config.getProperty("jdbc.password"));
            prepareConnection.setMinIdle(5);
            prepareConnection.setMaxIdle(10);
            prepareConnection.setMaxOpenPreparedStatements(100);
        } catch (IOException io) {
            LOG.error("Ошибка чтения пропертей для пула подключения к бд", io);
        }
    }

    public static CommonPoolDbConnection getInstance() {
        return DB_CONNECTION;
    }

    public Connection getConnection() throws SQLException {
        return prepareConnection.getConnection();
    }
}
