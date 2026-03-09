import { defineStore } from 'pinia'
import { useUserStore } from "@/stores/userStore";

import {
  obtenerUsuarioFirewallPorDispositivo,
  crearUsuarioFirewall,
  eliminarUsuarioFirewall as apiEliminarUsuarioFirewall,
  actualizarUsuarioFirewall as apiActualizarUsuarioFirewall  // <- nuevo
} from '@/services/usuarioFirewallService'

export const useUsuarioFirewallStore = defineStore('usuarioFirewall', {
  state: () => ({
    usuarios: [],
    cargando: false,
    mensaje: "",
  }),

  actions: {

    async cargarUsuarioFirewall(dispositivoId) {
      this.cargando = true
      const res = await obtenerUsuarioFirewallPorDispositivo(dispositivoId)
      this.usuarios = res.data
      this.cargando = false
    },

    async crearUsuarioFirewall(usuarioFirewall) {
      try {
        const userStore = useUserStore();

        if (!userStore.autenticado) {
          this.mensaje = "Debes iniciar sesión";
          return;
        }

        const res = await crearUsuarioFirewall(usuarioFirewall);
        this.mensaje = "Usuario Firewall creado correctamente";
        return res.data;

      } catch (error) {
        console.error(error);
        this.mensaje = "Error al crear el usuario firewall";
        throw error;
      }
    },

    async eliminarUsuarioFirewall(id) {
      try {
        await apiEliminarUsuarioFirewall(id)
        this.usuarios = this.usuarios.filter(u => u.id !== id)
      } catch (error) {
        console.error("Error eliminando usuario firewall:", error)
        this.mensaje = "Error eliminando usuario firewall"
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
        console.error("Error actualizando usuario firewall", error)
        this.mensaje = "Error al actualizar usuario firewall"
        throw error
      }
    }

  }
})