package co.edu.uniajc.estudiante.opemay.model;

public enum OrderStatus {
    PENDING("Pendiente"),
    CONFIRMED("Confirmado"),
    PROCESSING("Procesando"),
    SHIPPED("Enviado"),
    DELIVERED("Entregado"),
    CANCELLED("Cancelado");

    private final String displayName;

    OrderStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}