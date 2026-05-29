<template>
  <div class="service-list">
    <div v-if="!mostrandoFormulario">

      <div class="table-header">
        <h2>Services</h2>

        <v-btn
          class="add-btn"
          size="small"
          @click="mostrarCrear()"
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
            <th>Comentario</th>
            <th>Acciones</th>
          </tr>
        </thead>

        <tbody>
          <tr
            v-for="service in serviceStore.services"
            :key="service.id"
          >
            <td class="name">{{ service.nombre }}</td>
            <td>{{ service.tipoProtocolo }}</td>
            <td>{{ service.ip }}</td>
            <td>{{ service.destinationPort }}</td>
            <td class="comment">{{ service.comentario }}</td>

            <td>

              <v-btn
                class="rounded-0 px-4"
                color="red"
                size="small"
                @click="eliminarService(service.id)"
              >
                <span style="color: white; font-weight: bold;">ELIMINAR</span>
              </v-btn>
            </td>
          </tr>
        </tbody>
      </v-table>
    </div>
    <div v-else class="formulario-inline">
      <ServiceForm
        :service-edit="serviceSeleccionado"
        :dispositivo-id="dispositivoId"
        :modo="props.modo"
        @creada="recargarYCerrar"
        @actualizado="recargarYCerrar"
        @cancelar="cerrarFormulario"
      />
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useServiceStore } from '@/stores/serviceStore'
import { useDispositivoSeleccionadoStore } from "@/stores/dispositivoSeleccionadoStore"
import ServiceForm from '@/components/ServiceForm.vue'

const props = defineProps({
  dispositivoId: Number,
  modo: {
    type: String,
    default: 'simple'
  }
})

const serviceStore = useServiceStore()
const seleccionadoStore = useDispositivoSeleccionadoStore()

const dispositivoId = computed(() => seleccionadoStore.dispositivo?.id)

const mostrandoFormulario = ref(false)
const serviceSeleccionado = ref(null)

const mostrarCrear = () => {
  serviceSeleccionado.value = null
  mostrandoFormulario.value = true
}

const editarService = (service) => {
  serviceSeleccionado.value = { ...service }
  mostrandoFormulario.value = true
}

const eliminarService = async (id) => {
  try {
    await serviceStore.eliminarService(id)
    await serviceStore.cargarServices(dispositivoId.value)
  } catch (error) {
    console.error("Error eliminando service", error)
  }
}

const recargarYCerrar = async () => {
  mostrandoFormulario.value = false
  await serviceStore.cargarServices(dispositivoId.value)
}

const cerrarFormulario = () => {
  mostrandoFormulario.value = false
}

onMounted(() => {
  if (dispositivoId.value) {
    serviceStore.cargarServices(dispositivoId.value)
  }
})
</script>

<style scoped>
.formulario-inline {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  margin-bottom: 16px;
}

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

.category {
  background: #e0f2fe;
  color: #0369a1;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}
</style>
