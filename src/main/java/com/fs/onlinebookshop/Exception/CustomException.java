package com.fs.onlinebookshop.Exception;

import io.jsonwebtoken.ExpiredJwtException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.security.SignatureException;

@RestControllerAdvice
public class CustomException {

    public ProblemDetail errorDetail=null;
    @ExceptionHandler
    public ProblemDetail handleSecurityException(Exception ex){
        if (ex instanceof BadCredentialsException){
            errorDetail=ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(401), ex.getMessage());
            errorDetail.setProperty("Access_Denied_Reason","Authentication Failure");
        }
        if (ex instanceof AccessDeniedException){
            errorDetail=ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403),ex.getMessage());
            errorDetail.setProperty("Access_Denied_Reason","not_authorized");
        }
        if (ex instanceof SignatureException){
            errorDetail=ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403),ex.getMessage());
            errorDetail.setProperty("Access_Detail","Jwt Signature not valid!!");
        }
        if (ex instanceof ExpiredJwtException){
            errorDetail=ProblemDetail.forStatusAndDetail(HttpStatusCode.valueOf(403),ex.getMessage());
            errorDetail.setProperty("Access_Denied_Reason","Jwt Token Already Expired!!");
        }
        return errorDetail;
    }
}
