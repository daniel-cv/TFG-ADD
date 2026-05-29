import api from './api'

export function obtenerInterfacesUsuario() {
  return api.get('/api/firewall/interfaz/usuario')
}

export function obtenerInterfacesPorDispositivo(dispositivoId) {
  return api.get(`/api/firewall/interfaz/dispositivo/${dispositivoId}`)
}

export function crearInterfaz(interfaz) {
  return api.post('/api/firewall/interfaz/create', interfaz)
}

export function crearInterfazBasica(interfaz) {
  return api.post('/api/firewall/interfaz/create/basic', interfaz)
}

export function asignarInterfaz(interfazId, dispositivosIds) {
  return api.post(`/api/firewall/interfaz/${interfazId}/asignar`, dispositivosIds)
}

export function actualizarInterfaz(id, interfaz) {
  return api.put(`/api/firewall/interfaz/edit/${id}`, interfaz)
}

export function eliminarInterfaz(id, dispositivosIds) {
  return api.delete(`/api/firewall/interfaz/${id}`, {
    data: dispositivosIds
  })
}

export function preeliminarInterfaz(interfazId) {
  return api.delete('/api/firewalls/interfaz/predelete/' + interfazId)
}

export function actualizarSinImplementacionService(id, interfaz){
  return api.put('/api/firewalls/interfaz/preedit' + address.id, interfaz)
}