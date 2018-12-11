package no.difi.vefa.validator.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@Slf4j
@ControllerAdvice
@SuppressWarnings("unused")
public class ExceptionController {

    @ExceptionHandler(Exception.class)
    private ModelAndView handleException(Exception e) {
        log.warn(e.getMessage(), e);
        return new ModelAndView("error", "exception", e);
    }
}
