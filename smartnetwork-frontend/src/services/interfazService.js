import api from './api' // tu axios configurado

export function obtenerInterfacesPorDispositivo(dispositivoId) {
  return api.get('/api/firewall/interfaz/findById/' + dispositivoId)
}

export function crearInterfaz(interfaz) {
  return api.post('/api/firewall/interfaz/create', {
    ...interfaz
  })
}