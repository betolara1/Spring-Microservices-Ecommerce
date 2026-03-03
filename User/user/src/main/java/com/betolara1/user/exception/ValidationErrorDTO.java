package com.betolara1.user.exception;

import java.util.ArrayList;
import java.util.List;

import com.betolara1.user.DTO.response.StandardErrorDTO;

import lombok.Getter;

@Getter
public class ValidationErrorDTO extends StandardErrorDTO {

    private List<FieldMessageDTO> errors = new ArrayList<>();

    public void addError(String fieldName, String message) {
        errors.add(new FieldMessageDTO(fieldName, message));
    }
}
