import api from './api' // tu axios configurado

// Obtener todas las VirtualIPs de un dispositivo
export function obtenerVirtualIpsPorDispositivo(dispositivoId) {
  return api.get('/api/firewalls/virtualips/dispositivo/' + dispositivoId)
}

// Crear una nueva VirtualIP
export function crearVirtualIp(virtualIp) {
  return api.post('/api/firewalls/virtualips/create', {
    ...virtualIp
  })
}

export function eliminarVirtualIp(id) {
  return api.delete('/api/firewalls/virtualips/delete' + id)
}
