<template>
  <v-container fluid class="pa-6">
    <v-row justify="center">
      <v-col cols="12" md="8">
        <v-card elevation="8" class="pa-6">
          <v-card-title class="text-h5 font-weight-bold text-center">
            Crear política de firewall
          </v-card-title>

          <v-form ref="form" @submit.prevent="handleSubmit">

            <!-- Nombre de la política -->
            <v-text-field
              v-model="name"
              label="Nombre de la política"
              prepend-inner-icon="mdi-shield"
              required
            />

            <!-- Interfaces origen y destino -->
            <v-text-field
              v-model="srcInt"
              label="Interfaz origen"
              prepend-inner-icon="mdi-lan-connect"
              required
            />
            <v-text-field
              v-model="dstintf"
              label="Interfaz destino"
              prepend-inner-icon="mdi-lan-connect"
              required
            />

            <!-- Direcciones origen y destino -->
            <v-text-field
              v-model="srcaddr"
              label="Dirección origen"
              prepend-inner-icon="mdi-lan-connect"
              required
            />
            <v-text-field
              v-model="dstaddr"
              label="Dirección destino"
              prepend-inner-icon="mdi-lan-connect"
              required
            />

            <!-- Servicios -->
            <v-text-field
              v-model="service"
              label="Servicio"
              prepend-inner-icon="mdi-lan-connect"
              required
            />

            <v-btn color="primary" type="submit" block class="mt-4">
              Crear política
            </v-btn>

            <p v-if="mensaje" class="mt-2 text-center">{{ mensaje }}</p>
          </v-form>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, reactive } from "vue";
import axios from "axios";
import { useDispositivoStore } from "@/stores/dispositivoStore";
import { useRouter } from "vue-router";

const dispositivoStore = useDispositivoStore();

const form = ref(null);
const mensaje = ref("");

const name = ref("");
const srcInt = ref("");
const dstintf =ref("");
const srcaddr =ref("");
const dstaddr =ref("");
const service = ref("");

const handleSubmit = async () => {
  try {
    // Validación básica
    const { valid } = await form.value.validate?.();
    if (valid === false) return;

    await dispositivoStore.crearReglaFirewall({
        name: name.value,
        srcInf: srcInt.value,
        destInf: dstintf.value,
        srcIp: srcaddr.value,
        destIp: dstaddr.value,
        service: service.value
    })

    mensaje.value = "Política creada correctamente: " + response.status;
  } catch (error) {
    console.error(error);
    mensaje.value = "Error al crear política: " + (error.response?.data || error.message);
  }
};
</script>