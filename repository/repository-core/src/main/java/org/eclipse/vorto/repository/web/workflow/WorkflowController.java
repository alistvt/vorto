/**
 * Copyright (c) 2020 Contributors to the Eclipse Foundation
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information regarding copyright ownership.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * https://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */
package org.eclipse.vorto.repository.web.workflow;

import java.util.List;
import java.util.Optional;

import org.eclipse.vorto.model.ModelId;
import org.eclipse.vorto.repository.core.IUserContext;
import org.eclipse.vorto.repository.core.ModelInfo;
import org.eclipse.vorto.repository.core.ModelNotFoundException;
import org.eclipse.vorto.repository.core.impl.UserContext;
import org.eclipse.vorto.repository.tenant.ITenantService;
import org.eclipse.vorto.repository.web.workflow.dto.WorkflowResponse;
import org.eclipse.vorto.repository.web.workflow.dto.WorkflowState;
import org.eclipse.vorto.repository.workflow.IWorkflowService;
import org.eclipse.vorto.repository.workflow.WorkflowException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;

/**
 * @author Alexander Edelmann - Robert Bosch (SEA) Pte. Ltd.
 */
@RestController
@RequestMapping(value = "/rest/workflows")
public class WorkflowController {

  @Autowired
  private IWorkflowService workflowService;
  
 
  
  @Autowired
  private ITenantService tenantService;

  @ApiOperation(value = "Returns the list of possible actions for a the specific model state")
  @RequestMapping(method = RequestMethod.GET, value = "/{modelId:.+}/actions",
      produces = "application/json")
  @PreAuthorize("hasRole('ROLE_USER')")
  public List<String> getPossibleActions(
      @ApiParam(value = "modelId", required = true) @PathVariable String modelId) {
	  return workflowService.getPossibleActions(ModelId.fromPrettyFormat(modelId),
		        UserContext.user(SecurityContextHolder.getContext().getAuthentication(), getTenant(modelId)));  
  }

  @ApiOperation(value = "Transitions the model state to the next for the provided action.")
  @RequestMapping(method = RequestMethod.PUT, value = "/{modelId:.+}/actions/{actionName}",
      produces = "application/json")
  @PreAuthorize("hasRole('ROLE_MODEL_PROMOTER') || hasRole('ROLE_MODEL_REVIEWER')")
  public WorkflowResponse executeAction(
      @ApiParam(value = "modelId", required = true) @PathVariable String modelId,
      @ApiParam(value = "actionName", required = true) @PathVariable String actionName) {
	  try {
	      ModelInfo model = workflowService.doAction(ModelId.fromPrettyFormat(modelId),
	          UserContext.user(SecurityContextHolder.getContext().getAuthentication(), getTenant(modelId)),
	          actionName);
	      return WorkflowResponse.create(model);
	    } catch (WorkflowException e) {
	      return WorkflowResponse.withErrors(e);
	    }
  }

  @ApiOperation(value = "Gets the model of the current workflow state")
  @RequestMapping(method = RequestMethod.GET, value = "/{modelId:.+}",
      produces = "application/json")
  @PreAuthorize("hasRole('ROLE_USER')")
  public WorkflowState getState(
      @ApiParam(value = "modelId", required = true) @PathVariable String modelId) {
	  IUserContext user = UserContext.user(SecurityContextHolder.getContext().getAuthentication(), getTenant(modelId));
	    
	    return new WorkflowState(
	        this.workflowService.getStateModel(ModelId.fromPrettyFormat(modelId), user).get());
	  }
  
  
  private String getTenant(String modelId) {
	    return getTenant(ModelId.fromPrettyFormat(modelId)).orElseThrow(
	        () -> new ModelNotFoundException("The tenant for '" + modelId + "' could not be found."));
	  }

private Optional<String> getTenant(ModelId modelId) {
	    return tenantService.getTenantFromNamespace(modelId.getNamespace())
	        .map(tenant -> tenant.getTenantId());
	  }
}
