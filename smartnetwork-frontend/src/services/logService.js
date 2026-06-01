import api from './api'

export function obtenerLogsPorDispositivo(dispositivoId) {
  return api.get(`/api/log/${dispositivoId}`)
}
