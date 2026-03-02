package com.cms.contract.exception;

import com.cms.common.exception.DuplicateResourceException;

import java.util.UUID;

public class ContractAlreadyExistsException extends DuplicateResourceException {

    public ContractAlreadyExistsException(UUID collaboratorId) {
        super(String.format("An active contract already exists for collaborator with ID: %s", collaboratorId));
    }

    public ContractAlreadyExistsException(String message) {
        super(message);
    }
}
