<template>
  <v-form @submit.prevent="handleCrearUsuario">
    
    <!-- NOMBRE -->
    <v-text-field
      v-model="name"
      label="Nombre de Usuario"
      prepend-inner-icon="mdi-account"
      variant="outlined"
      class="mb-3"
      required
    />

    <!-- PASSWORD -->
    <v-text-field
      v-model="password"
      label="Contraseña"
      prepend-inner-icon="mdi-lock"
      type="password"
      variant="outlined"
      class="mb-3"
      required
    />


    <!-- TWO FACTOR -->
    <v-select
      v-model="twoFactor"
      :items="twoFactorOptions"
      label="Autenticación en Dos Factores"
      prepend-inner-icon="mdi-shield-key"
      variant="outlined"
      class="mb-3"
    />

    <v-btn color="primary" size="large" block type="submit">
      Crear Usuario
    </v-btn>

    <p v-if="mensaje" class="mt-3 text-center">{{ mensaje }}</p>
  </v-form>
</template>

<script setup>
import { ref } from "vue";
import { useRoute } from "vue-router";
import { useUsuarioFirewallStore } from "@/stores/usuarioFirewallStore";

const route = useRoute();
const emit = defineEmits(['creado'])

const userStore = useUsuarioFirewallStore()

const name = ref("");
const password = ref("");
const email = ref("");
const twoFactor = ref("disable");
const mensaje = ref("");

const twoFactorOptions = [
  { title: "Deshabilitado", value: "disable" },
  { title: "Habilitado", value: "enable" }
];

const dispositivoId = Number(route.params.id);

const handleCrearUsuario = async () => {
  try {

    const payload = {
      name: name.value,
      password: password.value,
      email: email.value || null,
      type: 'password',
      dispositivoId: dispositivoId
    };

    await userStore.crearUsuarioFirewall(payload);

    mensaje.value = "Usuario creado correctamente";

    emit("creado");

    // Reset
    name.value = "";
    password.value = "";
    email.value = "";
    twoFactor.value = "disable";

  } catch (error) {
    mensaje.value = "Error al crear el usuario";
  }
};
</script>
