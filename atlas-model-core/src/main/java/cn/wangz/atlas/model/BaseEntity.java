package cn.wangz.atlas.model;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.atlas.model.glossary.relations.AtlasTermAssignmentHeader;
import org.apache.atlas.model.instance.AtlasClassification;
import org.apache.atlas.model.instance.AtlasEntity;

import cn.wangz.atlas.model.utils.AtlasUtils;

public class BaseEntity {

    public BaseEntity() {
        this.guid = AtlasUtils.nextInternalId();
    }

    private String guid = null;
    private String homeId = null;
    private Boolean isProxy = Boolean.FALSE;
    private Boolean isIncomplete = Boolean.FALSE;
    private Integer provenanceType = 0;
    private AtlasEntity.Status status = AtlasEntity.Status.ACTIVE;
    private String createdBy = null;
    private String updatedBy = null;
    private Date createTime = null;
    private Date updateTime = null;
    private Long version = 0L;
    private String typeName;

    private List<AtlasClassification> classifications;
    private List<AtlasTermAssignmentHeader> meanings;
    private Map<String, String> customAttributes;
    private Map<String, Map<String, Object>> businessAttributes;
    private Set<String> labels;

    private Map<String, Object> attributes;
    private Map<String, Object> relationshipAttributes;

    public String getGuid() {
        return guid;
    }

    public void setGuid(String guid) {
        this.guid = guid;
    }

    public String getHomeId() {
        return homeId;
    }

    public void setHomeId(String homeId) {
        this.homeId = homeId;
    }

    public Boolean getIsProxy() {
        return isProxy;
    }

    public void setIsProxy(Boolean isProxy) {
        isProxy = isProxy;
    }

    public Boolean getIsIncomplete() {
        return isIncomplete;
    }

    public void setIsIncomplete(Boolean isIncomplete) {
        isIncomplete = isIncomplete;
    }

    public Integer getProvenanceType() {
        return provenanceType;
    }

    public void setProvenanceType(Integer provenanceType) {
        this.provenanceType = provenanceType;
    }

    public AtlasEntity.Status getStatus() {
        return status;
    }

    public void setStatus(AtlasEntity.Status status) {
        this.status = status;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getUpdatedBy() {
        return updatedBy;
    }

    public void setUpdatedBy(String updatedBy) {
        this.updatedBy = updatedBy;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Object> getRelationshipAttributes() {
        return relationshipAttributes;
    }

    public void setRelationshipAttributes(Map<String, Object> relationshipAttributes) {
        this.relationshipAttributes = relationshipAttributes;
    }

    public List<AtlasClassification> getClassifications() {
        return classifications;
    }

    public void setClassifications(List<AtlasClassification> classifications) {
        this.classifications = classifications;
    }

    public List<AtlasTermAssignmentHeader> getMeanings() {
        return meanings;
    }

    public void setMeanings(List<AtlasTermAssignmentHeader> meanings) {
        this.meanings = meanings;
    }

    public Map<String, String> getCustomAttributes() {
        return customAttributes;
    }

    public void setCustomAttributes(Map<String, String> customAttributes) {
        this.customAttributes = customAttributes;
    }

    public Map<String, Map<String, Object>> getBusinessAttributes() {
        return businessAttributes;
    }

    public void setBusinessAttributes(Map<String, Map<String, Object>> businessAttributes) {
        this.businessAttributes = businessAttributes;
    }

    public Set<String> getLabels() {
        return labels;
    }

    public void setLabels(Set<String> labels) {
        this.labels = labels;
    }
}
