<template>
  <div class="usuarios-firewall-wrapper">
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
            <th>Autenticación</th>
            <th>Implementado</th>
            <th>Acciones</th>
          </tr>
        </thead>

        <tbody>
          <tr
            v-for="usuario in usuarios"
            :key="usuario.id"
          >
            <td class="name">{{ usuario.nombre }}</td>
            <td>{{ usuario.email }}</td>
            <td>{{ usuario.tipo }}</td>
            <td>
              <div v-if="implementaciones[usuario.id]?.length">
                <v-chip
                  v-for="nombreDisp in implementaciones[usuario.id]"
                  :key="nombreDisp"
                  size="x-small"
                  color="blue"
                  class="ma-1"
                  variant="flat"
                >
                  {{ nombreDisp }}
                </v-chip>
              </div>

              <span
                v-else
                class="text-caption text-grey"
              >
                No aplicado
              </span>
            </td>

            <td>

              <v-btn
                color="green"
                size="small"
                @click="editarUsuario(usuario)"
              >
                EDITAR
              </v-btn>

              <v-btn
                color="red"
                size="small"
                @click="abrirEliminar(usuario)"
              >
                ELIMINAR
              </v-btn>

              <v-btn
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
    <div v-else class="formulario-inline">
      <UsuarioFirewallForm
        :usuario-edit="usuarioSeleccionado"
        :dispositivo-id="props.dispositivoId"
        :modo="props.modo"
        @creado="recargarYCerrar"
        @cancelar="cerrarFormulario"
      />
    </div>
    <v-dialog v-model="dialogEditar" max-width="650px">
      <v-card class="apply-card">

        <v-card-title class="apply-title">
          Editar Usuario y Sincronizar
        </v-card-title>

        <v-card-text>

          <p>Selecciona dispositivos donde actualizar:</p>

          <div class="device-list">

            <v-card
              v-for="d in dispositivosEditDisponibles"
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
                  <div class="device-name">
                    {{ d.nombre || d.name || d.hostname }}
                  </div>

                  <div class="device-ip">
                    {{ d.ip }}
                  </div>
                </div>

              </div>

            </v-card>

          </div>

        </v-card-text>

        <v-card-actions>

          <v-btn
            variant="text"
            @click="dialogEditar = false"
          >
            Cancelar
          </v-btn>

          <v-btn
            color="green"
            @click="confirmarEditar"
          >
            Continuar a Edición
          </v-btn>

        </v-card-actions>

      </v-card>
    </v-dialog>
    <v-dialog v-model="dialogAplicar" max-width="650px">
      <v-card class="apply-card">

        <v-card-title class="apply-title">
          Aplicar Usuario
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
                  <div class="device-name">
                    {{ d.nombre || d.name || d.hostname }}
                  </div>

                  <div class="device-ip">
                    {{ d.ip }}
                  </div>
                </div>

              </div>

            </v-card>

          </div>

        </v-card-text>

        <v-card-actions>

          <v-btn
            variant="text"
            @click="dialogAplicar = false"
          >
            Cancelar
          </v-btn>

          <v-btn
            color="primary"
            @click="aplicarAhora"
          >
            Aplicar
          </v-btn>

        </v-card-actions>

      </v-card>
    </v-dialog>
    <v-dialog v-model="dialogEliminar" max-width="650px">
      <v-card class="apply-card">

        <v-card-title class="apply-title">
          Eliminar Usuario
        </v-card-title>

        <v-card-text>

          <p>Selecciona dispositivos donde eliminar:</p>

          <div class="device-list">

            <v-card
              v-for="d in dispositivosEliminar"
              :key="d.id"
              class="device-item-modern"
              :class="{ selected: seleccionadosEliminar.includes(d.id) }"
              @click="toggleSeleccionEliminar(d.id)"
            >

              <div class="device-info">

                <v-checkbox
                  :model-value="seleccionadosEliminar.includes(d.id)"
                  hide-details
                />

                <div>
                  <div class="device-name">
                    {{ d.nombre || d.name || d.hostname }}
                  </div>

                  <div class="device-ip">
                    {{ d.ip }}
                  </div>
                </div>

              </div>

            </v-card>

          </div>

        </v-card-text>

        <v-card-actions>

          <v-btn
            variant="text"
            @click="dialogEliminar = false"
          >
            Cancelar
          </v-btn>

          <v-btn
            color="red"
            @click="eliminarAhora"
          >
            Eliminar
          </v-btn>

        </v-card-actions>

      </v-card>
    </v-dialog>

  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'

import { useUsuarioFirewallStore } from '@/stores/usuarioFirewallStore'
import { useDispositivoStore } from '@/stores/dispositivoStore'

import UsuarioFirewallForm from '@/components/UsuarioFirewallForm.vue'

const usuarioFirewallStore = useUsuarioFirewallStore()
const dispositivoStore = useDispositivoStore()

const usuarios = ref([])
const dispositivos = ref([])
const implementaciones = ref({})

const mostrandoFormulario = ref(false)

