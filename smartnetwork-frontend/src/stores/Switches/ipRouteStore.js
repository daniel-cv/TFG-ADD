import { defineStore } from 'pinia'
import {
  obtenerIpRoutesPorDispositivo,
  actualizarIpRoute as apiActualizarIpRoute,
  crearIpRoute as apiCrearIpRoute,
  eliminarIpRoute as apiEliminarIpRoute
} from '@/services/Switches/ipRouteService'

export const useIpRouteStore = defineStore('ipRoute', {
  state: () => ({
    ipRoutes: [],
    cargando: false
  }),

  actions: {
      async cargarIpRoutes(dispositivoId) {
        this.cargando = true
        try {
          const res = await obtenerIpRoutesPorDispositivo(dispositivoId)
          this.ipRoutes = res.data
        } catch (e) {
          console.error(e)
        } finally {
          this.cargando = false
        }
      },

      async crearIpRoute(route) {
        const res = await apiCrearIpRoute(route)
        this.ipRoutes.push(res.data)
      },

      async actualizarIpRoute(id, route) {
        const res = await apiActualizarIpRoute(id, route)

        const index = this.ipRoutes.findIndex(r => r.id === id)
        if (index !== -1) this.ipRoutes[index] = res.data
      },

      async eliminarIpRoute(id) {
        await apiEliminarIpRoute(id)
        this.ipRoutes = this.ipRoutes.filter(r => r.id !== id)
      }
    }
})
