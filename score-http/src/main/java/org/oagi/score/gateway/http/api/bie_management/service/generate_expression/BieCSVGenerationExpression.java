package org.oagi.score.gateway.http.api.bie_management.service.generate_expression;

import org.oagi.score.data.*;
import org.oagi.score.gateway.http.api.bie_management.data.expression.GenerateExpressionOption;
import org.oagi.score.gateway.http.helper.ScoreGuid;
import org.oagi.score.repo.api.impl.utils.StringUtils;
import org.oagi.score.repository.TopLevelAsbiepRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

import static org.oagi.score.gateway.http.api.bie_management.service.generate_expression.Helper.camelCase;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_PROTOTYPE;

@Component
@Scope(SCOPE_PROTOTYPE)
public class BieCSVGenerationExpression implements BieGenerateExpression, InitializingBean {

    private static final String INDEXER_STR = "[0]";

    private class RowRecord {
        String columnName;
        String fullPath;
        String maxCardinality;
        String contextDefinition;
        String example;
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private GenerateExpressionOption option;

    private List<RowRecord> rowRecords;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private TopLevelAsbiepRepository topLevelAsbiepRepository;

    private GenerationContext generationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        rowRecords = new ArrayList();
    }

    @Override
    public void reset() throws Exception {
        rowRecords = null;
    }

    @Override
    public GenerationContext generateContext(List<TopLevelAsbiep> topLevelAsbieps, GenerateExpressionOption option) {
        return applicationContext.getBean(GenerationContext.class, topLevelAsbieps);
    }

    @Override
    public void generate(TopLevelAsbiep topLevelAsbiep, GenerationContext generationContext, GenerateExpressionOption option) {
        this.generationContext = generationContext;
        this.option = option;

        generateTopLevelAsbiep(topLevelAsbiep);
    }

    private void generateTopLevelAsbiep(TopLevelAsbiep topLevelAsbiep) {
        ASBIEP asbiep = generationContext.findASBIEP(topLevelAsbiep.getAsbiepId());
        ABIE typeAbie = generationContext.queryTargetABIE(asbiep);

        ASCCP basedAsccp = generationContext.findASCCP(asbiep.getBasedAsccpManifestId());
        String name = camelCase(basedAsccp.getPropertyTerm());

        Stack<String> paths = new Stack();
        paths.push(name);

        RowRecord rowRecord = new RowRecord();
        rowRecord.fullPath = String.join(".", paths);
        rowRecord.maxCardinality = "1";
        rowRecord.contextDefinition = asbiep.getDefinition();
        rowRecords.add(rowRecord);

        traverse(typeAbie, paths);
    }

    private void traverse(ABIE abie, Stack<String> paths) {
        List<BIE> children = generationContext.queryChildBIEs(abie);
        ACC acc = generationContext.findACC(abie.getBasedAccManifestId());
        for (BIE bie : children) {
            traverse(bie, paths);
        }
    }

