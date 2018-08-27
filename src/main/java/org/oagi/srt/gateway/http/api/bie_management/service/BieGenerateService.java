package org.oagi.srt.gateway.http.api.bie_management.service;

import lombok.Data;
import org.oagi.srt.gateway.http.api.bie_management.data.expression.GenerateExpressionOption;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class BieGenerateService {

    @Data
    public static class GenerateBieExpression {
        private String filename;
        private String contentType;
        private File file;
    }

    public GenerateBieExpression generate(
            User user, List<Long> topLevelAbieIds, GenerateExpressionOption option) {

        GenerateBieExpression generateBieExpression = new GenerateBieExpression();
        generateBieExpression.setFilename(UUID.randomUUID().toString() + ".pdf");
        generateBieExpression.setContentType("application/pdf");
        generateBieExpression.setFile(
                new File("/Users/hno2/Downloads/img-180823162348-0001.pdf")
        );

        return generateBieExpression;
    }
}
