package com.smartnetwork.backend.domain.dtos.firewalls.Services;

import com.smartnetwork.backend.domain.Entity.firewalls.Service;

public class ServiceJsonBuilder {

    private ServiceJsonBuilder() {}

    public static String build(Service service){
        return switch (service.getTipoProtocolo()) {

            case "TCP" -> buildTcp(service);
            case "UDP" -> buildUdp(service);
            case "ICMP" -> buildIcmp(service);

            default -> throw new IllegalStateException("Unexpected value: " + service.getTipoProtocolo());
        };
    }

    private static String buildTcp(Service service) {
        return """
            {
              "name": "%s",
              "tcp-portrange": "%s",
              "comment": "%s"
            }
            """.formatted(
                service.getNombre(),
                service.getDestinationPort(),
                sanitize(service.getComentario())
        );
    }

    private static String buildUdp(Service service) {
        return """
            {
              "name": "%s",
              "udp-portrange": "%s",
              "comment": "%s"
            }
            """.formatted(
                service.getNombre(),
                service.getDestinationPort(),
                sanitize(service.getComentario())
        );
    }

    private static String buildIcmp(Service service) {
        return """
            {
              "name": "%s",
              "protocol": "ICMP",
              "comment": "%s"
            }
            """.formatted(
                service.getNombre(),
                sanitize(service.getComentario())
        );
    }

    private static String sanitize(String value) {
        return value == null ? "" : value.replace("\"", "");
    }
}
