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
import { onMounted } from 'vue'
import { useReglaFirewallStore } from '@/stores/reglafirewallStore'
import ReglaFirewallForm from '@/components/ReglaFirewallForm.vue'
import ReglaFirewallList from '@/components/ReglaFirewallList.vue'

const dispositivoId = 1 // luego vendrá de la ruta

const reglaStore = useReglaFirewallStore()

/*function recargar() {
  reglaStore.cargarReglas(dispositivoId)
}

onMounted(() => {
  reglaStore.cargarReglas(dispositivoId)
})*/
</script>