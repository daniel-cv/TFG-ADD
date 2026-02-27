package com.smartnetwork.backend.domain.dtos.address;

import com.smartnetwork.backend.domain.Entity.Address;

public class AddressJsonBuilder {

    public static String build(Address address) {
        return switch (address.getType()) {
            case "subnet" -> buildSubnet(address);
            case "ipmask" -> buildIpmask(address);
            case "iprange" -> buildIpRange(address);
            default -> throw new IllegalStateException(
                    "Unexpected address type: " + address.getType()
            );
        };
    }

    // -----------------------------
    // SUBNET
    // -----------------------------
    private static String buildSubnet(Address address) {
        return """
        {
          "name": "%s",
          "type": "subnet",
          "subnet": "%s",
          "comment": "%s"
        }
        """.formatted(
                address.getName(),
                address.getIp(),               // ej: "192.168.1.0 255.255.255.0"
                sanitize(address.getComentario())
        );
    }

    // -----------------------------
    // SINGLE IP (ipmask)
    // -----------------------------
    private static String buildIpmask(Address address) {
        return """
    {
      "name": "%s",
      "type": "ipmask",
      "ip": "%s",
      "mask": "%s",
      "comment": "%s"
    }
    """.formatted(
                address.getName(),
                address.getIp(),        // IP
                address.getIpdestino(), // MÁSCARA
                sanitize(address.getComentario())
        );
    }

    // -----------------------------
    // IP RANGE
    // -----------------------------
    private static String buildIpRange(Address address) {
        System.out.println(address.getName()+"          "+address.getIp()+"          "+address.getIpdestino()+"          "+ address.getComentario());
        return """
        {
          "name": "%s",
          "type": "iprange",
          "start-ip": "%s",
          "end-ip": "%s",
          "comment": "%s"
        }
        """.formatted(
                address.getName(),
                address.getIp(),
                address.getIpdestino(),
                sanitize(address.getComentario())
        );
    }

    private static String sanitize(String value) {
        return value == null ? "" : value.replace("\"", "");
    }
}