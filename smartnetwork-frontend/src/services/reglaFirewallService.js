import api from './api' // tu axios configurado

export function obtenerReglasPorDispositivo(dispositivoId) {
  return api.get('/api/firewalls/reglas/dispositivo/' + dispositivoId)
}

export function crearReglaFirewall(regla) {
  return api.post('/api/firewalls/reglas', {
    ...regla
  })
}
// 🔥 ELIMINAR regla simplificado: solo reglaId
export function eliminarReglaFirewall(reglaId) {
  return api.delete(`/api/firewalls/reglas/${reglaId}`)
}
