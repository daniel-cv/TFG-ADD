// stores/Switches/aclStore.ts
import { defineStore } from 'pinia'
import {
  obtenerAclsPorDispositivo,
  crearAcl as apiCrearAcl,
  eliminarAcl as apiEliminarAcl,
  agregarRegla as apiAgregarRegla,
  eliminarRegla as apiEliminarRegla
} from '@/services/Switches/AclService'

export const useAclStore = defineStore('acl', {
  state: () => ({
    acls: [],
    cargando: false
  }),

  actions: {
    async cargarAcls(dispositivoId) {
      this.cargando = true
      try {
        const res = await obtenerAclsPorDispositivo(dispositivoId)
        this.acls = res.data
      } finally {
        this.cargando = false
      }
    },

    async crearAcl(acl) {
      const res = await apiCrearAcl(acl)
      this.acls.push(res.data)
    },

    async eliminarAcl(id) {
      await apiEliminarAcl(id)
      this.acls = this.acls.filter(a => a.id !== id)
    },

    async agregarRegla(aclId, regla) {
      await apiAgregarRegla(aclId, regla)
    },

    async eliminarRegla(id) {
      await apiEliminarRegla(id)
    }
  }
})
