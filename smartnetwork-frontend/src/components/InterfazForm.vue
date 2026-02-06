<template>
  <v-form @submit.prevent="handleCrearInterfaz">

    <v-text-field
      v-model="name"
      label="Nombre de la interfaz"
      prepend-inner-icon="mdi-lan"
      variant="outlined"
      required
    />

    <v-text-field
      v-model="ip"
      label="IP / Máscara"
      placeholder="192.168.1.1/24"
      prepend-inner-icon="mdi-ip"
      variant="outlined"
      required
    />

    <v-select
      v-model="tipo"
      :items="['physical', 'vlan', 'loopback']"
      label="Tipo"
      prepend-inner-icon="mdi-cog"
      variant="outlined"
      required
    />

    <v-textarea
      v-model="comentario"
      label="Comentario"
      variant="outlined"
    />

    <v-btn type="submit" block color="primary">
      Crear Interfaz
    </v-btn>

    <p v-if="mensaje" class="text-center mt-3">{{ mensaje }}</p>

  </v-form>
</template>

<script setup>
import { ref } from "vue"
import { useRoute } from "vue-router"
//import api from "@/api"

const route = useRoute()
const dispositivoId = route.params.id  // 🔹 ID automático

const name = ref("")
const ip = ref("")
const tipo = ref("")
const comentario = ref("")
const mensaje = ref("")

const handleCrearInterfaz = async () => {
  try {
    await api.post(`/api/dispositivos/${dispositivoId}/interfaces`, {
      name: name.value,
      ip: ip.value,
      tipo: tipo.value,
      comentario: comentario.value
    })

    mensaje.value = "Interfaz creada correctamente"
  } catch (e) {
    mensaje.value = "Error al crear la interfaz"
  }
}
</script>