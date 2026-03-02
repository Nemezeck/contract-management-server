package com.cms.collaborator.exception;

import com.cms.common.exception.ResourceNotFoundException;

import java.util.UUID;

public class CollaboratorNotFoundException extends ResourceNotFoundException {

    public CollaboratorNotFoundException(UUID id) {
        super("Collaborator", "id", id);
    }

    public CollaboratorNotFoundException(String fieldName, Object fieldValue) {
        super("Collaborator", fieldName, fieldValue);
    }
}
