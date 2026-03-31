<template>
  <div>

    <!-- PANEL DE CONTROL (SE MANTIENE IGUAL) -->
    <h1>Panel de Control</h1>
    <p>Bienvenido al sistema de gestión de red</p>

    <router-link to="/devices">Mis dispositivos</router-link>

    <!-- CONTENEDOR PROFESIONAL PARA LA TABLA -->
    <div class="addresses-wrapper">

      <!-- LISTA -->
      <div v-if="!mostrandoFormulario">

        <!-- HEADER -->
        <div class="table-header">
          <h2>Addresses</h2>

          <v-btn
            class="add-btn"
            size="small"
            @click="mostrarCrear()"
          >
            Añadir Address
          </v-btn>
        </div>

        <!-- TABLA -->
        <v-table class="professional-table">
          <thead>
            <tr>
              <th>Nombre</th>
              <th>Tipo</th>
              <th>IP/IP inicio</th>
              <th>Interfaz</th>
              <th>Comentario</th>
              <th>Máscara/IP Final</th>
              <th>Acciones</th>
            </tr>
          </thead>

          <tbody>
            <tr
              v-for="address in addresses"
              :key="address.id"
            >
              <td class="name">{{ address.name }}</td>
              <td>{{ address.type }}</td>
              <td>{{ address.ip }}</td>
              <td>{{ address.interfaz ? address.interfaz.name : 'N/A' }}</td>
              <td><span class="comment">{{ address.comentario || 'Sin comentario' }}</span></td>
              <td>{{ address.ipdestino || '' }}</td>

              <td>
                <v-btn
                  class="rounded-0 px-4 me-2"
                  color="green"
                  size="small"
                  @click="editarAddress(address)"
                >
                  <span style="color: white; font-weight: bold;">EDITAR</span>
                </v-btn>

                <v-btn
                  class="rounded-0 px-4 me-2"
                  color="red"
                  size="small"
                  @click="eliminarAddress(address.id)"
                >
                  <span style="color: white; font-weight: bold;">ELIMINAR</span>
                </v-btn>

                <v-btn
                  class="rounded-0 px-4"
                  color="blue"
                  size="small"
                  @click="aplicarAddressToDispositivos(address)"
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
        <AddressForm
          :address-edit="addressSeleccionada"
          @creada="recargarYCerrar"
          @cancelar="cerrarFormulario"
        />
      </div>

      <!-- MODAL PARA APLICAR ADDRESS -->
      <v-dialog v-model="dialogAplicar" max-width="600px">
        <v-card>
          <v-card-title class="text-h6">
            Aplicar Address
          </v-card-title>

          <v-card-text>
            <p>Selecciona los dispositivos donde quieres aplicar esta address:</p>

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

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAddressStore } from '@/stores/addressStores'
import AddressForm from '@/components/AddressForm.vue'
import { useDispositivoStore } from '@/stores/dispositivoStore'

const addressStore = useAddressStore()
const dispositivoStore = useDispositivoStore()

const addresses = ref([])
const dispositivos = ref([])

const mostrandoFormulario = ref(false)
const addressSeleccionada = ref(null)

const dialogAplicar = ref(false)
const addressAAplicar = ref(null)
const seleccionados = ref([])

onMounted(async () => {
  await addressStore.obtenerMisAddresses()
  addresses.value = addressStore.addresses

  await dispositivoStore.getMisDispositivos()
  dispositivos.value = dispositivoStore.dispositivos
})

const aplicarAddressToDispositivos = (address) => {
  addressAAplicar.value = address
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
  await addressStore.aplicarAddressToDispositivos(addressAAplicar.value.id, seleccionados.value)
  dialogAplicar.value = false
}

const eliminarAddress = async (id) => {
  await addressStore.eliminarAddress(id)
  await addressStore.obtenerMisAddresses()
  addresses.value = addressStore.addresses
}

const editarAddress = (address) => {
  addressSeleccionada.value = { ...address }
  mostrandoFormulario.value = true
}

const mostrarCrear = () => {
  addressSeleccionada.value = null
  mostrandoFormulario.value = true
}

const recargarYCerrar = async () => {
  mostrandoFormulario.value = false
  await addressStore.obtenerMisAddresses()
  addresses.value = addressStore.addresses
}

const cerrarFormulario = () => {
  mostrandoFormulario.value = false
}
</script>

<style scoped>
/* CONTENEDOR CON MARGEN PROFESIONAL */
.addresses-wrapper {
  margin-top: 40px;
  padding: 30px;
  background: white;
  border-radius: 16px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.08);
}

/* FORMULARIO */
.formulario-inline {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  margin-bottom: 16px;
}

/* HEADER */
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

/* LISTA DE DISPOSITIVOS EN EL MODAL */
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
