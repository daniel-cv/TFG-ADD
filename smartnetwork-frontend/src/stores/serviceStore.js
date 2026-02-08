import { defineStore } from 'pinia'
import { useUserStore } from "@/stores/userStore";

import {
  obtenerServicesPorDispositivo,
  crearService
} from '@/services/serviceService'

export const useServiceStore = defineStore('service', {
  state: () => ({
    reglas: [],
    cargando: false,
    mensaje: "",
  }),

  actions: {
    async cargarServices(dispositivoId) {
      this.cargando = true
      const res = await obtenerServicesPorDispositivo(dispositivoId)
      this.reglas = res.data
      this.cargando = false
    },

    async crearService(service) {
      try {
        const userStore = useUserStore();

        if (!userStore.autenticado) {
          this.mensaje = "Debes iniciar sesión";
          return;
        }

        const res = await crearService(service);

        this.mensaje = "Service creado correctamente";
        return res.data;

      } catch (error) {
        console.error(error);
        this.mensaje = "Error al crear el service";
        throw error;
      }
    }

  }
})