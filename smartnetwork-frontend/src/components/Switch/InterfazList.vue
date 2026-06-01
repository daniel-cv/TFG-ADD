<template>
  <div class="service-list">
    <div v-if="!mostrandoFormulario">
      <div class="table-header">
        <h2>Interfaces</h2>
      </div>
      <v-table class="professional-table">
        <thead>
          <tr>
            <th>Nombre</th>
            <th>Estado</th>
            <th>Modo</th>
            <th>VLAN</th>
            <th>Descripción</th>
            <th>ACL</th> 
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="interfaz in interfazStore.interfaces" :key="interfaz.id">
            <td>{{ interfaz.name }}</td>
            <td>
              <span :class="[
                'badge',
                interfaz.enabled === false ? 'red' : 'green'
              ]">
                {{
                  interfaz.enabled === false
                    ? 'disconnected'
                    : interfaz.estado
                }}
              </span>
            </td>
            <td>{{ interfaz.mode }}</td>
            <td>
              <span v-if="interfaz.vlanAccess" class="badge blue">
                VLAN {{ interfaz.vlanAccess }}
              </span>
              <span v-else-if="interfaz.vlansTrunk?.length" class="badge gray">
                {{ interfaz.vlansTrunk.join(', ') }}
              </span>
              <span v-else class="badge gray">
                -
              </span>
            </td>
            <td>{{ interfaz.descripcion }}</td>
            <td>
              <span v-if="interfaz.aclIn" class="badge purple">
                {{ interfaz.aclIn }}
              </span>
              <span v-else class="badge gray">
                -
              </span>
            </td>
            <td>
              <v-btn color="green" size="small" @click="editarInterfaz(interfaz)">
                EDITAR
              </v-btn>
            </td>
          </tr>
        </tbody>
      </v-table>
    </div>

    <div v-else class="formulario-inline">
      <InterfazForm
        :dispositivo-id="dispositivoId"
        :interfaz-edit="interfazSeleccionada"
        @creada="recargarYCerrar"
        @cancelar="cerrarFormulario"
      />
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useInterfazStore } from '@/stores/Switches/interfacesStore'
import { useDispositivoSeleccionadoStore } from '@/stores/dispositivoSeleccionadoStore'
import InterfazForm from '@/components/Switch/InterfazForm.vue'

const interfazStore = useInterfazStore()
const seleccionadoStore = useDispositivoSeleccionadoStore()

const dispositivoId = seleccionadoStore.dispositivo.id

const mostrandoFormulario = ref(false)
const interfazSeleccionada = ref(null)

const eliminarInterfaz = async (id) => {
  await interfazStore.eliminarInterfaz(id)
  await interfazStore.cargarInterfaces(dispositivoId)
}

const editarInterfaz = (interfaz) => {
  interfazSeleccionada.value = { ...interfaz }
  mostrandoFormulario.value = true
}

const mostrarCrear = () => {
  interfazSeleccionada.value = null
  mostrandoFormulario.value = true
}

const recargarYCerrar = () => {
  mostrandoFormulario.value = false
  interfazStore.cargarInterfaces(dispositivoId)
}

const cerrarFormulario = () => {
  mostrandoFormulario.value = false
}

onMounted(() => {
  interfazStore.cargarInterfaces(dispositivoId)
})
</script>

<style scoped>
.service-list {
  width: 100%;
}

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.add-btn {
  background: #3b82f6;
  color: white;
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

/* FORM */
.formulario-inline {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  margin-bottom: 16px;
}

/* BADGES */
.badge {
  padding: 4px 10px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 500;
}

.green {
  background: #dcfce7;
  color: #166534;
}

.red {
  background: #fee2e2;
  color: #991b1b;
}

.blue {
  background: #dbeafe;
  color: #1e40af;
}

.gray {
  background: #e5e7eb;
  color: #374151;
}

/* 🔥 ACL COLOR */
.purple {
  background: #ede9fe;
  color: #5b21b6;
}
</style>
