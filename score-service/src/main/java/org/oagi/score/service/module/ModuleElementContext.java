package org.oagi.score.service.module;

import org.oagi.score.repo.api.module.ModuleReadRepository;
import org.oagi.score.repo.api.module.model.GetModuleListRequest;
import org.oagi.score.repo.api.module.model.ModuleDir;
import org.oagi.score.repo.api.module.model.Module;
import org.oagi.score.repo.api.module.model.ModuleElement;
import org.oagi.score.repo.api.user.model.ScoreUser;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

public class ModuleElementContext {
    private Map<BigInteger, ModuleDir> allModuleDirMap;
    private Map<BigInteger, ModuleDir> currentModuleDirMap;
    private Map<BigInteger, List<ModuleDir>> currentModuleDirByParentMap;
    private Map<BigInteger, List<Module>> moduleByModuleDirMap;

    private ModuleElement rootElement;


    public ModuleElementContext(ScoreUser requester, ModuleReadRepository repository, BigInteger moduleSetId, BigInteger moduleDirId) {
        GetModuleListRequest request = new GetModuleListRequest(requester);
        List<ModuleDir> dirs = repository.getAllModuleDirs(request);
        allModuleDirMap = dirs.stream().collect(toMap(ModuleDir::getModuleDirId, Function.identity()));

        currentModuleDirMap = new HashMap<>();
        currentModuleDirByParentMap = new HashMap<>();
        moduleByModuleDirMap = new HashMap<>();

        rootElement = new ModuleElement();
        rootElement.setDirectory(true);

        if (moduleDirId != null) {
            rootElement.setId(moduleDirId);
        } else {
            ModuleDir root = dirs.stream().filter(e -> e.getParentModuleDirId() == null).findFirst().get();
            rootElement.setId(root.getModuleDirId());
        }

        request.setModuleSetId(moduleSetId);

        List<Module> files = repository.getAllModules(request);
        files.forEach(e -> addCurrentModuleDirMap(this.allModuleDirMap.get(e.getModuleDirId())));

        currentModuleDirMap.values().forEach(e -> {
            if (e.getParentModuleDirId() == null) {
                return;
            }
            List<ModuleDir> child = currentModuleDirByParentMap.get(e.getParentModuleDirId());
            if (child == null) {
                List<ModuleDir> newChild = new ArrayList<>();
                newChild.add(e);
                currentModuleDirByParentMap.put(e.getParentModuleDirId(), newChild);
            } else {
                child.add(e);
                currentModuleDirByParentMap.put(e.getParentModuleDirId(), child);
            }
        });

        files.forEach(e -> {
            List<Module> child = moduleByModuleDirMap.get(e.getModuleDirId());
            if (child == null) {
                List<Module> newChild = new ArrayList<>();
                newChild.add(e);
                moduleByModuleDirMap.put(e.getModuleDirId(), newChild);
            } else {
                child.add(e);
                moduleByModuleDirMap.put(e.getModuleDirId(), child);
            }
        });

        build(this.rootElement);

    }

    private void addCurrentModuleDirMap(ModuleDir moduleDir) {
        if (this.currentModuleDirMap.get(moduleDir.getModuleDirId()) == null) {
            this.currentModuleDirMap.put(moduleDir.getModuleDirId(), moduleDir);
            if (moduleDir.getParentModuleDirId() != null) {
                addCurrentModuleDirMap(this.allModuleDirMap.get(moduleDir.getParentModuleDirId()));
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
                item.setId(e.getModuleDirId());
                child.add(item);
            });
        }

        if (moduleByModuleDirMap.get(element.getId()) != null) {
            moduleByModuleDirMap.get(element.getId()).forEach(e -> {
                ModuleElement item = new ModuleElement();
                item.setDirectory(false);
                item.setName(e.getName());
                item.setId(e.getModuleId());
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
