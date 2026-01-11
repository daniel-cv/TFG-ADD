import { defineStore } from "pinia";
import axios from "axios";

// Crear instancia de Axios centralizada
const api = axios.create({
  baseURL: "http://localhost:8080/api/dispositivos", // Base URL de tu backend Spring
  headers: { "Content-Type": "application/json" },
});

export const useDispositivoStore = defineStore("dispositivo", {
    state: () => ({
        dispositivos: [],
        mensaje: "",
    }),
    actions: {
        // Traer todos los dispositivos del usuario actual
        async getMisDispositivos() {
            try {
                const username = "admin"; // usuario predefinido
                const password = "1234";  // contraseña predefinida
            
                const response = await api.get("/mios", {
                    headers: {
                        "Authorization": "Basic " + btoa(username + ":" + password)
                    }
                });
            
                this.dispositivos = response.data;
            
            } catch (error) {
                console.error(error);
                this.mensaje = "Error al obtener dispositivos";
            }
        }
    },
});