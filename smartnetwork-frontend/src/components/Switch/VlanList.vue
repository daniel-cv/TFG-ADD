<template>
  <div class="service-list">

    <!-- HEADER -->
    <div class="table-header">
      <h2>VLANs</h2>

      <button class="add-btn" @click="mostrarFormulario = !mostrarFormulario">
        + Nueva VLAN
      </button>
    </div>

    <!-- FORMULARIO CREAR -->
    <div v-if="mostrarFormulario" class="formulario-inline">
      <input v-model="nuevaVlan.vlanId" placeholder="VLAN ID" class="input-edit" type="number" />
      <input v-model="nuevaVlan.nombre" placeholder="Nombre" class="input-edit" />

      <button class="btn-guardar" @click="crear">
        Crear
      </button>
    </div>

    <!-- TABLA -->
    <table class="professional-table">

      <thead>
        <tr>
          <th>ID</th>
          <th>Nombre</th>
          <th>Acciones</th>
        </tr>
      </thead>

      <tbody>
        <tr v-for="vlan in vlanStore.vlans" :key="vlan.id">

          <!-- VLAN ID -->
          <td>
            <span class="badge blue">
              {{ vlan.vlanId }}
            </span>
          </td>

          <!-- NOMBRE -->
          <td>
            <input v-model="vlan.nombre" class="input-edit" />
          </td>

          <!-- ACCIONES -->
          <td>
            <button class="btn-guardar" @click="guardar(vlan)">
              Editar
            </button>

            <button class="btn-borrar" @click="borrar(vlan.id)">
              Borrar
            </button>
          </td>

        </tr>
      </tbody>

    </table>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useVlanStore } from '@/stores/Switches/vlanStore'
import { useDispositivoSeleccionadoStore } from '@/stores/dispositivoSeleccionadoStore'

const vlanStore = useVlanStore()
const dispositivoStore = useDispositivoSeleccionadoStore()

const mostrarFormulario = ref(false)

const nuevaVlan = ref({
  vlanId: null,
  nombre: ''
})

// 🔥 CREAR
const crear = async () => {
  await vlanStore.crearVlan({
    ...nuevaVlan.value,
    dispositivoId: dispositivoStore.dispositivo.id
  })

  nuevaVlan.value = { vlanId: null, nombre: '' }
  mostrarFormulario.value = false
}

// 🔥 EDITAR
const guardar = async (vlan) => {
  await vlanStore.actualizarVlan(vlan.id, vlan)
}

// 🔥 BORRAR
const borrar = async (id) => {
  await vlanStore.eliminarVlan(id)
}

// 🔥 CARGAR
onMounted(() => {
  if (dispositivoStore.dispositivo?.id) {
    vlanStore.cargarVlans(dispositivoStore.dispositivo.id)
  }
})
</script>
<style scoped>
/* CONTENEDOR */
.service-list {
  width: 100%;
}

/* HEADER */
.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

/* BOTÓN AÑADIR */
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

/* TABLA */
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

/* FORMULARIO */
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

/* INPUT */
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

/* BOTONES */
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
</style>
