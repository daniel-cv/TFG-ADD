import { defineStore } from 'pinia'
import { useUserStore } from "@/stores/userStore";

import {
  obtenerAddressesPorDispositivo,
  crearAddress
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

    async crearAddress(address) {
      try {
        const userStore = useUserStore();

        if (!userStore.autenticado) {
          this.mensaje = "Debes iniciar sesión";
          return;
        }

        const res = await crearAddress(address);

        this.mensaje = "Address creada correctamente";
        return res.data;

      } catch (error) {
        console.error(error);
        this.mensaje = "Error al crear la address";
        throw error;
      }
    }

  }
})