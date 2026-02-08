<template>
  <div class="configuration-view">
    <h1>Configuration</h1>
    <p>Configuración del dispositivo seleccionado</p>
  </div>

  <div class="links">
    <v-btn color="primary" @click="irCrearPolicity(dispositivoId)">
      Crear Policy
    </v-btn>

    <v-btn color="primary" @click="irCrearAddress(dispositivoId)">
      Crear Address
    </v-btn>

    <v-btn color="primary" @click="irCrearService(dispositivoId)">
      Crear Service
    </v-btn>

    <v-btn color="primary" @click="irCrearUsuarioFirewall(dispositivoId)">
      Crear Usuario Firewall
    </v-btn>
  </div>
</template>

<script setup lang="ts">
import { useRouter } from "vue-router";
import { useDispositivoSeleccionadoStore } from "@/stores/dispositivoSeleccionadoStore";

const router = useRouter();
const seleccionadoStore = useDispositivoSeleccionadoStore();

const dispositivoId = seleccionadoStore.id;

function irCrearPolicity(id: number | string) {
  seleccionadoStore.seleccionar(id);
  router.push(`/crearpolicy/${id}`);
}

function irCrearAddress(id: number | string) {
  seleccionadoStore.seleccionar(id);
  router.push(`/crearAddress/${id}`);
}

function irCrearService(id: number | string) {
  seleccionadoStore.seleccionar(id);
  router.push(`/service/${id}`);
}

function irCrearUsuarioFirewall(id: number | string) {
  seleccionadoStore.seleccionar(id);
  router.push(`/usuariofirewall/${id}`);
}
</script>


<style scoped>
.links {
  display: flex;
  gap: 1rem;
}
</style>
