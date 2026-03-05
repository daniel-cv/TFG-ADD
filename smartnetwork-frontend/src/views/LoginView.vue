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

            <div class="text-center mt-4 register-link">
              <span>¿No tienes cuenta?</span>
              <span class="link" @click="irARegistro">
                Crear usuario
              </span>
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

<style scoped>
.login-bg{
  background:
    radial-gradient(circle at 20% 20%, rgba(59,130,246,0.15), transparent 40%),
    radial-gradient(circle at 80% 70%, rgba(59,130,246,0.12), transparent 40%),
    #0b1220;
}

.v-card{
  background: #111827;
  border-radius: 14px;
  border: 1px solid #1f2937;
  box-shadow: 0 10px 35px rgba(0,0,0,0.5);
}

.v-card-title{
  color: white;
  font-size: 26px;
  letter-spacing: 0.5px;
}

.v-card-subtitle{
  color: #9aa4b2;
  font-size: 14px;
}

.v-text-field{
  --v-field-border-opacity: 0;
}

.v-field{
  background: #020617;
  border: 1px solid #1e293b;
  border-radius: 8px;
  transition: all 0.2s ease;
}

.v-field:hover{
  border-color: #3b82f6;
}

.v-field--active{
  border-color: #3b82f6;
}

.v-field input{
  color: #e6edf3;
}

.v-label{
  color: #9aa4b2;
}

.v-field .v-icon{
  color: #64748b;
}

.v-btn{
  background: linear-gradient(135deg,#3b82f6,#2563eb);
  border-radius: 8px;
  font-weight: 600;
  letter-spacing: 0.3px;
  box-shadow: 0 4px 14px rgba(37,99,235,0.4);
  transition: all 0.2s ease;
}

.v-btn:hover{
  transform: translateY(-1px);
  box-shadow: 0 6px 20px rgba(37,99,235,0.5);
}

.register-link{
  font-size:14px;
  color:#64748b;
}

.link{
  color:#3b82f6;
  cursor:pointer;
  font-weight:500;
  margin-left:6px;
  transition:color .2s ease;
}

.link:hover{
  color:#1d4ed8;
  text-decoration:underline;
}

p{
  color: #ef4444;
  font-size: 13px;
}

@media (max-width:600px){
  .v-card{
    padding: 28px !important;
  }
}
</style>
