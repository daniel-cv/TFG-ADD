import api from './api' // tu axios configurado

export function obtenerReglasPorDispositivo(dispositivoId) {
  return api.get('/api/firewalls/reglas/dispositivo/' + dispositivoId)
}

export function crearReglaFirewall(regla, dispositivoId) {
  console.log('dispositivoId:', dispositivoId)
  return api.post('/api/firewalls/reglas', {
    ...regla,
    dispositivoId
  })
}
