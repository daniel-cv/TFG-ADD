<template>
  <v-card class="pa-4 mb-4">
    <v-card-title>Nueva regla</v-card-title>

    <v-text-field v-model="regla.nombre" label="Nombre" />

    <v-text-field v-model="regla.origen" label="Origen" />
    <v-text-field v-model="regla.destino" label="Destino" />
    <v-text-field v-model="regla.iporigen" label="ipOrigen" />
    <v-text-field v-model="regla.ipdestino" label="ipDestino" />

    <v-select
      v-model="regla.servicio"
      :items="['HTTP', 'HTTPS', 'ALL']"
      label="Servicio"
    />


    <v-select
      v-model="regla.nat"
      :items="['disable', 'enable']"
      label="NAT"
    />

    <v-btn color="primary" @click="guardar">Guardar</v-btn>
  </v-card>
</template>

<script setup>
import { ref } from 'vue'
import { useReglaFirewallStore } from '@/stores/reglafirewallStore'

const emit = defineEmits(['creada'])
const props = defineProps({
  dispositivoId: 11
})

const store = useReglaFirewallStore()

const regla = ref({
  nombre: '',
  origen: '',
  destino: '',
  iporigen: '',
  ipdestino: '',
  servicio: 'ALL',
  schedule :'always',
  action: 'accept',
  nat: 'disable',
  habilitada: true,
  dispositivo: { id: 12 }
})

async function guardar() {
  await store.crearRegla(regla.value)
  emit('creada')
}
</script>