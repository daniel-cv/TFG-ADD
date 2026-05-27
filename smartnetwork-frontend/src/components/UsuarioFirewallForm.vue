<template>
  <v-form @submit.prevent="handleSubmitUsuario">

    <v-text-field
      v-model="name"
      label="Nombre de Usuario"
      prepend-inner-icon="mdi-account"
      variant="outlined"
      class="mb-3"
      required
      :disabled="usuarioEdit"
    />

    <v-text-field
      v-model="password"
      label="Contraseña"
      prepend-inner-icon="mdi-lock"
      type="password"
      variant="outlined"
      class="mb-3"
      :required="!usuarioEdit"
    />

    <v-text-field
      v-model="email"
      label="Email"
      prepend-inner-icon="mdi-email"
      variant="outlined"
      class="mb-3"
    />

    <v-select
      v-model="twoFactor"
      :items="twoFactorOptions"
      label="Autenticación en Dos Factores"
      prepend-inner-icon="mdi-shield-key"
      variant="outlined"
      class="mb-3"
    />

    <v-btn color="primary" size="large" block type="submit">
      {{ usuarioEdit ? 'Actualizar Usuario' : 'Crear Usuario' }}
    </v-btn>

    <v-btn
      variant="outlined"
      size="large"
      block
      class="mt-2"
      @click="cancelar()"
    >
      Cancelar
    </v-btn>

    <p v-if="mensaje" class="mt-3 text-center">{{ mensaje }}</p>
  </v-form>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useUsuarioFirewallStore } from "@/stores/usuarioFirewallStore";

const props = defineProps({
  usuarioEdit: {
    type: Object,
    default: null
  },

  dispositivoId: {
    type: Number,
    required: false
  },

  modo: {
    type: String,
    default: 'simple'
  }
})

const emit = defineEmits([
  'creado',
  'cancelar'
])

const userStore = useUsuarioFirewallStore()
const name = ref("")
const password = ref("")
const email = ref("")
const twoFactor = ref("disable")
const mensaje = ref("")
const twoFactorOptions = [
  {
    title: "Deshabilitado",
    value: "disable"
  },
  {
    title: "Habilitado",
    value: "enable"
  }
]

onMounted(() => {
  if (props.usuarioEdit) {
    name.value = props.usuarioEdit.nombre || ""
    email.value = props.usuarioEdit.email || ""
    twoFactor.value =
      props.usuarioEdit.factor ||
      props.usuarioEdit.twoFactor ||
      "disable"
  }
})

const handleSubmitUsuario = async () => {
  try {
    const payload = {
      name: name.value,
      password: password.value || undefined,
      email: email.value || null,
      type: 'password',
      twoFactor: twoFactor.value
    }
    if (props.usuarioEdit) {
      if (props.usuarioEdit.dispositivosIds &&props.usuarioEdit.dispositivosIds.length) {
        payload.dispositivosIds =props.usuarioEdit.dispositivosIds
      }
      else if (props.dispositivoId) {
        payload.dispositivosIds = [
          props.dispositivoId
        ]
      }
      console.log('PAYLOAD EDIT =>', payload)
      await userStore.actualizarUsuarioFirewall(props.usuarioEdit.id,payload)
      mensaje.value ="Usuario actualizado correctamente"
    }

    else {
      if (props.modo === 'full' & props.dispositivoId) {
        payload.dispositivosIds = [props.dispositivoId]
        await userStore.crearUsuarioFirewallCompleto(payload)
      } else {
        await userStore.crearUsuarioFirewall( payload )
      }
      mensaje.value = "Usuario creado correctamente"
    }
    emit("creado")
  } catch (error) {
    console.error(error)
    mensaje.value = props.usuarioEdit? "Error actualizando usuario" : "Error creando usuario"
  }
}
function cancelar() {
  emit('cancelar')
}
</script>