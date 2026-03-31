import { defineStore } from 'pinia'
import { useUserStore } from "@/stores/userStore";

import {
  obtenerReglasPorDispositivo,
  crearReglaFirewall,
  eliminarReglaFirewall,
  actualizarReglaFirewall
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
      try {
        const res = await obtenerReglasPorDispositivo(dispositivoId)
        this.reglas = res.data
      } catch (error) {
        console.error(error)
        this.mensaje = "Error cargando las reglas"
      } finally {
        this.cargando = false
      }
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
    },

    async eliminarRegla(reglaId) {
  try {
    const userStore = useUserStore()
    if (!userStore.autenticado) {
      this.mensaje = "Debes iniciar sesión"
      return
    }

    await eliminarReglaFirewall(reglaId)
    this.mensaje = "Regla eliminada correctamente"

  } catch (error) {
    console.error("Error al eliminar la regla",error)
    this.mensaje = "Error eliminando regla"
  }
},

async actualizarRegla(id, regla) {
      try {
        const res = await actualizarReglaFirewall(id, regla)
        const index = this.reglas.findIndex(a => a.id === id)
        if (index !== -1) this.reglas[index] = res.data
        return res.data
      } catch (error) {
        console.error("Error actualizando regla", error)
        throw error
      }
    }
  }
})
