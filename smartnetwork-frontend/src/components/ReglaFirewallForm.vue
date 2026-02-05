<template>
  <v-card class="pa-4 mb-4">
    <v-card-title>Nueva regla</v-card-title>

    <v-text-field v-model="regla.nombre" label="Nombre" />

    <v-text-field v-model="regla.origen" label="Origen" />
    <v-text-field v-model="regla.destino" label="Destino" />
    <v-text-field v-model="regla.ipOrigen" label="ipOrigen" />
    <v-text-field v-model="regla.ipDestino" label="ipDestino" />

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
  <v-text-field v-model="dispositivoId" label="ipDestino" />
</template>

<script setup>
import { ref } from 'vue'
import { useReglaFirewallStore } from '@/stores/reglafirewallStore'
import { useRoute } from "vue-router";

const route = useRoute();
const dispositivoId = Number(route.params.id);
const emit = defineEmits(['creada']);



const store = useReglaFirewallStore()

const regla = ref({
  nombre: '',
  origen: '',
  destino: '',
  ipOrigen: '',
  ipDestino: '',
  servicio: 'ALL',
  schedule :'always',
  action: 'accept',
  nat: 'disable',
  habilitada: true,
  dispositivoId: dispositivoId
})

async function guardar() {
  await store.crearRegla(regla.value)
  emit('creada')
}
</script>