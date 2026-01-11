<template>
  <v-container fluid class="fill-height login-bg">
    <v-row align="center" justify="center">
      <v-col cols="12" sm="8" md="4">
        <v-card elevation="10" class="pa-6">
          <v-card-title class="text-center text-h5 font-weight-bold">
            SmartNetwork
          </v-card-title>

          <v-card-subtitle class="text-center mb-4">
            Gestión de switches y firewalls
          </v-card-subtitle>

          <v-form @submit.prevent="handleLogin">
            <v-text-field
              v-model="username"
              label="Usuario"
              prepend-inner-icon="mdi-account"
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
              Iniciar sesión
            </v-btn>

            <div class="text-center mt-4">
              <span>¿No tienes cuenta?</span>
              <v-btn
                variant="text"
                color="primary"
                @click="irARegistro"
              >
                Crear usuario
              </v-btn>
            </div>

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
import { useRouter } from "vue-router";

const username = ref("");
const password = ref("");
const userStore = useUserStore();
const router = useRouter();

const handleLogin = async () => {
  try {
    await userStore.login(username.value, password.value);
    username.value = "";
    password.value = "";

      router.push("/dashboard");
  } catch (error) {
    console.error(error);
  }
};

const irARegistro = () => {
  router.push("/register"); // Asegúrate de tener esta ruta creada
};

</script>
