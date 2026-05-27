<template>
  <div class="form-container">
    <h3>Editar Interfaz</h3>
    <v-switch label="Estado de interfaz" v-model="form.enabled" />
    <v-text-field label="Nombre" v-model="form.name" disabled />
    <v-text-field label="Estado" v-model="form.estado" disabled />
    <v-select
      label="Modo"
      :items="['bridged', 'routed']"
      v-model="form.mode"
    />
    <v-text-field label="Descripción" v-model="form.descripcion" />
    <v-text-field
      label="VLAN Access"
      v-model="form.vlanAccess"
      type="number"
    />
    <v-text-field
      label="VLANs Trunk (ej: 10,20,30)"
      v-model="vlansTrunkInput"
    />
    <v-select
      label="ACL IN"
      :items="aclOptions"
      v-model="form.aclIn"
      clearable
    />
    <v-btn color="warning" @click="quitarAcl">
      Quitar ACL
    </v-btn>
    <div class="actions">
      <v-btn color="primary" @click="guardar">
        Guardar cambios
      </v-btn>
      <v-btn color="grey" @click="$emit('cancelar')">
        Cancelar
      </v-btn>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useInterfazStore } from '@/stores/Switches/interfacesStore'
import { useAclStore } from '@/stores/Switches/aclStore'
import { useDispositivoSeleccionadoStore } from '@/stores/dispositivoSeleccionadoStore'

const interfazStore = useInterfazStore()
const aclStore = useAclStore()
const dispositivoStore = useDispositivoSeleccionadoStore()

const props = defineProps({
  interfazEdit: Object
})

const emit = defineEmits(['creada', 'cancelar'])

const form = ref({
  name: '',
  estado: '',
  descripcion: '',
  vlanAccess: null,
  vlansTrunk: [],
  enabled: true,
  mode: '',
  aclIn: null
})

const vlansTrunkInput = ref('')

const aclOptions = computed(() => {
  return aclStore.acls.map(a => a.nombre)
})

onMounted(async () => {

  const dispositivoId = dispositivoStore.dispositivo?.id

  if (dispositivoId) {
    await aclStore.cargarAcls(dispositivoId)
  }

  if (props.interfazEdit) {

    form.value = {
      ...props.interfazEdit,
      aclIn: props.interfazEdit.aclIn || null
    }

    vlansTrunkInput.value =
      props.interfazEdit.vlansTrunk?.join(', ') || ''
  }
})

const guardar = async () => {

  if (vlansTrunkInput.value) {
    form.value.vlansTrunk = vlansTrunkInput.value
      .split(',')
      .map(v => parseInt(v.trim()))
      .filter(v => !isNaN(v))
  } else {
    form.value.vlansTrunk = []
  }

  await interfazStore.actualizarInterfaz(
    props.interfazEdit.id,
    {
      ...form.value,
      aclIn: form.value.aclIn || null
    }
  )

  emit('creada')
}

const quitarAcl = async () => {

  if (!form.value.aclIn) return

  await interfazStore.actualizarInterfaz(
    props.interfazEdit.id,
    {
      eliminarAcl: true,
      aclIn: form.value.aclIn
    }
  )

  form.value.aclIn = null
}
</script>

<style scoped>
.form-container {
  background: white;
  padding: 24px;
  border-radius: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.actions {
  margin-top: 16px;
  display: flex;
  gap: 10px;
}
</style>
