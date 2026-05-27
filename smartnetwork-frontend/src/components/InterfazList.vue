<template>
  <div class="service-list">

    <div v-if="!mostrandoFormulario">
      <div class="table-header">
        <h2>Interfaces</h2>
        <v-btn class="add-btn" size="small" @click="mostrarCrear()">Añadir Interfaz</v-btn>
      </div>

      <v-table class="professional-table">
        <thead>
          <tr>
            <th>Nombre</th>
            <th>Tipo</th>
            <th>VlanId</th>
            <th>VDOM</th>
            <th>Modo</th>
            <th>Interfaz Padre</th>
            <th>AllowAccess</th>
            <th>Rol</th>
            <th>Descripción</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="interfaz in interfaces" :key="interfaz.id">
            <td>{{ interfaz.name }}</td>
            <td>{{ interfaz.tipo }}</td>
            <td>{{ interfaz.vlanid }}</td>
            <td>{{ interfaz.vdom }}</td>
            <td>{{ interfaz.mode }}</td>
            <td>{{ interfaz.interfacePadre }}</td>
            <td>{{ interfaz.allowaccess }}</td>
            <td>{{ interfaz.role }}</td>
            <td>{{ interfaz.description }}</td>
            <td>
              <v-btn color="green" size="small" @click="editarInterfaz(interfaz)">EDITAR</v-btn>
              <v-btn color="red" size="small" @click="eliminarInterfaz(interfaz.id)">ELIMINAR</v-btn>
            </td>
          </tr>
        </tbody>
      </v-table>
    </div>

    <div v-else class="formulario-inline">
      <InterfazForm
        :dispositivo-id="dispositivoId"
        :interfaz-edit="interfazSeleccionada"
        :modo="props.modo"
        @creada="recargarYCerrar"
        @cancelar="cerrarFormulario"
      />
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useInterfazStore } from '@/stores/interfazStore'
import { useDispositivoSeleccionadoStore } from '@/stores/dispositivoSeleccionadoStore'
import InterfazForm from '@/components/InterfazForm.vue'
const props = defineProps({
  modo: {
    type: String,
    default: 'full'
  }
})
const interfazStore = useInterfazStore()
const seleccionadoStore = useDispositivoSeleccionadoStore()

const dispositivoId = seleccionadoStore.dispositivo.id

const interfaces = ref([])
const mostrandoFormulario = ref(false)
const interfazSeleccionada = ref(null)

const cargar = async () => {
  await interfazStore.cargarInterfaces(dispositivoId)
  interfaces.value = interfazStore.interfaces
}

onMounted(cargar)

const eliminarInterfaz = async (id) => {
  await interfazStore.eliminarInterfazEnDispositivos(id, [dispositivoId])
  await cargar()
}

const editarInterfaz = (i) => {
  interfazSeleccionada.value = i
  mostrandoFormulario.value = true
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

<style scoped>
.formulario-inline { background: white; border-radius: 12px; padding: 24px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); margin-bottom: 16px; }
.service-list { width: 100%; }
.table-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.add-btn { background: #3b82f6; color: white; border-radius: 8px; }
.add-btn:hover { background: #2563eb; }
.professional-table { background: white; border-radius: 12px; overflow: hidden; }
.professional-table thead { background: #f8fafc; }
.professional-table th { color: #64748b; font-size: 13px; font-weight: 600; padding: 14px; text-align: left; }
.professional-table td { padding: 14px; font-size: 14px; color: #0f172a; border-top: 1px solid #f1f5f9; }
.professional-table tbody tr:hover { background: #f8fafc; }
</style>
