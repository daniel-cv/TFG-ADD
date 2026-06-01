import { defineStore } from "pinia";
import { useUserStore } from "@/stores/userStore";
import api from "@/services/api";

export const useDispositivoStore = defineStore("dispositivo", {
  state: () => ({
    dispositivos: [],
    mensaje: "",
  }),

  actions: {
    async getMisDispositivos() {
      try {
        const userStore = useUserStore();

        if (!userStore.autenticado) {
          this.mensaje = "Debes iniciar sesión";
          return;
        }

        const response = await api.get("/api/dispositivos/mios");
        this.dispositivos = response.data;

      } catch (error) {
        console.error(error);
        this.mensaje = "Error al obtener dispositivos";
      }
    },

    async crearNuevoDispositivo(dispositivo) {
      try {
        const userStore = useUserStore();
      if (!userStore.autenticado) {
        this.mensaje = "Debes iniciar sesión";
        return;
      }

      const payload = {
        nombre: dispositivo.nombre,
        tipo: dispositivo.tipo,
        ip: dispositivo.ip,
        puerto: dispositivo.puerto,
        fabricante: dispositivo.fabricante,
        estado: "ONLINE"
      };
      if (dispositivo.tipo === "FIREWALL") {
        payload.token = dispositivo.token;
      }
      if (dispositivo.tipo === "SWITCH") {
        payload.usuarioConexion = dispositivo.usuario;
        payload.passwordConexion = dispositivo.password;
      }

      const response = await api.post("/api/dispositivos/crear", payload);

      this.dispositivos.push(response.data);
      this.mensaje = "Dispositivo creado correctamente";

      return response.data;

    } catch (error) {
      console.error(error);
      this.mensaje = "Error al crear el dispositivo";
      throw error;
    }
    },
    async getDispositivo(id) {
      try {
        const userStore = useUserStore();

        if (!userStore.autenticado) {
          this.mensaje = "Debes iniciar sesión";
          return null;
        }

        const response = await api.get(`/api/dispositivos/${id}`);
        return response.data;

      } catch (error) {
        console.error(error);
        this.mensaje = "Error al obtener el dispositivo";
        return null;
      }
    },

    async eliminarDispositivo(id) {
      try {
        const userStore = useUserStore();

        if (!userStore.autenticado) {
          this.mensaje = "Debes iniciar sesión";
          return;
        }
        await api.delete(`/api/dispositivos/delete/${id}`);
        this.dispositivos = this.dispositivos.filter(d => d.id !== id);
        this.mensaje = "Dispositivo eliminado correctamente";
      } catch (error) {
        console.error(error);
        this.mensaje = "Error al eliminar el dispositivo";
        throw error;
      }
    },
    async editarDispositivo(id, datos) {
      try {
        const response = await api.put(`/api/dispositivos/${id}`, {
          nombre: datos.nombre,
          ip: datos.ip
        });

        const index = this.dispositivos.findIndex(d => d.id === id);

        if (index !== -1) {
          this.dispositivos[index] = response.data
        }

        return response.data

      } catch (error) {
        console.error(error)
        throw error
      }
    }
  }
});
