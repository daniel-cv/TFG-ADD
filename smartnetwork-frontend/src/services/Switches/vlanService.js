import api from '../api'

export function obtenerVlansPorDispositivo(dispositivoId) {
  return api.get('/api/switch/vlans/dispositivo/' + dispositivoId)
}

export function crearVlan(vlan) {
  return api.post('/api/switch/vlans/create', {
    ...vlan
  })
}

export function eliminarVlan(id) {
  return api.delete('/api/switch/vlans/' + id)
}

export function actualizarVlan(id, vlan) {
  return api.post('/api/switch/vlans/edit/' + id, {
     ...vlan
    })
}
