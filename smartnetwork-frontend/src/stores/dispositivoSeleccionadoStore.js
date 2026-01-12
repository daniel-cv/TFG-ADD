import { defineStore } from "pinia";

export const useDispositivoSeleccionadoStore = defineStore("dispositivoSeleccionado", {
  state: () => ({
    id: null,
  }),

  actions: {
    seleccionar(id) {
      this.id = id;
    },
    limpiar() {
      this.id = null;
    }
  }
});
