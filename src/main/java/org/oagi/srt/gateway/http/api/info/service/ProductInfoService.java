package org.oagi.srt.gateway.http.api.info.service;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.oagi.srt.gateway.http.api.info.data.ProductInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Optional;
import java.util.Properties;

@Service
public class ProductInfoService {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private static final String groupId = "org.oagi";

    private static final String artifactId = "srt-http-gateway";

    private static final String unknownVersion = "0.0.0.0";

    @Autowired
    private DataSource dataSource;

    @Autowired
    private RedisConnectionFactory redisConnectionFactory;

    public ProductInfo gatewayMetadata() {
        ProductInfo metadata = new ProductInfo();
        metadata.setProductName(artifactId);

        String resourcePath = "/META-INF/maven/" + groupId + "/" + artifactId + "/pom.xml";
        try {
            try (InputStream stream = Optional.ofNullable(getClass().getResourceAsStream(resourcePath))
                    .orElse(new FileInputStream(new File("pom.xml")))) {

                SAXBuilder sax = new SAXBuilder();
                Document doc = sax.build(stream);
                Element root = doc.getRootElement();
                Element versionElement = root.getChild("version", root.getNamespace());
                if (versionElement != null) {
                    metadata.setProductVersion(versionElement.getTextTrim());
                } else {
                    logger.warn("Could not read " + artifactId + " version.");
                    metadata.setProductVersion(unknownVersion);
                }
            }
        } catch (IOException | JDOMException e) {
            logger.error("Could not retrieve " + artifactId + " version.", e);
            metadata.setProductVersion(unknownVersion);
        }

        return metadata;
    }

    public ProductInfo databaseMetadata() {
        ProductInfo metadata = new ProductInfo();
        metadata.setProductName("MySQL");

        try (Connection conn = dataSource.getConnection()) {
            DatabaseMetaData databaseMetaData = conn.getMetaData();
            metadata.setProductName(databaseMetaData.getDatabaseProductName());
            metadata.setProductVersion(databaseMetaData.getDatabaseProductVersion());
        } catch (SQLException e) {
            logger.error("Could not retrieve database version.", e);
            metadata.setProductVersion(unknownVersion);
        }
        return metadata;
    }

    public ProductInfo redisMetadata() {
        ProductInfo metadata = new ProductInfo();
        metadata.setProductName("Redis");

        RedisConnection redisConnection = redisConnectionFactory.getConnection();
        try {
            Properties redisInfo = redisConnection.info();
            metadata.setProductVersion(redisInfo.getProperty("redis_version"));
            return metadata;
        } finally {
            redisConnection.close();
        }
    }
}
