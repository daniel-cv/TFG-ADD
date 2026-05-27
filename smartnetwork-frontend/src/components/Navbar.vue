<template>
  <v-app-bar
    color="primary"
    background-color="#3b82f6"
    flat
    app
  >

    <v-toolbar-title class="font-weight-bold">
      SmartNetwork
    </v-toolbar-title>

    <v-spacer />
    <v-btn variant="text" to="/dashboard">
      Dashboard
    </v-btn>

    <v-btn variant="text" to="/devices">
      Dispositivos
    </v-btn>

    <v-divider vertical class="mx-3" />
    <template v-if="!userStore.autenticado">
      <v-btn variant="outlined" color="white" to="/login">
        Login
      </v-btn>
    </template>
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
