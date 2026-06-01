<template>
  <v-container fluid class="devices-page">
    <v-row class="devices-header" align="center">
      <v-col>
        <h1 class="page-title">
          Dispositivos
        </h1>
      </v-col>
      <v-col cols="auto">
        <v-btn class="add-btn" @click="irACrearDispositivo">
          <v-icon start>mdi-plus</v-icon>
          Añadir dispositivo
        </v-btn>
      </v-col>
    </v-row>

    <v-row>
      <v-col v-for="d in dispositivos" :key="d.id" cols="12" md="4" lg="3">
        <v-card class="device-card" @click="configurar(d)">
          <div class="device-name">
            {{ d.nombre }}
          </div>
          <div class="device-type">
            {{ d.tipo }} · {{ d.fabricante }}
          </div>
          <div class="device-info">
            IP: {{ d.ip }}
          </div>
          <div class="device-status">
            <span class="status-dot"></span>
            {{ d.estado }}
          </div>
          <div class="device-actions">
            <v-btn variant="text" class="action-btn" @click.stop="configurar(d)">
              Configurar
            </v-btn>

            <v-btn variant="text" class="action-btn edit-btn" @click.stop="abrirEditar(d)">
              Editar
            </v-btn>

            <v-btn variant="text" class="action-btn delete-btn" @click.stop="abrirDialogoEliminar(d)">
              Eliminar
            </v-btn>
          </div>
        </v-card>
      </v-col>
    </v-row>

    <!-- Diálogo de confirmación de eliminación -->
    <v-dialog v-model="mostrarDialogoEliminar" max-width="450">
      <v-card class="dialog-card">
        <v-card-title class="dialog-title">
          <v-icon class="warning-icon">mdi-alert-circle</v-icon>
          Confirmar eliminación
        </v-card-title>
        <v-divider class="dialog-divider"></v-divider>
        <v-card-text class="dialog-text">
          ¿Estás seguro de que deseas eliminar <strong>{{ dispositivoAEliminar?.nombre }}</strong>? Esta acción no se
          puede deshacer.
        </v-card-text>
        <v-card-actions class="dialog-actions">
          <v-btn variant="text" @click="cerrarDialogoEliminar" class="cancel-btn">
            Cancelar
          </v-btn>
          <v-btn variant="elevated" @click="confirmarEliminar" class="confirm-delete-btn">
            Eliminar
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <v-dialog v-model="dialogEditar" max-width="500">
      <v-card class="dialog-card">
        <v-card-title class="dialog-title">
          Editar dispositivo
        </v-card-title>
        <v-divider class="dialog-divider"></v-divider>
        <v-card-text>
          <v-text-field v-model="dispositivoEditado.nombre" label="Nombre" variant="outlined" class="mb-4" />
          <v-text-field v-model="dispositivoEditado.ip" label="Dirección IP" variant="outlined" />
        </v-card-text>
        <v-card-actions class="dialog-actions">
          <v-btn variant="text" @click="dialogEditar = false">
            Cancelar
          </v-btn>
          <v-spacer />
          <v-btn color="primary" @click="guardarEdicion">
            Guardar
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
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
const mostrarDialogoEliminar = ref(false);
const dispositivoAEliminar = ref(null);
const dialogEditar = ref(false);

const dispositivoEditado = ref({
  id: null,
  nombre: "",
  ip: ""
});

const dispositivoStore = useDispositivoStore();
const userStore = useUserStore();
const router = useRouter();

function configurar(dispositivo) {
  seleccionadoStore.seleccionar(dispositivo);
  router.push(`/device/${dispositivo.id}`);
}

function abrirDialogoEliminar(dispositivo) {
  dispositivoAEliminar.value = dispositivo;
  mostrarDialogoEliminar.value = true;
}

function cerrarDialogoEliminar() {
  mostrarDialogoEliminar.value = false;
  dispositivoAEliminar.value = null;
}

