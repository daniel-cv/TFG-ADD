<template>
  <div class="service-list">

    <!-- HEADER -->
    <div class="table-header">
      <h2>Services</h2>

      <v-btn
        class="add-btn"
        size="small"
        @click="emit('crear')"
      >
        Añadir Interfaz
      </v-btn>
    </div>


    <!-- TABLA -->
    <v-table class="professional-table">

      <thead>
        <tr>
          <th>Nombre</th>
          <th>Tipo</th>
          <th>VlanId</th>
          <th>Vdom</th>
          <th>Modo</th>
          <th>Ip</th>
          <th>AllowAccess</th>
          <th>Role</th>
          <th>Descripción</th>
          <th>Acciones</th>
        </tr>
      </thead>


      <tbody>
        <tr
          v-for="interfaz in interfazStore.reglas"
          :key="interfaz.id"
        >
          <td class="name">
            {{ interfaz.name }}
          </td>

          <td>
            {{ interfaz.tipo }}
          </td>

          <td>
            {{ interfaz.vlanid }}
          </td>

          <td>
            {{ interfaz.vdom }}
          </td>

          <td>
            <span>
              {{ interfaz.mode }}
            </span>
          </td>

          <td>
            {{ interfaz.ip }}
          </td>

          <td>
            {{ interfaz.allowaccess }}
          </td>

          <td>
            {{ interfaz.role }}
          </td>

          <td>
            {{ interfaz.description }}
          </td>
          
          <td>
            <v-btn
                    class="rounded-0 px-4"
                    color="red"
                    size="small"
                    @click="eliminarInterfaz(interfaz.id)"
                   >
                  <span style="color: white; font-weight: bold;">ELIMINAR</span>
                </v-btn>
          </td>

        </tr>
      </tbody>

    </v-table>

  </div>
</template>



<script setup>
import { useInterfazStore } from '@/stores/interfazStore'
import { useDispositivoSeleccionadoStore } from "@/stores/dispositivoSeleccionadoStore"
import { onMounted } from 'vue'

const seleccionadoStore = useDispositivoSeleccionadoStore()
const dispositivoId = seleccionadoStore.dispositivo.id
const emit = defineEmits(['crear'])
const interfazStore = useInterfazStore()

const eliminarInterfaz = async (id) => {
  try {
    await interfazStore.eliminarInterfaz(id)
    await interfazStore.cargarInterfaces(dispositivoId)
  } catch (error) {
    console.error("Error eliminando address", error)
  }
}

onMounted(() => {
  interfazStore.cargarInterfaces(dispositivoId)
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