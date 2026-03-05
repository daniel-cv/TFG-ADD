import { defineStore } from 'pinia'
import { useUserStore } from "@/stores/userStore";

import {
  obtenerServicesPorDispositivo,
  crearService,
  eliminarService as eliminarServiceService
} from '@/services/serviceService'

export const useServiceStore = defineStore('service', {
  state: () => ({
    services: [],
    cargando: false,
    mensaje: "",
  }),

  actions: {
    async cargarServices(dispositivoId) {
      this.cargando = true
      const res = await obtenerServicesPorDispositivo(dispositivoId)
      this.services = res.data
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
    },
    
    async eliminarService(id) {
          try {
            await eliminarServiceService(id)  // Llamamos al service importado
            this.services = this.services.filter(a => a.id !== id) // opcional: actualizar localmente
          } catch (error) {
            console.error("Error eliminando service", error)
            throw error
          }
        }

  }
})