package cn.wangz.atlas.model;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.atlas.model.glossary.relations.AtlasTermAssignmentHeader;
import org.apache.atlas.model.instance.AtlasClassification;
import org.apache.atlas.model.instance.AtlasEntity;
import org.apache.atlas.model.instance.AtlasObjectId;
import org.apache.atlas.model.instance.AtlasRelatedObjectId;
import org.apache.atlas.type.AtlasTypeUtil;
import org.apache.commons.lang3.StringUtils;

import cn.wangz.atlas.model.annotation.AtlasAttribute;
import cn.wangz.atlas.model.annotation.AtlasRelationshipAttribute;
import cn.wangz.atlas.model.utils.TypeUtils;

public class BaseEntity {

    private AtlasEntity atlasEntity = null;

    public BaseEntity() {
        this.atlasEntity = new AtlasEntity();
    }

    public AtlasEntity.AtlasEntityWithExtInfo toAtlasEntityWithExtInfo() {
        AtlasEntity.AtlasEntityExtInfo atlasEntityExtInfo = new AtlasEntity.AtlasEntityExtInfo();
        toAtlasEntity(atlasEntityExtInfo);

        return new AtlasEntity.AtlasEntityWithExtInfo(this.atlasEntity, atlasEntityExtInfo);
    }

    public AtlasEntity toAtlasEntity(AtlasEntity.AtlasEntityExtInfo extInfo) {
        // fill attribute
        fillAttributes(extInfo);

        // fill relationship attribute
        fillRelationshipAttributes(extInfo);

        return atlasEntity;
    }

    private void fillAttributes(AtlasEntity.AtlasEntityExtInfo extInfo) {
        List<Field> fields = TypeUtils.getAllFields(this.getClass());
        for (Field field: fields) {
            AtlasAttribute atlasAttributeAnno = field.getAnnotation(AtlasAttribute.class);
            if (atlasAttributeAnno != null) {
                String name = atlasAttributeAnno.name();
                if (StringUtils.isBlank(name)) {
                    name = field.getName();
                }
                Object value = null;
                try {
                    value = TypeUtils.getFiledValue(this, field);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                    // do noting
                }
                if (value == null) {
                    continue;
                }
                if (value instanceof List || value instanceof Set) {
                    List<Object> list = new ArrayList<>();
                    ((Collection<Object>) value).forEach(item -> {
                        if (item instanceof BaseEntity) {
                            AtlasEntity entity = ((BaseEntity) item).toAtlasEntity(extInfo);
                            AtlasObjectId objectId = getAtlasObjectId(entity);
                            list.add(objectId);
                            extInfo.addReferredEntity(entity);
                        } else {
                            list.add(item);
                        }
                    });
                    this.atlasEntity.setAttribute(name, list);
                } else if (value instanceof BaseEntity) {
                    AtlasEntity entity = ((BaseEntity) value).toAtlasEntity(extInfo);
                    AtlasObjectId objectId = getAtlasObjectId(entity);
                    this.atlasEntity.setAttribute(name, objectId);
                    extInfo.addReferredEntity(entity);
                } else {
                    this.atlasEntity.setAttribute(name, value);
                }
            }
        }
    }

    private void fillRelationshipAttributes(AtlasEntity.AtlasEntityExtInfo extInfo) {
        List<Field> fields = TypeUtils.getAllFields(this.getClass());
        for (Field field: fields) {
            AtlasRelationshipAttribute atlasRelationshipAttributeAnno = field.getAnnotation(AtlasRelationshipAttribute.class);
            if (atlasRelationshipAttributeAnno != null) {
                String name = atlasRelationshipAttributeAnno.name();
                String relationShipName = field.getAnnotation(AtlasRelationshipAttribute.class).relationShipName();
                if (StringUtils.isBlank(name)) {
                    name = field.getName();
                }
                Object value = null;
                try {
                    value = TypeUtils.getFiledValue(this, field);
                } catch (IllegalAccessException e) {
                    // do noting
                }
                if (value == null) {
                    continue;
                }
                if (value instanceof List || value instanceof Set) {
                    List<Object> list = new ArrayList<>();
                    ((Collection<Object>) value).forEach(item -> {
                        if (item instanceof BaseEntity) {
                            AtlasEntity entity = ((BaseEntity) item).toAtlasEntity(extInfo);
                            AtlasObjectId relatedObjectId = getAtlasRelatedObjectId(entity, relationShipName);
                            list.add(relatedObjectId);
                            extInfo.addReferredEntity(entity);
                        } else {
                            list.add(item);
                        }
                    });
                    this.atlasEntity.setRelationshipAttribute(name, list);
                } else if (value instanceof BaseEntity) {
                    AtlasEntity entity = ((BaseEntity) value).toAtlasEntity(extInfo);
                    AtlasRelatedObjectId relatedObjectId = getAtlasRelatedObjectId(entity, relationShipName);
                    this.atlasEntity.setRelationshipAttribute(name, relatedObjectId);
                    extInfo.addReferredEntity(entity);
                } else {
                    this.atlasEntity.setRelationshipAttribute(name, value);
                }
            }
        }
    }

    private AtlasRelatedObjectId getAtlasRelatedObjectId(AtlasEntity entity, String relationshipName) {
        return new AtlasRelatedObjectId(getAtlasObjectId(entity), relationshipName);
    }

    private AtlasObjectId getAtlasObjectId(AtlasEntity entity) {
        return AtlasTypeUtil.getObjectId(entity);
    }


    public void setGuid(String guid) {
        this.atlasEntity.setGuid(guid);
    }

    public void setHomeId(String homeId) {
        this.atlasEntity.setHomeId(homeId);
    }

    public void setIsProxy(Boolean isProxy) {
        this.atlasEntity.setIsProxy(isProxy);
    }

    public void setIncomplete(Boolean isIncomplete) {
        this.atlasEntity.setIsIncomplete(isIncomplete);
    }

    public void setProvenanceType(Integer provenanceType) {
        this.atlasEntity.setProvenanceType(provenanceType);
    }

    public void setStatus(AtlasEntity.Status status) {
        this.atlasEntity.setStatus(status);
    }

    public void setCreatedBy(String createdBy) {
        this.atlasEntity.setCreatedBy(createdBy);
    }

    public void setUpdatedBy(String updatedBy) {
        this.atlasEntity.setUpdatedBy(updatedBy);
    }

    public void setCreateTime(Date createTime) {
        this.atlasEntity.setCreateTime(createTime);
    }

    public void setUpdateTime(Date updateTime) {
        this.atlasEntity.setUpdateTime(updateTime);
    }

    public void setVersion(Long version) {
        this.atlasEntity.setVersion(version);
    }

    public void setTypeName(String typeName) {
        this.atlasEntity.setTypeName(typeName);
    }

    public void addClassifications(List<AtlasClassification> classifications) {
        this.atlasEntity.addClassifications(classifications);
    }

    public void setMeanings(AtlasTermAssignmentHeader meaning) {
        this.atlasEntity.addMeaning(meaning);
    }

    public void setCustomAttributes(Map<String, String> customAttributes) {
        this.atlasEntity.setCustomAttributes(customAttributes);
    }

    public void setBusinessAttributes(Map<String, Map<String, Object>> businessAttributes) {
        this.atlasEntity.setBusinessAttributes(businessAttributes);
    }

    public void setBusinessAttribute(String nsName, String nsAttrName, Object nsValue) {
        this.atlasEntity.setBusinessAttribute(nsName, nsAttrName, nsValue);
    }

    public void setLabels(Set<String> labels) {
        this.atlasEntity.setLabels(labels);
    }


}
