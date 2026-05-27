
import { defineStore } from 'pinia'
import { useUserStore } from "@/stores/userStore";

import {
  obtenerReglasPorDispositivo,
  obtenerReglasUsuario,
  crearReglaFirewall,
  crearReglaFirewallCompleta,
  eliminarReglaFirewall,
  actualizarReglaFirewall,
  asignarReglaADispositivos
} from '@/services/reglaFirewallService'

export const useReglaFirewallStore =
defineStore('reglaFirewall', {
  state: () => ({
    reglas: [],
    cargando: false,
    mensaje: "",
  }),
  actions: {
    async cargarReglas(dispositivoId) {
      this.cargando = true
      try {
        const res =
          await obtenerReglasPorDispositivo(
            dispositivoId
          )
        this.reglas = res.data
      } catch (error) {
        console.error(error)
        this.mensaje =
          "Error cargando las reglas"
      } finally {
        this.cargando = false
      }
    },
    async obtenerReglasDispositivo( dispositivoId) {
      const res = await obtenerReglasPorDispositivo( dispositivoId)
      return res.data
    },

    async cargarReglasUsuario() {
      this.cargando = true
      try {
        const res = await obtenerReglasUsuario()
        this.reglas = res.data
      } catch (error) {
        console.error(error)
        this.mensaje ="Error cargando reglas del usuario"
      } finally {
        this.cargando = false
      }
    },

    async crearRegla(regla) {
      try {
        const userStore = useUserStore();
        if (!userStore.autenticado) {
          this.mensaje ="Debes iniciar sesión";
          return;
        }
        const res =await crearReglaFirewall(regla);
        this.reglas.push(res.data)
        this.mensaje ="Regla creada correctamente";
        return res.data;
      } catch (error) {
        console.error(error);
        this.mensaje ="Error al crear la regla";
        throw error;
      }
    },

    async crearReglaCompleta(regla) {
      try {
        const res = await crearReglaFirewallCompleta( regla)
        this.reglas.push(res.data)
        return res.data
      } catch (error) {
        console.error(error)
        throw error
      }
    },

    async eliminarRegla(reglaId,dispositivosIds) {
      try {
        const userStore = useUserStore()
        if (!userStore.autenticado) {
          this.mensaje = "Debes iniciar sesión"
          return
        }

        await eliminarReglaFirewall(reglaId, dispositivosIds)
        this.reglas =this.reglas.filter( r => r.id !== reglaId)
        this.mensaje ="Regla eliminada correctamente"
      } catch (error) {
        console.error(
          "Error eliminando regla",
          error
        )
        this.mensaje =
          "Error eliminando regla"
        throw error
      }
    },

    async eliminarReglaEnDispositivos(reglaId,dispositivosIds) {
      try {
        await eliminarReglaFirewall(reglaId,dispositivosIds)
        this.mensaje ="Regla eliminada correctamente"
      } catch (error) {
        console.error(error)
        throw error
      }
    },

    async actualizarRegla(id, regla) {
      try {
        const userStore = useUserStore()
        if (!userStore.autenticado) {
          this.mensaje ="Debes iniciar sesión"
          return
        }
        const res =
          await actualizarReglaFirewall(id,regla)
        const index =
          this.reglas.findIndex(r => r.id === id)
        if (index !== -1) {
          this.reglas[index] = res.data
        }
        this.mensaje ="Regla actualizada correctamente"
        return res.data
      } catch (error) {
        console.error(
          "Error actualizando regla",
          error
        )
        this.mensaje ="Error actualizando regla"
        throw error
      }
    },

    async asignarRegla(reglaId,dispositivosIds ) {
      try {
        await asignarReglaADispositivos(
          reglaId,
          dispositivosIds
        )
        this.mensaje ="Regla asignada correctamente"
      } catch (error) {
        console.error(
         "Error asignando regla",
          error
        )
        this.mensaje =
          "Error asignando regla"
        throw error
      }
    }
  }
})