    private void traverse(BIE bie, Stack<String> paths) {
        Stack<String> copiedPaths = new Stack();
        copiedPaths.addAll(paths);

        if (bie instanceof BBIE) {
            BBIE bbie = (BBIE) bie;

            BCC bcc = generationContext.queryBasedBCC(bbie);
            BCCP bccp = generationContext.queryToBCCP(bcc);
            DT bdt = generationContext.queryBDT(bccp);

            List<BBIESC> bbieScList = generationContext.queryBBIESCs(bbie)
                    .stream().filter(e -> e.getCardinalityMax() != 0).collect(Collectors.toList());

            String name = camelCase(bccp.getPropertyTerm());
            String prefix = name;
            if (!bbieScList.isEmpty()) {
                if (bbie.getCardinalityMax() == -1 || bbie.getCardinalityMax() > 1) {
                    prefix = prefix + INDEXER_STR;
                }
                name = prefix + ".content";
            }

            copiedPaths.push(name);

            RowRecord rowRecord = new RowRecord();
            rowRecord.fullPath = String.join(".", copiedPaths);
            rowRecord.maxCardinality = (bbie.getCardinalityMax() == -1) ? "unbounded" : Integer.toString(bbie.getCardinalityMax());
            if (StringUtils.hasLength(bbie.getDefinition())) {
                rowRecord.contextDefinition = bbie.getDefinition();
            }
            if (StringUtils.hasLength(bbie.getExample())) {
                rowRecord.example = bbie.getExample();
            }
            rowRecords.add(rowRecord);

            for (BBIESC bbieSc : bbieScList) {
                DTSC dtSc = generationContext.findDtSc(bbieSc.getBasedDtScManifestId());
                String dtScName = toName(dtSc.getPropertyTerm(), dtSc.getRepresentationTerm(), rt -> {
                    if ("Text".equals(rt)) {
                        return "";
                    }
                    return rt;
                }, true);

                Stack<String> dtScPaths = new Stack();
                dtScPaths.addAll(copiedPaths);
                dtScPaths.pop();
                dtScPaths.push(prefix);
                dtScPaths.push(dtScName);

                RowRecord dtScRowRecord = new RowRecord();
                dtScRowRecord.fullPath = String.join(".", dtScPaths);
                dtScRowRecord.maxCardinality = (bbieSc.getCardinalityMax() == -1) ? "unbounded" : Integer.toString(bbieSc.getCardinalityMax());
                if (StringUtils.hasLength(bbieSc.getDefinition())) {
                    dtScRowRecord.contextDefinition = bbieSc.getDefinition();
                }
                if (StringUtils.hasLength(bbieSc.getExample())) {
                    dtScRowRecord.example = bbieSc.getExample();
                }
                rowRecords.add(dtScRowRecord);
            }
        } else {
            ASBIE asbie = (ASBIE) bie;

            ASBIEP asbiep = generationContext.queryAssocToASBIEP(asbie);
            ABIE typeAbie = generationContext.queryTargetABIE2(asbiep);

            ASCCP asccp = generationContext.queryBasedASCCP(asbiep);
            String name = camelCase(asccp.getPropertyTerm());

            copiedPaths.push(name);

            RowRecord rowRecord = new RowRecord();
            rowRecord.fullPath = String.join(".", copiedPaths);
            rowRecord.maxCardinality = (asbie.getCardinalityMax() == -1) ? "unbounded" : Integer.toString(asbie.getCardinalityMax());
            if (StringUtils.hasLength(asbie.getDefinition())) {
                rowRecord.contextDefinition = asbie.getDefinition();
            }
            rowRecords.add(rowRecord);

            if (asbie.getCardinalityMax() == -1 || asbie.getCardinalityMax() > 1) {
                copiedPaths.push(copiedPaths.pop() + INDEXER_STR);
            }

            traverse(typeAbie, copiedPaths);
        }
    }

    @Override
    public File asFile(String filename) throws IOException {
        generateColumnNames(rowRecords, 1);

        File tempFile = File.createTempFile(ScoreGuid.randomGuid(), null);
        String extension = "csv";

        tempFile = new File(tempFile.getParentFile(), filename + "." + extension);

        try (PrintWriter writer = new PrintWriter(new BufferedOutputStream(new FileOutputStream(tempFile)))) {
            writer.println("\"ColumnName\",\"FullPath\",\"MaxCardinality\",\"ContextDefinition\",\"ExampleData\"");
            for (RowRecord rowRecord : rowRecords) {
                writer.println(rowRecord.columnName + "," +
                        rowRecord.fullPath + "," +
                        rowRecord.maxCardinality + "," +
                        (StringUtils.hasLength(rowRecord.contextDefinition) ? rowRecord.contextDefinition : "") + "," +
                        (StringUtils.hasLength(rowRecord.example) ? rowRecord.example : ""));
            }
            writer.flush();
        }
        logger.info("Comma-separated value schema is generated: " + tempFile);

        return tempFile;
    }

    private void generateColumnNames(List<RowRecord> rowRecords, int depth) {
        Map<String, List<RowRecord>> columnNameMap = new HashMap();
        for (RowRecord rowRecord : rowRecords) {
            String fullPath = rowRecord.fullPath.replaceAll("\\[0\\]", "");
            List<String> paths = Arrays.asList(fullPath.split("\\."));
            if (paths.size() > depth) {
                paths = paths.subList(paths.size() - depth, paths.size());
            }

            String columnName = String.join(".", paths);
            if (!columnNameMap.containsKey(columnName)) {
                columnNameMap.put(columnName, new ArrayList());
            }
            columnNameMap.get(columnName).add(rowRecord);
        }
        for (Map.Entry<String, List<RowRecord>> entry : columnNameMap.entrySet()) {
            if (entry.getValue().size() > 1) {
                generateColumnNames(entry.getValue(), depth + 1);
            } else {
                entry.getValue().get(0).columnName = entry.getKey();
            }
        }
    }
}
