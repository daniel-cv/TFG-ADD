<template>
  <div>
    <h2>Routing</h2>

    <v-form @submit.prevent="guardar">

      <v-switch v-model="ipRouting" label="Activar IP Routing" />

      <v-text-field v-model="destino" label="Destino (0.0.0.0/0)" />
      <v-text-field v-model="gateway" label="Gateway" />

      <v-btn type="submit">Guardar</v-btn>
    </v-form>
  </div>
</template>

<script setup>
import { ref } from "vue";

const ipRouting = ref(true);
const destino = ref("");
const gateway = ref("");

const guardar = () => {
  console.log({
    ipRouting: ipRouting.value,
    destino: destino.value,
    gateway: gateway.value
  });
};
</script>