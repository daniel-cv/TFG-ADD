import api from './api'

export function obtenerUsuariosFirewallPorDispositivo(dispositivoId) {
  return api.get(`/api/firewalls/usuarioFirewall/dispositivo/${dispositivoId}`)
}

export function obtenerUsuariosFirewallPorUsuario() {
  return api.get(`/api/firewalls/usuarioFirewall/usuario`)
}

export function crearUsuarioFirewall(usuarioFirewall) {
  return api.post(`/api/firewalls/usuarioFirewall/create`, usuarioFirewall)
}

export function crearUsuarioFirewallCompleto(usuarioFirewall) {
  return api.post(`/api/firewalls/usuarioFirewall/full`, usuarioFirewall)
}

export function asignarUsuarioFirewallADispositivos(usuarioId, dispositivosIds) {
  return api.post(`/api/firewalls/usuarioFirewall/${usuarioId}/dispositivos`, dispositivosIds)
}

export function actualizarUsuarioFirewall(id, usuarioFirewall) {
  return api.put(`/api/firewalls/usuarioFirewall/edit/${id}`, usuarioFirewall)
}

export function eliminarUsuarioFirewall(id, dispositivosIds) {
  return api.delete(
    `/api/firewalls/usuarioFirewall/delete/${id}`,
    {
      data: dispositivosIds
    }
  )
}