const usuarioSeleccionado = ref(null)

const dialogEditar = ref(false)
const usuarioEditar = ref(null)
const dispositivosEditDisponibles = ref([])

const dialogAplicar = ref(false)
const usuarioAplicar = ref(null)

const seleccionados = ref([])

const dialogEliminar = ref(false)
const usuarioEliminar = ref(null)

const seleccionadosEliminar = ref([])

const props = defineProps({
  dispositivoId: Number,
  modo: {
    type: String,
    default: 'simple'
  }
})

const obtenerNombreDispositivo = (d) => {
  return d.name || d.nombre || d.hostname || 'Sin nombre'
}

const cargarDatos = async () => {
  await Promise.all([
    usuarioFirewallStore.cargarUsuariosPorUsuario(),
    dispositivoStore.getMisDispositivos()
  ])
  usuarios.value = usuarioFirewallStore.usuarios
  dispositivos.value = dispositivoStore.dispositivos
  await mapearImplementaciones()
}
const mapearImplementaciones = async () => {
  const mapa = {}
  for (const disp of dispositivos.value) {
    try {
      const usuariosDisp =await usuarioFirewallStore.cargarUsuariosPorDispositivo(disp.id)
      if (Array.isArray(usuariosDisp)) {
        const nombre = obtenerNombreDispositivo(disp)
        usuariosDisp.forEach(usuario => {
          if (!mapa[usuario.id]) {
            mapa[usuario.id] = []
          }
          if (!mapa[usuario.id].includes(nombre)) {
            mapa[usuario.id].push(nombre)
          }
        })
      }
    } catch (e) {
      console.error(e)
    }
  }
  implementaciones.value = mapa
  console.log('IMPLEMENTACIONES =>', mapa)
}

onMounted(cargarDatos)

const dispositivosEliminar = computed(() => {
  if (!usuarioEliminar.value) return []
  const lista = implementaciones.value[usuarioEliminar.value.id] || []
  return dispositivos.value.filter(d => {
    const nombre = obtenerNombreDispositivo(d)
    return lista.includes(nombre)
  })
})

const mostrarCrear = () => {
  usuarioSeleccionado.value = null
  mostrandoFormulario.value = true
}

const editarUsuario = async (usuario) => {
  usuarioEditar.value = usuario
  await mapearImplementaciones()
  const lista = implementaciones.value[usuario.id] || []
  dispositivosEditDisponibles.value = dispositivos.value.filter(d => {
    const nombre = obtenerNombreDispositivo(d)
    return lista.includes(nombre)
  })
  seleccionados.value = dispositivosEditDisponibles.value.map(d => d.id)
  dialogEditar.value = true
}

const confirmarEditar = () => {
  usuarioSeleccionado.value = {
    ...usuarioEditar.value,
    dispositivosIds: [...seleccionados.value]
  }
  dialogEditar.value = false
  mostrandoFormulario.value = true
}

const recargarYCerrar = async () => {
  mostrandoFormulario.value = false
  await usuarioFirewallStore.cargarUsuariosPorUsuario()
  usuarios.value = usuarioFirewallStore.usuarios
  usuarioSeleccionado.value = null
  seleccionados.value = []
  await mapearImplementaciones()
}

const cerrarFormulario = () => {
  mostrandoFormulario.value = false
}

const aplicarUsuarioToDispositivos = (usuario) => {
  usuarioAplicar.value = usuario
  seleccionados.value = []
  dialogAplicar.value = true
}

const toggleSeleccion = (id) => {
  const i = seleccionados.value.indexOf(id)
  if (i > -1) {
    seleccionados.value.splice(i, 1)
  } else {
    seleccionados.value.push(id)
  }
}

const aplicarAhora = async () => {

  if (!seleccionados.value.length) {
    return alert('Selecciona al menos un dispositivo')
  }
  await usuarioFirewallStore.asignarUsuarioFirewallADispositivos(
    usuarioAplicar.value.id,
    seleccionados.value
  )
  dialogAplicar.value = false
  await mapearImplementaciones()
}

const abrirEliminar = (usuario) => {
  usuarioEliminar.value = usuario
  seleccionadosEliminar.value = []
  dialogEliminar.value = true
}
const toggleSeleccionEliminar = (id) => {
  const i = seleccionadosEliminar.value.indexOf(id)
  if (i > -1) {
    seleccionadosEliminar.value.splice(i, 1)
  } else {
    seleccionadosEliminar.value.push(id)
  }
}

const eliminarAhora = async () => {
  if (!seleccionadosEliminar.value.length) {
    return alert('Selecciona al menos un dispositivo')
  }
  await usuarioFirewallStore.eliminarUsuarioFirewallEnDispositivos(
    usuarioEliminar.value.id,
    seleccionadosEliminar.value
  )
  dialogEliminar.value = false
  await usuarioFirewallStore.cargarUsuariosPorUsuario()
  usuarios.value = usuarioFirewallStore.usuarios
  await mapearImplementaciones()
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