<template>
  <div class="usuarios-firewall-wrapper">

    <!-- LISTA -->
    <div v-if="!mostrandoFormulario">

      <div class="table-header">
        <h2>Usuarios de Firewall</h2>

        <v-btn class="add-btn" size="small" @click="mostrarCrear()">
          Añadir Usuario
        </v-btn>
      </div>

      <v-table class="professional-table">
        <thead>
          <tr>
            <th>Nombre</th>
            <th>Email</th>
            <th>Rol</th>
            <th>Acciones</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="usuario in usuarioFirewallStore.usuarios" :key="usuario.id">
            <td class="name">{{ usuario.nombre }}</td>
            <td>{{ usuario.email }}</td>
            <td>{{ usuario.tipo }}</td>

            <td>
              <v-btn
                class="rounded-0 px-4 me-2"
                color="green"
                size="small"
                @click="editarUsuario(usuario)"
              >
                EDITAR
              </v-btn>

              <v-btn
                class="rounded-0 px-4 me-2"
                color="red"
                size="small"
                @click="eliminarUsuario(usuario.id)"
              >
                ELIMINAR
              </v-btn>

              <v-btn
                class="rounded-0 px-4"
                color="blue"
                size="small"
                @click="aplicarUsuarioToDispositivos(usuario)"
              >
                APLICAR
              </v-btn>
            </td>
          </tr>
        </tbody>
      </v-table>
    </div>

    <!-- FORMULARIO -->
    <div v-else class="formulario-inline">
      <UsuarioFirewallForm
        :dispositivo-id="props.dispositivoId"
        :usuario-edit="usuarioSeleccionado"
        :modo="props.modo"
        @creado="recargarYCerrar"
        @cancelar="cerrarFormulario"
      />
    </div>

    <!-- MODAL APLICAR -->
    <v-dialog v-model="dialogAplicar" max-width="650px">
      <v-card class="apply-card">

        <v-card-title class="apply-title">
          Aplicar Usuario: {{ usuarioAplicar?.nombre }}
        </v-card-title>

        <v-card-text>
          <p>Selecciona dispositivos:</p>

          <div class="device-list">
            <v-card
              v-for="d in dispositivos"
              :key="d.id"
              class="device-item-modern"
              :class="{ selected: seleccionados.includes(d.id) }"
              @click="toggleSeleccion(d.id)"
            >
              <div class="device-info">
                <v-checkbox
                  :model-value="seleccionados.includes(d.id)"
                  hide-details
                />
                <div>
                  <div class="device-name">{{ d.nombre }}</div>
                  <div class="device-ip">{{ d.ip }}</div>
                </div>
              </div>
            </v-card>
          </div>

        </v-card-text>

        <v-card-actions>
          <v-btn variant="text" @click="dialogAplicar = false">
            Cancelar
          </v-btn>

          <v-btn color="primary" @click="aplicarAhora">
            Aplicar
          </v-btn>
        </v-card-actions>

      </v-card>
    </v-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUsuarioFirewallStore } from '@/stores/usuarioFirewallStore'
import UsuarioFirewallForm from '@/components/UsuarioFirewallForm.vue'
import { useDispositivoStore } from '@/stores/dispositivoStore'

const usuarioFirewallStore = useUsuarioFirewallStore()
const dispositivoStore = useDispositivoStore()

const mostrandoFormulario = ref(false)
const usuarioSeleccionado = ref(null)

/* ===================== */
/* DISPOSITIVOS */
/* ===================== */
const dispositivos = ref([])

/* ===================== */
/* APLICAR */
/* ===================== */
const dialogAplicar = ref(false)
const usuarioAplicar = ref(null)
const seleccionados = ref([])

const props = defineProps({
  dispositivoId: Number,
  modo: {
    type: String,
    default: 'simple'
  }
})

/* ===================== */
/* LOAD */
/* ===================== */
onMounted(async () => {
  await usuarioFirewallStore.cargarUsuariosPorUsuario()

  await dispositivoStore.getMisDispositivos()
  dispositivos.value = dispositivoStore.dispositivos
})

/* ===================== */
/* CRUD */
/* ===================== */
const eliminarUsuario = async (id) => {
  try {
    await usuarioFirewallStore.eliminarUsuarioFirewall(id)
    await usuarioFirewallStore.cargarUsuariosPorUsuario()
  } catch (error) {
    console.error("Error eliminando usuario", error)
  }
}

const editarUsuario = (usuario) => {
  usuarioSeleccionado.value = { ...usuario }
  mostrandoFormulario.value = true
}

const mostrarCrear = () => {
  usuarioSeleccionado.value = null
  mostrandoFormulario.value = true
}

/* ===================== */
/* FORM */
/* ===================== */
const recargarYCerrar = async () => {
  mostrandoFormulario.value = false
  await usuarioFirewallStore.cargarUsuariosPorUsuario()
}

const cerrarFormulario = () => {
  mostrandoFormulario.value = false
}

/* ===================== */
/* APLICAR */
/* ===================== */
const aplicarUsuarioToDispositivos = (usuario) => {
  usuarioAplicar.value = usuario
  seleccionados.value = []
  dialogAplicar.value = true
}

const toggleSeleccion = (id) => {
  if (seleccionados.value.includes(id)) {
    seleccionados.value = seleccionados.value.filter(x => x !== id)
  } else {
    seleccionados.value.push(id)
  }
}

const aplicarAhora = async () => {
  await usuarioFirewallStore.asignarUsuarioFirewallADispositivos(
    usuarioAplicar.value.id,
    seleccionados.value
  )

  dialogAplicar.value = false
}
</script>

<style scoped>
.usuarios-firewall-wrapper {
  margin-top: 40px;
  padding: 30px;
  background: white;
  color: black;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
}

.formulario-inline {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  margin-bottom: 16px;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.table-header h2 {
  font-size: 20px;
  font-weight: 600;
  color: #0f172a;
}

.add-btn {
  background: #3b82f6;
  color: white;
  text-transform: none;
  font-weight: 500;
  border-radius: 8px;
}

.add-btn:hover {
  background: #2563eb;
}

.professional-table {
  background: white;
  border-radius: 12px;
  overflow: hidden;
}

.professional-table thead {
  background: #f8fafc;
}

.professional-table th {
  color: #64748b;
  font-size: 13px;
  font-weight: 600;
  padding: 14px;
  text-align: left;
}

.professional-table td {
  padding: 14px;
  font-size: 14px;
  color: #0f172a;
  border-top: 1px solid #f1f5f9;
}

.professional-table tbody tr:hover {
  background: #f8fafc;
}

.name {
  font-weight: 600;
}

/* MODAL */
.apply-card {
  border-radius: 16px;
  padding: 10px 0;
  box-shadow: 0 6px 28px rgba(0, 0, 0, 0.12);
}

.apply-title {
  font-size: 20px;
  font-weight: 600;
  color: #0f172a;
  padding: 20px 24px;
  border-bottom: 1px solid #e2e8f0;
}

/* LISTA DISPOSITIVOS */
.device-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.device-item-modern {
  padding: 14px 18px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  cursor: pointer;
  transition: all 0.25s ease;
  background: #ffffff;
}

.device-item-modern:hover {
  background: #f8fafc;
  border-color: #cbd5e1;
}

.device-item-modern.selected {
  border-color: #3b82f6;
  background: #eff6ff;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.25);
}

.device-info {
  display: flex;
  align-items: center;
  gap: 14px;
}

.device-name {
  font-weight: 600;
  color: #0f172a;
  font-size: 15px;
}

.device-ip {
  font-size: 13px;
  color: #64748b;
}
</style>