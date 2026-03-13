<template>
  <div class="service-list">

    <div v-if="!mostrandoFormulario">
      <div class="table-header">
        <h2>Virtual IPs</h2>
        <v-btn class="add-btn" size="small" @click="mostrarCrear()">Añadir Virtual IP</v-btn>
      </div>

      <v-table class="professional-table">
        <thead>
          <tr>
            <th>Nombre</th>
            <th>Interfaz</th>
            <th>Tipo</th>
            <th>IP Externa</th>
            <th>IP Interna</th>
            <th>Comentarios</th>
            <th>Acciones</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="vip in virtualIpStore.virtualips" :key="vip.id">
            <td class="name">{{ vip.name }}</td>
            <td>{{ vip.interfazId }}</td>
            <td>{{ vip.type }}</td>
            <td>{{ vip.externalIp }}</td>
            <td>{{ vip.internalIp }}</td>
            <td class="comment">{{ vip.comments }}</td>
            <td>
              <v-btn color="green" size="small" class="mr-2" @click="editarVip(vip)">EDITAR</v-btn>
              <v-btn color="red" size="small" @click="eliminarVip(vip.id)">ELIMINAR</v-btn>
            </td>
          </tr>
        </tbody>
      </v-table>
    </div>

    <div v-else class="formulario-inline">
      <VirtualIpForm
        :dispositivo-id="dispositivoId"
        :virtual-ip-edit="vipSeleccionada"
        @creada="recargarYCerrar"
        @cancelar="cerrarFormulario"
      />
    </div>

  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useVirtualIpStore } from '@/stores/virtualIpStore'
import { useDispositivoSeleccionadoStore } from '@/stores/dispositivoSeleccionadoStore'
import VirtualIpForm from '@/components/VirtualIpForm.vue' // Asegúrate de que el nombre sea correcto

const virtualIpStore = useVirtualIpStore()
const seleccionadoStore = useDispositivoSeleccionadoStore()
const dispositivoId = seleccionadoStore.dispositivo.id

const mostrandoFormulario = ref(false)
const vipSeleccionada = ref(null)

// ACCIONES
const eliminarVip = async (id) => {
  if (confirm('¿Estás seguro de eliminar esta Virtual IP?')) {
    try {
      await virtualIpStore.eliminarVirtualIp(id)
      await virtualIpStore.cargarVirtualIps(dispositivoId)
    } catch (error) {
      console.error('Error eliminando Virtual IP', error)
    }
  }
}

const editarVip = (vip) => {
  vipSeleccionada.value = { ...vip }
  mostrandoFormulario.value = true
}

const mostrarCrear = () => {
  vipSeleccionada.value = null
  mostrandoFormulario.value = true
}

const recargarYCerrar = () => {
  mostrandoFormulario.value = false
  virtualIpStore.cargarVirtualIps(dispositivoId)
}

const cerrarFormulario = () => {
  mostrandoFormulario.value = false
}

onMounted(() => {
  virtualIpStore.cargarVirtualIps(dispositivoId)
})
</script>

<style scoped>
/* ESTILOS IDÉNTICOS A INTERFACES */
.formulario-inline { background: white; border-radius: 12px; padding: 24px; box-shadow: 0 2px 8px rgba(0,0,0,0.05); margin-bottom: 16px; }
.service-list { width: 100%; }
.table-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.table-header h2 { font-size: 18px; font-weight: 600; color: #0f172a; }
.add-btn { background: #3b82f6 !important; color: white !important; border-radius: 8px; text-transform: none; }
.professional-table { background: white; border-radius: 12px; overflow: hidden; border: 1px solid #f1f5f9; }
.professional-table thead { background: #f8fafc; }
.professional-table th { color: #64748b; font-size: 13px; font-weight: 600; padding: 14px; text-align: left; }
.professional-table td { padding: 14px; font-size: 14px; color: #0f172a; border-top: 1px solid #f1f5f9; }
.professional-table tbody tr:hover { background: #f8fafc; }
.name { font-weight: 600; }
.comment { color: #64748b; }
</style>