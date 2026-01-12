import api from './api' // tu axios configurado

export function obtenerReglasPorDispositivo(dispositivoId) {
  return api.get('/api/firewalls/reglas/dispositivo/11')
}

export function crearReglaFirewall(regla) {
  return api.post('/api/firewalls/reglas', regla)
}