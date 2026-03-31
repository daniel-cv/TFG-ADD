import api from './api' 

export function obtenerReglasPorDispositivo(dispositivoId) {
  return api.get('/api/firewalls/reglas/dispositivo/' + dispositivoId)
}

export function crearReglaFirewall(regla) {
  return api.post('/api/firewalls/reglas', {
    ...regla
  })
}

export function eliminarReglaFirewall(id) {
  return api.delete('/api/firewalls/reglas/delete/' + id)
}

export function actualizarReglaFirewall(id, regla) {
  return api.put('api/firewalls/reglas/edit/' + id, { 
    ...regla
  })
}
