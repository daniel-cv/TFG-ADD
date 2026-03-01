<template>
  <v-container fluid class="devices-page">
    <!-- HEADER -->
    <v-row class="devices-header" align="center">
      <v-col>
        <h1 class="page-title">
          Dispositivos
        </h1>
      </v-col>
      <v-col cols="auto">
        <v-btn
          class="add-btn"
          @click="irACrearDispositivo"
        >
          <v-icon start>mdi-plus</v-icon>
          Añadir dispositivo
        </v-btn>
      </v-col>
    </v-row>

    <!-- GRID DISPOSITIVOS -->
    <v-row>
      <v-col
        v-for="d in dispositivos"
        :key="d.id"
        cols="12"
        md="4"
        lg="3"
      >
        <v-card
          class="device-card"
          @click="configurar(d)"
        >
          <!-- NOMBRE -->
          <div class="device-name">
            {{ d.nombre }}
          </div>

          <!-- TIPO -->
          <div class="device-type">
            {{ d.tipo }} · {{ d.fabricante }}
          </div>

          <!-- INFO -->
          <div class="device-info">
            IP: {{ d.ip }}
          </div>

          <!-- STATUS -->
          <div class="device-status">
            <span class="status-dot"></span>
            {{ d.estado }}
          </div>

          <!-- ACTIONS -->
          <div class="device-actions">
            <v-btn
              variant="text"
              class="action-btn"
              @click.stop="configurar(d)"
            >
              Configurar
            </v-btn>

            <v-btn
              variant="text"
              class="action-btn-secondary"
              @click.stop="crearInterfaz(d.id)"
            >
              Interfaces
            </v-btn>
          </div>
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

function configurar(dispositivo) {
  seleccionadoStore.seleccionar(dispositivo);
  router.push(`/device/${dispositivo.id}`);
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

<style scoped>

.devices-page{
background:#0b1220;
min-height:100vh;
padding:30px;
}

/* HEADER */
.devices-header{
margin-bottom:30px;
}

.page-title{
color:#e6edf3;
font-weight:600;
font-size:26px;
}

/* ADD BUTTON */
.add-btn{
background:#22c55e;
color:white;
border-radius:8px;
font-weight:500;
}

.add-btn:hover{
background:#16a34a;
}

/* DEVICE CARD */
.device-card{
background:linear-gradient(135deg,#111827 0%,#1f2937 100%);
border:1px solid #1f2937;
border-radius:12px;
padding:20px;
cursor:pointer;
transition:0.25s;
}

.device-card:hover{
transform:translateY(-6px);
border:1px solid #3b82f6;
box-shadow:0 15px 40px rgba(0,0,0,0.5);
}

/* NAME */
.device-name{
font-size:18px;
font-weight:600;
color:#e6edf3;
}

/* TYPE */
.device-type{
color:#9aa4b2;
font-size:13px;
margin-top:5px;
}

/* INFO */
.device-info{
margin-top:15px;
color:#9aa4b2;
font-size:14px;
}

/* STATUS */
.device-status{
margin-top:15px;
display:inline-flex;
align-items:center;
gap:8px;
background:rgba(34,197,94,0.15);
color:#22c55e;
padding:4px 10px;
border-radius:20px;
font-size:13px;
}

.status-dot{
width:8px;
height:8px;
background:#22c55e;
border-radius:50%;
}

/* ACTIONS */
.device-actions{
margin-top:20px;
display:flex;
gap:15px;
}

.action-btn{
color:#3b82f6;
}

.action-btn-secondary{
color:#9aa4b2;
}

.action-btn-secondary:hover{
color:white;
}
</style>
