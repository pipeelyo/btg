package com.example.btg.dto.exception;

public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException(String message) {
        super(message);
    }
    
    public static SaldoInsuficienteException paraFondo(String nombreFondo) {
        return new SaldoInsuficienteException(
            String.format("No tiene saldo disponible para vincularse al fondo %s", nombreFondo)
        );
    }
}
