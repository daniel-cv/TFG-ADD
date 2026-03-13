<template>
  <v-form @submit.prevent="handleSubmit">

    <!-- NAME -->
    <v-text-field
      v-model="name"
      label="Nombre"
      prepend-inner-icon="mdi-lan"
      variant="outlined"
      class="mb-3"
      :disabled="interfazEdit"
      required
    />

    <!-- TIPO -->
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
<!--VLAN ID-->
    <v-select
      v-if="tipo === 'vlan'"
      v-model="interfacePadre"
      :items="interfacesDisponibles"
      item-title="name"
      item-value="name"
      label="Interfaz padre"
      variant="outlined"
      class="mb-3"
      clearable
      :disabled="interfazEdit"  
    />
<!-- INTERFAZ PADRE -->
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

    <!-- VDOM -->
    <v-text-field
      v-model="vdom"
      label="VDOM"
      variant="outlined"
      class="mb-3"
      disabled
    />

    <!-- MODE -->
    <v-select
      v-model="mode"
      :items="['dhcp']"
      label="Modo IP"
      variant="outlined"
      class="mb-3"
      required
    />

    <!-- IP -->
    <v-text-field
      v-if="mode === 'static'"
      v-model="ip"
      label="IP / Máscara (ej: 192.168.1.1/24)"
      variant="outlined"
      class="mb-3"
      required
    />

    <!-- ALLOW ACCESS -->
    <v-select
      v-model="allowaccess"
      :items="['ping', 'https', 'ssh']"
      label="Allow Access"
      variant="outlined"
      class="mb-3"
    />

    <!-- ROLE -->
    <v-select
      v-model="role"
      :items="['lan', 'wan', 'dmz']"
      label="Rol"
      variant="outlined"
      class="mb-3"
      required
    />

    <!-- DESCRIPTION -->
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
import { ref, onMounted, watch } from 'vue'
import { useInterfazStore } from '@/stores/interfazStore'

const props = defineProps({
  dispositivoId: { type: Number, required: true },
  interfazEdit: { type: Object, default: null }
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

const interfacesDisponibles = ref(['port1','port2','port3','port4'])

onMounted(() => {
  if (props.interfazEdit) {
    name.value = props.interfazEdit.name
    tipo.value = props.interfazEdit.tipo
    interfacePadre.value = props.interfazEdit.interfacePadre
    vlanid.value = props.interfazEdit.vlanid
    vdom.value = props.interfazEdit.vdom
    mode.value = props.interfazEdit.mode
    ip.value = props.interfazEdit.ip
    allowaccess.value = props.interfazEdit.allowaccess
    role.value = props.interfazEdit.role
    description.value = props.interfazEdit.description
  }
})

watch(tipo, t => {
  if (t !== 'vlan') {
    interfacePadre.value = null
    vlanid.value = null
  }
})
watch(mode, m => {
  if (m !== 'static') ip.value = null
})

const handleSubmit = async () => {
  try {
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
      dispositivoId: props.dispositivoId
    }

    if (props.interfazEdit) {
      await interfazStore.actualizarInterfaz(props.interfazEdit.id, payload)
      mensaje.value = 'Interfaz actualizada correctamente'
    } else {
      await interfazStore.crearInterfaz(payload)
      mensaje.value = 'Interfaz creada correctamente'
    }

    emit('creada')
  } catch (error) {
    mensaje.value = props.interfazEdit ? 'Error al actualizar interfaz' : 'Error al crear interfaz'
  }
}
</script>