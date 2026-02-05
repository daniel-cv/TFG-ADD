import api from './api' // tu axios configurado

export function obtenerAddressesPorDispositivo(dispositivoId) {
  return api.get('/api/firewalls/addresses/dispositivo/' + dispositivoId)
}

export function crearAddress(address) {
  return api.post('/api/firewalls/addresses/create', address)
}