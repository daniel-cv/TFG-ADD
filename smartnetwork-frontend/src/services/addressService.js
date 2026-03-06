import api from './api'

export function obtenerAddressesPorDispositivo(dispositivoId) {
  return api.get('/api/firewalls/addresses/dispositivo/' + dispositivoId)
}

export function crearAddress(address) {
  return api.post('/api/firewalls/addresses/create', {
    ...address
  })
}

export function eliminarAddress(id) {
  return api.delete('/api/firewalls/addresses/' + id)
}