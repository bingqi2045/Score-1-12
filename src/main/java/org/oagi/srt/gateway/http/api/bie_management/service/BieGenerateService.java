package org.oagi.srt.gateway.http.api.bie_management.service;

import org.oagi.srt.data.TopLevelAbie;
import org.oagi.srt.gateway.http.api.bie_management.data.expression.BieGenerateExpressionResult;
import org.oagi.srt.gateway.http.api.bie_management.data.expression.GenerateExpressionOption;
import org.oagi.srt.gateway.http.api.bie_management.service.generate_expression.BieGenerateExpression;
import org.oagi.srt.gateway.http.api.bie_management.service.generate_expression.BieGenerateFailureException;
import org.oagi.srt.gateway.http.api.bie_management.service.generate_expression.BieJSONGenerateExpression;
import org.oagi.srt.gateway.http.api.bie_management.service.generate_expression.BieXMLGenerateExpression;
import org.oagi.srt.gateway.http.helper.SrtGuid;
import org.oagi.srt.gateway.http.helper.Zip;
import org.oagi.srt.repository.TopLevelAbieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional(readOnly = true)
public class BieGenerateService {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private TopLevelAbieRepository topLevelAbieRepository;

    public BieGenerateExpressionResult generate(
            User user, List<Long> topLevelAbieIds, GenerateExpressionOption option) throws BieGenerateFailureException {

        List<TopLevelAbie> topLevelAbies = topLevelAbieRepository.findByIdIn(topLevelAbieIds);
        File file = generateSchema(topLevelAbies, option);
        return toResult(file);
    }

    public BieGenerateExpressionResult toResult(File file) {
        BieGenerateExpressionResult result = new BieGenerateExpressionResult();
        result.setFile(file);

        String filename = file.getName();
        result.setFilename(filename);

        String contentType;
        if (filename.endsWith(".xsd")) {
            contentType = "text/xml";
        } else if (filename.endsWith(".json")) {
            contentType = "application/json";
        } else if (filename.endsWith(".zip")) {
            contentType = "application/zip";
        } else {
            contentType = "application/octet-stream";
        }

        result.setContentType(contentType);

        return result;
    }

    public File generateSchema(List<TopLevelAbie> topLevelAbies, GenerateExpressionOption option) throws BieGenerateFailureException {
        if (topLevelAbies == null || topLevelAbies.isEmpty()) {
            throw new IllegalArgumentException();
        }
        if (option == null) {
            throw new IllegalArgumentException();
        }

        String packageOption = option.getPackageOption();
        if (packageOption != null) {
            packageOption = packageOption.trim();
        }
        
        switch (packageOption.toUpperCase()) {
            case "ALL":
                return generateSchemaForAll(topLevelAbies, option);

            case "EACH":
                Map<Long, File> files = generateSchemaForEach(topLevelAbies, option);
                try {
                    return Zip.compression(files.values(), SrtGuid.randomGuid());
                } catch (IOException e) {
                    throw new BieGenerateFailureException("Compression failure.", e);
                }

            default:
                throw new IllegalStateException();
        }
    }

    public File generateSchemaForAll(List<TopLevelAbie> topLevelAbies,
                                     GenerateExpressionOption option) throws BieGenerateFailureException {
        BieGenerateExpression generateExpression = createBieGenerateExpression(option);

        for (TopLevelAbie topLevelAbie : topLevelAbies) {
            generateExpression.generate(topLevelAbie, option);
        }

        File schemaExpressionFile = null;
        try {
            schemaExpressionFile = generateExpression.asFile(SrtGuid.randomGuid() + "_standalone");
        } catch (IOException e) {
            throw new BieGenerateFailureException("I/O operation failure.", e);
        }
        return schemaExpressionFile;
    }

    public Map<Long, File> generateSchemaForEach(List<TopLevelAbie> topLevelAbies,
                                                 GenerateExpressionOption option) throws BieGenerateFailureException {
        Map<Long, File> targetFiles = new HashMap();
        for (TopLevelAbie topLevelAbie : topLevelAbies) {
            BieGenerateExpression generateExpression = createBieGenerateExpression(option);

            generateExpression.generate(topLevelAbie, option);

            File schemaExpressionFile = null;
            try {
                schemaExpressionFile = generateExpression.asFile(SrtGuid.randomGuid());
            } catch (IOException e) {
                throw new BieGenerateFailureException("I/O operation failure.", e);
            }
            targetFiles.put(topLevelAbie.getTopLevelAbieId(), schemaExpressionFile);
        }

        return targetFiles;
    }

    private BieGenerateExpression createBieGenerateExpression(GenerateExpressionOption option) {
        String expressionOption = option.getExpressionOption();
        if (expressionOption != null) {
            expressionOption = expressionOption.trim();
        }

        BieGenerateExpression generateExpression = null;
        switch (expressionOption.toUpperCase()) {
            case "XML":
                generateExpression = applicationContext.getBean(BieXMLGenerateExpression.class);
                break;
            case "JSON":
                generateExpression = applicationContext.getBean(BieJSONGenerateExpression.class);
                break;
            default:
                throw new IllegalArgumentException("Unknown expression option: " + expressionOption);
        }

        return generateExpression;
    }
}
