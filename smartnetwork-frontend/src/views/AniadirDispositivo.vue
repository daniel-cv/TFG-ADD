<template>
  <v-container fluid class="fill-height login-bg">
    <v-row align="center" justify="center">
      <v-col cols="12" sm="8" md="4">
        <v-card elevation="10" class="pa-6">
          <v-card-title class="text-center text-h5 font-weight-bold">
            Añadir dispositivo
          </v-card-title>

          <v-form @submit.prevent="handleSubmit" ref="form">
            <v-text-field
              v-model="code"
              label="Fabricante"
              prepend-inner-icon="mdi-shield-key"
              variant="outlined"
              class="mb-4"
              required
            />
            <v-text-field
              v-model="code"
              label="Dispositivo"
              prepend-inner-icon="mdi-shield-key"
              variant="outlined"
              class="mb-4"
              required
            />
            <v-text-field
              v-model="ip"
              label="Dirección IP"
              prepend-inner-icon="mdi-lan"
              variant="outlined"
              class="mb-3"
              :rules="ipRules"
              required
            />
            <v-text-field
              v-model="code"
              label="Puerto"
              prepend-inner-icon="mdi-shield-key"
              variant="outlined"
              class="mb-4"
              required
            />

            <v-text-field
              v-model="code"
              label="Token"
              prepend-inner-icon="mdi-shield-key"
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

const ip = ref("");
const token = ref("");
const mensaje = ref("");
const form = ref(null);

const ipRules = [
  v => !!v || "La IP es obligatoria",
  v =>
    /^(25[0-5]|2[0-4]\d|[01]?\d\d?)\.(25[0-5]|2[0-4]\d|[01]?\d\d?)\.(25[0-5]|2[0-4]\d|[01]?\d\d?)\.(25[0-5]|2[0-4]\d|[01]?\d\d?)$/.test(v)
      || "Formato de IP no válido"
];

const handleSubmit = async () => {
  const { valid } = await form.value.validate();
  if (!valid) return;

  mensaje.value = "Datos enviados correctamente";
  ip.value = "";
  token.value = "";
};
</script>