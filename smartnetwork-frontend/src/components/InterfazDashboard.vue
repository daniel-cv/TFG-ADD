<template>
  <div class="services-wrapper">
    <div v-if="!mostrandoFormulario">

      <div class="table-header">
        <h2>Interfaces</h2>

        <v-btn class="add-btn" size="small" @click="mostrarCrear">
          Añadir Interfaz
        </v-btn>
      </div>

      <v-table class="professional-table">
        <thead>
          <tr>
            <th>Nombre</th>
            <th>Tipo</th>
            <th>VlanId</th>
            <th>VDOM</th>
            <th>Interfaz Padre</th>
            <th>AllowAccess</th>
            <th>Rol</th>
            <th>Descripción</th>
            <th>Implementado</th>
            <th>Acciones</th>
          </tr>
        </thead>

        <tbody>
          <tr v-for="interfaz in interfaces" :key="interfaz.id">
            <td class="name">{{ interfaz.name }}</td>
            <td>{{ interfaz.tipo }}</td>
            <td>{{ interfaz.vlanid || "-" }}</td>
            <td>{{ interfaz.vdom }}</td>
            <td>{{ interfaz.interfacePadre || "-"}}</td>
            <td>{{ interfaz.allowaccess || "-"}}</td>
            <td>{{ interfaz.role || "-"}}</td>
            <td>{{ interfaz.description || "-"}}</td>

            <td>
              <div v-if="implementaciones[interfaz.id]?.length">
                <v-chip
                  v-for="nombre in implementaciones[interfaz.id]"
                  :key="nombre"
                  size="x-small"
                  color="blue"
                  class="ma-1"
                  variant="flat"
                >
                  {{ nombre }}
                </v-chip>
              </div>
              <span v-else class="text-caption text-grey">
                No aplicado
              </span>
            </td>

            <td>
              <v-btn color="green" size="small" @click="editarInterfaz(interfaz)">
                EDITAR
              </v-btn>

              <v-btn color="red" size="small" @click="abrirEliminar(interfaz)">
                ELIMINAR
              </v-btn>

              <v-btn color="blue" size="small" @click="asignarInterfaz(interfaz)">
                APLICAR
              </v-btn>
            </td>
          </tr>
        </tbody>
      </v-table>
    </div>

    <div v-else class="formulario-inline">
      <InterfazForm
        :interfaz-edit="interfazSeleccionada"
        modo="simple"
        @creada="recargarYCerrar"
        @cancelar="cerrarFormulario"
      />
    </div>

    <v-dialog v-model="dialogEditar" max-width="650px">
      <v-card class="apply-card">
        <v-card-title class="apply-title">
          Editar Interfaz
        </v-card-title>

        <v-card-text>
          <p>Selecciona dispositivos:</p>

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
                    {{ d.name || d.nombre || d.hostname || 'Sin nombre' }}
                  </div>
                  <div class="device-ip">{{ d.ip }}</div>
                </div>
              </div>
            </v-card>
          </div>
        </v-card-text>

        <v-card-actions>
          <v-btn variant="text" @click="dialogEditar = false">
            Cancelar
          </v-btn>
          <v-btn color="green" @click="confirmarEditar">
            Continuar
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
    <v-dialog v-model="dialogAplicar" max-width="650px">
      <v-card class="apply-card">
        <v-card-title class="apply-title">Aplicar Interfaz</v-card-title>

        <v-card-text>
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
                    {{ d.name || d.nombre || d.hostname || 'Sin nombre' }}
                  </div>
                  <div class="device-ip">{{ d.ip }}</div>
                </div>
              </div>
            </v-card>
          </div>
        </v-card-text>

        <v-card-actions>
          <v-btn variant="text" @click="dialogAplicar = false">Cancelar</v-btn>
          <v-btn color="primary" @click="aplicarAhora">Aplicar</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

    <v-dialog v-model="dialogEliminar" max-width="650px">
      <v-card class="apply-card">
        <v-card-title class="apply-title">Eliminar Interfaz</v-card-title>

        <v-card-text>
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
                    {{ d.name || d.nombre || d.hostname || 'Sin nombre' }}
                  </div>
                  <div class="device-ip">{{ d.ip }}</div>
                </div>
              </div>
            </v-card>
          </div>
        </v-card-text>

        <v-card-actions>
          <v-btn variant="text" @click="dialogEliminar = false">Cancelar</v-btn>
          <v-btn color="red" @click="eliminarAhora">Eliminar</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useInterfazStore } from '@/stores/interfazStore'
import { useDispositivoStore } from '@/stores/dispositivoStore'
import InterfazForm from '@/components/InterfazForm.vue'
import { preeliminarInterfaz } from '@/services/interfazService'
const interfazStore = useInterfazStore()
const dispositivoStore = useDispositivoStore()

const interfaces = ref([])
const dispositivos = ref([])
const implementaciones = ref({})

const mostrandoFormulario = ref(false)
const interfazSeleccionada = ref(null)

const dialogEditar = ref(false)
const interfazEditar = ref(null)
const dispositivosEditDisponibles = ref([])

const dialogAplicar = ref(false)
const interfazAplicar = ref(null)
const seleccionados = ref([])

const dialogEliminar = ref(false)
const interfazEliminar = ref(null)
const seleccionadosEliminar = ref([])

