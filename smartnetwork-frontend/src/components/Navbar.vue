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
    <v-btn
      variant="text"
      to="/dashboard"
    >
      Dashboard
    </v-btn>

    <v-btn
      variant="text"
      to="/devices"
    >
      Dispositivos
    </v-btn>

    <!-- Separador -->
    <v-divider vertical class="mx-3" />

    <!-- Auth (temporal) -->
    <v-btn
      variant="outlined"
      color="white"
      to="/login"
    >
      Login
    </v-btn>
  </v-app-bar>
</template>

<script setup>
// No lógica aún (perfecto para prototipo)
</script>

<style scoped>
.v-toolbar-title {
  letter-spacing: 0.5px;
}
</style>
