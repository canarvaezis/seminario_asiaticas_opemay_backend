package co.edu.uniajc.estudiante.opemay.model;

public enum PaymentStatus {
    PENDING("Pendiente"),
    PAID("Pagado"),
    FAILED("Fallido"),
    REFUNDED("Reembolsado"),
    CANCELLED("Cancelado");

    private final String displayName;

    PaymentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}