package no.difi.vefa.validator.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
@SuppressWarnings("unused")
public class ExceptionController {

    private static Logger logger = LoggerFactory.getLogger(ExceptionController.class);

    @ExceptionHandler(Exception.class)
    private ModelAndView handleException(Exception e) {
        logger.warn(e.getMessage(), e);
        return new ModelAndView("error", "exception", e);
    }
}
