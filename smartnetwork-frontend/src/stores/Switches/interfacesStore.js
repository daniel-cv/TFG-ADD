import { defineStore } from 'pinia'
import { useUserStore } from "@/stores/userStore"

import {
  obtenerInterfacesPorDispositivo,
  crearInterfaz as apiCrearInterfaz,
  actualizarInterfaz as apiActualizarInterfaz,
  eliminarInterfaz as apiEliminarInterfaz
} from '@/services/Switches/InterfazService'

export const useInterfazStore = defineStore('interfaz', {
  state: () => ({
    interfaces: [],   // 🔥 mejor nombre que "reglas"
    cargando: false,
    mensaje: "",
  }),

  actions: {

    // 🔹 CARGAR
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

    // 🔹 CREAR
    async crearInterfaz(interfaz) {
      try {
        const userStore = useUserStore()

        if (!userStore.autenticado) {
          this.mensaje = "Debes iniciar sesión"
          return
        }

        const res = await apiCrearInterfaz(interfaz)

        // 🔥 añadir al array
        this.interfaces.push(res.data)

        this.mensaje = "Interfaz creada correctamente"
        return res.data

      } catch (error) {
        console.error(error)
        this.mensaje = "Error al crear la interfaz"
        throw error
      }
    },

    // 🔹 ACTUALIZAR
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

    // 🔹 ELIMINAR
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
    }

  }
})
