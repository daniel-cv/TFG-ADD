import api from './api' // tu axios configurado

export function obtenerUsuarioFirewallPorDispositivo(dispositivoId) {
  return api.get('/api/firewalls/usuarioFirewall/dispositivo/' + dispositivoId)
}

export function crearUsuarioFirewall(usuarioFirewall) {
  return api.post('/api/firewalls/usuarioFirewall/create', {
    ...usuarioFirewall
  })
}