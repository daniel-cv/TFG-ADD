<template>
  <div class="service-list">

    <!-- SI NO MOSTRAMOS FORMULARIO, MOSTRAMOS LISTA -->
    <div v-if="!mostrandoFormulario">

      <!-- HEADER -->
      <div class="table-header">
        <h2>Usuarios de Firewall</h2>

        <v-btn
          class="add-btn"
          size="small"
          @click="mostrarCrear()"
        >
          Añadir Usuario
        </v-btn>
      </div>

      <!-- TABLA -->
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
          <tr
            v-for="usuario in usuarioFirewallStore.usuarios"
            :key="usuario.id"
          >
            <td class="name">{{ usuario.nombre}}</td>
            <td>{{ usuario.email }}</td>
            <td>{{ usuario.tipo }}</td>
            <td>
              <v-btn
                class="rounded-0 px-4 me-2"
                color="green"
                size="small"
                @click="editarUsuario(usuario)"
              >
                <span style="color: white; font-weight: bold;">EDITAR</span>
              </v-btn>
              <v-btn
                class="rounded-0 px-4"
                color="red"
                size="small"
                @click="eliminarUsuario(usuario.id)"
              >
                <span style="color: white; font-weight: bold;">ELIMINAR</span>
              </v-btn>
            </td>
          </tr>
        </tbody>
      </v-table>

    </div>

    <!-- FORMULARIO CREAR / EDITAR -->
    <div v-else class="formulario-inline">
      <UsuarioFirewallForm
        :dispositivo-id="dispositivoId"
        :usuario-edit="usuarioSeleccionado"
        :modo="props.modo"
        @creado="recargarYCerrar"
        @cancelar="cerrarFormulario"
      />
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useUsuarioFirewallStore } from '@/stores/usuarioFirewallStore'
import { useDispositivoSeleccionadoStore } from "@/stores/dispositivoSeleccionadoStore"
import UsuarioFirewallForm from '@/components/UsuarioFirewallForm.vue'

const usuarioFirewallStore = useUsuarioFirewallStore()
const seleccionadoStore = useDispositivoSeleccionadoStore()
const dispositivoId = seleccionadoStore.dispositivo.id

const mostrandoFormulario = ref(false)
const usuarioSeleccionado = ref(null)

const props = defineProps({
  dispositivoId: Number,
  modo: {
    type: String,
    default: 'simple'
  }
})

const eliminarUsuario = async (id) => {
  try {
    await usuarioFirewallStore.eliminarUsuarioFirewall(id)
    await usuarioFirewallStore.cargarUsuariosPorDispositivo(dispositivoId)
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

const recargarYCerrar = () => {
  mostrandoFormulario.value = false
  usuarioFirewallStore.cargarUsuariosPorDispositivo(dispositivoId)
}

const cerrarFormulario = () => {
  mostrandoFormulario.value = false
}

onMounted(() => {
  usuarioFirewallStore.cargarUsuariosPorDispositivo(dispositivoId)
})
</script>

<style scoped>
/* FORMULARIO EN BLANCO */
.formulario-inline {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  margin-bottom: 16px;
}

/* Mantener estilos de lista */
.service-list { width: 100%; }

.table-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.table-header h2 { font-size: 18px; font-weight: 600; color: #0f172a; }

.add-btn { background: #3b82f6; color: white; text-transform: none; font-weight: 500; border-radius: 8px; }
.add-btn:hover { background: #2563eb; }

.professional-table { background: white; border-radius: 12px; overflow: hidden; }
.professional-table thead { background: #f8fafc; }
.professional-table th { color: #64748b; font-size: 13px; font-weight: 600; padding: 14px; text-align: left; }
.professional-table td { padding: 14px; font-size: 14px; color: #0f172a; border-top: 1px solid #f1f5f9; }
.professional-table tbody tr:hover { background: #f8fafc; }
.name { font-weight: 600; }
.comment { color: #64748b; }
</style>
