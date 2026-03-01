import { defineStore } from "pinia";

export const useDispositivoSeleccionadoStore = defineStore("dispositivoSeleccionado", {
  state: () => ({
    dispositivo: null,
  }),

  actions: {
    seleccionar(dispositivo) {
      this.dispositivo = dispositivo;
    },
    limpiar() {
      this.dispositivo = null;
    }
  }
});
