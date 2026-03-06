import { defineStore } from 'pinia'
import { useUserStore } from "@/stores/userStore";

import {
  obtenerInterfacesPorDispositivo,
  crearInterfaz,
  eliminarInterfaz as apiEliminarInterfaz
} from '@/services/interfazService'

export const useInterfazStore = defineStore('interfaz', {
  state: () => ({
    reglas: [],
    cargando: false,
    mensaje: "",
  }),

  actions: {
    async cargarInterfaces(dispositivoId) {
      this.cargando = true
      const res = await obtenerInterfacesPorDispositivo(dispositivoId)
      this.reglas = res.data
      this.cargando = false
    },

    async crearInterfaz(interfaz) {
      try {
        const userStore = useUserStore();

        if (!userStore.autenticado) {
          this.mensaje = "Debes iniciar sesión";
          return;
        }

        const res = await crearInterfaz(interfaz);

        this.mensaje = "Interfaz creada correctamente";
        return res.data;

      } catch (error) {
        console.error(error);
        this.mensaje = "Error al crear la interfaz";
        throw error;
      }
    },


    async eliminarInterfaz(id) {
       try {
        await apiEliminarInterfaz(id)
      } catch (error) {
        console.error("Error eliminando interfaz",error)
        this.mensaje = "Error eliminando interfaz"
      }
  }
  }
})