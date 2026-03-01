<template>
  <div class="policy-list">

    <!-- HEADER -->
    <div class="table-header">
      <h2>Policies</h2>

      <v-btn
        class="add-btn"
        size="small"
        @click="emit('crear')"
      >
        Añadir Policy
      </v-btn>
    </div>


    <!-- TABLA -->
    <v-table class="professional-table">

      <thead>
        <tr>
          <th>Nombre</th>
          <th>Origen</th>
          <th>Destino</th>
          <th>IP Origen</th>
          <th>IP Destino</th>
          <th>Servicio</th>
        </tr>
      </thead>


      <tbody>
        <tr
          v-for="regla in reglaStore.reglas"
          :key="regla.id"
        >

          <td class="name">
            {{ regla.nombre }}
          </td>

          <td>
            {{ regla.origen }}
          </td>

          <td>
            {{ regla.destino }}
          </td>

          <td class="ip">
            {{ regla.ipOrigen }}
          </td>

          <td class="ip">
            {{ regla.ipDestino }}
          </td>

          <td>
            <span class="service-badge">
              {{ regla.servicio }}
            </span>
          </td>

        </tr>
      </tbody>

    </v-table>

  </div>
</template>



<script setup>
import { useRouter } from 'vue-router'
import { useReglaFirewallStore } from '@/stores/reglafirewallStore'
import { useDispositivoSeleccionadoStore } from "@/stores/dispositivoSeleccionadoStore"
import { onMounted } from 'vue'

const router = useRouter()
const seleccionadoStore = useDispositivoSeleccionadoStore()
const dispositivoId = seleccionadoStore.dispositivo.id
const emit = defineEmits(['crear'])
const reglaStore = useReglaFirewallStore()

onMounted(() => {
  reglaStore.cargarReglas(dispositivoId)
})


function irANuevaRegla() {
  router.push({
    name: 'crearpolicy',
    params: { id: dispositivoId }
  })
}
</script>



<style scoped>

/* CONTENEDOR */

.policy-list {
  width: 100%;
}


/* HEADER */

.table-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
}

.table-header h2 {
  font-size: 18px;
  font-weight: 600;
  color: #0f172a;
}


/* BOTÓN */

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


/* TABLA */

.professional-table {
  background: white;
  border-radius: 12px;
  overflow: hidden;
}


/* HEADER TABLA */

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


/* FILAS */

.professional-table td {
  padding: 14px;
  font-size: 14px;
  color: #0f172a;
  border-top: 1px solid #f1f5f9;
}

.professional-table tbody tr:hover {
  background: #f8fafc;
}


/* COLUMNAS ESPECIALES */

.name {
  font-weight: 600;
}

.ip {
  font-family: monospace;
  color: #334155;
}


/* BADGE SERVICIO */

.service-badge {
  background: #dbeafe;
  color: #1d4ed8;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

</style>