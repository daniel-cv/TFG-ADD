<template>
  <v-container class="mt-5">
    <v-row justify="center">
      <v-col cols="12" md="6">
        <v-card>
          <v-card-title class="text-h5">Crear Address</v-card-title>
          <v-card-subtitle class="mb-4">
            Completa el formulario para crear una nueva dirección.
          </v-card-subtitle>

          <v-card-text>
            <AddressForm
              :dispositivo-id="dispositivoId"
              @creada="recargar"
            />
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import AddressForm from "@/components/AddressForm.vue";
import { ref } from "vue";

const dispositivoId = ref(1); // o el que recibas por props/route

const recargar = () => {
  console.log("Address creada, recargando lista...");
};
</script>
