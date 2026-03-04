package com.betolara1.order.exception;

import java.util.ArrayList;
import java.util.List;

import com.betolara1.order.dto.response.StandardErrorDTO;

import lombok.Getter;

@Getter
public class ValidationErrorDTO extends StandardErrorDTO {

    private List<FieldMessageDTO> errors = new ArrayList<>();

    public void addError(String fieldName, String message) {
        errors.add(new FieldMessageDTO(fieldName, message));
    }
}
