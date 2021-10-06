package com.restflow.core.WorkflowExecution.Objects;

import org.springframework.lang.NonNull;

public interface IMonitoringInfo {

	/**
	 * @param currentActivity name of the activity that is currently being processed
	 * @return self-reference
	 */
	IMonitoringInfo setCurrentActivity(@NonNull final String currentActivity);

	/**
	 * @param workflowStatus representation of the current workflow status
	 * @return self-reference
	 */
	IMonitoringInfo setWorkflowStatus(@NonNull final EWorkflowStatus workflowStatus);

	void sendMessage();
}
