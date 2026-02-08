<template>
  <v-container>
    <h2>Interfaces</h2>

    <InterfazForm
      :dispositivo-id="dispositivoId"
      @creada="recargar"
    />

  </v-container>
</template>

<script setup>
import { useInterfazStore } from '@/stores/interfazStore'
import InterfazForm from '@/components/InterfazForm.vue'
import { useDispositivoSeleccionadoStore } from '@/stores/dispositivoSeleccionadoStore'

const seleccionadoStore = useDispositivoSeleccionadoStore()
const dispositivoId = seleccionadoStore.id

const interfazStore = useInterfazStore()

const recargar = () => {
  console.log("Address creada, recargando lista...");
};
</script>
