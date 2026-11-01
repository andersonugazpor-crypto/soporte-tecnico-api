package pe.edu.idat.soporte.model.enums;

import java.util.EnumSet;
import java.util.Set;

/**
 * Ciclo de vida de una solicitud de soporte.
 * <p>
 * Cada estado conoce a qué estados puede pasar, de modo que las reglas
 * de transición quedan encapsuladas en el propio enum.
 *
 * <pre>
 * PENDIENTE  → EN_PROCESO | CANCELADA
 * EN_PROCESO → RESUELTA   | CANCELADA
 * RESUELTA   → CERRADA    | EN_PROCESO (reapertura)
 * CERRADA, CANCELADA → estados finales (sin cambios)
 * </pre>
 */
public enum EstadoSolicitud {
    PENDIENTE,
    EN_PROCESO,
    RESUELTA,
    CERRADA,
    CANCELADA;

    /** Estados a los que se puede pasar desde el estado actual. */
    public Set<EstadoSolicitud> transicionesPermitidas() {
        return switch (this) {
            case PENDIENTE -> EnumSet.of(EN_PROCESO, CANCELADA);
            case EN_PROCESO -> EnumSet.of(RESUELTA, CANCELADA);
            case RESUELTA -> EnumSet.of(CERRADA, EN_PROCESO);
            case CERRADA, CANCELADA -> EnumSet.noneOf(EstadoSolicitud.class);
        };
    }

    public boolean puedeCambiarA(EstadoSolicitud destino) {
        return transicionesPermitidas().contains(destino);
    }

    /** Un estado final ya no admite cambios. */
    public boolean esFinal() {
        return this == CERRADA || this == CANCELADA;
    }

    /** Estados que cuentan como solicitud "abierta". */
    public boolean esActivo() {
        return this == PENDIENTE || this == EN_PROCESO;
    }
}
