<template>
  <v-container>
    <h2>Reglas de Firewall</h2>

    <ReglaFirewallForm
      :dispositivo-id="dispositivoId"
      @creada="recargar"
    />

    <ReglaFirewallList :reglas="reglaStore.reglas" />
  </v-container>
</template>

<script setup>
import { useReglaFirewallStore } from '@/stores/reglafirewallStore'
import ReglaFirewallForm from '@/components/ReglaFirewallForm.vue'
import ReglaFirewallList from '@/components/ReglaFirewallList.vue'
import { useDispositivoSeleccionadoStore } from "@/stores/dispositivoSeleccionadoStore";

const seleccionadoStore = useDispositivoSeleccionadoStore();
const dispositivoId = seleccionadoStore.id;

const reglaStore = useReglaFirewallStore()

/*function recargar() {
  reglaStore.cargarReglas(dispositivoId)
}

onMounted(() => {
  reglaStore.cargarReglas(dispositivoId)
})*/
</script>