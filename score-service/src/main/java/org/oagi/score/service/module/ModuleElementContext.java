package org.oagi.score.service.module;

import org.oagi.score.repo.api.module.ModuleReadRepository;
import org.oagi.score.repo.api.module.model.*;
import org.oagi.score.repo.api.module.model.Module;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

public class ModuleElementContext {
    private Map<BigInteger, Module> allModuleDirMap;
    private Map<BigInteger, Module> currentModuleDirMap;
    private Map<BigInteger, List<Module>> currentModuleDirByParentMap;
    private Map<BigInteger, List<Module>> moduleByModuleDirMap;

    private ModuleElement rootElement;


    public ModuleElementContext(ScoreUser requester, ModuleReadRepository repository, BigInteger moduleSetId, BigInteger moduleDirId) {
        GetModuleListRequest request = new GetModuleListRequest(requester);
        List<Module> modules = repository.getAllModules(request);
        List<Module> files = modules.stream().filter(e -> e.getType().equals(ModuleType.FILE.name())).collect(Collectors.toList());
        List<Module> dirs = modules.stream().filter(e -> e.getType().equals(ModuleType.DIRECTORY.name())).collect(Collectors.toList());
        allModuleDirMap = dirs.stream().collect(toMap(Module::getModuleId, Function.identity()));

        currentModuleDirMap = new HashMap<>();
        currentModuleDirByParentMap = new HashMap<>();
        moduleByModuleDirMap = new HashMap<>();

        rootElement = new ModuleElement();
        rootElement.setDirectory(true);

        if (moduleDirId != null) {
            rootElement.setId(moduleDirId);
        } else {
            Module root = dirs.stream().filter(e -> e.getParentModuleId() == null).findFirst().get();
            rootElement.setId(root.getParentModuleId());
        }

        request.setModuleSetId(moduleSetId);


        files.forEach(e -> addCurrentModuleDirMap(this.allModuleDirMap.get(e.getParentModuleId())));

        currentModuleDirMap.values().forEach(e -> {
            if (e.getParentModuleId() == null) {
                return;
            }
            List<Module> child = currentModuleDirByParentMap.get(e.getParentModuleId());
            if (child == null) {
                List<Module> newChild = new ArrayList<>();
                newChild.add(e);
                currentModuleDirByParentMap.put(e.getParentModuleId(), newChild);
            } else {
                child.add(e);
                currentModuleDirByParentMap.put(e.getParentModuleId(), child);
            }
        });

        files.forEach(e -> {
            List<Module> child = moduleByModuleDirMap.get(e.getParentModuleId());
            if (child == null) {
                List<Module> newChild = new ArrayList<>();
                newChild.add(e);
                moduleByModuleDirMap.put(e.getParentModuleId(), newChild);
            } else {
                child.add(e);
                moduleByModuleDirMap.put(e.getParentModuleId(), child);
            }
        });

        build(this.rootElement);

    }

    private void addCurrentModuleDirMap(Module module) {
        if (this.currentModuleDirMap.get(module.getParentModuleId()) == null) {
            this.currentModuleDirMap.put(module.getParentModuleId(), module);
            if (module.getParentModuleId() != null) {
                addCurrentModuleDirMap(this.allModuleDirMap.get(module.getParentModuleId()));
            }
        }
    }

    private void build(ModuleElement element) {
        if (!element.isDirectory()) {
            return;
        }
        List<ModuleElement> child = new ArrayList<>();
        if (currentModuleDirByParentMap.get(element.getId()) != null) {
            currentModuleDirByParentMap.get(element.getId()).forEach(e -> {
                ModuleElement item = new ModuleElement();
                item.setDirectory(true);
                item.setName(e.getName());
                item.setId(e.getParentModuleId());
                child.add(item);
            });
        }

        if (moduleByModuleDirMap.get(element.getId()) != null) {
            moduleByModuleDirMap.get(element.getId()).forEach(e -> {
                ModuleElement item = new ModuleElement();
                item.setDirectory(false);
                item.setName(e.getName());
                item.setId(e.getModuleId());
                item.setVersionNum(e.getVersionNum());
                item.setNamespaceUri(e.getNamespaceUri());
                item.setNamespaceId(e.getNamespaceId());
                child.add(item);
            });
        }

        element.setChild(child);

        child.forEach(this::build);
    }

    public ModuleElement getRootElement() {
        return rootElement;
    }
}
