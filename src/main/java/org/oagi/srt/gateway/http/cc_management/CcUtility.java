package org.oagi.srt.gateway.http.cc_management;

import org.oagi.srt.gateway.http.Trackable;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

public class CcUtility {

    public static class ReleaseComparator implements Comparator<Trackable> {
        @Override
        public int compare(Trackable o1, Trackable o2) {
            // What the release_id equals zero(0) means is that it's the latest one.
            long o1ReleaseId = o1.getReleaseId();
            if (o1ReleaseId == 0L) {
                o1ReleaseId = Long.MAX_VALUE;
            }
            long o2ReleaseId = o2.getReleaseId();
            if (o2ReleaseId == 0L) {
                o2ReleaseId = Long.MAX_VALUE;
            }

            return Long.compare(o1ReleaseId, o2ReleaseId);
        }
    }

    public static class RevisionNumComparator implements Comparator<Trackable> {
        @Override
        public int compare(Trackable o1, Trackable o2) {
            // What the revision_num equals zero(0) means is that it's the latest one.
            int o1RevisionNum = o1.getRevisionNum();
            if (o1RevisionNum == 0) {
                o1RevisionNum = Integer.MAX_VALUE;
            }
            int o2RevisionNum = o2.getRevisionNum();
            if (o2RevisionNum == 0) {
                o2RevisionNum = Integer.MAX_VALUE;
            }

            return Integer.compare(o1RevisionNum, o2RevisionNum);
        }
    }

    public static class RevisionTrackingNumComparator implements Comparator<Trackable> {
        @Override
        public int compare(Trackable o1, Trackable o2) {
            // What the revision_tracking_num equals zero(0) means is that it's the latest one.
            int o1RevisionTrackingNum = o1.getRevisionTrackingNum();
            if (o1RevisionTrackingNum == 0) {
                o1RevisionTrackingNum = Integer.MAX_VALUE;
            }
            int o2RevisionTrackingNum = o1.getRevisionTrackingNum();
            if (o2RevisionTrackingNum == 0) {
                o2RevisionTrackingNum = Integer.MAX_VALUE;
            }

            return Integer.compare(o1RevisionTrackingNum, o2RevisionTrackingNum);
        }
    }

    public static Comparator<Trackable> TRACKABLE_COMPARATOR = new Comparator<Trackable>() {

        private Comparator<Trackable> releaseComparator = new ReleaseComparator();
        private Comparator<Trackable> revisionNumComparator = new RevisionNumComparator();
        private Comparator<Trackable> revisionTrackingNumComparator = new RevisionTrackingNumComparator();

        @Override
        public int compare(Trackable o1, Trackable o2) {
            int c = releaseComparator.compare(o1, o2);
            if (c != 0) { // is not equals?
                return c;
            }
            c = revisionNumComparator.compare(o1, o2);
            if (c != 0) { // is not equals?
                return c;
            }
            return revisionTrackingNumComparator.compare(o1, o2);
        }
    };

    public static <T extends Trackable> T getLatestEntity(long releaseId, List<T> entities) {
        if (entities == null || entities.isEmpty()) {
            return null;
        }

        Stream<T> filteredEntities;
        if (releaseId == 0L) {
            filteredEntities = entities.stream()
                    .filter(e -> e.getReleaseId() == releaseId);
        } else {
            filteredEntities = entities.stream()
                    .filter(e -> {
                        long entityReleaseId = e.getReleaseId();
                        return (entityReleaseId == 0L) || (entityReleaseId <= releaseId);
                    });
        }

        return filteredEntities.max(TRACKABLE_COMPARATOR).orElse(null);
    }
}
