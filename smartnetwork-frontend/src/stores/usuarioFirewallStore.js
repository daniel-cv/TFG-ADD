import { defineStore } from 'pinia'
import { useUserStore } from "@/stores/userStore"

import {
  obtenerUsuariosFirewallPorDispositivo,
  obtenerUsuariosFirewallPorUsuario,
  crearUsuarioFirewall,
  crearUsuarioFirewallCompleto,
  asignarUsuarioFirewallADispositivos,
  eliminarUsuarioFirewall as apiEliminarUsuarioFirewall,
  actualizarUsuarioFirewall as apiActualizarUsuarioFirewall
} from '@/services/usuarioFirewallService'

export const useUsuarioFirewallStore = defineStore('usuarioFirewall', {
  state: () => ({
    usuarios: [],
    cargando: false,
    mensaje: "",
  }),

  actions: {

   async cargarUsuariosPorDispositivo(dispositivoId) {
  try {

    this.cargando = true

    const res =
      await obtenerUsuariosFirewallPorDispositivo(dispositivoId)

    this.usuarios = res.data

    return res.data

  } catch (error) {

    console.error(
      "Error cargando usuarios firewall por dispositivo:",
      error
    )

    this.mensaje =
      "Error cargando usuarios del dispositivo"

    return []

  } finally {

    this.cargando = false
  }
},

    async cargarUsuariosPorUsuario() {
      try {
        this.cargando = true
        const res = await obtenerUsuariosFirewallPorUsuario()
        this.usuarios = res.data
      } catch (error) {
        console.error("Error cargando usuarios firewall del usuario:", error)
        this.mensaje = "Error cargando tus usuarios firewall"
      } finally {
        this.cargando = false
      }
    },

    async crearUsuarioFirewall(usuarioFirewall) {
      try {
        const userStore = useUserStore()
        if (!userStore.autenticado) {
          this.mensaje = "Debes iniciar sesión"
          return
        }

        const res = await crearUsuarioFirewall(usuarioFirewall)
        this.usuarios.push(res.data)
        this.mensaje = "Usuario Firewall creado correctamente"
        return res.data

      } catch (error) {
        console.error("Error creando usuario firewall:", error)
        this.mensaje = "Error al crear el usuario firewall"
        throw error
      }
    },

    async crearUsuarioFirewallCompleto(usuarioFirewall) {
      try {
        const res = await crearUsuarioFirewallCompleto(usuarioFirewall)
        this.usuarios.push(res)
        this.mensaje = "Usuario Firewall creado correctamente (modo completo)"
        return res
      } catch (error) {
        console.error("Error creando usuario firewall completo:", error)
        this.mensaje = "Error al crear el usuario firewall completo"
        throw error
      }
    },

    async asignarUsuarioFirewallADispositivos(usuarioId, dispositivosIds) {
      try {
        await asignarUsuarioFirewallADispositivos(usuarioId, dispositivosIds)
        this.mensaje = "Usuario asignado correctamente a los dispositivos"
      } catch (error) {
        console.error("Error asignando usuario firewall:", error)
        this.mensaje = "Error al asignar usuario firewall"
        throw error
      }
    },

    async eliminarUsuarioFirewall(id, dispositivosIds) {

  try {

    await apiEliminarUsuarioFirewall(
      id,
      dispositivosIds
    )

    this.usuarios =
      this.usuarios.filter(u => u.id !== id)

    this.mensaje =
      "Usuario Firewall eliminado correctamente"

  } catch (error) {

    console.error(
      "Error eliminando usuario firewall:",
      error
    )

    this.mensaje =
      "Error eliminando usuario firewall"

    throw error
  }
},

    async actualizarUsuarioFirewall(id, payload) {
      try {
        const res = await apiActualizarUsuarioFirewall(id, payload)
        const index = this.usuarios.findIndex(u => u.id === id)
        if (index !== -1) this.usuarios[index] = res.data

        this.mensaje = "Usuario Firewall actualizado correctamente"
        return res.data

      } catch (error) {
        console.error("Error actualizando usuario firewall:", error)
        this.mensaje = "Error al actualizar usuario firewall"
        throw error
      }
    },
    async eliminarUsuarioFirewallEnDispositivos(usuarioId, dispositivosIds) {

  try {

    if (!dispositivosIds.length) {
      this.mensaje = "Selecciona dispositivos"
      return
    }

    await apiEliminarUsuarioFirewall(usuarioId, dispositivosIds)

    this.mensaje = "Usuario eliminado correctamente"

  } catch (error) {

    console.error(error)

    this.mensaje = "Error eliminando usuario"

    throw error
  }
}

  }
})
