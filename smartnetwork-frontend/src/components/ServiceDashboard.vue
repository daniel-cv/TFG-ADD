<template>
  <div class="services-wrapper">

    <!-- LISTA -->
    <div v-if="!mostrandoFormulario">

      <!-- HEADER -->
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

      <!-- TABLA -->
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
            v-for="service in services"
            :key="service.id"
          >
            <td class="name">{{ service.nombre }}</td>
            <td>{{ service.tipoProtocolo }}</td>
            <td>{{ service.ip }}</td>
            <td>{{ service.destinationPort }}</td>
            <td class="comment">{{ service.comentario || 'Sin comentario' }}</td>

            <td>
              <v-btn
                class="rounded-0 px-4 me-2"
                color="green"
                size="small"
                @click="editarService(service)"
              >
                <span style="color: white; font-weight: bold;">EDITAR</span>
              </v-btn>

              <v-btn
                class="rounded-0 px-4 me-2"
                color="red"
                size="small"
                @click="eliminarService(service.id)"
              >
                <span style="color: white; font-weight: bold;">ELIMINAR</span>
              </v-btn>

              <v-btn
                class="rounded-0 px-4"
                color="blue"
                size="small"
                @click="aplicarServiceToDispositivos(service)"
              >
                <span style="color: white; font-weight: bold;">APLICAR</span>
              </v-btn>
            </td>
          </tr>
        </tbody>
      </v-table>
    </div>

    <!-- FORMULARIO -->
    <div v-else class="formulario-inline">
      <div class="">
        <ServiceForm
          :service-edit="serviceSeleccionado"
          modo="simple"
          @creada="recargarYCerrar"
          @cancelar="cerrarFormulario"
        />
      </div>
    </div>

    <!-- MODAL PARA APLICAR SERVICE -->
    <v-dialog v-model="dialogAplicar" max-width="600px">
      <v-card>
        <v-card-title class="text-h6">
          Aplicar Service
        </v-card-title>

        <v-card-text>
          <p>Selecciona los dispositivos donde quieres aplicar este service:</p>

          <div class="device-list">
            <v-card
              v-for="d in dispositivos"
              :key="d.id"
              class="device-item"
              :class="{ selected: seleccionados.includes(d.id) }"
              @click="toggleSeleccion(d.id)"
            >
              <v-checkbox
                :model-value="seleccionados.includes(d.id)"
                :label="d.nombre + ' (' + d.ip + ')'"
                hide-details
                @change="toggleSeleccion(d.id)"
              />
            </v-card>
          </div>
        </v-card-text>

        <v-card-actions>
          <v-spacer></v-spacer>

          <v-btn text @click="dialogAplicar = false">Cancelar</v-btn>

          <v-btn color="blue" @click="aplicarAhora">
            Aplicar ahora
          </v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useServiceStore } from '@/stores/serviceStore'
import { useDispositivoStore } from '@/stores/dispositivoStore'
import ServiceForm from '@/components/ServiceForm.vue'

const serviceStore = useServiceStore()
const dispositivoStore = useDispositivoStore()

const services = ref([])
const dispositivos = ref([])

const mostrandoFormulario = ref(false)
const serviceSeleccionado = ref(null)

const dialogAplicar = ref(false)
const serviceAAplicar = ref(null)
const seleccionados = ref([])

onMounted(async () => {
  await serviceStore.cargarServicesUsuario()
  services.value = serviceStore.services

  await dispositivoStore.getMisDispositivos()
  dispositivos.value = dispositivoStore.dispositivos
})

const aplicarServiceToDispositivos = (service) => {
  serviceAAplicar.value = service
  seleccionados.value = []
  dialogAplicar.value = true
}

const toggleSeleccion = (id) => {
  if (seleccionados.value.includes(id)) {
    seleccionados.value = seleccionados.value.filter(x => x !== id)
  } else {
    seleccionados.value.push(id)
  }
}

const aplicarAhora = async () => {
  try {
    await serviceStore.asignarService(
      serviceAAplicar.value.id,
      seleccionados.value
    )

    dialogAplicar.value = false

    await serviceStore.cargarServicesUsuario()
    services.value = serviceStore.services

  } catch (error) {
    console.error(error)
  }
}

const eliminarService = async (id) => {
  await serviceStore.eliminarService(id)
  await serviceStore.cargarServicesUsuario()
  services.value = serviceStore.services
}

const editarService = (service) => {
  serviceSeleccionado.value = { ...service }
  mostrandoFormulario.value = true
}

const mostrarCrear = () => {
  serviceSeleccionado.value = null
  mostrandoFormulario.value = true
}

const recargarYCerrar = async () => {
  mostrandoFormulario.value = false
  await serviceStore.cargarServicesUsuario()
  services.value = serviceStore.services
}

const cerrarFormulario = () => {
  mostrandoFormulario.value = false
}
</script>

<style scoped>
.services-wrapper {
  margin-top: 40px;
  padding: 30px;
  background: white;
  border-radius: 16px;
  color: black;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
}

.formulario-inline {
  margin-top: 20px;
}

.form-wrapper {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  width: 100%;
  display: block;
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
</style>
