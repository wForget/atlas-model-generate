package cn.wangz.atlas.model.utils;

import java.util.concurrent.atomic.AtomicLong;

import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.atlas.model.instance.AtlasObjectId;
import org.apache.atlas.model.instance.AtlasRelatedObjectId;
import org.apache.atlas.type.AtlasTypeUtil;

public class AtlasUtils {

    public static AtlasRelatedObjectId getAtlasRelatedObjectId(AtlasEntity entity, String relationshipName) {
        return new AtlasRelatedObjectId(getAtlasObjectId(entity), relationshipName);
    }

    public static AtlasObjectId getAtlasObjectId(AtlasEntity entity) {
        return AtlasTypeUtil.getObjectId(entity);
    }

    private static AtomicLong s_nextId = new AtomicLong(System.nanoTime());
    public static String nextInternalId() {
        return "-" + Long.toString(s_nextId.getAndIncrement());
    }

}
