import api from '../api'

export function obtenerIpRoutesPorDispositivo(dispositivoId) {
  return api.get('/api/switch/iproutes/dispositivo/' + dispositivoId)
}

export function crearIpRoute(route) {
  return api.post('/api/switch/iproutes/create', {
    ...route
  })
}

export function eliminarIpRoute(id) {
  return api.delete('/api/switch/iproutes/' + id)
}

export function actualizarIpRoute(id, route) {
  return api.post('/api/switch/iproutes/edit/' + id, {
     ...route
    })
}
