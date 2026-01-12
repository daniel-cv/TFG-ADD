package com.smartnetwork.backend.domain.Entity;
import com.smartnetwork.backend.domain.Enum.AccionFirewall;
import jakarta.persistence.*;

@Entity
@Table(name = "reglas_firewall")
public class ReglaFirewall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Asociación al firewall (Dispositivo)
    @ManyToOne
    @JoinColumn(name = "dispositivo_id", nullable = false)
    private Dispositivo dispositivo;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String origen;   // IP / red / objeto

    @Column(nullable = false)
    private String destino;  // IP / red / objeto

    @Column(nullable = false)
    private String iporigen;   // IP / red / objeto

    @Column(nullable = false)
    private String ipdestino;  // IP / red / objeto

    @Column(nullable = false)
    private String servicio; // HTTP, HTTPS, ALL...

    private boolean habilitada;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Dispositivo getDispositivo() {
        return dispositivo;
    }

    public void setDispositivo(Dispositivo dispositivo) {
        this.dispositivo = dispositivo;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public boolean isHabilitada() {
        return habilitada;
    }

    public void setHabilitada(boolean habilitada) {
        this.habilitada = habilitada;
    }

    public String getServicio() {
        return servicio;
    }

    public void setServicio(String servicio) {
        this.servicio = servicio;
    }

    public String getIpdestino() {
        return ipdestino;
    }

    public void setIpdestino(String ipdestino) {
        this.ipdestino = ipdestino;
    }

    public String getIporigen() {
        return iporigen;
    }

    public void setIporigen(String iporigen) {
        this.iporigen = iporigen;
    }
}
