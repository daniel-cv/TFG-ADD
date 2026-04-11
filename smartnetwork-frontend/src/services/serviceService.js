import api from './api'

// Obtener services de un dispositivo
export function obtenerServicesPorDispositivo(dispositivoId) {
  return api.get(`/api/firewalls/services/dispositivo/${dispositivoId}`)
}

// Obtener services del usuario
export function obtenerServicesPorUsuario() {
  return api.get(`/api/firewalls/services/usuario`)
}

// Crear service simple
export function crearService(data) {
  return api.post('/api/firewalls/services/create', data)
}

// Crear + asignar (FULL)
export function crearServiceCompleto(data) {
  return api.post('/api/firewalls/services/full', data)
}

// Asignar service a dispositivos
export function asignarService(serviceId, dispositivosIds) {
  return api.post(`/api/firewalls/services/${serviceId}/dispositivos`, dispositivosIds)
}

// Obtener service por ID + dispositivo
export function obtenerServicePorId(serviceId, dispositivoId) {
  return api.get(`/api/firewalls/services/${serviceId}/dispositivo/${dispositivoId}`)
}

// Actualizar service
export function actualizarService(serviceId, dispositivoId, data) {
  return api.put(`/api/firewalls/services/${serviceId}/dispositivo/${dispositivoId}`, data)
}

// Eliminar service
export function eliminarService(id) {
  return api.delete(`/api/firewalls/services/delete/${id}`)
}
