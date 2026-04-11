<template>
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
        modo="simple"
        @creada="recargarYCerrar"
        @cancelar="cerrarFormulario"
      />
    </div>

    <!-- MODAL PARA APLICAR ADDRESS -->
    <v-dialog v-model="dialogAplicar" max-width="650px">
  <v-card class="apply-card">

    <v-card-title class="apply-title">
      Aplicar Address
    </v-card-title>

    <v-card-text class="apply-body">
      <p class="apply-description">
        Selecciona los dispositivos donde quieres aplicar esta address:
      </p>

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
              color="primary"
            />
            <div>
              <div class="device-name">{{ d.nombre }}</div>
              <div class="device-ip">{{ d.ip }}</div>
            </div>
          </div>
        </v-card>
      </div>
    </v-card-text>

    <v-card-actions class="apply-actions">
      <v-btn variant="text" @click="dialogAplicar = false">
        Cancelar
      </v-btn>

      <v-btn color="primary" class="apply-btn" @click="aplicarAhora">
        Aplicar ahora
      </v-btn>
    </v-card-actions>

  </v-card>
</v-dialog>

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
  try {
    await addressStore.aplicarAddressToDispositivos(
      addressAAplicar.value.id,
      seleccionados.value
    )

    dialogAplicar.value = false

    await addressStore.obtenerMisAddresses()
    addresses.value = addressStore.addresses

  } catch (error) {
    console.error(error)
  }
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
.addresses-wrapper {
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

/* --- LISTA DE DISPOSITIVOS (ANTIGUA) --- */
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

/* --- MODAL APLICAR ADDRESS --- */
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

.apply-body {
  padding: 20px 24px;
}

.apply-description {
  color: #475569;
  margin-bottom: 16px;
  font-size: 15px;
}

/* --- LISTA DE DISPOSITIVOS MODERNA --- */
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

/* --- INFO DEL DISPOSITIVO --- */
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

/* --- ACCIONES DEL MODAL --- */
.apply-actions {
  padding: 16px 24px;
  border-top: 1px solid #e2e8f0;
  display: flex;
  justify-content: flex-end;
  gap: 12px;
}

.apply-btn {
  font-weight: 600;
  padding: 8px 18px;
  border-radius: 8px;
}
</style>
