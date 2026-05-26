<template>
  <div>
    <h2>Gestión de VLAN</h2>

    <v-form @submit.prevent="crear">

      <v-text-field v-model="vlanId" label="ID VLAN (10)" />

      <v-text-field v-model="nombre" label="Nombre VLAN" />

      <v-btn type="submit">Crear VLAN</v-btn>
    </v-form>
  </div>
</template>

<script setup>
import { ref } from "vue";

const vlanId = ref("");
const nombre = ref("");

const crear = () => {
  console.log({
    vlan: vlanId.value,
    nombre: nombre.value
  });
};
</script>