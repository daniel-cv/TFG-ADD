<template>
  <v-container fluid class="fill-height login-bg">
    <v-row align="center" justify="center">
      <v-col cols="12" sm="8" md="4">
        <v-card elevation="10" class="pa-6">
          <v-card-title class="text-center text-h5 font-weight-bold">
            SmartNetwork
          </v-card-title>

          <v-card-subtitle class="text-center mb-4">
            Crear un nuevo usuario
          </v-card-subtitle>

          <v-form @submit.prevent="handleCrearUsuario">
            <v-text-field
              v-model="username"
              label="Usuario"
              prepend-inner-icon="mdi-account"
              variant="outlined"
              class="mb-3"
              required
            />

            <v-text-field
              v-model="email"
              label="Email"
              prepend-inner-icon="mdi-email"
              variant="outlined"
              class="mb-3"
              required
            />

            <v-text-field
              v-model="password"
              label="Contraseña"
              type="password"
              prepend-inner-icon="mdi-lock"
              variant="outlined"
              class="mb-4"
              required
            />

            <v-btn
              color="primary"
              size="large"
              block
              type="submit"
            >
              Crear Usuario
            </v-btn>

            <p v-if="userStore.mensaje" class="mt-3 text-center">{{ userStore.mensaje }}</p>
          </v-form>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref } from "vue";
import { useUserStore } from "@/stores/userStore";

const username = ref("");
const email = ref("");
const password = ref("");

const userStore = useUserStore();

const handleCrearUsuario = async () => {
  try {
    await userStore.crearUsuario({
      username: username.value,
      email: email.value,
      password: password.value,
    });

    // Limpiar inputs tras crear usuario
    username.value = "";
    email.value = "";
    password.value = "";
  } catch (error) {
    console.error(error);
  }
};
</script>

<style scoped>
</style>
