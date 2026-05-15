// services/reglaFirewallService.js

import api from './api'

/* ========================= */
/* GET */
/* ========================= */

export function obtenerReglasPorDispositivo(
  dispositivoId
) {

  return api.get(
    `/api/firewalls/reglas/dispositivo/${dispositivoId}`
  )
}

export function obtenerReglasUsuario() {

  return api.get(
    '/api/firewalls/reglas/usuario'
  )
}

/* ========================= */
/* CREATE */
/* ========================= */

export function crearReglaFirewall(
  regla
) {

  return api.post(
    '/api/firewalls/reglas/create',
    {
      ...regla
    }
  )
}

export function crearReglaFirewallCompleta(
  regla
) {

  return api.post(
    '/api/firewalls/reglas/full',
    {
      ...regla
    }
  )
}

/* ========================= */
/* DELETE */
/* ========================= */

export function eliminarReglaFirewall(
  id,
  dispositivosIds
) {

  return api.delete(
    `/api/firewalls/reglas/delete/${id}`,
    {
      headers: {
        'Content-Type': 'application/json'
      },

      data: {
        dispositivosIds
      }
    }
  )
}

/* ========================= */
/* EDIT */
/* ========================= */

export function actualizarReglaFirewall(
  id,
  regla
) {

  return api.put(
    `/api/firewalls/reglas/edit/${id}`,
    {
      ...regla
    }
  )
}

/* ========================= */
/* APPLY */
/* ========================= */

export function asignarReglaADispositivos(
  reglaId,
  dispositivosIds
) {

  return api.post(
    `/api/firewalls/reglas/${reglaId}/dispositivos`,
    dispositivosIds
  )
}