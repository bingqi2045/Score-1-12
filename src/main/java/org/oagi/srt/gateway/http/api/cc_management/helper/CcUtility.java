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

        if (releaseId == null || releaseId == 0L) {
            entities = entities.stream()
                    .filter(e -> e.getCurrentId() == null)
                    .sorted(Comparator.comparingLong(T::getId).reversed())
                    .collect(Collectors.toList());
        } else {
            entities = entities.stream()
                    .filter(e -> e.getCurrentId() != null)
                    .filter(e -> e.getReleaseId() <= releaseId)
                    .sorted(Comparator.comparingLong(T::getId).reversed())
                    .collect(Collectors.toList());
        }

        return (entities.isEmpty()) ? null : entities.get(0);
    }

    public static <T extends Trackable> String getRevision(Long releaseId, List<T> entities) {
        if (entities == null || entities.isEmpty()) {
            return null;
        }

        if (releaseId == null || releaseId == 0L) {
            entities = entities.stream()
                    .filter(e -> e.getCurrentId() != null)
                    .sorted(Comparator.comparingLong(T::getId).reversed())
                    .collect(Collectors.toList());
        } else {
            entities = entities.stream()
                    .filter(e -> e.getCurrentId() != null)
                    .filter(e -> e.getReleaseId() <= releaseId)
                    .sorted(Comparator.comparingLong(T::getId).reversed())
                    .collect(Collectors.toList());
        }

        if (entities.isEmpty()) {
            return null;
        }

        T entity = entities.get(0);
        return entity.getRevisionNum() + "." + entity.getRevisionTrackingNum();
    }
}
