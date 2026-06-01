<template>
  <div class="services-wrapper">

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
            <th>Implementado</th>
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
            <td>

              <div
                v-if="implementaciones[regla.id]?.length"
              >

                <v-chip
                  v-for="nombreDisp in implementaciones[regla.id]"
                  :key="nombreDisp"
                  size="x-small"
                  color="blue"
                  class="ma-1"
                  variant="flat"
                >
                  {{ nombreDisp }}
                </v-chip>

              </div>

              <span
                v-else
                class="text-caption text-grey"
              >
                No aplicado
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
                @click="abrirEliminar(regla)"
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

    <div
      v-else
      class="formulario-inline"
    >

      <ReglaFirewallForm
        :regla-edit="reglaSeleccionada"
        modo="simple"
        @creada="recargarYCerrar"
        @cancelar="cerrarFormulario"
      />

    </div>

    <v-dialog
      v-model="dialogEditar"
      max-width="650px"
    >

      <v-card class="apply-card">

        <v-card-title class="apply-title">
          Editar Policy y Sincronizar
        </v-card-title>

        <v-card-text>

          <p>
            Selecciona dispositivos donde quieres actualizar:
          </p>

          <div class="device-list">

            <v-card
              v-for="d in dispositivosEditDisponibles"
              :key="d.id"
              class="device-item-modern"
              :class="{
                selected:
                  seleccionados.includes(d.id)
              }"
              @click="toggleSeleccion(d.id)"
            >

              <div class="device-info">

                <v-checkbox
                  :model-value="
                    seleccionados.includes(d.id)
                  "
                  hide-details
                />

                <div>

                  <div class="device-name">
                    {{
                      d.name ||
                      d.nombre ||
                      d.hostname ||
                      'Sin nombre'
                    }}
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
            @click="dialogEditar = false"
          >
            Cancelar
          </v-btn>

          <v-btn
            color="green"
            @click="confirmarEditar"
          >
            Continuar
          </v-btn>

        </v-card-actions>

      </v-card>

    </v-dialog>

    <v-dialog
      v-model="dialogAplicar"
      max-width="650px"
    >

      <v-card class="apply-card">

        <v-card-title class="apply-title">
          Aplicar Policy
        </v-card-title>

        <v-card-text>

          <p>
            Selecciona dispositivos:
          </p>

          <div class="device-list">

            <v-card
              v-for="d in dispositivos"
              :key="d.id"
              class="device-item-modern"
              :class="{
                selected:
                  seleccionados.includes(d.id)
              }"
              @click="toggleSeleccion(d.id)"
            >

              <div class="device-info">

                <v-checkbox
                  :model-value="
                    seleccionados.includes(d.id)
                  "
                  hide-details
                />

                <div>

                  <div class="device-name">
                    {{
                      d.name ||
                      d.nombre ||
                      d.hostname ||
                      'Sin nombre'
                    }}
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

    <v-dialog
      v-model="dialogEliminar"
      max-width="650px"
    >

      <v-card class="apply-card">

        <v-card-title class="apply-title">
          Eliminar Policy
        </v-card-title>

        <v-card-text>

          <p>
            Selecciona dispositivos:
          </p>

          <div class="device-list">

            <v-card
              v-for="d in dispositivosEliminar"
              :key="d.id"
              class="device-item-modern"
              :class="{
                selected:
                  seleccionadosEliminar.includes(d.id)
              }"
              @click="
                toggleSeleccionEliminar(d.id)
              "
            >

              <div class="device-info">

                <v-checkbox
                  :model-value="
                    seleccionadosEliminar.includes(d.id)
                  "
                  hide-details
                />

                <div>

                  <div class="device-name">
                    {{
                      d.name ||
                      d.nombre ||
                      d.hostname ||
                      'Sin nombre'
                    }}
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
            @click="dialogEliminar = false"
          >
            Cancelar
          </v-btn>

          <v-btn
            color="red"
            @click="eliminarAhora"
          >
            Eliminar
          </v-btn>

        </v-card-actions>

      </v-card>

    </v-dialog>

  </div>
</template>

<script setup>
import {ref,onMounted,computed} from 'vue'
import { useReglaFirewallStore} from '@/stores/reglafirewallStore'
import { useDispositivoStore} from '@/stores/dispositivoStore'
import ReglaFirewallForm from '@/components/ReglaFirewallForm.vue'
import {preeliminarregla} from '@/services/reglaFirewallService';
const reglaStore = useReglaFirewallStore()
const dispositivoStore = useDispositivoStore()
const reglas = ref([])
const dispositivos = ref([])
const implementaciones = ref({})
const mostrandoFormulario = ref(false)
const reglaSeleccionada = ref(null)
const dialogEditar = ref(false)
const reglaEditar = ref(null)
const dispositivosEditDisponibles =ref([])
const dialogAplicar = ref(false)
const reglaAplicar = ref(null)
const seleccionados = ref([])
const dialogEliminar = ref(false)
const reglaEliminar = ref(null)
const seleccionadosEliminar = ref([])

