import api from './api' 

export function obtenerInterfacesPorDispositivo(dispositivoId) {
  return api.get('/api/firewall/interfaz/dispositivo/' + dispositivoId)
}

export function crearInterfaz(interfaz) {
  return api.post('/api/firewall/interfaz/create', {
    ...interfaz
  })
}

export function eliminarInterfaz(id) {
  return api.delete('/api/firewall/interfaz/' + id)
}