const cargar = async () => {
  await Promise.all([
    interfazStore.cargarInterfacesUsuario(),
    dispositivoStore.getMisDispositivos()
  ])

  interfaces.value = interfazStore.interfaces
  dispositivos.value = dispositivoStore.dispositivos

  await mapearImplementaciones()
}

const mapearImplementaciones = async () => {
  const mapa = {}

  for (const disp of dispositivos.value) {
    try {
      const res = await interfazStore.cargarInterfaces(disp.id)

      const lista = res || interfazStore.interfaces

      if (Array.isArray(lista)) {
        const nombre = disp.name || disp.nombre || disp.hostname || 'Sin nombre'

        lista.forEach(interfaz => {
          if (!mapa[interfaz.id]) mapa[interfaz.id] = []
          if (!mapa[interfaz.id].includes(nombre)) {
            mapa[interfaz.id].push(nombre)
          }
        })
      }

    } catch (e) {
      console.error(e)
    }
  }

  implementaciones.value = mapa
}

onMounted(cargar)

const editarInterfaz = async (interfaz) => {
  interfazEditar.value = interfaz

  await mapearImplementaciones()

  const lista = implementaciones.value[interfaz.id] || []
  if (!lista.length) {
    interfazSeleccionada.value = {
      ...interfaz,
      sinImplementacion: true
    }
    mostrandoFormulario.value = true
    return
  }
  dispositivosEditDisponibles.value = dispositivos.value.filter(d => {
    const nombre = d.name || d.nombre || d.hostname || 'Sin nombre'
    return lista.includes(nombre)
  })

  seleccionados.value = dispositivosEditDisponibles.value.map(d => d.id)

  dialogEditar.value = true
}

const confirmarEditar = () => {
  if (!seleccionados.value.length) {
    alert('Debes seleccionar al menos un dispositivo para continuar')
    return
  }

  interfazSeleccionada.value = {
    ...interfazEditar.value,
    dispositivosId: [...seleccionados.value]
  }

  dialogEditar.value = false
  mostrandoFormulario.value = true
}

const asignarInterfaz = (interfaz) => {
  interfazAplicar.value = interfaz
  seleccionados.value = []
  dialogAplicar.value = true
}

const toggleSeleccion = (id) => {
  const i = seleccionados.value.indexOf(id)
  if (i > -1) seleccionados.value.splice(i, 1)
  else seleccionados.value.push(id)
}

const aplicarAhora = async () => {
  if (!seleccionados.value.length) return alert('Selecciona al menos uno')

  await interfazStore.asignarInterfaz(
    interfazAplicar.value.id,
    seleccionados.value
  )

  dialogAplicar.value = false
  await mapearImplementaciones()
}

const dispositivosEliminar = computed(() => {
  if (!interfazEliminar.value) return []

  const lista = implementaciones.value[interfazEliminar.value.id] || []

  return dispositivos.value.filter(d => {
    const nombre = d.name || d.nombre || d.hostname || 'Sin nombre'
    return lista.includes(nombre)
  })
})

const abrirEliminar = async (interfaz) => {
  if (!tieneImplementaciones(interfaz.id)) {
    await preeliminarInterfaz(interfaz.id)
    await cargar()
    return
  }
  interfazEliminar.value = interfaz
  seleccionadosEliminar.value = []
  dialogEliminar.value = true
}
const tieneImplementaciones = (interfazId) => {
  return (implementaciones.value[interfazId] || []).length > 0
}
const toggleSeleccionEliminar = (id) => {
  const i = seleccionadosEliminar.value.indexOf(id)
  if (i > -1) seleccionadosEliminar.value.splice(i, 1)
  else seleccionadosEliminar.value.push(id)
}

const eliminarAhora = async () => {
  if (!seleccionadosEliminar.value.length) return alert('Selecciona al menos un dispositivo')
  await interfazStore.eliminarInterfazEnDispositivos(interfazEliminar.value.id,seleccionadosEliminar.value)
  dialogEliminar.value = false
  await cargar()
}

const mostrarCrear = () => {
  interfazSeleccionada.value = null
  mostrandoFormulario.value = true
}

const recargarYCerrar = async () => {
  mostrandoFormulario.value = false
  await cargar()
}

const cerrarFormulario = () => {
  mostrandoFormulario.value = false
}
</script>
<style>
.services-wrapper {
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

.comment {
  color: #64748b;
}

.device-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.device-item {
  padding: 10px;
  border-radius: 10px;
  border: 1px solid #e2e8f0;
  cursor: pointer;
  transition: 0.2s;
}

.device-item:hover {
  background: #f8fafc;
}

.device-item.selected {
  border-color: #3b82f6;
  background: #eff6ff;
}

.apply-card {
  border-radius: 16px;
  padding: 10px 0;
  box-shadow: 0 6px 28px rgba(0, 0, 0, 0.12);
}

.apply-title {
  font-size: 20px;
  font-weight: 600;
  color: #ffffff;
  padding: 20px 24px;
  border-bottom: 1px solid #e2e8f0;
}

.apply-body {
  padding: 20px 24px;
}

.apply-description {
  color: #475569;
  margin-bottom: 16px;
  font-size: 15px;
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
  border-color: #f7faff;
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

.apply-actions {
  padding: 16px 24px;
  border-top: 1px solid #e2e8f0;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.apply-btn {
  font-weight: 600;
  padding: 8px 18px;
  border-radius: 8px;
}
.name {
  font-weight: 600;
}
</style>
