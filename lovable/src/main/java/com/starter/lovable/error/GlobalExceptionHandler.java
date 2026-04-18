package com.starter.lovable.error;

import com.stripe.exception.StripeException;
import io.jsonwebtoken.JwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.security.sasl.AuthenticationException;
import java.util.List;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiError> handleBadRequest(BadRequestException ex)
    {
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, ex.getMessage());
        log.error(apiError.toString(), ex);
        return ResponseEntity.status(apiError.status())
                             .body(apiError);
    }

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleResourceNotFoundException(ResourceNotFoundException ex)
    {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, ex.getResourceName() + " with id " + ex.getResourceId() + " not found.");
        log.error(apiError.toString(), ex);
        return ResponseEntity.status(apiError.status())
                             .body(apiError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleInputValidationError(MethodArgumentNotValidException exception)
    {
        List<ApiFiledError> errorList = exception.getBindingResult()
                                                 .getFieldErrors()
                                                 .stream()
                                                 .map(exc -> new ApiFiledError(exc.getField(), exc.getDefaultMessage()))
                                                 .toList();
        ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST, "Input validation failed", errorList);
        log.error(apiError.toString(), exception);
        return ResponseEntity.status(apiError.status())
                             .body(apiError);
    }

    // 1. Handle UsernameNotFoundException (Spring Security / Custom)
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> handleUserNotFound(UsernameNotFoundException exception)
    {
        ApiError apiError = new ApiError(HttpStatus.NOT_FOUND, exception.getMessage(), null);
        log.error("User login failed: {}", exception.getMessage());
        return ResponseEntity.status(apiError.status())
                             .body(apiError);
    }

    // 2. Handle AuthenticationException (Bad Credentials, Locked, etc.)
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ApiError> handleAuthenticationError(AuthenticationException exception)
    {
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, "Authentication failed: " + exception.getMessage(), null);
        log.error("Security Authentication error: {}", exception.getMessage());
        return ResponseEntity.status(apiError.status())
                             .body(apiError);
    }

    // 3. Handle AccessDeniedException (@PreAuthorize failures)
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException exception)
    {
        ApiError apiError = new ApiError(HttpStatus.FORBIDDEN, "You do not have permission to perform this action", null);
        log.error("Access denied: {}", exception.getMessage());
        return ResponseEntity.status(apiError.status())
                             .body(apiError);
    }

    // 4. Handle JWT Related Exceptions (Expired, Malformed)
    // Assuming your AuthUtil throws JwtException from the JJWT library
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiError> handleJwtError(JwtException exception)
    {
        ApiError apiError = new ApiError(HttpStatus.UNAUTHORIZED, "Invalid or expired token", null);
        log.error("JWT Error: {}", exception.getMessage());
        return ResponseEntity.status(apiError.status())
                             .body(apiError);
    }


    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneralException(Exception exception)
    {
        ApiError apiError = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", null);
        log.error("Internal Server Error: ", exception);
        return ResponseEntity.status(apiError.status())
                             .body(apiError);
    }

    @ExceptionHandler(StripeException.class)
    public ResponseEntity<ApiError> handleStripeError(StripeException exception) {
        ApiError apiError = new ApiError(
                HttpStatus.BAD_REQUEST,
                "Payment Provider Error: " + exception.getMessage(),
                null
        );
        log.error("Stripe API error: Status: {}, Code: {}", exception.getStatusCode(), exception.getCode());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(apiError);
    }
}

