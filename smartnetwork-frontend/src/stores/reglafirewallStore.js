import { defineStore } from 'pinia'
import { useUserStore } from "@/stores/userStore";

import {
  obtenerReglasPorDispositivo,
  crearReglaFirewall
} from '@/services/reglaFirewallService'

export const useReglaFirewallStore = defineStore('reglaFirewall', {
  state: () => ({
    reglas: [],
    cargando: false,
    mensaje: "",
  }),

  actions: {
    async cargarReglas(dispositivoId) {
      this.cargando = true
      const res = await obtenerReglasPorDispositivo(dispositivoId)
      this.reglas = res.data
      this.cargando = false
    },

    async crearRegla(regla) {
      try {
        const userStore = useUserStore();

        if (!userStore.autenticado) {
          this.mensaje = "Debes iniciar sesión";
          return;
        }

        const res = await crearReglaFirewall(regla);

        this.mensaje = "Regla creada correctamente";
        return res.data;

      } catch (error) {
        console.error(error);
        this.mensaje = "Error al crear la regla";
        throw error;
      }
    }

  }
})