// src/main/java/com/braservone/config/ApiExceptionHandler.java
package com.braservone.config;

import com.braservone.services.exceptions.BadRequestException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class ApiExceptionHandler {

  private ResponseEntity<BadRequestException> body(HttpStatus status, String message) {
    return ResponseEntity.status(status).body(new BadRequestException(message, status.value()));
  }

  // 404
  @ExceptionHandler(EntityNotFoundException.class)
  public ResponseEntity<BadRequestException> notFound(EntityNotFoundException ex) {
    return body(HttpStatus.NOT_FOUND, ex.getMessage());
  }

  // 400 – argumentos inválidos
  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<BadRequestException> badRequest(IllegalArgumentException ex) {
    return body(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  // 422 – regras de negócio (ex.: saldo insuficiente)
  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<BadRequestException> unprocessable(IllegalStateException ex) {
    return body(HttpStatus.UNPROCESSABLE_ENTITY, ex.getMessage());
  }

  // 422 – violações de integridade (FK/unique/etc.)
  @ExceptionHandler({
      DataIntegrityViolationException.class,
      org.hibernate.exception.ConstraintViolationException.class
  })
  public ResponseEntity<BadRequestException> dataIntegrity(Exception ex) {
    String raw = mostSpecificMessage(ex);

    String msg = "Violação de integridade.";
    String lower = raw.toLowerCase();

    // FK de poço
    if (lower.contains("foreign key") && lower.contains("poco_codigo_anp")) {
      msg = "Poço (código ANP) inexistente ou inválido para esta movimentação.";
    }
    // FK de químico
    else if (lower.contains("foreign key") && lower.contains("quimico_codigo")) {
      msg = "Químico (código) inexistente ou inválido para esta movimentação.";
    }
    // duplicidade
    else if (lower.contains("duplicate entry") || lower.contains("unique")) {
      msg = "Registro duplicado. Verifique os dados enviados.";
    }

    return body(HttpStatus.UNPROCESSABLE_ENTITY, msg);
  }

  // 400 – Bean Validation no body
  @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
  public ResponseEntity<BadRequestException> validation(Exception ex) {
    String msg = "Requisição inválida.";
    if (ex instanceof MethodArgumentNotValidException manv && !manv.getBindingResult().getFieldErrors().isEmpty()) {
      var fe = manv.getBindingResult().getFieldErrors().get(0);
      msg = fe.getField() + ": " + fe.getDefaultMessage();
    } else if (ex instanceof BindException be && !be.getBindingResult().getFieldErrors().isEmpty()) {
      var fe = be.getBindingResult().getFieldErrors().get(0);
      msg = fe.getField() + ": " + fe.getDefaultMessage();
    }
    return body(HttpStatus.BAD_REQUEST, msg);
  }

  // 400 – validação de parâmetros (@Validated)
  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<BadRequestException> constraint(ConstraintViolationException ex) {
    return body(HttpStatus.BAD_REQUEST, ex.getMessage());
  }

  // 500 – fallback
  @ExceptionHandler(Exception.class)
  public ResponseEntity<BadRequestException> generic(Exception ex) {
    return body(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno. Tente novamente.");
  }

  // ---- helpers ----
  private String mostSpecificMessage(Exception ex) {
    Throwable t = ex;
    while (t.getCause() != null) t = t.getCause();
    return t.getMessage() != null ? t.getMessage() : ex.getMessage();
  }
}
