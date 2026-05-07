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
<!-- INTERFAZ PADRE -->
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
<!--VLAN ID-->
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
const interfacesDisponibles = ref([])
const mensaje = ref('')

onMounted(async () => {

  // 🔥 RELLENAR FORMULARIO EN EDIT
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

  interfacesDisponibles.value = res.data.map(i => ({
    title: i.name,
    value: i.name
  }))
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

    // ================= EDITAR =================
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

    // ================= CREAR =================
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
