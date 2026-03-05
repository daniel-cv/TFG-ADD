import api from './api' // tu axios configurado

export function obtenerServicesPorDispositivo(dispositivoId) {
  return api.get('/api/firewalls/services/dispositivo/' + dispositivoId)
}

export function crearService(service) {
  return api.post('/api/firewalls/services/create', {
    ...service
  })
}

export function eliminarService(id) {
  return api.delete('/api/firewalls/services/delete/' + id, {
  })
}