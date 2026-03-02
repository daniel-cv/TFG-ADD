import { defineStore } from 'pinia'
import { useUserStore } from "@/stores/userStore";

import {
  obtenerVirtualIpsPorDispositivo,
  crearVirtualIp
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
    }

  }
})
