import api from './api' 

export function obtenerAddressesPorDispositivo(dispositivoId) {
  return api.get('/api/firewalls/addresses/dispositivo/' + dispositivoId)
}
export function obtenerAddressesPorUsuario() {
  return api.get('/api/firewalls/addresses/usuario/')
}
export function crearAddress(address) {
  return api.post('/api/firewalls/addresses/create', {
    ...address
  })
}
export function eliminarAddress(addressId, dispositivosIds) {
  console.log(dispositivosIds)
  return api.delete('/api/firewalls/addresses/delete/' + addressId, {
    data: dispositivosIds
  })
}
export function actualizarAddress(id, address) {
  return api.put('/api/firewalls/addresses/edit/' + id, address)
}
export function obtenerAddressPorId() {
  return api.get('/api/firewalls/addresses/usuario/')
}
export function aplicarAddressToDispositivos(addressId, dispositivosIds) {
  return api.post(`/api/firewalls/addresses/${addressId}/dispositivos`, dispositivosIds)
}
export function crearAddressCompleto(address) {
  return api.post('/api/firewalls/addresses/full', address)
}
