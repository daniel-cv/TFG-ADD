import { defineStore } from 'pinia'
import {
  obtenerServicesPorDispositivo,
  obtenerServicesPorUsuario,
  crearService,
  crearServiceCompleto,
  asignarService,
  eliminarService as eliminarServiceApi,
  actualizarService as actualizarServiceApi,
  obtenerServicePorId
} from '@/services/serviceService'

export const useServiceStore = defineStore('service', {
  state: () => ({
    services: [],
    cargando: false,
    mensaje: "",
  }),

  actions: {

    // 🔥 LISTAR POR DISPOSITIVO
    async cargarServices(dispositivoId) {
      this.cargando = true
      try {
        const res = await obtenerServicesPorDispositivo(dispositivoId)
        this.services = res.data
      } finally {
        this.cargando = false
      }
    },

    // 🔥 LISTAR POR USUARIO
    async cargarServicesUsuario() {
      const res = await obtenerServicesPorUsuario()
      this.services = res.data
    },

    // 🔥 CREAR SIMPLE
    async crearService(data) {
      const res = await crearService(data)
      return res.data
    },

    // 🔥 CREAR FULL (crear + asignar)
    async crearServiceCompleto(data) {
      const res = await crearServiceCompleto(data)
      return res.data
    },

    // 🔥 ASIGNAR SERVICE A DISPOSITIVOS
    async asignarService(serviceId, dispositivosIds) {
      await asignarService(serviceId, dispositivosIds)
    },

    // 🔥 OBTENER SERVICE POR ID + DISPOSITIVO
    async obtenerService(serviceId, dispositivoId) {
      const res = await obtenerServicePorId(serviceId, dispositivoId)
      return res.data
    },

    // 🔥 ACTUALIZAR SERVICE
    async actualizarService(serviceId, dispositivoId, data) {
      const res = await actualizarServiceApi(serviceId, dispositivoId, data)

      const index = this.services.findIndex(s => s.id === serviceId)
      if (index !== -1) this.services[index] = res.data

      return res.data
    },

    // 🔥 ELIMINAR SERVICE
    async eliminarService(id) {
      await eliminarServiceApi(id)
      this.services = this.services.filter(s => s.id !== id)
    }
  }
})
