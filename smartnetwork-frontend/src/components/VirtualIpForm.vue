<template>
  <v-form>

    <!-- NAME -->
    <v-text-field
      v-model="name"
      label="Nombre"
      prepend-inner-icon="mdi-swap-horizontal"
      variant="outlined"
      class="mb-3"
      :disabled="virtualIpEdit"
      required
    />

    <!-- INTERFAZ EXTERNA (extintf) -->
    <v-select
      v-model="interfazId"
      :items="interfaces"
      label="Interfaz externa"
      variant="outlined"
      class="mb-3"
      required
    />

    <!-- TYPE -->
    <v-select
      v-model="type"
      :items="['static-nat']"
      label="Tipo"
      variant="outlined"
      class="mb-3"
      :disabled="virtualIpEdit"
      required
    />

    <!-- EXTERNAL IP -->
    <v-select
      v-model="externalIp"
      :items="direcciones"
      item-title="title"
      item-value="value"
      label="IP Externa (extip)"
      variant="outlined"
      class="mb-3"
      required
    />

    <!-- INTERNAL IP -->
    <v-select
      v-model="internalIp"
      :items="direcciones"
      item-title="title"
      item-value="value"
      label="IP Interna (mappedip)"
      variant="outlined"
      class="mb-3"
      required
    />

    <!-- COMMENTS -->
    <v-textarea
      v-model="comments"
      label="Comentarios"
      variant="outlined"
      class="mb-3"
    />

    <!-- BOTÓN CREAR / ACTUALIZAR -->
    <v-btn
      color="primary"
      size="large"
      block
      @click="handleSubmit"
    >
       {{ virtualIpEdit ? 'Actualizar VirtualIP' : 'Crear VirtualIP' }}
    </v-btn>

    <!-- BOTÓN CANCELAR -->
    <v-btn
      variant="outlined"
      size="large"
      block
      class="mt-2"
      @click="$emit('cancelar')"
    >
      Cancelar
    </v-btn>

    <p v-if="mensaje" class="mt-3 text-center">{{ mensaje }}</p>

  </v-form>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useVirtualIpStore } from "@/stores/virtualIpStore";
import { useInterfazStore } from "@/stores/interfazStore";
import { obtenerAddressesPorDispositivo } from '@/services/addressService'
import { obtenerInterfacesPorDispositivo } from '@/services/interfazService'

const props = defineProps({
  dispositivoId: { type: Number, required: true },
  virtualIpEdit: { type: Object, default: null }
})

const emit = defineEmits(['creada', 'cancelar'])
const virtualIpStore = useVirtualIpStore()
const interfazStore = useInterfazStore()

const name = ref("")
const type = ref("static-nat")
const externalIp = ref("")
const internalIp = ref("")
const comments = ref("")
const interfazId = ref("")
const interfaces = ref([])
const mensaje = ref("")
const direcciones = ref([])

onMounted(async () => {
  if (props.virtualIpEdit) {
    name.value = props.virtualIpEdit.name
    type.value = props.virtualIpEdit.type || "static-nat"
    externalIp.value = props.virtualIpEdit.externalIp
    internalIp.value = props.virtualIpEdit.internalIp
    comments.value = props.virtualIpEdit.comments
    interfazId.value = props.virtualIpEdit.interfazId
  }

  try {
    const res = await obtenerAddressesPorDispositivo(props.dispositivoId)
    direcciones.value = res.data.map(addr => ({
      title: addr.name,
      value: addr.ip
    }))
  } catch (error) {
    console.error('Error cargando direcciones', error)
  }
  try {
    const res = await obtenerInterfacesPorDispositivo(props.dispositivoId)
    interfaces.value = [
      { title: 'Port1', value: 'port1' },
      { title: 'Port2', value: 'port2' },
      { title: 'Port3', value: 'port3' },
      { title: 'Port4', value: 'port4' },
      ...res.data.map(inter => ({
        title: inter.name,
        value: inter.name      
      }))
    ]
  } catch (error) {
    console.error('Error cargando interfaces', error)
  }
})


const handleSubmit = async () => {
  console.log('Formulario enviado', {
    name: name.value,
    type: type.value,
    externalIp: externalIp.value,
    internalIp: internalIp.value,
    comments: comments.value,
    interfazId: interfazId.value
  })
  emit('creada')
}
</script>