import http from './http'

export async function registrarUsuario(datos) {
  const { data } = await http.post('/auth/register', datos)
  return data
}

export async function loginUsuario(email, password) {
  const { data } = await http.post('/auth/login', { email, password })
  return data
}

export async function obtenerUsuario(id) {
  const { data } = await http.get(`/users/profile/${id}`)
  return data
}

export async function actualizarSueldo(id, nuevoSueldo) {
  const { data } = await http.post(`/users/${id}/salary`, { nuevoSueldo })
  return data
}

export async function obtenerHistorialSueldo(id) {
  const { data } = await http.get(`/users/${id}/salary-history`)
  return data
}

export async function obtenerResumenMensual(id) {
  const { data } = await http.get(`/transactions/summary/${id}`)
  return data
}

export async function eliminarCuenta(id) {
  await http.delete(`/users/${id}`)
}
