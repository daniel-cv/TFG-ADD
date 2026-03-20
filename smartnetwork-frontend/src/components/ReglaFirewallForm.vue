<template>
  <v-form @submit.prevent="handleSubmit">
    <v-text-field
      v-model="nombre"
      label="Nombre / ID"
      prepend-inner-icon="mdi-label"
      variant="outlined"
      class="mb-3"
      :disabled="!!props.reglaEdit"
      required
    />

    <v-text-field
      v-model="origen"
      label="Interfaz Origen"
      prepend-inner-icon="mdi-login-variant"
      variant="outlined"
      class="mb-3"
    />

    <v-text-field
      v-model="destino"
      label="Interfaz Destino"
      prepend-inner-icon="mdi-logout-variant"
      variant="outlined"
      class="mb-3"
    />

    <v-text-field
      v-model="ipOrigen"
      label="Objeto Dirección Origen"
      prepend-inner-icon="mdi-ip-network"
      variant="outlined"
      class="mb-3"
    />

    <v-text-field
      v-model="ipDestino"
      label="Objeto Dirección Destino"
      prepend-inner-icon="mdi-ip-network-outline"
      variant="outlined"
      class="mb-3"
    />

    <v-select
      v-model="servicio"
      :items="['HTTP', 'HTTPS', 'ALL', 'SSH', 'DNS']"
      label="Servicio"
      prepend-inner-icon="mdi-server"
      variant="outlined"
      class="mb-3"
    />

    <v-select
      v-model="nat"
      :items="['disable', 'enable']"
      label="NAT"
      prepend-inner-icon="mdi-network"
      variant="outlined"
      class="mb-3"
      :disabled="!!props.reglaEdit"
    />

    <v-select
      v-model="action"
      :items="['accept', 'deny']"
      label="Acción"
      prepend-inner-icon="mdi-shield-check"
      variant="outlined"
      class="mb-3"
      :disabled="!!props.reglaEdit"
    />

    <v-btn color="primary" size="large" block type="submit">
      {{ reglaEdit ? 'Actualizar Regla' : 'Crear Regla' }}
    </v-btn>

    <v-btn
      variant="outlined"
      size="large"
      block
      class="mt-2"
      @click="emit('cancelar')"
    >
      Cancelar
    </v-btn>

    <p v-if="mensaje" class="mt-3 text-center">
  {{ mensaje }}
</p>
  </v-form>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useReglaFirewallStore } from '@/stores/reglafirewallStore'
import { useRoute } from 'vue-router'

const props = defineProps({
  reglaEdit: { type: Object, default: null }
})

const emit = defineEmits(['creada', 'cancelar'])
const store = useReglaFirewallStore()
const route = useRoute()
const dispositivoId = Number(route.params.id)

const nombre = ref('')
const origen = ref('')
const destino = ref('')
const ipOrigen = ref('')
const ipDestino = ref('')
const servicio = ref('')
const nat = ref('')
const action = ref('')
const mensaje = ref('')

onMounted(() => {
  if (props.reglaEdit) {
    nombre.value = props.reglaEdit.nombre
    origen.value = props.reglaEdit.origen
    destino.value = props.reglaEdit.destino
    ipOrigen.value = props.reglaEdit.ipOrigen
    ipDestino.value = props.reglaEdit.ipDestino
    servicio.value = props.reglaEdit.servicio
    nat.value = props.reglaEdit.nat
    action.value = props.reglaEdit.action || 'accept'
  }
})

async function handleSubmit() {
  try {
    const payload = {
      nombre: nombre.value,
      origen: origen.value,
      destino: destino.value,
      ipOrigen: ipOrigen.value,
      ipDestino: ipDestino.value,
      servicio: servicio.value,
      nat: nat.value,
      action: action.value,
      dispositivoId: dispositivoId,
      schedule: 'always',
      habilitada: true
    }

    if (props.reglaEdit) {
      await store.actualizarRegla(props.reglaEdit.id, payload)
      mensaje.value = 'Regla actualizada correctamente'
    } else {
      await store.crearRegla(payload)
      mensaje.value = 'Regla creada correctamente'
    }

    setTimeout(() => emit('creada'), 1000)
  } catch (error) {
    mensaje.value = props.reglaEdit ? 'Error al actualizar la regla' : 'Error al crear la regla'
  }
}
</script>
