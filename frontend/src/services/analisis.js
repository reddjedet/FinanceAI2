import http from './http'

export async function guardarAnalisisFinanciero(idUsuario) {
  const { data } = await http.post(`/financial-analysis/generate/${idUsuario}`)
  return data
}

export async function obtenerHistorialAnalisis(idUsuario) {
  const { data } = await http.get(`/financial-analysis/history/${idUsuario}`)
  return data
}
