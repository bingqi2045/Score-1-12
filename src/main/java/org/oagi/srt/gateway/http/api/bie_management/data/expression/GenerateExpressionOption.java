package org.oagi.srt.gateway.http.api.bie_management.data.expression;

import lombok.Data;

import java.math.BigInteger;

@Data
public class GenerateExpressionOption {

    private boolean bieDefinition = true;
    private boolean bieGuid;
    private boolean bieCctsMetaData;
    private boolean businessContext;
    private boolean includeCctsDefinitionTag;
    private boolean includeWhoColumns;
    private boolean bieOagiSrtMetaData;
    private boolean basedCcMetaData;

    private String expressionOption;
    private String packageOption;

    private boolean arrayForJsonExpression;
    private boolean includeMetaHeaderForJson;
    private BigInteger metaHeaderTopLevelAbieId;
    private boolean includePaginationResponseForJson;
    private BigInteger paginationResponseTopLevelAbieId;

    private String openAPIExpressionFormat;
    private boolean openAPI30GetTemplate;
    private boolean arrayForJsonExpressionForOpenAPI30GetTemplate;
    private boolean includeMetaHeaderForJsonForOpenAPI30GetTemplate;
    private BigInteger metaHeaderTopLevelAbieIdForOpenAPI30GetTemplate;
    private boolean includePaginationResponseForJsonForOpenAPI30GetTemplate;
    private BigInteger paginationResponseTopLevelAbieIdForOpenAPI30GetTemplate;

    private boolean openAPI30PostTemplate;
    private boolean arrayForJsonExpressionForOpenAPI30PostTemplate;
    private boolean includeMetaHeaderForJsonForOpenAPI30PostTemplate;
    private BigInteger metaHeaderTopLevelAbieIdForOpenAPI30PostTemplate;
}
