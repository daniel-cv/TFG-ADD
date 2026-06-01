import { defineStore } from 'pinia'
import { useUserStore } from "@/stores/userStore";

import {
  obtenerVirtualIpsPorDispositivo,
  crearVirtualIp,
  eliminarVirtualIp as eliminarVirtualIpService,
  actualizarVirtualIp as actualizarVirtualIpService

} from '@/services/VirtualIpService'

export const useVirtualIpStore = defineStore('virtualIp', {
  state: () => ({
    virtualips: [],
    cargando: false,
    mensaje: "",
  }),

  actions: {
    async cargarVirtualIps(dispositivoId) {
      this.cargando = true
      const res = await obtenerVirtualIpsPorDispositivo(dispositivoId)
      this.virtualips = res.data
      this.cargando = false
    },

    async crearVirtualIp(vip) {
      try {
        const userStore = useUserStore();

        if (!userStore.autenticado) {
          this.mensaje = "Debes iniciar sesión";
          return;
        }

        const res = await crearVirtualIp(vip);

        this.mensaje = "VirtualIP creada correctamente";
        return res.data;

      } catch (error) {
        console.error(error);
        this.mensaje = "Error al crear la VirtualIP";
        throw error;
      }
    },

    async eliminarAddress(id) {
          try {
            await eliminarVirtualIpService(id) 
            this.virtualips = this.virtualips.filter(a => a.id !== id) 
          } catch (error) {
            console.error("Error eliminando virtualip", error)
            throw error
          }
        },
    
     async actualizarVirtualIp(id, virtualip) {
          try {
            const res = await actualizarVirtualIpService(id, virtualip)
            const index = this.virtualips.findIndex(a => a.id === id)
            if (index !== -1) this.virtualips[index] = res.data
            return res.data
          } catch (error) {
            console.error("Error actualizando virtualip", error)
            throw error
          }
        }
  }
})
