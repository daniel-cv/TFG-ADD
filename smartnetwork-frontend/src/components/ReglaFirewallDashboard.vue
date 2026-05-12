<template>
  <div class="services-wrapper">

    <!-- LISTADO -->
    <div v-if="!mostrandoFormulario">

      <div class="table-header">
        <h2>Firewall Policies</h2>

        <v-btn
          class="add-btn"
          size="small"
          @click="mostrarCrear"
        >
          Añadir Policy
        </v-btn>
      </div>

      <v-table class="professional-table">
        <thead>
          <tr>
            <th>Nombre</th>
            <th>Origen</th>
            <th>Destino</th>
            <th>IP Origen</th>
            <th>IP Destino</th>
            <th>Servicio</th>
            <th>Acciones</th>
          </tr>
        </thead>

        <tbody>
          <tr
            v-for="regla in reglas"
            :key="regla.id"
          >
            <td class="name">
              {{ regla.nombre }}
            </td>

            <td>{{ regla.origen }}</td>

            <td>{{ regla.destino }}</td>

            <td class="ip">
              {{ regla.iporigen }}
            </td>

            <td class="ip">
              {{ regla.ipdestino }}
            </td>

            <td>
              <span class="service-badge">
                {{ regla.servicio }}
              </span>
            </td>

            <td class="actions">
              <v-btn
                color="green"
                size="small"
                @click="editarRegla(regla)"
              >
                EDITAR
              </v-btn>

              <v-btn
                color="red"
                size="small"
                @click="eliminarRegla(regla.id)"
              >
                ELIMINAR
              </v-btn>

              <v-btn
                color="blue"
                size="small"
                @click="aplicarReglaToDispositivos(regla)"
              >
                APLICAR
              </v-btn>
            </td>
          </tr>
        </tbody>
      </v-table>
    </div>

    <!-- FORMULARIO -->
    <div v-else class="formulario-inline">

      <ReglaFirewallForm
        :regla-edit="reglaSeleccionada"
        :modo="props.modo"
        @creada="recargarYCerrar"
        @cancelar="cerrarFormulario"
      />
    </div>

    <!-- MODAL APLICAR -->
    <v-dialog v-model="dialogAplicar" max-width="650px">
      <v-card class="apply-card">

        <v-card-title class="apply-title">
          Aplicar Policy: {{ reglaAplicar?.nombre }}
        </v-card-title>

        <v-card-text>
          <p>Selecciona dispositivos:</p>

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
          <v-btn
            variant="text"
            @click="dialogAplicar = false"
          >
            Cancelar
          </v-btn>

          <v-btn
            color="primary"
            @click="aplicarAhora"
          >
            Aplicar
          </v-btn>
        </v-card-actions>

      </v-card>
    </v-dialog>

  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'

import { useReglaFirewallStore } from '@/stores/reglafirewallStore'
import { useDispositivoSeleccionadoStore } from "@/stores/dispositivoSeleccionadoStore"
import { useDispositivoStore } from '@/stores/dispositivoStore'

import ReglaFirewallForm from '@/components/ReglaFirewallForm.vue'

const props = defineProps({
  dispositivoId: Number,
  modo: {
    type: String,
    default: 'simple'
  }
})

const reglaStore = useReglaFirewallStore()
const seleccionadoStore = useDispositivoSeleccionadoStore()
const dispositivoStore = useDispositivoStore()

const reglas = ref([])

const mostrandoFormulario = ref(false)
const reglaSeleccionada = ref(null)

const dispositivoId = computed(
  () => seleccionadoStore.dispositivo?.id
)

/* ===================== */
/* DISPOSITIVOS */
/* ===================== */
const dispositivos = ref([])

/* ===================== */
/* APLICAR */
/* ===================== */
const dialogAplicar = ref(false)
const reglaAplicar = ref(null)
const seleccionados = ref([])

onMounted(async () => {
  await cargar()

  await dispositivoStore.getMisDispositivos()
  dispositivos.value = dispositivoStore.dispositivos
})

const cargar = async () => {

  // MODO FULL
  if (props.modo === 'full') {

    if (dispositivoId.value) {
      await reglaStore.cargarReglas(dispositivoId.value)
      reglas.value = reglaStore.reglas
    }
  }

  // MODO SIMPLE
  if (props.modo === 'simple') {
    await reglaStore.cargarReglasUsuario()
    reglas.value = reglaStore.reglas
  }
}

const mostrarCrear = () => {
  reglaSeleccionada.value = null
  mostrandoFormulario.value = true
}

const editarRegla = (regla) => {
  reglaSeleccionada.value = { ...regla }
  mostrandoFormulario.value = true
}

const eliminarRegla = async (id) => {

  if (confirm('¿Estás seguro?')) {

    try {

      await reglaStore.eliminarRegla(id)

      await cargar()

    } catch (error) {

      console.error("Error eliminando regla", error)
    }
  }
}

const recargarYCerrar = async () => {

  mostrandoFormulario.value = false

  await cargar()
}

const cerrarFormulario = () => {
  mostrandoFormulario.value = false
}

/* ===================== */
/* APLICAR */
/* ===================== */
const aplicarReglaToDispositivos = (regla) => {
  reglaAplicar.value = regla
  seleccionados.value = []
  dialogAplicar.value = true
}

const toggleSeleccion = (id) => {

  if (seleccionados.value.includes(id)) {

    seleccionados.value = seleccionados.value.filter(
      x => x !== id
    )

  } else {

    seleccionados.value.push(id)
  }
}

const aplicarAhora = async () => {

  try {

    await reglaStore.asignarRegla(
      reglaAplicar.value.id,
      seleccionados.value
    )

    dialogAplicar.value = false

  } catch (error) {

    console.error(
      "Error aplicando regla",
      error
    )
  }
}
</script>

<style scoped>

.services-wrapper {
  margin-top: 40px;
  padding: 30px;
  background: white;
  color: black;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
}

.formulario-inline {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  margin-bottom: 16px;
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
  background: #3b82f6 !important;
  color: white !important;
  text-transform: none;
  font-weight: 500;
  border-radius: 8px;
}

.add-btn:hover {
  background: #2563eb !important;
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

.ip {
  font-family: monospace;
  color: #334155;
}

.service-badge {
  background: #dbeafe;
  color: #1d4ed8;
  padding: 4px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}

.actions {
  display: flex;
  gap: 8px;
}

/* MODAL */
.apply-card {
  border-radius: 16px;
  padding: 10px 0;
  box-shadow: 0 6px 28px rgba(0, 0, 0, 0.12);
}

.apply-title {
  font-size: 20px;
  font-weight: 600;
  color: #0f172a;
  padding: 20px 24px;
  border-bottom: 1px solid #e2e8f0;
}

/* LISTA DISPOSITIVOS */
.device-list {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.device-item-modern {
  padding: 14px 18px;
  border-radius: 12px;
  border: 1px solid #e2e8f0;
  cursor: pointer;
  transition: all 0.25s ease;
  background: #ffffff;
}

.device-item-modern:hover {
  background: #f8fafc;
  border-color: #cbd5e1;
}

.device-item-modern.selected {
  border-color: #3b82f6;
  background: #eff6ff;
  box-shadow: 0 0 0 2px rgba(59, 130, 246, 0.25);
}

.device-info {
  display: flex;
  align-items: center;
  gap: 14px;
}

.device-name {
  font-weight: 600;
  color: #0f172a;
  font-size: 15px;
}

.device-ip {
  font-size: 13px;
  color: #64748b;
}

</style>