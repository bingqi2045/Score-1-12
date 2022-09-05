package org.oagi.score.export.model;

import org.apache.commons.io.FilenameUtils;
import org.oagi.score.export.impl.XMLExportSchemaModuleVisitor;
import org.oagi.score.repo.api.impl.utils.StringUtils;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SchemaModule {

    private final ScoreModule module;

    private List<SchemaModule> includeModules = new ArrayList();
    private List<SchemaModule> importModules = new ArrayList();

    private Map<String, AgencyId> agencyIdList = new LinkedHashMap();
    private Map<String, SchemaCodeList> schemaCodeLists = new LinkedHashMap();
    private Map<String, XBTSimpleType> xbtSimples = new LinkedHashMap();
    private Map<String, BDTSimple> bdtSimples = new LinkedHashMap();

    private Map<String, BCCP> bccpList = new LinkedHashMap();
    private Map<String, ACC> accList = new LinkedHashMap();
    private Map<String, ASCCP> asccpList = new LinkedHashMap();

    private byte[] content;

    private File moduleFile;

    public SchemaModule(ScoreModule module) {
        this.module = module;
    }

    public String getPath() {
        return FilenameUtils.separatorsToSystem(module.getModulePath());
    }

    public String getVersionNum() {
        return module.getVersionNum();
    }

    public String getModuleSetReleaseId() {
        return module.getModuleSetReleaseId();
    }

    public String getNamespaceId() {
        if (module.getModuleNamespaceId() != null) {
            return module.getModuleNamespaceId();
        }
        return module.getReleaseNamespaceId();
    }

    public String getNamespaceUri() {
        if (StringUtils.hasLength(module.getModuleNamespaceUri())) {
            return module.getModuleNamespaceUri();
        }
        return module.getReleaseNamespaceUri();
    }

    public String getNamespacePrefix() {
        if (StringUtils.hasLength(module.getModuleNamespacePrefix())) {
            return module.getModuleNamespacePrefix();
        }
        return module.getReleaseNamespacePrefix();
    }

    public boolean hasInclude(SchemaModule schemaModule) {
        List<SchemaModule> references = new ArrayList();
        references.add(schemaModule);
        return hasInclude(schemaModule, references);
    }

    public boolean hasInclude(SchemaModule schemaModule, List<SchemaModule> references) {
        List<SchemaModule> nextReferences = null;
        try {
            if (this.equals(schemaModule)) {
                return true;
            }
            if (this.includeModules.indexOf(schemaModule) > -1) {
                return true;
            }

            for (SchemaModule include : this.includeModules) {
                if (references.contains(include)) {
                    references.add(include);

                    throw new IllegalArgumentException("Circular reference found: " +
                            references.stream().map(m -> m.module.getModulePath()).collect(Collectors.joining(" -> ")));
                }

                nextReferences = new ArrayList(references);
                nextReferences.add(include);

                if (include.hasInclude(schemaModule, nextReferences)) {
                    return true;
                }
            }
            return false;

        } catch (StackOverflowError e) {
            throw new IllegalArgumentException("Circular reference found: " +
                    nextReferences.stream().map(m -> m.module.getModulePath()).collect(Collectors.joining(" -> "))
                    , e);
        }
    }

    private boolean hasImport(SchemaModule schemaModule) {
        if (this.equals(schemaModule)) {
            return true;
        }
        if (this.importModules.indexOf(schemaModule) > -1) {
            return true;
        }
        for (SchemaModule imported : this.importModules) {
            if (imported.hasImport(schemaModule)) {
                return true;
            }
        }
        return false;
    }

    public void addInclude(SchemaModule schemaModule) {
        if (!hasInclude(schemaModule)) {
            includeModules.add(schemaModule);
        }
    }

    public void addImport(SchemaModule schemaModule) {
        if (!hasImport(schemaModule)) {
            importModules.add(schemaModule);
        }
    }

    public Collection<SchemaModule> getDependedModules() {
        return Stream.concat(includeModules.stream(), importModules.stream())
                .collect(Collectors.toList());
    }

    public boolean addAgencyId(AgencyId agencyId) {
        if (this.agencyIdList.containsKey(agencyId.getGuid())) {
            return false;
        }
        this.agencyIdList.put(agencyId.getGuid(), agencyId);
        return true;
    }

    public boolean addCodeList(SchemaCodeList schemaCodeList) {
        if (this.schemaCodeLists.containsKey(schemaCodeList.getGuid())) {
            return false;
        }
        this.schemaCodeLists.put(schemaCodeList.getGuid(), schemaCodeList);
        return true;
    }

    public boolean addXBTSimpleType(XBTSimpleType xbtSimple) {
        if (this.xbtSimples.containsKey(xbtSimple.getGuid())) {
            return false;
        }
        this.xbtSimples.put(xbtSimple.getGuid(), xbtSimple);
        return true;
    }

    public boolean addBDTSimple(BDTSimple bdtSimple) {
        if (this.bdtSimples.containsKey(bdtSimple.getGuid())) {
            return false;
        }
        this.bdtSimples.put(bdtSimple.getGuid(), bdtSimple);
        return true;
    }

    public boolean addBCCP(BCCP bccp) {
        if (this.bccpList.containsKey(bccp.getGuid())) {
            return false;
        }
        this.bccpList.put(bccp.getGuid(), bccp);
        return true;
    }

    public boolean addACC(ACC acc) {
        if (this.accList.containsKey(acc.getGuid())) {
            return false;
        }
        this.accList.put(acc.getGuid(), acc);
        return true;
    }

    public boolean addASCCP(ASCCP asccp) {
        if (this.asccpList.containsKey(asccp.getGuid())) {
            return false;
        }
        this.asccpList.put(asccp.getGuid(), asccp);
        return true;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }

    public void visit(XMLExportSchemaModuleVisitor schemaModuleVisitor) throws Exception {
        schemaModuleVisitor.startSchemaModule(this);

        if (content == null) {
            for (SchemaModule include : includeModules) {
                schemaModuleVisitor.visitIncludeModule(include);
            }

            for (SchemaModule imported : importModules) {
                schemaModuleVisitor.visitIncludeModule(imported);
            }

            for (AgencyId agencyId : agencyIdList.values()) {
                schemaModuleVisitor.visitAgencyId(agencyId);
            }

            for (SchemaCodeList codeList : schemaCodeLists.values()) {
                schemaModuleVisitor.visitCodeList(codeList);
            }

            for (XBTSimpleType xbtSimple : xbtSimples.values()) {
                schemaModuleVisitor.visitXBTSimpleType(xbtSimple);
            }

            for (BDTSimple bdtSimple : bdtSimples.values()) {
                if (bdtSimple instanceof BDTSimpleType) {
                    schemaModuleVisitor.visitBDTSimpleType((BDTSimpleType) bdtSimple);
                } else if (bdtSimple instanceof BDTSimpleContent) {
                    schemaModuleVisitor.visitBDTSimpleContent((BDTSimpleContent) bdtSimple);
                }
            }

            for (BCCP bccp : bccpList.values()) {
                schemaModuleVisitor.visitBCCP(bccp);
            }

            for (ACC acc : accList.values()) {
                if (acc instanceof ACCComplexType) {
                    schemaModuleVisitor.visitACCComplexType((ACCComplexType) acc);
                } else if (acc instanceof ACCGroup) {
                    schemaModuleVisitor.visitACCGroup((ACCGroup) acc);
                }
            }

            for (ASCCP asccp : asccpList.values()) {
                if (asccp instanceof ASCCPComplexType) {
                    schemaModuleVisitor.visitASCCPComplexType((ASCCPComplexType) asccp);
                } else if (asccp instanceof ASCCPGroup) {
                    schemaModuleVisitor.visitASCCPGroup((ASCCPGroup) asccp);
                }
            }
        } else {
            schemaModuleVisitor.visitBlobContent(content);
        }

        this.moduleFile = schemaModuleVisitor.endSchemaModule(this);
    }

    public void minimizeDependency() {
        for (SchemaModule cur: new ArrayList<>(includeModules)) {
            for (SchemaModule next: new ArrayList<>(includeModules)) {
                if (cur.equals(next)) {
                    continue;
                }
                if (cur.hasInclude(next)) {
                    includeModules.remove(next);
                }
            }
        }

        for (SchemaModule cur: new ArrayList<>(importModules)) {
            for (SchemaModule next: new ArrayList<>(importModules)) {
                if (cur.equals(next)) {
                    continue;
                }
                if (cur.hasImport(next)) {
                    importModules.remove(next);
                } else if (next.hasImport(cur)) {
                    importModules.remove(cur);
                    break;
                }
            }
        }
    }

    public File getModuleFile() {
        return this.moduleFile;
    }
}
