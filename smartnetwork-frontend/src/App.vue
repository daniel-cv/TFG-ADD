<template>
  <v-app>
    <Navbar />

    <v-main class="no-padding">
      <router-view />
    </v-main>
  </v-app>
</template>

<script setup>
import Navbar from '@/components/Navbar.vue'

import { onMounted } from 'vue'
import { useUserStore } from '@/stores/userStore'

const userStore = useUserStore()

onMounted(() => {
  userStore.cargarSesion()
})
</script>
<style>
html, body, .v-application {
  margin: 0;
  padding: 0;
  background-color: #0b1220;
}
.v-application--wrap {
  min-height: 100vh;
}

</style>
