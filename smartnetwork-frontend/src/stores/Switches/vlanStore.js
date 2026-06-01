import { defineStore } from 'pinia'
import {
  obtenerVlansPorDispositivo,
  actualizarVlan as apiActualizarVlan,
  crearVlan as apiCrearVlan,
  eliminarVlan as apiEliminarVlan
} from '@/services/Switches/vlanService'

export const useVlanStore = defineStore('vlan', {
  state: () => ({
    vlans: [],
    cargando: false,
    mensaje: ''
  }),

  actions: {
    async cargarVlans(dispositivoId) {
      this.cargando = true
      try {
        const res = await obtenerVlansPorDispositivo(dispositivoId)
        this.vlans = res.data
      } catch (e) {
        console.error(e)
      } finally {
        this.cargando = false
      }
    },

    async crearVlan(vlan) {
      const res = await apiCrearVlan(vlan)
      this.vlans.push(res.data)
    },

    async actualizarVlan(id, vlan) {
      const res = await apiActualizarVlan(id, vlan)

      const index = this.vlans.findIndex(v => v.id === id)
      if (index !== -1) this.vlans[index] = res.data
    },

    async eliminarVlan(id) {
      await apiEliminarVlan(id)
      this.vlans = this.vlans.filter(v => v.id !== id)
    }
  }
})
