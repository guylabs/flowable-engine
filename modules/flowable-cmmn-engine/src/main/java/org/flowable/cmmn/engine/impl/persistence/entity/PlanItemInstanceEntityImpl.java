/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.cmmn.engine.impl.persistence.entity;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.cmmn.engine.CmmnEngineConfiguration;
import org.flowable.cmmn.engine.impl.repository.CaseDefinitionUtil;
import org.flowable.cmmn.engine.impl.util.CommandContextUtil;
import org.flowable.cmmn.model.Case;
import org.flowable.cmmn.model.PlanItem;
import org.flowable.variable.api.type.VariableScopeType;
import org.flowable.variable.service.impl.persistence.entity.VariableInstanceEntity;
import org.flowable.variable.service.impl.persistence.entity.VariableScopeImpl;

/**
 * @author Joram Barrez
 */
public class PlanItemInstanceEntityImpl extends VariableScopeImpl implements PlanItemInstanceEntity {
    
    protected String caseDefinitionId;
    protected String caseInstanceId;
    protected String stageInstanceId;
    protected boolean isStage;
    protected String elementId;
    protected String name;
    protected String state;
    protected Date startTime;
    protected String startUserId;
    protected String referenceId;
    protected String referenceType;
    protected String tenantId = CmmnEngineConfiguration.NO_TENANT_ID;
    
    // Non-persisted
    protected PlanItem planItem;
    protected List<PlanItemInstanceEntity> children;
    protected PlanItemInstanceEntity stagePlanItemInstance;
    protected List<SentryPartInstanceEntity> satisfiedSentryPartInstances;
    
    public Object getPersistentState() {
        Map<String, Object> persistentState = new HashMap<>();
        persistentState.put("caseDefinitionId", caseDefinitionId);
        persistentState.put("caseInstanceId", caseInstanceId);
        persistentState.put("stageInstanceId", stageInstanceId);
        persistentState.put("isStage", isStage);
        persistentState.put("elementId", elementId);
        persistentState.put("name", name);
        persistentState.put("state", state);
        persistentState.put("startTime", startTime);
        persistentState.put("startUserId", startUserId);
        persistentState.put("referenceId", referenceId);
        persistentState.put("referenceType", referenceType);
        persistentState.put("tenantId", tenantId);
        return persistentState;
    }
    
    @Override
    public PlanItem getPlanItem() {
        if (planItem == null) {
            Case caze = CaseDefinitionUtil.getCase(caseDefinitionId);
            return (PlanItem) caze.getAllCaseElements().get(elementId);
        }
        return planItem;
    }
    
    public String getCaseDefinitionId() {
        return caseDefinitionId;
    }
    public void setCaseDefinitionId(String caseDefinitionId) {
        this.caseDefinitionId = caseDefinitionId;
    }
    public String getCaseInstanceId() {
        return caseInstanceId;
    }
    public void setCaseInstanceId(String caseInstanceId) {
        this.caseInstanceId = caseInstanceId;
    }
    public String getStageInstanceId() {
        return stageInstanceId;
    }
    public void setStageInstanceId(String stageInstanceId) {
        this.stageInstanceId = stageInstanceId;
    }
    public boolean isStage() {
        return isStage;
    }
    public void setStage(boolean isStage) {
        this.isStage = isStage;
    }
    public String getElementId() {
        return elementId;
    }
    public void setElementId(String elementId) {
        this.elementId = elementId;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getState() {
        return state;
    }
    public void setState(String state) {
        this.state = state;
    }
    public Date getStartTime() {
        return startTime;
    }
    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }
    public String getStartUserId() {
        return startUserId;
    }
    public void setStartUserId(String startUserId) {
        this.startUserId = startUserId;
    }
    public String getReferenceId() {
        return referenceId;
    }
    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }
    public String getReferenceType() {
        return referenceType;
    }
    public void setReferenceType(String referenceType) {
        this.referenceType = referenceType;
    }
    public String getTenantId() {
        return tenantId;
    }
    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }
    
    @Override
    public void setChildren(List<PlanItemInstanceEntity> children) {
        this.children = children;
    }
    
    public List<PlanItemInstanceEntity> getChildren() {
        if (children == null) {
            children = CommandContextUtil.getPlanItemInstanceEntityManager().findChildPlanItemInstancesForStage(id);
        }
        return children;
    }
    
    @Override
    public List<SentryPartInstanceEntity> getSatisfiedSentryPartInstances() {
        if (satisfiedSentryPartInstances == null) {
            satisfiedSentryPartInstances = CommandContextUtil.getSentryPartInstanceEntityManager().findSentryPartInstancesByPlanItemInstanceId(id);
        }
        return satisfiedSentryPartInstances;
    }
    
    public void setSatisfiedSentryPartInstances(List<SentryPartInstanceEntity> satisfiedSentryPartInstances) {
        this.satisfiedSentryPartInstances = satisfiedSentryPartInstances;
    }

    // VariableScopeImpl methods

    @Override
    protected Collection<VariableInstanceEntity> loadVariableInstances() {
        return CommandContextUtil.getVariableService().findVariableInstanceBySubScopeIdAndScopeType(id, VariableScopeType.CMMN);
    }

    @Override
    protected VariableScopeImpl getParentVariableScope() {
        if (caseInstanceId != null) {
            return (VariableScopeImpl) CommandContextUtil.getCaseInstanceEntityManager().findById(caseInstanceId);
        }
        return null;
    }

    @Override
    protected void initializeVariableInstanceBackPointer(VariableInstanceEntity variableInstance) {
        variableInstance.setScopeId(caseInstanceId);
        variableInstance.setSubScopeId(id);
        variableInstance.setScopeType(VariableScopeType.CMMN);
    }

    @Override
    protected VariableInstanceEntity getSpecificVariable(String variableName) {
        return CommandContextUtil.getVariableService().findVariableInstanceBySubScopeIdAndScopeTypeAndName(id, VariableScopeType.CMMN, variableName);
    }

    @Override
    protected List<VariableInstanceEntity> getSpecificVariables(Collection<String> variableNames) {
        return CommandContextUtil.getVariableService().findVariableInstancesBySubScopeIdAndScopeTypeAndNames(id, VariableScopeType.CMMN, variableNames);
    }

    @Override
    protected boolean isPropagateToHistoricVariable() {
        return true;
    }

}