async function confirmarEliminar() {
  if (!dispositivoAEliminar.value) return;

  try {
    await dispositivoStore.eliminarDispositivo(dispositivoAEliminar.value.id);
    dispositivos.value = dispositivoStore.dispositivos;
    mensaje.value = dispositivoStore.mensaje;
    cerrarDialogoEliminar();
  } catch (error) {
    mensaje.value = "Error al eliminar el dispositivo";
    console.error(error);
  }
}
const abrirEditar = (dispositivo) => {
  dispositivoEditado.value = {
    id: dispositivo.id,
    nombre: dispositivo.nombre,
    ip: dispositivo.ip
  };

  dialogEditar.value = true;
};

const guardarEdicion = async () => {
  try {
    await dispositivoStore.editarDispositivo(
      dispositivoEditado.value.id,
      dispositivoEditado.value
    );

    await dispositivoStore.getMisDispositivos();
    dispositivos.value = dispositivoStore.dispositivos;

    dialogEditar.value = false;

  } catch (error) {
    console.error(error);
  }
};



onMounted(async () => {
  try {
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

const irACrearDispositivo = () => {
  router.push("/newdevice");
};

</script>

<style scoped>
.devices-page {
  background: #0b1220;
  min-height: 100vh;
  padding: 30px;
}

.devices-header {
  margin-bottom: 30px;
}

.page-title {
  color: #e6edf3;
  font-weight: 600;
  font-size: 26px;
}

.add-btn {
  background: #22c55e;
  color: white;
  border-radius: 8px;
  font-weight: 500;
}

.add-btn:hover {
  background: #16a34a;
}

.device-card {
  background: linear-gradient(135deg, #111827 0%, #1f2937 100%);
  border: 1px solid #1f2937;
  border-radius: 12px;
  padding: 20px;
  cursor: pointer;
  transition: 0.25s;
}

.device-card:hover {
  transform: translateY(-6px);
  border: 1px solid #3b82f6;
  box-shadow: 0 15px 40px rgba(0, 0, 0, 0.5);
}

.device-name {
  font-size: 18px;
  font-weight: 600;
  color: #e6edf3;
}

.device-type {
  color: #9aa4b2;
  font-size: 13px;
  margin-top: 5px;
}

.device-info {
  margin-top: 15px;
  color: #9aa4b2;
  font-size: 14px;
}

.device-status {
  margin-top: 15px;
  display: inline-flex;
  align-items: center;
  gap: 8px;
  background: rgba(34, 197, 94, 0.15);
  color: #22c55e;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 13px;
}

.status-dot {
  width: 8px;
  height: 8px;
  background: #22c55e;
  border-radius: 50%;
}

.device-actions {
  margin-top: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
}

.action-btn {
  color: #3b82f6;
}

.action-btn:hover {
  color: #60a5fa;
}

.delete-btn {
  color: #ef4444;
}

.delete-btn:hover {
  color: #f87171;
}

.action-btn-secondary {
  color: #9aa4b2;
}

.action-btn-secondary:hover {
  color: white;
}

/* Estilos del diálogo */
.dialog-card {
  background: linear-gradient(135deg, #111827 0%, #1f2937 100%);
  border: 1px solid #3b82f6;
  border-radius: 16px;
  overflow: hidden;
}

.dialog-divider {
  border-color: #3b82f6 !important;
  opacity: 0.3;
}

.dialog-title {
  color: #e6edf3;
  font-size: 18px;
  font-weight: 600;
  display: flex;
  align-items: center;
  gap: 12px;
}

.warning-icon {
  color: #ef4444;
  font-size: 24px;
}

.dialog-text {
  color: #9aa4b2;
  font-size: 14px;
  line-height: 1.6;
}

.dialog-actions {
  gap: 12px;
  padding: 16px;
}

.cancel-btn {
  color: #9aa4b2;
}

.cancel-btn:hover {
  color: #e6edf3;
}

.confirm-delete-btn {
  background: #ef4444;
  color: white;
}

.confirm-delete-btn:hover {
  background: #dc2626;
}
.edit-btn {
  color: #f59e0b;
}

.edit-btn:hover {
  color: #fbbf24;
}
</style>
