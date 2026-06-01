<template>
  <div class="service-list">
    <div class="table-header">
      <h2>IP Routes</h2>

      <button class="add-btn" @click="mostrarFormulario = !mostrarFormulario">
        + Nueva Route
      </button>
    </div>
    <div v-if="mostrarFormulario" class="formulario-inline">
      <input v-model="nuevaRoute.ipDestino" placeholder="Destino (0.0.0.0)" class="input-edit"/>
      <input v-model="nuevaRoute.mascara" placeholder="Máscara (24)" class="input-edit"/>
      <input v-model="nuevaRoute.gateway" placeholder="Gateway (192.168.1.1)" class="input-edit"/>
      <button class="btn-guardar" @click="crear">Crear</button>
    </div>
    <table class="professional-table">
      <thead>
        <tr>
          <th>Destino</th>
          <th>Máscara</th>
          <th>Gateway</th>
          <th>Acciones</th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="route in ipRouteStore.ipRoutes" :key="route.id">

          <td>
            <input v-model="route.ipDestino" class="input-edit"/>
          </td>

          <td>
            <input v-model="route.mascara" class="input-edit"/>
          </td>

          <td>
            <input v-model="route.gateway" class="input-edit"/>
          </td>

          <td>
            <button class="btn-guardar" @click="guardar(route)">Editar</button>
            <button class="btn-borrar" @click="borrar(route.id)">Borrar</button>
          </td>

        </tr>
      </tbody>
    </table>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useIpRouteStore } from '@/stores/Switches/ipRouteStore'
import { useDispositivoSeleccionadoStore } from '@/stores/dispositivoSeleccionadoStore'

const ipRouteStore = useIpRouteStore()
const dispositivoStore = useDispositivoSeleccionadoStore()

const mostrarFormulario = ref(false)

const nuevaRoute = ref({
  ipDestino: '',
  mascara: '',
  gateway: ''
})

const crear = async () => {

  const payload = {
    ...nuevaRoute.value,
    dispositivoId: dispositivoStore.dispositivo.id
  }

  console.log("PAYLOAD QUE ENVÍAS:", payload)

  await ipRouteStore.crearIpRoute(payload)

  nuevaRoute.value = {
    ipDestino: '',
    mascara: '',
    gateway: ''
  }

  mostrarFormulario.value = false

  await ipRouteStore.cargarIpRoutes(dispositivoStore.dispositivo.id)
}

const guardar = async (route) => {
  await ipRouteStore.actualizarIpRoute(route.id, route)
}

const borrar = async (id) => {
  await ipRouteStore.eliminarIpRoute(id)
  await ipRouteStore.cargarIpRoutes(dispositivoStore.dispositivo.id)
}

onMounted(() => {
  console.log("ROUTE dispositivo:", dispositivoStore.dispositivo)
  if (dispositivoStore.dispositivo?.id) {
    ipRouteStore.cargarIpRoutes(dispositivoStore.dispositivo.id)
  }
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
  padding: 8px 14px;
  border: none;
  cursor: pointer;
  font-weight: 500;
}

.add-btn:hover {
  background: #2563eb;
}

.professional-table {
  width: 100%;
  background: white;
  border-radius: 12px;
  overflow: hidden;
  border-collapse: collapse;
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

.formulario-inline {
  display: flex;
  gap: 12px;
  align-items: center;
  background: white;
  border-radius: 12px;
  padding: 16px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  margin-bottom: 16px;
}

.input-edit {
  width: 100%;
  padding: 6px 10px;
  border: 1px solid #e5e7eb;
  border-radius: 6px;
  font-size: 13px;
  color: black;
}

.input-edit:focus {
  outline: none;
  border-color: #3b82f6;
}

.btn-guardar {
  background: #22c55e;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 6px;
  cursor: pointer;
  margin-right: 6px;
}

.btn-guardar:hover {
  background: #16a34a;
}

.btn-borrar {
  background: #ef4444;
  color: white;
  border: none;
  padding: 6px 12px;
  border-radius: 6px;
  cursor: pointer;
}

.btn-borrar:hover {
  background: #dc2626;
}

.badge {
  padding: 4px 10px;
  border-radius: 10px;
  font-size: 12px;
  font-weight: 500;
}

.blue {
  background: #dbeafe;
  color: #1e40af;
}
</style>
