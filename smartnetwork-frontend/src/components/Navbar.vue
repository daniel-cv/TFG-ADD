<template>
  <v-app-bar
    color="primary"
    dark
    flat
    app
  >
    <!-- Logo / Nombre -->
    <v-toolbar-title class="font-weight-bold">
      SmartNetwork
    </v-toolbar-title>

    <v-spacer />

    <!-- Links -->
    <v-btn variant="text" to="/dashboard">
      Dashboard
    </v-btn>

    <v-btn variant="text" to="/devices">
      Dispositivos
    </v-btn>

    <v-divider vertical class="mx-3" />

    <!-- Si NO está autenticado → mostrar Login -->
    <template v-if="!userStore.autenticado">
      <v-btn variant="outlined" color="white" to="/login">
        Login
      </v-btn>
    </template>

    <!-- Si SÍ está autenticado → mostrar Logout -->
    <template v-else>
      <span class="mr-4">Hola, {{ userStore.usuarioActual.username }}</span>

      <v-btn variant="outlined" color="white" @click="logout">
        Logout
      </v-btn>
    </template>
  </v-app-bar>
</template>

<script setup>
import { useUserStore } from "@/stores/userStore";
import { useRouter } from "vue-router";

const userStore = useUserStore();
const router = useRouter();

const logout = () => {
  userStore.logout();
  router.push("/login");
};
</script>

<style scoped>
.v-toolbar-title {
  letter-spacing: 0.5px;
}
</style>
