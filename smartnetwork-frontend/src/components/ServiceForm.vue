<template>
  <v-form @submit.prevent="handleCrearService">
    
    <!-- NAME -->
    <v-text-field
      v-model="name"
      label="Nombre del Servicio"
      prepend-inner-icon="mdi-label"
      variant="outlined"
      class="mb-3"
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

    <v-text-field
      v-if="protocol === 'TCP' || protocol === 'UDP'"
      v-model="address"
      label="Dirección IP (ej: 192.168.1.1)"  
      prepend-inner-icon="mdi-lan-connect"
      variant="outlined"
      class="mb-3"
    />

    <!-- UDP PORT RANGE -->
    <v-text-field
      v-if="protocol === 'TCP' || protocol === 'UDP'"
      v-model="portRange"
      label="Rango de Puertos UDP (ej: 53 o 1000-2000)"
      prepend-inner-icon="mdi-lan-connect"
      variant="outlined"
      class="mb-3"
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
      Crear Service
    </v-btn>

    <p v-if="mensaje" class="mt-3 text-center">{{ mensaje }}</p>
  </v-form>
</template>

<script setup>
import { ref } from "vue";
import { useRoute } from "vue-router";
import { useServiceStore } from "@/stores/serviceStore";

const route = useRoute();
const emit = defineEmits(['creada'])

const serviceStore = useServiceStore()

const name = ref("");
const protocol = ref("");
const address = ref("");
const portRange = ref("");
const comentario = ref("");
const mensaje = ref("");

const protocolos = ["TCP", "UDP", "ICMP"];

const dispositivoId = Number(route.params.id);

const handleCrearService = async () => {
  try {
    const payload = {
      name: name.value,
      protocol: protocol.value,
      address: address.value,
      portRange: protocol.value === "ICMP" ? null : portRange.value,
      comentario: comentario.value,
      dispositivoId: dispositivoId
    };

    await serviceStore.crearService(payload);

    mensaje.value = "Service creado correctamente";

    emit("creada");

    // Reset del formulario
    name.value = "";
    protocol.value = "";
    portRange.value = "";
    comentario.value = "";


  } catch (error) {
    mensaje.value = "Error al crear el service";
  }
};
</script>
