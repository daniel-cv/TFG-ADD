<template>
  <v-form @submit.prevent="handleSubmit">
    <v-text-field
      v-model="name"
      label="Nombre"
      prepend-inner-icon="mdi-lan"
      variant="outlined"
      class="mb-3"
      :disabled="interfazEdit"
      required
    />

    <v-select
      v-model="tipo"
      :items="['vlan']"
      label="Tipo"
      variant="outlined"
      class="mb-3"
      clearable
      :disabled="interfazEdit"
      required
    />
    <v-select
      v-if="tipo === 'vlan'"
      v-model="interfacePadre"
      :items="interfacesDisponibles"
      label="Interfaz padre"
      variant="outlined"
      class="mb-3"
      clearable
      :disabled="interfazEdit"
    />

    <v-text-field
      v-if="tipo === 'vlan'"
      v-model="vlanid"
      label="VLAN ID"
      type="number"
      variant="outlined"
      class="mb-3"
      required
      :disabled="interfazEdit"
    />

    <v-text-field
      v-model="vdom"
      label="VDOM"
      variant="outlined"
      class="mb-3"
      disabled
    />

    <v-select
      v-model="mode"
      :items="['dhcp']"
      label="Modo IP"
      variant="outlined"
      class="mb-3"
      required
    />

    <v-text-field
      v-if="mode === 'static'"
      v-model="ip"
      label="IP / Máscara (ej: 192.168.1.1/24)"
      variant="outlined"
      class="mb-3"
      required
    />

    <v-select
      v-model="allowaccess"
      :items="['ping', 'https', 'ssh']"
      label="Allow Access"
      variant="outlined"
      class="mb-3"
    />

    <v-select
      v-model="role"
      :items="['lan', 'wan', 'dmz']"
      label="Rol"
      variant="outlined"
      class="mb-3"
      required
    />

    <v-textarea
      v-model="description"
      label="Descripción"
      variant="outlined"
      class="mb-3"
    />

    <v-btn color="primary" size="large" block type="submit">
      {{ interfazEdit ? 'Actualizar Interfaz' : 'Crear Interfaz' }}
    </v-btn>

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
import { ref, onMounted } from 'vue'
import { useInterfazStore } from '@/stores/interfazStore'
import { obtenerInterfacesUsuario } from '@/services/interfazService'

const props = defineProps({
  dispositivoId: Number,
  interfazEdit: Object,
  modo: { type: String, default: 'simple' }
})

const emit = defineEmits(['creada', 'cancelar'])
const interfazStore = useInterfazStore()

const name = ref('')
const tipo = ref('')
const interfacePadre = ref(null)
const vlanid = ref(null)
const vdom = ref('root')
const mode = ref('')
const ip = ref('')
const allowaccess = ref('')
const role = ref('')
const description = ref('')
const mensaje = ref('')

const interfacesDisponibles = ref([])

onMounted(async () => {

  if (props.interfazEdit) {

    name.value = props.interfazEdit.name || ''
    tipo.value = props.interfazEdit.tipo || ''
    interfacePadre.value = props.interfazEdit.interfacePadre || null
    vlanid.value = props.interfazEdit.vlanid || null
    vdom.value = props.interfazEdit.vdom || 'root'
    mode.value = props.interfazEdit.mode || ''
    ip.value = props.interfazEdit.ip || ''
    allowaccess.value = props.interfazEdit.allowaccess || ''
    role.value = props.interfazEdit.role || ''
    description.value = props.interfazEdit.description || ''
  }

  const res = await obtenerInterfacesUsuario()

const puertosBase = [
  { title: 'Port1', value: 'port1' },
  { title: 'Port2', value: 'port2' },
  { title: 'Port3', value: 'port3' },
  { title: 'Port4', value: 'port4' }
]

const nombresBase = new Set(
  puertosBase.map(p => p.value.toLowerCase())
)

interfacesDisponibles.value = [
  ...puertosBase,
  ...res.data
    .filter(i => !nombresBase.has(i.name.toLowerCase()))
    .map(i => ({
      title: i.name,
      value: i.name
    }))
]
})

const handleSubmit = async () => {

  const payload = {
    name: name.value,
    tipo: tipo.value,
    interfacePadre: interfacePadre.value,
    vlanid: vlanid.value,
    vdom: vdom.value,
    mode: mode.value,
    ip: ip.value,
    allowaccess: allowaccess.value,
    role: role.value,
    description: description.value,
  }

  try {

    if (props.interfazEdit) {

      if (props.interfazEdit.dispositivosId?.length) {
        payload.dispositivosId = props.interfazEdit.dispositivosId
      }
      else if (props.modo === 'full') {
        payload.dispositivosId = [props.dispositivoId]
      }

      await interfazStore.actualizarInterfaz(
        props.interfazEdit.id,
        payload
      )

      mensaje.value = "Actualizada"
    }

    else {

      if (props.modo === 'full') {
        payload.dispositivosId = [props.dispositivoId]
      }

      await interfazStore.crearInterfaz(payload)

      mensaje.value = "Creada"
    }

    emit('creada')

  } catch (e) {
    console.error(e)
    mensaje.value = "Error"
  }
}
</script>
