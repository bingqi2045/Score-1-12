package org.oagi.srt.gateway.http.api.cc_management.helper;

import org.oagi.srt.data.Trackable;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CcUtility {

    public static <T extends Trackable> T getLatestEntity(Long releaseId, List<T> entities) {
        if (entities == null || entities.isEmpty()) {
            return null;
        }

        Stream<T> filteredEntities;
        if (releaseId == null || releaseId == 0L) {
            filteredEntities = entities.stream()
                    .filter(e -> e.getReleaseId() == null);
        } else {
            filteredEntities = entities.stream()
                    .filter(e -> {
                        Long entityReleaseId = e.getReleaseId();
                        return (entityReleaseId != null) && (entityReleaseId <= releaseId);
                    });

            entities = filteredEntities.sorted(Comparator.comparingLong(T::getReleaseId).reversed())
                    .collect(Collectors.toList());
            long maxReleaseId = entities.get(0).getReleaseId();
            filteredEntities = entities.stream().filter(e -> e.getReleaseId() == maxReleaseId);
        }

        entities = filteredEntities.collect(Collectors.toList());
        if (entities.isEmpty()) {
            return null;
        }
        if (entities.size() == 1) {
            return entities.get(0);
        }

        long maxReleaseId = entities.get(0).getReleaseId();
        filteredEntities = entities.stream().filter(e -> e.getReleaseId() == maxReleaseId);
        entities = filteredEntities.sorted(Comparator.comparingLong(T::getRevisionNum).reversed())
                .collect(Collectors.toList());
        if (entities.isEmpty()) {
            return null;
        }
        if (entities.size() == 1) {
            return entities.get(0);
        }

        long maxRevisionNum = entities.get(0).getRevisionNum();
        filteredEntities = entities.stream().filter(e -> e.getRevisionNum() == maxRevisionNum);
        entities = filteredEntities.sorted(Comparator.comparingLong(T::getRevisionTrackingNum).reversed())
                .collect(Collectors.toList());
        return (entities.isEmpty()) ? null : entities.get(0);
    }

    public static <T extends Trackable> String getRevision(Long releaseId, List<T> entities) {
        if (entities == null || entities.isEmpty()) {
            return null;
        }

        if (releaseId == null || releaseId == 0) {
            entities = entities.stream()
                    .filter(e -> (e.getReleaseId() != null) && (e.getReleaseId() > 0L))
                    .collect(Collectors.toList());
            releaseId = entities.get(0).getReleaseId();
        }
        T entity = getLatestEntity(releaseId, entities);
        if (entity == null) {
            return null;
        }

        return entity.getRevisionNum() + "." + entity.getRevisionTrackingNum();
    }
}
