import api from './api'

export function obtenerServicesPorDispositivo(dispositivoId) {
  return api.get(`/api/firewalls/services/dispositivo/${dispositivoId}`)
}

export function obtenerServicesPorUsuario() {
  return api.get(`/api/firewalls/services/usuario`)
}

export function crearService(data) {
  return api.post('/api/firewalls/services/create', data)
}

export function crearServiceCompleto(data) {
  return api.post('/api/firewalls/services/full', data)
}

export function asignarService(serviceId, dispositivosIds) {
  return api.post(`/api/firewalls/services/${serviceId}/dispositivos`, dispositivosIds)
}

export function obtenerServicePorId(serviceId, dispositivoId) {
  return api.get(`/api/firewalls/services/${serviceId}/dispositivo/${dispositivoId}`)
}

export function actualizarService(serviceId, payload) {
  return api.put(
    `/api/firewalls/services/edit/${serviceId}`,payload)
}

export function eliminarService(serviceId, dispositivosIds) {
  return api.delete(`/api/firewalls/services/delete/${serviceId}`,{
      data: dispositivosIds
    }
  )
}

export function preeliminarService(serviceId) {
  return api.delete(`/api/firewalls/services/predelete/${serviceId}`)
}