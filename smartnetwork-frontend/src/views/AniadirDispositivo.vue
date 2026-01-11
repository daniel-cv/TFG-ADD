<template>
  <v-container fluid class="fill-height login-bg">
    <v-row align="center" justify="center">
      <v-col cols="12" sm="8" md="4">
        <v-card elevation="10" class="pa-6">
          <v-card-title class="text-center text-h5 font-weight-bold">
            Añadir dispositivo
          </v-card-title>

          <v-form @submit.prevent="handleSubmit" ref="form">
            <!-- Nombre -->
            <v-text-field
              v-model="nombre"
              label="Nombre"
              prepend-inner-icon="mdi-tag"
              variant="outlined"
              class="mb-3"
              required
            />

            <!-- Fabricante -->
            <v-select
              v-model="fabricante"
              :items="fabricantes"
              label="Fabricante"
              prepend-inner-icon="mdi-factory"
              variant="outlined"
              class="mb-3"
              :rules="[v => !!v || 'Selecciona un fabricante']"
              required
            />

            <!-- Tipo de dispositivo -->
            <v-select
              v-model="tipo"
              :items="tipos"
              label="Tipo de dispositivo"
              prepend-inner-icon="mdi-devices"
              variant="outlined"
              class="mb-3"
              :rules="[v => !!v || 'Selecciona un tipo']"
              required
            />

            <!-- Dirección IP -->
            <v-text-field
              v-model="ip"
              label="Dirección IP"
              prepend-inner-icon="mdi-lan"
              variant="outlined"
              class="mb-3"
              :rules="ipRules"
              required
            />

            <!-- Puerto -->
            <v-text-field
              v-model="puerto"
              label="Puerto"
              type="number"
              prepend-inner-icon="mdi-ethernet"
              variant="outlined"
              class="mb-4"
              required
            />

            <v-btn color="primary" size="large" block type="submit">
              Enviar
            </v-btn>

            <p v-if="mensaje" class="mt-3 text-center">
              {{ mensaje }}
            </p>
          </v-form>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref } from "vue";
import { useDispositivoStore } from "@/stores/dispositivoStore";

const dispositivoStore = useDispositivoStore();
const form = ref(null);
const mensaje = ref("");

// Campos del dispositivo
const nombre = ref("");
const fabricante = ref("");
const tipo = ref("");
const ip = ref("");
const puerto = ref("");

// ENUMS (deben coincidir EXACTAMENTE con el backend)
const fabricantes = ["FORTINET", "CISCO", "ARISTA"];
const tipos = ["FIREWALL", "SWITCH"];

const ipRules = [
  v => !!v || "La IP es obligatoria",
  v =>
    /^(25[0-5]|2[0-4]\d|[01]?\d\d?)\.(25[0-5]|2[0-4]\d|[01]?\d\d?)\.(25[0-5]|2[0-4]\d|[01]?\d\d?)\.(25[0-5]|2[0-4]\d|[01]?\d\d?)$/.test(v)
      || "Formato de IP no válido"
];

const handleSubmit = async () => {
  const { valid } = await form.value.validate();
  if (!valid) return;

  try {
    await dispositivoStore.crearNuevoDispositivo({
      nombre: nombre.value,
      fabricante: fabricante.value,
      tipo: tipo.value,
      ip: ip.value,
      puerto: Number(puerto.value),
      estado: "ACTIVO",
    });

    mensaje.value = "Dispositivo creado correctamente";

    // Reset formulario
    nombre.value = "";
    fabricante.value = "";
    tipo.value = "";
    ip.value = "";
    puerto.value = "";

  } catch (error) {
    mensaje.value = "Error al crear el dispositivo";
  }
};
</script>