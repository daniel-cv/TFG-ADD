import api from '../api'

export function obtenerInterfacesPorDispositivo(dispositivoId) {
  return api.get('/api/switch/interfaces/dispositivo/' + dispositivoId)
}

export function crearInterfaz(interfaz) {
  return api.post('/api/switch/interfaces/create', {
    ...interfaz
  })
}

export function eliminarInterfaz(id) {
  return api.delete('/api/switch/interfaces/' + id)
}

export function actualizarInterfaz(id, interfaz) {
  return api.post('/api/switch/interfaces/edit/' + id, {
     ...interfaz
    })
}
