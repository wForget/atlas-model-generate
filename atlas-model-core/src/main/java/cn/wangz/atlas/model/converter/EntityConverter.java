package cn.wangz.atlas.model.converter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.atlas.model.instance.AtlasObjectId;
import org.apache.commons.lang3.StringUtils;

import cn.wangz.atlas.model.BaseEntity;
import cn.wangz.atlas.model.annotation.AtlasAttribute;
import cn.wangz.atlas.model.annotation.AtlasRelationshipAttribute;
import cn.wangz.atlas.model.utils.AtlasUtils;
import cn.wangz.atlas.model.utils.TypeUtils;

public class EntityConverter implements Converter<AtlasEntity.AtlasEntityWithExtInfo> {

    private BaseEntity baseEntity;

    public EntityConverter(BaseEntity baseEntity) {
        this.baseEntity = baseEntity;
    }

    @Override
    public AtlasEntity.AtlasEntityWithExtInfo convert() {
        AtlasEntity.AtlasEntityExtInfo extInfo = new AtlasEntity.AtlasEntityExtInfo();
        AtlasEntity entity = convert(extInfo);
        extInfo.removeReferredEntity(entity.getGuid());
        return new AtlasEntity.AtlasEntityWithExtInfo(entity, extInfo);
    }

    public AtlasEntity convert(AtlasEntity.AtlasEntityExtInfo extInfo) {
        AtlasEntity entity = extInfo.getReferredEntity(this.baseEntity.getGuid());
        if (entity == null) {
            entity = new AtlasEntity();
            fillFromBaseEntity(entity);
            extInfo.addReferredEntity(entity);
            fillAttributes(entity, extInfo);
        }
        return entity;
    }


    private void fillAttributes(AtlasEntity atlasEntity, AtlasEntity.AtlasEntityExtInfo extInfo) {
        List<Field> fields = TypeUtils.getAllFields(this.baseEntity.getClass());
        for (Field field: fields) {
            AtlasRelationshipAttribute relationshipAttrAnnotation = field.getAnnotation(AtlasRelationshipAttribute.class);
            AtlasAttribute attrAnnotation = null;
            if (relationshipAttrAnnotation == null) {
                attrAnnotation = field.getAnnotation(AtlasAttribute.class);
                if (attrAnnotation == null) continue;
            }
            String name;
            if (relationshipAttrAnnotation != null) {
                name = relationshipAttrAnnotation.name();
            } else {
                name = attrAnnotation.name();
            }
            if (StringUtils.isBlank(name)) {
                name = field.getName();
            }
            Object value = null;
            try {
                value = TypeUtils.getFiledValue(this.baseEntity, field);
                // date type to long
                if (value != null && value instanceof Date) {
                    value = ((Date) value).getTime();
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                // do noting
            }
            if (value == null) {
                continue;
            }
            Object realValue = value;
            if (value instanceof List || value instanceof Set) {
                List<Object> list = new ArrayList<>();
                ((Collection<Object>) value).forEach(item -> {
                    if (item instanceof BaseEntity) {
                        AtlasEntity entity = new EntityConverter((BaseEntity) item).convert(extInfo);
                        AtlasObjectId objectId = getObjectId(entity, relationshipAttrAnnotation);
                        list.add(objectId);
                        extInfo.addReferredEntity(entity);
                    } else {
                        list.add(item);
                    }
                });
                realValue = list;
            } else if (value instanceof BaseEntity) {
                AtlasEntity entity = new EntityConverter((BaseEntity) value).convert(extInfo);
                AtlasObjectId objectId = getObjectId(entity, relationshipAttrAnnotation);
                extInfo.addReferredEntity(entity);
                realValue = objectId;
            }
            if (relationshipAttrAnnotation != null) {
                atlasEntity.setRelationshipAttribute(name, realValue);
            } else {
                atlasEntity.setAttribute(name, realValue);
            }
        }
    }

    private AtlasObjectId getObjectId(AtlasEntity entity, AtlasRelationshipAttribute relationshipAttrAnnotation) {
        if (relationshipAttrAnnotation != null) {
            return AtlasUtils.getAtlasRelatedObjectId(entity, relationshipAttrAnnotation.relationShipName());
        } else {
            return AtlasUtils.getAtlasObjectId(entity);
        }
    }

    private void fillFromBaseEntity(AtlasEntity entity) {
        Optional.ofNullable(baseEntity.getGuid()).ifPresent(entity::setGuid);
        Optional.ofNullable(baseEntity.getHomeId()).ifPresent(entity::setHomeId);
        Optional.ofNullable(baseEntity.getIsProxy()).ifPresent(entity::setIsProxy);
        Optional.ofNullable(baseEntity.getIsIncomplete()).ifPresent(entity::setIsIncomplete);
        Optional.ofNullable(baseEntity.getProvenanceType()).ifPresent(entity::setProvenanceType);
        Optional.ofNullable(baseEntity.getStatus()).ifPresent(entity::setStatus);
        Optional.ofNullable(baseEntity.getCreatedBy()).ifPresent(entity::setCreatedBy);
        Optional.ofNullable(baseEntity.getUpdatedBy()).ifPresent(entity::setUpdatedBy);
        Optional.ofNullable(baseEntity.getCreateTime()).ifPresent(entity::setCreateTime);
        Optional.ofNullable(baseEntity.getUpdateTime()).ifPresent(entity::setUpdateTime);
        Optional.ofNullable(baseEntity.getVersion()).ifPresent(entity::setVersion);
        Optional.ofNullable(baseEntity.getTypeName()).ifPresent(entity::setTypeName);

        if (baseEntity.getAttributes() != null) {
            baseEntity.getAttributes().entrySet().forEach(item -> entity.setAttribute(item.getKey(), item.getValue()));
        }
        if (baseEntity.getRelationshipAttributes() != null) {
            baseEntity.getRelationshipAttributes().entrySet().forEach(item -> entity.setRelationshipAttribute(item.getKey(), item.getValue()));
        }
        if (baseEntity.getClassifications() != null) {
            entity.setClassifications(baseEntity.getClassifications());
        }
        if (baseEntity.getMeanings() != null) {
            entity.setMeanings(baseEntity.getMeanings());
        }
        if (baseEntity.getCustomAttributes() != null) {
            entity.setCustomAttributes(baseEntity.getCustomAttributes());
        }
        if (baseEntity.getBusinessAttributes() != null) {
            entity.setBusinessAttributes(baseEntity.getBusinessAttributes());
        }
        if (baseEntity.getLabels() != null) {
            entity.setLabels(baseEntity.getLabels());
        }
    }

}
