<template>
  <div class="policy-list">

    <div v-if="!mostrandoFormulario">
      <div class="table-header">
        <h2>Firewall Policies</h2>
        <v-btn class="add-btn" size="small" @click="mostrarCrear">
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
          <tr v-for="regla in reglaStore.reglas" :key="regla.id">
            <td class="name">{{ regla.nombre }}</td>
            <td>{{ regla.origen }}</td>
            <td>{{ regla.destino }}</td>
            <td class="ip">{{ regla.iporigen }}</td>
            <td class="ip">{{ regla.ipdestino }}</td>
            <td>
              <span class="service-badge">{{ regla.servicio }}</span>
            </td>
            <td>
              <v-btn
                class="rounded-0 px-4 me-2"
                color="green"
                size="small"
                @click="editarRegla(regla)"
              >
                <span style="color: white; font-weight: bold;">EDITAR</span>
              </v-btn>
              <v-btn
                class="rounded-0 px-4"
                color="red"
                size="small"
                @click="eliminarRegla(regla.id)"
              >
                <span style="color: white; font-weight: bold;">ELIMINAR</span>
              </v-btn>
            </td>
          </tr>
        </tbody>
      </v-table>
    </div>

    <div v-else class="formulario-inline">
      <ReglaFirewallForm
        :regla-edit="reglaSeleccionada"
        @creada="recargarYCerrar"
        :modo="props.modo"
        :dispositivo-id="dispositivoId"
        @cancelar="cerrarFormulario"
      />
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted, computed } from 'vue'
import { useReglaFirewallStore } from '@/stores/reglafirewallStore'
import { useDispositivoSeleccionadoStore } from "@/stores/dispositivoSeleccionadoStore"
import ReglaFirewallForm from '@/components/ReglaFirewallForm.vue'



const reglaStore = useReglaFirewallStore()
const seleccionadoStore = useDispositivoSeleccionadoStore()

const mostrandoFormulario = ref(false)
const reglaSeleccionada = ref(null)
const dispositivoId = computed(() => seleccionadoStore.dispositivo?.id)

const props = defineProps({
  dispositivoId: Number,
  modo: {
    type: String,
    default: 'simple'
  }
})
console.log(props.modo)
const eliminarRegla = async (id) => {
  try {
  await reglaStore.eliminarRegla(id)
    if (dispositivoId.value) {
      await reglaStore.cargarReglas(dispositivoId.value)
    }
  } catch (error) {
    console.error("Error eliminando regla", error)
  }
}

const editarRegla = (regla) => {
  reglaSeleccionada.value = { ...regla }
  mostrandoFormulario.value = true
}

const mostrarCrear = () => {
  reglaSeleccionada.value = null
  mostrandoFormulario.value = true
}

const recargarYCerrar = () => {
  mostrandoFormulario.value = false
  if (dispositivoId.value) {
    reglaStore.cargarReglas(dispositivoId.value)
  }
}

const cerrarFormulario = () => {
  mostrandoFormulario.value = false
}

onMounted(() => {
  if (dispositivoId.value) {
    reglaStore.cargarReglas(dispositivoId.value)
  }
})
</script>

<style scoped>
/* ESTILOS UNIFICADOS */
.policy-list { width: 100%; }
.formulario-inline { background: white; border-radius: 12px; padding: 24px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); margin-bottom: 16px; }
.table-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.table-header h2 { font-size: 18px; font-weight: 600; color: #0f172a; }
.add-btn { background: #3b82f6 !important; color: white !important; text-transform: none; border-radius: 8px; }
.professional-table { background: white; border-radius: 12px; overflow: hidden; border: 1px solid #f1f5f9; }
.professional-table thead { background: #f8fafc; }
.professional-table th { color: #64748b; font-size: 13px; font-weight: 600; padding: 14px; text-align: left; }
.professional-table td { padding: 14px; font-size: 14px; color: #0f172a; border-top: 1px solid #f1f5f9; }
.professional-table tbody tr:hover { background: #f8fafc; }
.name { font-weight: 600; }
.ip { font-family: monospace; color: #334155; }
.service-badge { background: #dbeafe; color: #1d4ed8; padding: 4px 10px; border-radius: 20px; font-size: 12px; font-weight: 500; }
</style>
