<template>
  <div class="service-list">
    <div class="table-header">
      <h2>Logs</h2>
      <v-btn class="refresh-btn" size="small" @click="cargarLogs" :loading="cargando">
        Actualizar
      </v-btn>
    </div>

    <div v-if="error" class="error-message">{{ error }}</div>
    <div v-else-if="cargando" class="loading-message">Cargando logs...</div>
    <div v-else>
      <v-table class="professional-table" v-if="logs.length">
        <thead>
          <tr>
            <th>ID</th>
            <th>Fecha</th>
            <th>Nivel</th>
            <th>Mensaje</th>
            <th>Detalles</th>
          </tr>
        </thead>
        <tbody>
          <tr v-for="(log, index) in logs" :key="log.id || index">
            <td>{{ log.id ?? '-' }}</td>
            <td>{{ formatoFecha(log) || '-' }}</td>
            <td>{{ formatoNivel(log) || '-' }}</td>
            <td>{{ formatoMensaje(log) || '-' }}</td>
            <td>
              <pre class="json-data">{{ formatoDetalles(log) }}</pre>
            </td>
          </tr>
        </tbody>
      </v-table>

      <div v-else class="empty-state">
        No hay logs disponibles para este dispositivo.
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { obtenerLogsPorDispositivo } from '@/services/logService'

const props = defineProps({
  deviceId: {
    type: [String, Number],
    required: true
  },
  modo: {
    type: String,
    default: 'full'
  }
})

const logs = ref([])
const cargando = ref(false)
const error = ref(null)

const cargarLogs = async () => {
  if (!props.deviceId) return
  cargando.value = true
  error.value = null

  try {
    const response = await obtenerLogsPorDispositivo(props.deviceId)
    logs.value = response.data || []
  } catch (err) {
    error.value = err?.response?.data?.message || err?.message || 'Error cargando logs.'
  } finally {
    cargando.value = false
  }
}

const formatoFecha = (log) => {
  return log.timestamp || log.fecha || log.date || log.createdAt || ''
}

const formatoNivel = (log) => {
  return log.level || log.severity || log.tipo || log.levelName || ''
}

const formatoMensaje = (log) => {
  return log.message || log.mensaje || log.descripcion || log.detail || ''
}

const formatoDetalles = (log) => {
  const { id, timestamp, fecha, date, createdAt, level, severity, tipo, levelName, message, mensaje, descripcion, detail, ...rest } = log
  const extra = Object.keys(rest).length ? JSON.stringify(rest, null, 2) : ''
  return extra
}

onMounted(cargarLogs)
</script>

<style scoped>
.service-list { width: 100%; }
.table-header { display: flex; justify-content: space-between; align-items: center; margin-bottom: 16px; }
.refresh-btn { background: #3b82f6; color: white; border-radius: 8px; }
.refresh-btn:hover { background: #2563eb; }
.professional-table { background: white; border-radius: 12px; overflow: hidden; width: 100%; }
.professional-table thead { background: #f8fafc; }
.professional-table th { color: #64748b; font-size: 13px; font-weight: 600; padding: 14px; text-align: left; }
.professional-table td { padding: 14px; font-size: 14px; color: #0f172a; border-top: 1px solid #f1f5f9; vertical-align: top; }
.professional-table tbody tr:hover { background: #f8fafc; }
.error-message { color: #dc2626; margin-bottom: 16px; }
.loading-message, .empty-state { padding: 24px 0; color: #475569; }
.json-data { font-size: 12px; color: #334155; white-space: pre-wrap; word-break: break-word; margin: 0; }
</style>
