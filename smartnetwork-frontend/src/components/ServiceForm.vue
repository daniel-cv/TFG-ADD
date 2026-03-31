```vue
<template>
  <v-form @submit.prevent="handleSubmitService">
    
    <!-- NAME -->
    <v-text-field
      v-model="name"
      label="Nombre del Servicio"
      prepend-inner-icon="mdi-label"
      variant="outlined"
      class="mb-3"
      :disabled="serviceEdit"
      required
    />

    <!-- PROTOCOLO -->
    <v-select
      v-model="protocol"
      :items="protocolos"
      label="Protocolo"
      prepend-inner-icon="mdi-swap-horizontal"
      variant="outlined"
      class="mb-3"
      required
    />

    <v-select
      v-if="protocol === 'TCP' || protocol === 'UDP'"
      v-model="address"
      :items="direcciones"
      label="Dirección IP (ej: 192.168.1.1)"  
      prepend-inner-icon="mdi-lan-connect"
      variant="outlined"
      class="mb-3"
    />

    <!-- PORT RANGE -->
    <v-text-field
      v-if="protocol === 'TCP' || protocol === 'UDP'"
      v-model="portRange"
      label="Rango de Puertos (ej: 53 o 1000-2000)"
      prepend-inner-icon="mdi-lan-connect"
      variant="outlined"
      class="mb-3"
      required
    />

    <!-- COMENTARIO -->
    <v-textarea
      v-model="comentario"
      label="Comentario"
      prepend-inner-icon="mdi-comment"
      variant="outlined"
      class="mb-3"
    />

    <v-btn color="primary" size="large" block type="submit">
      {{ serviceEdit ? 'Actualizar Service' : 'Crear Service' }}
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

    <p v-if="mensaje" class="mt-3 text-center">{{ mensaje }}</p>
  </v-form>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useRoute } from "vue-router";
import { useServiceStore } from "@/stores/serviceStore";
import { obtenerAddressesPorDispositivo } from '@/services/addressService'

const props = defineProps({
  serviceEdit: { type: Object, default: null }
})

const emit = defineEmits(['creada', 'cancelar'])
const serviceStore = useServiceStore()
const route = useRoute()
const dispositivoId = Number(route.params.id)

const name = ref("")
const protocol = ref("")
const address = ref("")
const portRange = ref("")
const comentario = ref("")
const mensaje = ref("")
const protocolos = ["TCP", "UDP", "ICMP"]
const direcciones = ref([])

const handleSubmitService = async () => {
  try {
    if (props.serviceEdit) {
      await serviceStore.updateService({
        id: props.serviceEdit.id,
        nombre: name.value,
        tipoProtocolo: protocol.value,
        ip: address.value,
        destinationPort: portRange.value,
        comentario: comentario.value
      })
    } else {
      await serviceStore.crearService({
        nombre: name.value,
        tipoProtocolo: protocol.value,
        ip: address.value,
        destinationPort: portRange.value,
        comentario: comentario.value,
        dispositivoId: dispositivoId
      })
    }

    mensaje.value = "Service creado correctamente"
    emit('creada')

  } catch (error) {
    console.error("Error al crear el service:", error)
    mensaje.value = "Error al crearel service"
  }
}

onMounted(async () => {
  if (props.serviceEdit) {
    name.value = props.serviceEdit.nombre
    protocol.value = props.serviceEdit.tipoProtocolo
    address.value = props.serviceEdit.ip
    portRange.value = props.serviceEdit.destinationPort
    comentario.value = props.serviceEdit.comentario
  }

  try {
    const res = await obtenerAddressesPorDispositivo(dispositivoId)
    direcciones.value = res.data.map(addr => ({
      title: addr.name,
      value: addr.ip
    }))
  } catch (error) {
    console.error('Error cargando direcciones', error)
  }
})
</script>
```
