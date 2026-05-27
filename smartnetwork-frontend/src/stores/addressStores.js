import { defineStore } from 'pinia'
import { useUserStore } from "@/stores/userStore";

import {
  obtenerAddressesPorDispositivo,
  obtenerAddressPorId,
  crearAddress,
  aplicarAddressToDispositivos,
  crearAddressCompleto,
  eliminarAddress as eliminarAddressService,
  actualizarAddress as actualizarAddressService
  actualizarSinImplementacion as actualizarSinImplementacionService
} from '@/services/addressService'

export const useAddressStore = defineStore('address', {
  state: () => ({
    addresses: [],
    cargando: false,
    mensaje: "",
  }),

  actions: {
    async cargarAddresses(dispositivoId) {
      this.cargando = true
      const res = await obtenerAddressesPorDispositivo(dispositivoId)
      this.addresses = res.data
      this.cargando = false
    },

    async cargarAddressesUsuario() {
      const res = await obtenerAddressesPorUsuario()
      this.addresses = res.data
    },
    
     async crearAddress(address) {
      try {
        const res = await crearAddress(address)
        this.mensaje = "Address creada correctamente"
        return res.data
      } catch (error) {
        console.error(error)
        this.mensaje = "Error al crear la address"
        throw error
      }
    },

    async crearAddressCompleto(address) {
      try {
        const res = await crearAddressCompleto(address)
        this.mensaje = "Address creada y asignada"
        return res.data
      } catch (error) {
        console.error(error)
        this.mensaje = "Error al crear la address"
        throw error
      }
    },

     async eliminarAddress(id, dispositivos) {
      try {
        await eliminarAddressService(id, dispositivos) 
        this.addresses = this.addresses.filter(a => a.id !== id) 
      } catch (error) {
        console.error("Error eliminando address", error)
        throw error
      }
    },

    async actualizarAddress(id, address) {
      try {
        const res = await actualizarAddressService(id, address)
        const index = this.addresses.findIndex(a => a.id === id)
        if (index !== -1) this.addresses[index] = res.data
        return res.data
      } catch (error) {
        console.error("Error actualizando address", error)
        throw error
      }
    },

    async obtenerMisAddresses() {
      try {
        const userStore = useUserStore();
        
        if (!userStore.autenticado) {
          this.mensaje = "Debes iniciar sesión";
          return;
        }

        const res = await obtenerAddressPorId()
        this.addresses = res.data
        this.cargando = false
      } catch (error) {
        console.error("Error obteniendo address por ID", error)
        throw error
      }
    },

    async aplicarAddressToDispositivos(addressId, dispositivosIds) {
      try {
        if (!dispositivosIds.length) {
          this.mensaje = "Selecciona al menos un dispositivo"
          return
        }
      
        await aplicarAddressToDispositivos(addressId, dispositivosIds)
      
        this.mensaje = "Address aplicada correctamente"
      } catch (error) {
        console.error("Error aplicando address", error)
        this.mensaje = "Error al aplicar la address"
        throw error
      }
    },

    async eliminarAddressEnDispositivos(addressId, dispositivosIds) {
  try {

    if (!dispositivosIds.length) {
      this.mensaje = "Selecciona al menos un dispositivo"
      return
    }
    await eliminarAddressService(addressId, dispositivosIds)

    this.mensaje = "Address eliminada correctamente"

  } catch (error) {
    console.error("Error eliminando address", error)
    this.mensaje = "Error al eliminar la address"
    throw error
  }
},
async actualizarAddressSinImplementar(id, address) {
  try {
    const res = await actualizarSinImplementacionService(id, address)
    const index = this.addresses.findIndex(a => a.id === id)
    if (index !== -1) {
      this.addresses[index] = res.data
    }
    this.mensaje = "Address actualizada sin implementación"
    return res.data
  } catch (error) {
    console.error("Error actualizando address sin implementar", error)
    this.mensaje = "Error al actualizar Address"
    throw error
  }
}
  }
})