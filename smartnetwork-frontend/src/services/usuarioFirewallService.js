import api from './api'

export function obtenerUsuarioFirewallPorDispositivo(dispositivoId) {
  return api.get('/api/firewalls/usuarioFirewall/dispositivo/' + dispositivoId)
}

export function crearUsuarioFirewall(usuarioFirewall) {
  return api.post('/api/firewalls/usuarioFirewall/create', {
    ...usuarioFirewall
  })
}

export function eliminarUsuarioFirewall(id) {
  return api.delete('/api/firewalls/usuarioFirewall/' + id)
}
