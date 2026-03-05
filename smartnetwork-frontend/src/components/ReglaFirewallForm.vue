<template>
  <v-form @submit.prevent="guardar">
    <!-- NOMBRE -->
    <v-text-field
      v-model="regla.nombre"
      label="Nombre"
      prepend-inner-icon="mdi-label"
      variant="outlined"
      class="mb-3"
      required
    />

    <!-- ORIGEN -->
    <v-text-field
      v-model="regla.origen"
      label="Origen"
      prepend-inner-icon="mdi-map-marker"
      variant="outlined"
      class="mb-3"
    />

    <!-- DESTINO -->
    <v-text-field
      v-model="regla.destino"
      label="Destino"
      prepend-inner-icon="mdi-map-marker"
      variant="outlined"
      class="mb-3"
    />

    <!-- IP ORIGEN -->
    <v-text-field
      v-model="regla.ipOrigen"
      label="IP Origen"
      prepend-inner-icon="mdi-ip"
      variant="outlined"
      class="mb-3"
    />

    <!-- IP DESTINO -->
    <v-text-field
      v-model="regla.ipDestino"
      label="IP Destino"
      prepend-inner-icon="mdi-ip"
      variant="outlined"
      class="mb-3"
    />

    <!-- SERVICIO -->
    <v-select
      v-model="regla.servicio"
      :items="['HTTP', 'HTTPS', 'ALL']"
      label="Servicio"
      prepend-inner-icon="mdi-server"
      variant="outlined"
      class="mb-3"
    />

    <!-- NAT -->
    <v-select
      v-model="regla.nat"
      :items="['disable', 'enable']"
      label="NAT"
      prepend-inner-icon="mdi-network"
      variant="outlined"
      class="mb-3"
    />

    <!-- BOTONES -->
    <v-btn color="primary" size="large" block type="submit">
      Guardar
    </v-btn>

    <v-btn
      variant="outlined"
      size="large"
      block
      class="mt-2"
      @click="cancelar"
    >
      Cancelar
    </v-btn>

    <p v-if="mensaje" class="mt-3 text-center">{{ mensaje }}</p>
  </v-form>
</template>

<script setup>
import { ref } from 'vue'
import { useReglaFirewallStore } from '@/stores/reglafirewallStore'
import { useRoute } from 'vue-router'

const route = useRoute()
const dispositivoId = Number(route.params.id)
const emit = defineEmits(['creada', 'cancelar'])
const store = useReglaFirewallStore()

const regla = ref({
  nombre: '',
  origen: '',
  destino: '',
  ipOrigen: '',
  ipDestino: '',
  servicio: 'ALL',
  schedule: 'always',
  action: 'accept',
  nat: 'disable',
  habilitada: true,
  dispositivoId: dispositivoId
})

const mensaje = ref('')

async function guardar() {
  try {
    await store.crearRegla(regla.value)
    mensaje.value = 'Regla creada correctamente'
    emit('creada')
  } catch (error) {
    mensaje.value = 'Error al crear la regla'
  }
}

function cancelar() {
  emit('cancelar')
}
</script>
