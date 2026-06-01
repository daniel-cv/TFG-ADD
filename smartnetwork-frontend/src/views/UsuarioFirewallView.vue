<template>
  <v-container class="mt-5">
    <v-row justify="center">
      <v-col cols="12" md="6">
        <v-card>
          <v-card-title class="text-h5">Crear Usuario</v-card-title>
          <v-card-subtitle class="mb-4">
            Completa el formulario para crear un nuevo usuario.
          </v-card-subtitle>

          <v-card-text>
            <UsuarioFirewallForm
              :dispositivo-id="dispositivoId"
              @creado="recargar"
            />
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import UsuarioFirewallForm from "@/components/UsuarioFirewallForm.vue";
import { ref } from "vue";

const dispositivoId = ref(1); 

const recargar = () => {
  console.log("Usuario creado, recargando lista...");
};
</script>
