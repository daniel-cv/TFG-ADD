<template>
  <v-container fluid>
    <!-- Botón Añadir Dispositivo centrado debajo del NavBar -->
    <v-row justify="center" class="my-6">
      <v-col cols="auto">
        <v-btn
          color="success"
          dark
          large
          rounded
          elevation="6"
          @click="irACrearDispositivo"
        >
          <v-icon left>mdi-plus</v-icon>
          Añadir Dispositivo
        </v-btn>
      </v-col>
    </v-row>

    <!-- Lista de Dispositivos -->
    <v-row>
      <v-col
        v-for="d in dispositivos"
        :key="d.id"
        cols="12"
        md="4"
      >
        <v-card elevation="4">
          <v-card-title>{{ d.nombre }}</v-card-title>
          <v-card-subtitle>{{ d.tipo }} · {{ d.fabricante }}</v-card-subtitle>
          <v-card-text>
            IP: {{ d.ip }} <br>
            Estado: {{ d.estado }}
            D:{{ d.id }}
          </v-card-text>
         <v-card-actions>
  <v-btn color="primary" @click="configurar(d.id)">
    Configurar
  </v-btn>

  <!-- 🔹 Nuevo botón Crear Interfaz -->
  <v-btn color="secondary" @click="crearInterfaz(d.id)">
    Crear Interfaz
  </v-btn>
</v-card-actions>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>

<script setup>
import { ref, onMounted } from "vue";
import { useDispositivoStore } from "@/stores/dispositivoStore";
import { useUserStore } from "@/stores/userStore";
import { useRouter } from "vue-router";
import { useDispositivoSeleccionadoStore } from "@/stores/dispositivoSeleccionadoStore";



const dispositivos = ref([]);
const mensaje = ref("");
const seleccionadoStore = useDispositivoSeleccionadoStore();

const dispositivoStore = useDispositivoStore();
const userStore = useUserStore();   // ← usuario logado
const router = useRouter();

function configurar(id) {
  seleccionadoStore.seleccionar(id);
  router.push(`/crearpolicy/${id}`);
   // sin params
}


// Cargar dispositivos al montar
onMounted(async () => {
  try {
    // Verificar que hay usuario logado
    if (!userStore.autenticado) {
      mensaje.value = "Debes iniciar sesión para ver tus dispositivos";
      return;
    }

    await dispositivoStore.getMisDispositivos();
    dispositivos.value = dispositivoStore.dispositivos;
    mensaje.value = dispositivoStore.mensaje;

  } catch (error) {
    mensaje.value = "Error al cargar dispositivos";
    console.error(error);
  }
});

// Función para ir al formulario de creación
const irACrearDispositivo = () => {
  router.push("/newdevice");
};
function crearInterfaz(id) {
  // Redirige a la vista de crear interfaz con el ID del dispositivo
  router.push({ name: "CrearInterfaz", params: { id } });
}
</script>
