// services/Switches/aclService.js
import api from '../api'

export function obtenerAclsPorDispositivo(dispositivoId) {
  return api.get('/api/switch/acls/dispositivo/' + dispositivoId)
}

export function crearAcl(acl) {
  return api.post('/api/switch/acls/create', acl)
}

export function eliminarAcl(id) {
  return api.delete('/api/switch/acls/' + id)
}

export function agregarRegla(aclId, regla) {
  return api.post('/api/switch/acls/' + aclId + '/reglas', regla)
}

export function eliminarRegla(id) {
  return api.delete('/api/switch/acls/reglas/' + id)
}
