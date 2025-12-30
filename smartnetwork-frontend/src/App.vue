<template>
  <v-app>
    <Navbar />

    <v-main class="pa-4">
      <router-view />
    </v-main>
  </v-app>
</template>

<script setup>
import Navbar from '@/components/Navbar.vue'
</script>
