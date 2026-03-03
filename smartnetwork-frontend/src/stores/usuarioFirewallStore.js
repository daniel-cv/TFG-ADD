import { defineStore } from 'pinia'
import { useUserStore } from "@/stores/userStore";

import {
  obtenerUsuarioFirewallPorDispositivo,
  crearUsuarioFirewall
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
      this.reglas = res.data
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
    }

  }
})