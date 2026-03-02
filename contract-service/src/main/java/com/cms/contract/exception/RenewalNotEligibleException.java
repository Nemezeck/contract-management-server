package com.cms.contract.exception;

import com.cms.common.exception.BusinessRuleException;

import java.util.UUID;

public class RenewalNotEligibleException extends BusinessRuleException {

    public RenewalNotEligibleException(UUID collaboratorId) {
        super(String.format("Collaborator with ID %s is not eligible for contract renewal based on performance reviews", 
                collaboratorId), "RENEWAL_NOT_ELIGIBLE");
    }

    public RenewalNotEligibleException(String message) {
        super(message, "RENEWAL_NOT_ELIGIBLE");
    }
}
