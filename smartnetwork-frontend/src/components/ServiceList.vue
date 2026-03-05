<template>
  <div class="service-list">

    <div class="table-header">
      <h2>Services</h2>

      <v-btn
        class="add-btn"
        size="small"
        @click="emit('crear')"
      >
        Añadir Service
      </v-btn>
    </div>

    <v-table class="professional-table">

      <thead>
        <tr>
          <th>Nombre</th>
          <th>Protocolo</th>
          <th>IP</th>
          <th>Puerto</th>
          <th>Categoría</th>
          <th>Comentario</th>
          <th>Acciones</th>
        </tr>
      </thead>

      <tbody>

        <tr
          v-for="service in serviceStore.services"
          :key="service.id"
        >

          <td class="name">
            {{ service.nombre }}
          </td>

          <td>
            {{ service.tipoProtocolo }}
          </td>

          <td>
            {{ service.ip }}
          </td>

          <td>
            {{ service.destinationPort }}
          </td>

          <td>
            <span class="category">
              {{ service.categoria }}
            </span>
          </td>

          <td class="comment">
            {{ service.comentario }}
          </td>

          <td>

            <v-btn
              class="rounded-0 px-4"
              color="red"
              size="small"
              @click="eliminarService(service.id)"
            >
              <span style="color: white; font-weight: bold;">
                ELIMINAR
              </span>
            </v-btn>

          </td>

        </tr>

      </tbody>

    </v-table>

  </div>
</template>

<script setup>
import { useServiceStore } from '@/stores/serviceStore'
import { useDispositivoSeleccionadoStore } from "@/stores/dispositivoSeleccionadoStore"
import { onMounted, computed } from 'vue'

const seleccionadoStore = useDispositivoSeleccionadoStore()
const emit = defineEmits(['crear'])
const serviceStore = useServiceStore()

const dispositivoId = computed(() => seleccionadoStore.dispositivo?.id)

const eliminarService = async (id) => {
  try {
    await serviceStore.eliminarService(id)

    if (dispositivoId.value) {
      await serviceStore.cargarServices(dispositivoId.value)
    }

  } catch (error) {
    console.error("Error eliminando service", error)
  }
}

onMounted(() => {
  if (dispositivoId.value) {
    serviceStore.cargarServices(dispositivoId.value)
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

.comment {
  color: #64748b;
}


/* BADGE */

.category {
  background: #e0f2fe;
  color: #0369a1;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

</style>