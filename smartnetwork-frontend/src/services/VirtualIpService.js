import api from './api'

export function obtenerVirtualIpsPorDispositivo(dispositivoId) {
  return api.get('/api/firewalls/virtualips/dispositivo/' + dispositivoId)
}
export function crearVirtualIp(virtualIp) {
  return api.post('/api/firewalls/virtualips/create', {
    ...virtualIp
  })
}
export function eliminarVirtualIp(id) {
  return api.delete('/api/firewalls/virtualips/delete' + id)
}
