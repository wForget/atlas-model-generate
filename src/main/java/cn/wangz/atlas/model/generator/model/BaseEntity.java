package cn.wangz.atlas.model.generator.model;

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

public class BaseEntity {

    private AtlasEntity.AtlasEntityWithExtInfo atlasEntityWithExtInfo = null;
    private AtlasEntity atlasEntity = null;

    public BaseEntity() {
        this.atlasEntity = new AtlasEntity();
        this.atlasEntityWithExtInfo = new AtlasEntity.AtlasEntityWithExtInfo(this.atlasEntity);
    }

    public AtlasEntity.AtlasEntityWithExtInfo toAtlasEntity() {

        AtlasObjectId objId   = AtlasTypeUtil.getObjectId(this.atlasEntity);
        AtlasRelatedObjectId objIdRelatedObject =     new AtlasRelatedObjectId(objId);
//        this.atlasEntityWithExtInfo.addReferredEntity();

        return atlasEntityWithExtInfo;
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
