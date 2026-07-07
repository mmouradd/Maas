package com.gsrm.maas.exception;

/** Violation d'une règle de gestion (section 6 du cahier des charges). */
public class BusinessRuleException extends RuntimeException {
    public BusinessRuleException(String message) { super(message); }
}
