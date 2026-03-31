import { defineStore } from 'pinia'
import { useUserStore } from "@/stores/userStore";

import {
  obtenerServicesPorDispositivo,
  crearService,
  eliminarService as eliminarServiceService,
  actualizarService as actualizarServiceService
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
            await eliminarServiceService(id)  
            this.services = this.services.filter(a => a.id !== id) 
          } catch (error) {
            console.error("Error eliminando service", error)
            this.mensaje = "Error eliminando service"
          }
        },

    async actualizarService(id, service) {
          try {
            const res = await actualizarServiceService(id, service)
            const index = this.services.findIndex(a => a.id === id)
            if (index !== -1) this.services[index] = res.data
            return res.data
          } catch (error) {
            console.error("Error actualizando service", error)
            throw error
          }
        }
  }
})