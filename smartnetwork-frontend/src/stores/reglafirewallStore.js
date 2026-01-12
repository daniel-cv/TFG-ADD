import { defineStore } from 'pinia'
import {
  obtenerReglasPorDispositivo,
  crearReglaFirewall
} from '@/services/reglaFirewallService'

export const useReglaFirewallStore = defineStore('reglaFirewall', {
  state: () => ({
    reglas: [],
    cargando: false
  }),

  actions: {
    async cargarReglas(dispositivoId) {
      this.cargando = true
      const res = await obtenerReglasPorDispositivo(dispositivoId)
      this.reglas = res.data
      this.cargando = false
    },

    async crearRegla(regla) {
      await crearReglaFirewall(regla)
    }
  }
})