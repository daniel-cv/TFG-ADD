<template>
  <div class="service-list">

    <!-- Si NO estamos mostrando el formulario, se muestra la lista -->
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
            <th>Máscara/IP Final</th>
            <th>Comentario</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr
            v-for="address in addressStore.addresses"
            :key="address.id"
          >
            <td class="name">{{ address.name }}</td>
            <td>{{ address.type }}</td>
            <td>{{ address.ip }}</td>
            <td>{{ address.interfaz_id }}</td>
            <td>{{ address.ipdestino || '' }}</td>
            <td><span class="comment">{{ address.comentario || 'Sin comentario' }}</span></td>
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
                class="rounded-0 px-4"
                color="red"
                size="small"
                @click="eliminarAddress(address.id)"
              >
                <span style="color: white; font-weight: bold;">ELIMINAR</span>
              </v-btn>
            </td>
          </tr>
        </tbody>
      </v-table>
    </div>

    <!-- FORMULARIO CREAR / EDITAR -->
    <div v-else class="formulario-inline">
      <AddressForm
        :dispositivo-id="dispositivoId"
        :address-edit="addressSeleccionada"
        @creada="recargarYCerrar"
        @cancelar="cerrarFormulario"
      />
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useAddressStore } from '@/stores/addressStores'
import { useDispositivoSeleccionadoStore } from "@/stores/dispositivoSeleccionadoStore"
import AddressForm from '@/components/AddressForm.vue'
import { useInterfazStore } from '@/stores/interfazStore'

const addressStore = useAddressStore()
const seleccionadoStore = useDispositivoSeleccionadoStore()
const dispositivoId = seleccionadoStore.dispositivo.id
const interfazStore = useInterfazStore() // <--- 2. DEFINIDO

const mostrandoFormulario = ref(false)
const addressSeleccionada = ref(null)

const eliminarAddress = async (id) => {
  try {
    await addressStore.eliminarAddress(id)
    await addressStore.cargarAddresses(dispositivoId)
  } catch (error) {
    console.error("Error eliminando address", error)
  }
}

const editarAddress = (address) => {
  addressSeleccionada.value = { ...address }
  mostrandoFormulario.value = true
}

const mostrarCrear = () => {
  addressSeleccionada.value = null
  mostrandoFormulario.value = true
}

const recargarYCerrar = () => {
  mostrandoFormulario.value = false
  addressStore.cargarAddresses(dispositivoId)
}

const cerrarFormulario = () => {
  mostrandoFormulario.value = false
}

onMounted(async() => {
  await addressStore.cargarAddresses(dispositivoId)
  await interfazStore.cargarInterfaces(dispositivoId) // <--- 3. CARGADO
})
const obtenerNombreInterfaz = (interfazId) => {
  if (!interfazId) return 'N/A';

  const interfaz = interfazStore.interfaces.find(
    i => Number(i.id) === Number(interfazId)
  );

  return interfaz ? interfaz.name : 'N/A';
}
</script>

<style scoped>
/* FORMULARIO EN BLANCO */
.formulario-inline {
  background: white;
  border-radius: 12px;
  padding: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.05);
  margin-bottom: 16px;
}

/* Mantener el resto del style igual que tu lista */
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

.v-text-field,
.v-select,
.v-textarea {
  background: white;
  border-radius: 6px;
}

.v-text-field input,
.v-select input,
.v-textarea textarea {
  color: #0f172a;
}

p.mensaje { margin-top: 12px; text-align: center; font-size: 14px; color: #ef4444; }
</style>