const dispositivosEliminar = computed(() => {
  if (!reglaEliminar.value) return []
  const lista =implementaciones.value[reglaEliminar.value.id] || []
  return dispositivos.value.filter(d => {
    const nombre =d.name ||d.nombre ||d.hostname ||'Sin nombre'
    return lista.includes(nombre)
  })
})


const cargarDatos = async () => {
  await Promise.all([
    reglaStore.cargarReglasUsuario(),
    dispositivoStore.getMisDispositivos()
  ])
  reglas.value = reglaStore.reglas
  dispositivos.value =dispositivoStore.dispositivos
  await mapearImplementaciones()
}

const mapearImplementaciones = async () => {
  const mapa = {}
  for (const disp of dispositivos.value) {
    try {
      const res =await reglaStore.obtenerReglasDispositivo(disp.id)
      const reglasDisp = res || []
      reglasDisp.forEach(regla => {
        if (!mapa[regla.id]) {
          mapa[regla.id] = []
        }
        const nombre =disp.name || disp.nombre ||disp.hostname ||'Sin nombre'
        if (!mapa[regla.id].includes(nombre)) {
          mapa[regla.id].push(nombre)
        }
      })
    } catch (e) {
      console.error(e)
    }
  }
  implementaciones.value = mapa
}

onMounted(cargarDatos)

const mostrarCrear = () => {
  reglaSeleccionada.value = null
  mostrandoFormulario.value = true
}

const recargarYCerrar = async () => {
  mostrandoFormulario.value = false
  await cargarDatos()
  reglaSeleccionada.value = null
  seleccionados.value = []
}

const cerrarFormulario = () => {
  mostrandoFormulario.value = false
}
const editarRegla = async (regla) => {
  reglaEditar.value = regla
  await mapearImplementaciones()
  const lista =implementaciones.value[regla.id] || []
  if (!lista.length) {
    reglaSeleccionada.value = {
      ...regla,
      sinImplementacion: true
    }
    mostrandoFormulario.value = true
    return}
  dispositivosEditDisponibles.value = dispositivos.value.filter(d => {
      const nombre =d.name || d.nombre || d.hostname ||'Sin nombre'
      return lista.includes(nombre)
    })
  seleccionados.value = dispositivosEditDisponibles.value.map(d => d.id)
  dialogEditar.value = true
}

const confirmarEditar = () => {
  if (!seleccionados.value.length) {
    alert('Debes seleccionar al menos un dispositivo para continuar la edición')
    return
  }

  reglaSeleccionada.value = {
    ...reglaEditar.value,
    dispositivosIds: [...seleccionados.value]
  }

  dialogEditar.value = false
  mostrandoFormulario.value = true
}

const aplicarReglaToDispositivos = (regla) => {
  reglaAplicar.value = regla
  seleccionados.value = []
  dialogAplicar.value = true
}

const toggleSeleccion = (id) => {
  const i = seleccionados.value.indexOf(id)
  if (i > -1) {
    seleccionados.value.splice(i, 1)
  } else {
    seleccionados.value.push(id)
  }
}
const aplicarAhora = async () => {
 await reglaStore.asignarRegla( reglaAplicar.value.id, seleccionados.value)
  dialogAplicar.value = false
  await mapearImplementaciones()
}

const tieneImplementaciones = (reglaId) => {
  return (implementaciones.value[reglaId] || []).length > 0
}
const abrirEliminar = async (regla) => {
  if (!tieneImplementaciones(regla.id)) {
    await preeliminarregla(regla.id)
    await cargarDatos()
    return
  }

  reglaEliminar.value = regla
  seleccionadosEliminar.value = []
  dialogEliminar.value = true
}
const toggleSeleccionEliminar = (id) => {
  const i = seleccionadosEliminar.value.indexOf(id)
  if (i > -1) {
    seleccionadosEliminar.value.splice(i, 1)
  } else {
    seleccionadosEliminar.value.push(id)
  }
}
const eliminarAhora = async () => {
  if (!seleccionadosEliminar.value.length) return alert('Selecciona al menos un dispositivo')
  await reglaStore.eliminarReglaEnDispositivos( reglaEliminar.value.id,seleccionadosEliminar.value )
  dialogEliminar.value = false
  await cargarDatos()
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

.apply-card {
  border-radius: 16px;
  padding: 10px 0;
  box-shadow: 0 6px 28px rgba(0, 0, 0, 0.12);
}

.apply-title {
  font-size: 20px;
  font-weight: 600;
  color: #ffffff;
  padding: 20px 24px;
  border-bottom: 1px solid #e2e8f0;
}

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
  color: #000000;
  font-size: 15px;
}

.device-ip {
  font-size: 13px;
  color: #64748b;
}

</style>
