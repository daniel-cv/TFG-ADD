<template>
  <v-container class="mt-5">
    <v-row justify="center">
      <v-col cols="12" md="6">
        <v-card>
          <v-card-title class="text-h5">Usuarios de Firewall</v-card-title>

          <v-card-text>
            <!-- Botón para abrir el form si está oculto -->
            <v-btn
              v-if="!formVisible"
              color="primary"
              class="mb-4"
              @click="formVisible = true"
            >
              Añadir Usuario
            </v-btn>

            <!-- Formulario -->
            <UsuarioFirewallForm
              v-if="formVisible"
              :dispositivo-id="dispositivoId"
              @creada="cargarUsuarioFirewall"
              @cancelar="formVisible = false"
            />
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>

    <!-- Lista de usuarios -->
    <UsuarioFirewallList />
  </v-container>
</template>

<script setup>
import { ref } from "vue";
import UsuarioFirewallForm from "@/components/UsuarioFirewallForm.vue";
import UsuarioFirewallList from "@/components/UsuarioFirewallList.vue";
import { useUsuarioFirewallStore } from "@/stores/usuarioFirewallStore";

const dispositivoId = ref(1);
const formVisible = ref(true);

const usuarioStore = useUsuarioFirewallStore();

// 🔹 Manejar evento 'creada'
const handleCreada = () => {
  // 1️⃣ Ocultar formulario
  formVisible.value = false;

  // 2️⃣ Recargar lista de usuarios
  usuarioStore.cargarUsuarioFirewall(dispositivoId.value);
};
</script>