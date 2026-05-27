<template>
  <div class="services-wrapper">
    <div v-if="!mostrandoFormulario">
      <div class="table-header">
        <h2>Services</h2>
        <v-btn
          class="add-btn"
          size="small"
          @click="mostrarCrear"
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
            <th>Implementado</th>
            <th>Acciones</th>
          </tr>
        </thead>

        <tbody>
          <tr
            v-for="service in services"
            :key="service.id"
          >
            <td>{{ service.nombre }}</td>
            <td>{{ service.tipoProtocolo }}</td>
            <td>{{ service.ip }}</td>
            <td>{{ service.destinationPort }}</td>
            <td>{{ service.comentario || 'Sin comentario' }}</td>

            <td>
              <div v-if="implementaciones[service.id]?.length">
                <v-chip
                  v-for="nombre in implementaciones[service.id]"
                  :key="nombre"
                  size="x-small"
                  color="blue"
                   variant="flat"
                  class="ma-1"
                >
                  {{ nombre }}
                </v-chip>
              </div>

              <span v-else class="text-caption text-grey">
                No aplicado
              </span>
            </td>

            <td>

              <v-btn
                color="red"
                size="small"
                @click="abrirEliminar(service)"
              >
                ELIMINAR
              </v-btn>

              <v-btn
                color="blue"
                size="small"
                @click="aplicarService(service)"
              >
                APLICAR
              </v-btn>

            </td>
          </tr>
        </tbody>
      </v-table>
    </div>
    <div v-else class="formulario-inline">
      <ServiceForm
        :service-edit="serviceSeleccionado"
        modo="simple"
        @creada="recargarYCerrar"
        @cancelar="cerrarFormulario"
      />
    </div>

    <v-dialog v-model="dialogAplicar" max-width="650px">
      <v-card>

        <v-card-title>
          Aplicar Service
        </v-card-title>

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
                    {{ d.nombre }}
                  </div>

                  <div class="device-ip">
                    {{ d.ip }}
                  </div>
                </div>

              </div>

            </v-card>

          </div>

        </v-card-text>

        <v-card-actions>

          <v-btn @click="dialogAplicar = false">
            Cancelar
          </v-btn>

          <v-btn color="primary" @click="aplicarAhora">
            Aplicar
          </v-btn>

        </v-card-actions>

      </v-card>
    </v-dialog>

    <v-dialog v-model="dialogEliminar" max-width="650px">
      <v-card>

        <v-card-title>
          Eliminar Service
        </v-card-title>

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
                    {{ d.nombre }}
                  </div>

                  <div class="device-ip">
                    {{ d.ip }}
                  </div>
                </div>

              </div>

            </v-card>

          </div>

        </v-card-text>

        <v-card-actions>

          <v-btn @click="dialogEliminar = false">
            Cancelar
          </v-btn>

          <v-btn color="red" @click="eliminarAhora">
            Eliminar
          </v-btn>

        </v-card-actions>

      </v-card>
    </v-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useServiceStore } from '@/stores/serviceStore'
import { useDispositivoStore } from '@/stores/dispositivoStore'
import ServiceForm from '@/components/ServiceForm.vue'

const serviceStore = useServiceStore()
const dispositivoStore = useDispositivoStore()

const services = ref([])
const dispositivos = ref([])
const implementaciones = ref({})

const mostrandoFormulario = ref(false)

const serviceSeleccionado = ref(null)

const dialogAplicar = ref(false)
const serviceAplicar = ref(null)

const dialogEliminar = ref(false)
const serviceEliminar = ref(null)

const dialogEditar = ref(false)
const serviceEditar = ref(null)

const seleccionados = ref([])
const seleccionadosEliminar = ref([])

const dispositivosEditar = ref([])

onMounted(cargarDatos)

async function cargarDatos() {

  await Promise.all([
    serviceStore.cargarServicesUsuario(),
    dispositivoStore.getMisDispositivos()
  ])

  services.value = serviceStore.services
  dispositivos.value = dispositivoStore.dispositivos

  await mapearImplementaciones()
}

async function mapearImplementaciones() {

  const mapa = {}

  for (const disp of dispositivos.value) {

    const res = await serviceStore.cargarServices(disp.id)

    const lista = serviceStore.services

    lista.forEach(service => {

      if (!mapa[service.id]) {
        mapa[service.id] = []
      }

      mapa[service.id].push(disp.nombre)
    })
  }

  implementaciones.value = mapa
}

function mostrarCrear() {
  serviceSeleccionado.value = null
  mostrandoFormulario.value = true
}

function editarService(service) {

  serviceEditar.value = service

  const lista = implementaciones.value[service.id] || []

  dispositivosEditar.value = dispositivos.value.filter(
    d => lista.includes(d.nombre)
  )

  seleccionados.value = dispositivosEditar.value.map(d => d.id)

  dialogEditar.value = true
}

function confirmarEditar() {

  serviceSeleccionado.value = {
    ...serviceEditar.value,
    dispositivosIds: [...seleccionados.value]
  }

  dialogEditar.value = false
  mostrandoFormulario.value = true
}

function aplicarService(service) {
  serviceAplicar.value = service
  seleccionados.value = []
  dialogAplicar.value = true
}

function toggleSeleccion(id) {

  const i = seleccionados.value.indexOf(id)

  if (i > -1) {
    seleccionados.value.splice(i, 1)
  } else {
    seleccionados.value.push(id)
  }
}

async function aplicarAhora() {

  await serviceStore.asignarService(
    serviceAplicar.value.id,
    seleccionados.value
  )

  dialogAplicar.value = false

  await mapearImplementaciones()
}

function abrirEliminar(service) {

  serviceEliminar.value = service

  seleccionadosEliminar.value = []
  dialogEliminar.value = true
}

const dispositivosEliminar = computed(() => {

  if (!serviceEliminar.value) return []

  const lista = implementaciones.value[serviceEliminar.value.id] || []

  return dispositivos.value.filter(
    d => lista.includes(d.nombre)
  )
})

function toggleSeleccionEliminar(id) {

  const i = seleccionadosEliminar.value.indexOf(id)

  if (i > -1) {
    seleccionadosEliminar.value.splice(i, 1)
  } else {
    seleccionadosEliminar.value.push(id)
  }
}

async function eliminarAhora() {

  await serviceStore.eliminarServiceEnDispositivos(
    serviceEliminar.value.id,
    seleccionadosEliminar.value
  )

  dialogEliminar.value = false

  await cargarDatos()
}

async function recargarYCerrar() {

  mostrandoFormulario.value = false

  await cargarDatos()
}

function cerrarFormulario() {
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
