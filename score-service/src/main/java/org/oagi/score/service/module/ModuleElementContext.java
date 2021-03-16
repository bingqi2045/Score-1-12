package org.oagi.score.service.module;

import org.oagi.score.repo.api.module.ModuleSetReadRepository;
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


public class ModuleElementContext {
    private Map<BigInteger, List<Module>> filesByParentMap;
    private Map<BigInteger, List<Module>> directoriesByParentMap;

    private ModuleElement rootElement;


    public ModuleElementContext(ModuleSetReadRepository repository, BigInteger moduleSetId) {
        List<Module> modules = repository.getAllModules(moduleSetId);
        List<Module> files = modules.stream().filter(e -> e.getType().equals(ModuleType.FILE.name())).collect(Collectors.toList());
        List<Module> dirs = modules.stream().filter(e -> e.getType().equals(ModuleType.DIRECTORY.name())).collect(Collectors.toList());

        filesByParentMap = new HashMap<>();
        directoriesByParentMap = new HashMap<>();

        rootElement = new ModuleElement();
        rootElement.setDirectory(true);

        Module root = dirs.stream().filter(e -> e.getParentModuleId() == null).findFirst().get();
        rootElement.setId(root.getModuleId());

        files.forEach(file -> {
            if (filesByParentMap.get(file.getParentModuleId()) == null) {
                List<Module> childFiles = new ArrayList<>();
                childFiles.add(file);
                filesByParentMap.put(file.getParentModuleId(), childFiles);
            } else {
                filesByParentMap.get(file.getParentModuleId()).add(file);
            }
        });

        dirs.forEach(dir -> {
            if (directoriesByParentMap.get(dir.getParentModuleId()) == null) {
                List<Module> childDirs = new ArrayList<>();
                childDirs.add(dir);
                directoriesByParentMap.put(dir.getParentModuleId(), childDirs);
            } else {
                directoriesByParentMap.get(dir.getParentModuleId()).add(dir);
            }
        });

        build(this.rootElement);

    }

    private void build(ModuleElement element) {
        if (!element.isDirectory()) {
            return;
        }
        List<ModuleElement> child = new ArrayList<>();
        if (directoriesByParentMap.get(element.getId()) != null) {
            directoriesByParentMap.get(element.getId()).forEach(e -> {
                ModuleElement item = new ModuleElement();
                item.setDirectory(true);
                item.setName(e.getName());
                item.setId(e.getModuleId());
                child.add(item);
            });
        }

        if (filesByParentMap.get(element.getId()) != null) {
            filesByParentMap.get(element.getId()).forEach(e -> {
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
