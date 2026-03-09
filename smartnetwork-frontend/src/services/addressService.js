import api from './api' // tu axios configurado

export function obtenerAddressesPorDispositivo(dispositivoId) {
  return api.get('/api/firewalls/addresses/dispositivo/' + dispositivoId)
}

export function crearAddress(address) {
  return api.post('/api/firewalls/addresses/create', {
    ...address
  })
}

export function eliminarAddress(id) {
  return api.delete('/api/firewalls/addresses/delete/' + id)
}

export function actualizarAddress(id, address) {
  return api.put('/api/firewalls/addresses/edit/' + id, { 
    ...address 
  })
}