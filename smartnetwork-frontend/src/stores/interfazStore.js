import { defineStore } from 'pinia'
import { useUserStore } from "@/stores/userStore"

import {
  obtenerInterfacesUsuario,
  obtenerInterfacesPorDispositivo,
  crearInterfaz as apiCrearInterfaz,
  crearInterfazBasica as apiCrearInterfazBasica,
  actualizarInterfaz as apiActualizarInterfaz,
  eliminarInterfaz as apiEliminarInterfaz,
  asignarInterfaz as apiAsignarInterfaz
} from '@/services/interfazService'

export const useInterfazStore = defineStore('interfaz', {
  state: () => ({
    interfaces: [],
    cargando: false,
    mensaje: ""
  }),

  actions: {
    async cargarInterfacesUsuario() {
      this.cargando = true
      try {
        const res = await obtenerInterfacesUsuario()
        this.interfaces = res.data
      } catch (error) {
        console.error("Error cargando interfaces", error)
        this.mensaje = "Error al cargar interfaces"
      } finally {
        this.cargando = false
      }
    },

    async cargarInterfaces(dispositivoId) {
      this.cargando = true
      try {
        const res = await obtenerInterfacesPorDispositivo(dispositivoId)
        this.interfaces = res.data
      } catch (error) {
        console.error("Error cargando interfaces", error)
        this.mensaje = "Error al cargar interfaces"
      } finally {
        this.cargando = false
      }
    },

    async crearInterfaz(interfaz) {
      try {
        const userStore = useUserStore()

        if (!userStore.autenticado) {
          this.mensaje = "Debes iniciar sesión"
          return
        }

        const res = await apiCrearInterfaz(interfaz)
        this.interfaces.push(res.data)

        this.mensaje = "Interfaz creada correctamente"
        return res.data

      } catch (error) {
        console.error(error)
        this.mensaje = "Error al crear la interfaz"
        throw error
      }
    },

    async crearInterfazBasica(interfaz) {
      try {
        const res = await apiCrearInterfazBasica(interfaz)
        this.interfaces.push(res.data)

        this.mensaje = "Interfaz creada (modo básico)"
        return res.data

      } catch (error) {
        console.error(error)
        this.mensaje = "Error al crear interfaz básica"
        throw error
      }
    },

    async actualizarInterfaz(id, interfaz) {
      try {
        const res = await apiActualizarInterfaz(id, interfaz)

        const index = this.interfaces.findIndex(i => i.id === id)
        if (index !== -1) this.interfaces[index] = res.data

        this.mensaje = "Interfaz actualizada correctamente"
        return res.data

      } catch (error) {
        console.error("Error actualizando interfaz", error)
        this.mensaje = "Error al actualizar la interfaz"
        throw error
      }
    },

    async eliminarInterfaz(id) {
      try {
        await apiEliminarInterfaz(id)

        this.interfaces = this.interfaces.filter(i => i.id !== id)

        this.mensaje = "Interfaz eliminada correctamente"

      } catch (error) {
        console.error("Error eliminando interfaz", error)
        this.mensaje = "Error al eliminar la interfaz"
        throw error
      }
    },

    async asignarInterfaz(interfazId, dispositivosIds) {
      try {
        await apiAsignarInterfaz(interfazId, dispositivosIds)
        this.mensaje = "Interfaz asignada correctamente"
      } catch (error) {
        console.error("Error asignando interfaz", error)
        this.mensaje = "Error al asignar interfaz"
        throw error
      }
    }

  }
})
